package com.kitty.blog.service;

import com.kitty.blog.dto.user.MessageInfo;
import com.kitty.blog.model.Message;
import com.kitty.blog.model.User;
import com.kitty.blog.repository.MessageRepository;
import com.kitty.blog.repository.UserRepository;
import com.kitty.blog.service.contentReview.BaiduContentService;
import com.kitty.blog.utils.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "message")
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BaiduContentService baiduContentService;

    @Transactional
    public ResponseEntity<Boolean> create(Message message) {
        if (!userRepository.existsById(message.getSenderId())
                || !userRepository.existsById(message.getReceiverId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        String s = baiduContentService.checkText(message.getContent());
        if (!s.equals("合规")){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        message.setIsRead(false);
        save(message);
        return new ResponseEntity<>(true, HttpStatus.OK);
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
        if (!s.equals("合规")){
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
    public ResponseEntity<Boolean> readMessage(boolean isRead, Integer messageId) {
        if (!messageRepository.existsById(messageId)){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        messageRepository.readMessage(isRead,messageId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#userId")
    public ResponseEntity<List<Message>> findBySenderId(Integer senderId) {
        if (!userRepository.existsById(senderId)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(
                    messageRepository.findBySenderId(senderId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#userId")
    public ResponseEntity<List<Message>> findByReceiverId(Integer receiverId) {
        if (!userRepository.existsById(receiverId)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity(
                    messageRepository.findByReceiverId(receiverId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#senderId + #receiverId")
    public ResponseEntity<List<Message>> findByContentForSender(String content, Integer senderId) {
        if (!userRepository.existsById(senderId)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(messageRepository.
                    findByContentForSender(content, senderId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#receiverId + #senderId")
    public ResponseEntity<List<Message>> findByContentForReceiver(String content, Integer receiverId) {
        if (!userRepository.existsById(receiverId)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(messageRepository.
                    findByContentForReceiver(content, receiverId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#userId")
    public ResponseEntity<List<MessageInfo>> findContactedUserNames(Integer userId) {
        if (!userRepository.existsById(userId)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            List<Integer> contactedUserIds =
                    messageRepository.findContactedUserIds(userId).orElse(new ArrayList<>());
            if (contactedUserIds.isEmpty()){
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
            }
            List<MessageInfo> contactedUserNames = new ArrayList<>();
            for (Integer contactedUserId : contactedUserIds) {
                User user = (User) userRepository.findById(contactedUserId).get();
                contactedUserNames.add(
                        new MessageInfo(user.getUserId(), user.getUsername())
                );
            }
            return new ResponseEntity<>(contactedUserNames, HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#senderId + #receiverId")
    public ResponseEntity<List<Message>> findConversation(Integer senderId, Integer receiverId) {
        if (!userRepository.existsById(senderId) || !userRepository.existsById(receiverId)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(
                    messageRepository.findConversation(senderId, receiverId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
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
        if (messageRepository.count() == 0){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messageRepository.findAll(), HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer id) {
        if (!existsById(id).getBody()){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        messageRepository.deleteById(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public boolean hasDeletePermission(Integer userId, Integer messageId) {
        Message message = findById(messageId).getBody();
        if (message == null){
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
}
