package com.kitty.blog.service;

import com.kitty.blog.dto.message.MessageStatusUpdate;
import com.kitty.blog.dto.message.MessageUserInfo;
import com.kitty.blog.model.Message;
import com.kitty.blog.model.User;
import com.kitty.blog.constant.MessageStatus;
import com.kitty.blog.repository.MessageRepository;
import com.kitty.blog.repository.UserRepository;
import com.kitty.blog.service.contentReview.BaiduContentService;
import com.kitty.blog.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "message")
@Slf4j
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BaiduContentService baiduContentService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public ResponseEntity<Boolean> create(Message message) {
        if (!userRepository.existsById(message.getSenderId())
                || !userRepository.existsById(message.getReceiverId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        String s = baiduContentService.checkText(message.getContent());
        if (!s.equals("合规")) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        message.setIsRead(false);
        message.setStatus(MessageStatus.SENT.name());
        Message savedMessage = save(message).getBody();

        // 发送WebSocket消息
        assert savedMessage != null;
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverId().toString(),
                "/queue/message", savedMessage);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * 更新消息状态
     * @param messageId
     * @param messageStatus
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public void updateMessageStatus(Integer messageId, String messageStatus) {
        Message message = findById(messageId).getBody();
        if(message != null){
            message.setStatus(messageStatus);
            if (messageStatus.equals(MessageStatus.READ.name())){
                message.setIsRead(true);
            }
            save(message);

            // 通知发送者消息状态更新
            simpMessagingTemplate.convertAndSendToUser(message.getSenderId().toString(),
                    "/queue/message-status",
                    new MessageStatusUpdate(messageId, messageStatus));
        }
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> update(Message message) {
        if (!messageRepository.existsById(message.getMessageId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        if (!userRepository.existsById(message.getSenderId())
                || !userRepository.existsById(message.getReceiverId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        String s = baiduContentService.checkText(message.getContent());
        if (!s.equals("合规")) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        message.setIsRead(false);
        Message oldMessage = findById(message.getMessageId()).getBody();
        try {
            UpdateUtil.updateNotNullProperties(message, oldMessage);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        save(oldMessage);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> readMessage(Integer senderId,Integer receiverId) {
        if (!messageRepository.existsById(receiverId) || !userRepository.existsById(senderId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        messageRepository.readMessage(senderId, receiverId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> unReadMessage(Integer senderId, Integer receiverId) {
        if (!messageRepository.existsById(receiverId) || !userRepository.existsById(senderId)){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        messageRepository.unReadMessage(senderId, receiverId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#userId")
    public ResponseEntity<List<Message>> findBySenderId(Integer senderId) {
        if (!userRepository.existsById(senderId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(
                    messageRepository.findBySenderId(senderId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#userId")
    public ResponseEntity<List<Message>> findByReceiverId(Integer receiverId) {
        if (!userRepository.existsById(receiverId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity(
                    messageRepository.findByReceiverId(receiverId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#senderId + #receiverId")
    public ResponseEntity<List<Message>> findByContentForSender(String content, Integer senderId) {
        if (!userRepository.existsById(senderId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(messageRepository.
                    findByContentForSender(content, senderId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#receiverId + #senderId")
    public ResponseEntity<List<Message>> findByContentForReceiver(String content, Integer receiverId) {
        if (!userRepository.existsById(receiverId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(messageRepository.
                    findByContentForReceiver(content, receiverId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#userId")
    public ResponseEntity<List<MessageUserInfo>> findContactedUserNames(Integer userId) {
        if (!userRepository.existsById(userId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            List<Integer> contactedUserIds =
                    messageRepository.findContactedUserIds(userId).orElse(new ArrayList<>());
            if (contactedUserIds.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
            }
            List<MessageUserInfo> contactedUserNames = new ArrayList<>();
            for (Integer contactedUserId : contactedUserIds) {
                User user = (User) userRepository.findById(contactedUserId).get();
                Message message = messageRepository.findLastMessage(userId, contactedUserId).orElse(null);
                MessageUserInfo messageInfo = new MessageUserInfo.Builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .avatar(user.getAvatar())
                        .lastMessage(message == null ? "暂无消息" : message.getContent())
                        .unreadCount(messageRepository.findUnreadCount(userId, contactedUserId))
                        .lastMessageTime(message == null ? null : message.getCreatedAt())
                        .build();
                contactedUserNames.add(messageInfo);
            }
            return new ResponseEntity<>(contactedUserNames, HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#senderId + #receiverId")
    public ResponseEntity<List<Message>> findConversation(Integer senderId, Integer receiverId) {
        if (!userRepository.existsById(senderId) || !userRepository.existsById(receiverId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(
                    messageRepository.findConversation(senderId, receiverId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    /**
     * 发送私人消息
     * @param message
     * @return
     */
    @Transactional
    public ResponseEntity<Message> sendPrivateMessage(Message message){
        Message savedMessage = save(message).getBody();
        if (savedMessage != null){
            // 发送给接收者
            simpMessagingTemplate.convertAndSendToUser(
                    savedMessage.getReceiverId().toString(),
                    "/queue/message",
                    savedMessage
            );
            // 发送状态更新给发送者
            simpMessagingTemplate.convertAndSendToUser(
                    savedMessage.getSenderId().toString(),
                    "/queue/message-status",
                    new MessageStatusUpdate(savedMessage.getMessageId(), MessageStatus.SENT.name())
            );
            return new ResponseEntity<>(savedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    /**
     * 发送群组消息
     * @param message
     * @return
     */
    @Transactional
    public ResponseEntity<Message> sendTopicMessage(Message message){
        Message savedMessage = save(message).getBody();
        if (savedMessage != null){
            simpMessagingTemplate.convertAndSend(
                    "/topic/chat",
                    savedMessage
            );
            return new ResponseEntity<>(savedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    /**
     * 获取用户未读消息数量
     * @param receiverId
     * @return
     */
    @Transactional
    public ResponseEntity<Long> countByReceiverIdAndIsReadFalse(Integer receiverId) {
        return new ResponseEntity<>
                (messageRepository.countByReceiverIdAndIsReadFalse(receiverId), HttpStatus.OK);
    }

    /**
     * 标记消息为已读并通知发送者
     * @param messageId
     * @param receiverId
     */
    @Transactional
    public void markMessageAsRead(Integer messageId, Integer receiverId) {
        Message message = findById(messageId).getBody();
        if (message != null && message.getReceiverId().equals(receiverId) && !message.getIsRead()){
            message.setIsRead(true);
            message.setStatus(MessageStatus.READ.name());
            save(message);

            // 通知发送者消息已读
            simpMessagingTemplate.convertAndSendToUser(message.getSenderId().toString(),
                    "/queue/message-status",
                    new MessageStatusUpdate(messageId, MessageStatus.READ.name()));
        }
    }

    /**
     * Auto-generated methods
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Message> save(Message message) {
        return new ResponseEntity<>((Message) messageRepository.save(message), HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#id")
    public ResponseEntity<Message> findById(Integer id) {
        return new ResponseEntity<>((Message) messageRepository.findById(id).orElse(new Message()),
                HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<List<Message>> findAll() {
        if (messageRepository.count() == 0) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messageRepository.findAll(), HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer id) {
        if (!existsById(id).getBody()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        messageRepository.deleteById(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public boolean hasDeletePermission(Integer userId, Integer messageId) {
        Message message = findById(messageId).getBody();
        if (message == null) {
            return false;
        }
        return message.getSenderId().equals(userId) || message.getReceiverId().equals(userId);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(messageRepository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer id) {
        return new ResponseEntity<>(messageRepository.existsById(id), HttpStatus.OK);
    }

    /**
     * 分页查询消息列表
     */
    public ResponseEntity<Page<Message>> findMessagePage(
            String receiverName, String content,
            LocalDate startDate, LocalDate endDate,
            Integer userId, Pageable pageable) {
        try {
            // 转换日期范围
            LocalDateTime startTime = startDate != null ?
                    startDate.atStartOfDay() : null;
            LocalDateTime endTime = endDate != null ?
                    endDate.plusDays(1).atStartOfDay() : null;

            Page<Message> messages = messageRepository.findMessagePage(
                    receiverName, content, startTime, endTime, userId, pageable);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("查询消息列表失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 批量删除消息
     */
    public ResponseEntity<Void> batchDelete(List<Integer> messageIds, Integer userId) {
        try {
            // 验证权限
            List<Message> messages = messageRepository.findAllById(messageIds);
            for (Message message : messages) {
                if (!hasDeletePermission(userId, message.getMessageId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            messageRepository.deleteAllById(messageIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("批量删除消息失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
