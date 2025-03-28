package com.kitty.blog.repository;

import com.kitty.blog.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends BaseRepository<Message, Integer> {
    /*
      save(S entity)：保存实体。
      findById(ID id)：根据主键查找实体。
      findAll()：查找所有实体。
      deleteById(ID id)：根据主键删除实体。
      count()：返回实体的总数。
      existsById(ID id)：检查实体是否存在。
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
     * 标记消息为已读或未读
     * @param isRead
     * @param messageId
     */
    @Modifying // 标注更新操作
    @Query("update Message set isRead =?1 where messageId =?2")
    void readMessage(boolean isRead, Integer messageId);

    /**
     * find section
     */
    //   findById(ID id)：根据主键查找实体。
    //   findAll()

    /**
     * 根据发件人查找消息
     * @param senderId
     * @return
     */
    @Query("select m from Message m where m.senderId =?1")
    Optional<List<Message>> findBySenderId(Integer senderId);

    /**
     * 根据收件人查找消息
     * @param receiverId
     * @return
     */
    @Query("select m from Message m where m.receiverId =?1")
    Optional<List<Message>> findByReceiverId(Integer receiverId);

    /**
     * 根据内容和发件人查找消息
     * @param content
     * @param senderId
     * @return
     */
    @Query("select m from Message m where m.content LIKE %?1% and m.senderId = ?2")
    Optional<List<Message>> findByContentForSender(String content, Integer senderId);

    /**
     * 根据内容和收件人查找消息
     * @param content
     * @param receiverId
     * @return
     */
    @Query("select m from Message m where m.content LIKE %?1% and m.receiverId = ?2")
    Optional<List<Message>> findByContentForReceiver(String content, Integer receiverId);

    /**
     * 查找联系人列表
      * @param userId
     * @return
     */
    @Query("select m.receiverId from Message m where m.senderId = ?1")
    Optional<List<Integer>> findContactedUserIds(Integer userId);

    /**
     * 获取A和B聊天的全部内容
     * @param senderId
     * @param receiverId
     * @return
     */
    @Query("SELECT m FROM Message m WHERE (" +
            "m.senderId = :senderId AND m.receiverId = :receiverId) OR " +
            "(m.senderId = :receiverId AND m.receiverId = :senderId) ORDER BY m.parentId, m.messageId")
    Optional<List<Message>> findConversation(@Param("senderId") Integer senderId,
                                   @Param("receiverId") Integer receiverId);
}
