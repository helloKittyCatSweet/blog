package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends BaseRepository<Message, Integer> {
        /*
         * save(S entity)：保存实体。
         * findById(ID id)：根据主键查找实体。
         * findAll()：查找所有实体。
         * deleteById(ID id)：根据主键删除实体。
         * count()：返回实体的总数。
         * existsById(ID id)：检查实体是否存在。
         */

        /**
         * create section
         */
        // save
        @Query("SELECT CASE WHEN COUNT(m) > 0 " +
                        "THEN true ELSE false END FROM Message m WHERE m.messageId = ?1")
        boolean existsById(Integer messageId);

        /**
         * delete section
         */
        // deleteById

        /**
         * update section
         */
        /**
         * 标记消息为已读
         * 
         * @param senderId   发送方ID（消息的发送者）
         * @param receiverId 接收方ID（当前用户）
         */
        @Modifying
        @Query("update Message m set m.isRead = true where m.senderId = :senderId and m.receiverId = :receiverId")
        void readMessage(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId);

        /**
         * 标记发送者最后一条消息为未读
         * 
         * @param senderId   发送者ID
         * @param receiverId 接收者ID
         */
        @Modifying
        @Query(value = "UPDATE fs_messages SET is_read = false " +
                        "WHERE message_id = (" +
                        "    SELECT message_id FROM (" +
                        "        SELECT message_id " +
                        "        FROM fs_messages " +
                        "        WHERE sender_id = ?1 " +
                        "        AND receiver_id = ?2 " +
                        "        ORDER BY created_at DESC " +
                        "        LIMIT 1" +
                        "    ) AS temp" +
                        ")", nativeQuery = true)
        void unReadMessage(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId);

        /**
         * find section
         */
        // findById(ID id)：根据主键查找实体。
        // findAll()

        /**
         * 根据发件人查找消息
         *
         * @param senderId
         * @return
         */
        @Query("select m from Message m where m.senderId =?1")
        Optional<List<Message>> findBySenderId(Integer senderId);

        /**
         * 根据收件人查找消息
         *
         * @param receiverId
         * @return
         */
        @Query("select m from Message m where m.receiverId =?1")
        Optional<List<Message>> findByReceiverId(Integer receiverId);

        /**
         * 根据内容和发件人查找消息
         *
         * @param content
         * @param senderId
         * @return
         */
        @Query("select m from Message m where m.content LIKE %?1% and m.senderId = ?2")
        Optional<List<Message>> findByContentForSender(String content, Integer senderId);

        /**
         * 根据内容和收件人查找消息
         *
         * @param content
         * @param receiverId
         * @return
         */
        @Query("select m from Message m where m.content LIKE %?1% and m.receiverId = ?2")
        Optional<List<Message>> findByContentForReceiver(String content, Integer receiverId);

        /**
         * 查找联系人列表（包含双向联系人且去重）
         *
         * @param userId
         * @return
         */
        @Query("SELECT DISTINCT CASE " +
                        "WHEN m.senderId = :userId THEN m.receiverId " +
                        "WHEN m.receiverId = :userId THEN m.senderId " +
                        "END " +
                        "FROM Message m " +
                        "WHERE m.senderId = :userId OR m.receiverId = :userId")
        Optional<List<Integer>> findContactedUserIds(@Param("userId") Integer userId);

        /**
         * 获取A和B聊天的全部内容
         *
         * @param senderId
         * @param receiverId
         * @return
         */
        @Query("SELECT m FROM Message m WHERE (" +
                        "m.senderId = :senderId AND m.receiverId = :receiverId) OR " +
                        "(m.senderId = :receiverId AND m.receiverId = :senderId) ORDER BY m.parentId, m.messageId")
        Optional<List<Message>> findConversation(@Param("senderId") Integer senderId,
                        @Param("receiverId") Integer receiverId);

        /**
         * 获取A和B聊天的最新一条消息（双向）
         *
         * @param senderId
         * @param receiverId
         * @return
         */
        @Query("SELECT m FROM Message m WHERE " +
                        "(m.senderId = :senderId AND m.receiverId = :receiverId) OR " +
                        "(m.senderId = :receiverId AND m.receiverId = :senderId) " +
                        "ORDER BY m.messageId DESC LIMIT 1")
        Optional<Message> findLastMessage(@Param("senderId") Integer senderId,
                        @Param("receiverId") Integer receiverId);

        /**
         * 获取A和B聊天的未读消息数量
         *
         * @param senderId
         * @param receiverId
         * @return
         */
        @Query("SELECT COUNT(m) FROM Message m WHERE m.senderId = :senderId " +
                        "AND m.receiverId = :receiverId AND m.isRead = false")
        int findUnreadCount(Integer senderId, Integer receiverId);

        /**
         * 获取A收到的未读消息数量
         *
         * @param receiverId
         * @return
         */
        @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :receiverId AND m.isRead = false")
        Long countByReceiverIdAndIsReadFalse(Integer receiverId);

        @Query("SELECT m " +
                        "FROM Message m " +
                        "JOIN User u ON (CASE " +
                        "    WHEN m.senderId = :userId THEN m.receiverId = u.id " +
                        "    ELSE m.senderId = u.id " +
                        "END) " +
                        "WHERE (:receiverName IS NULL OR u.username LIKE %:receiverName%) " +
                        "AND (:content IS NULL OR m.content LIKE %:content%) " +
                        "AND (:startTime IS NULL OR m.createdAt >= :startTime) " +
                        "AND (:endTime IS NULL OR m.createdAt < :endTime) " +
                        "AND (m.senderId = :userId OR m.receiverId = :userId) " +
                        "ORDER BY m.createdAt DESC")
        Page<Message> findMessagePage(
                        @Param("receiverName") String receiverName,
                        @Param("content") String content,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime,
                        @Param("userId") Integer userId,
                        Pageable pageable);

        @Query("SELECT m FROM Message m " +
                        "JOIN User s ON m.senderId = s.id " +
                        "JOIN User r ON m.receiverId = r.id " +
                        "WHERE (:senderName IS NULL OR s.username LIKE %:senderName%) " +
                        "AND (:receiverName IS NULL OR r.username LIKE %:receiverName%) " +
                        "AND (:content IS NULL OR m.content LIKE %:content%) " +
                        "AND (:startDate IS NULL OR m.createdAt >= :startDate) " +
                        "AND (:endDate IS NULL OR m.createdAt < :endDate) " +
                        "ORDER BY m.createdAt DESC")
        Page<Message> searchMessages(
                        @Param("senderName") String senderName,
                        @Param("receiverName") String receiverName,
                        @Param("content") String content,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        @Modifying
        @Query("UPDATE Message m SET m.operation = :operation WHERE m.messageId = :messageId")
        Integer setOperation(Integer messageId, boolean operation);

        Optional<Message> findFirstBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtDesc(
                        Integer senderId1, Integer receiverId1,
                        Integer senderId2, Integer receiverId2);
}
