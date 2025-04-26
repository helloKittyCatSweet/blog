package com.kitty.blog.domain.repository;

import com.kitty.blog.common.constant.ReportReason;
import com.kitty.blog.common.constant.ReportStatus;
import com.kitty.blog.domain.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends BaseRepository<Report, Integer> {
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
    @Query("SELECT CASE WHEN COUNT(r) > 0 " +
            "THEN true ELSE false END FROM Report r WHERE r.reportId = ?1")
    boolean existsById(Integer reportId);

    /**
     * delete section
     */
    // deleteById

    /**
     * update section
     */
    /**
     * change status of report
     * @param reportId
     * @param status
     * @return
     */
    @Modifying
    @Query("UPDATE Report r SET r.status = ?2 WHERE r.reportId = ?1")
    Optional<Boolean> changeStatus(Integer reportId, String status);
    /**
     * find section
     */

    /**
     * 根据用户id查找举报信息
     * @param userId
     * @return
     */
    @Query("select r from Report r where r.userId =?1")
    Page<Report> findByUserId(Integer userId, Pageable pageable);

    /**
     * 根据文章id查找举报信息
     * @param postId
     * @return
     */
    @Query("select r from Report r where r.postId =?1")
    Page<Report> findByArticleId(Integer postId, Pageable pageable);

    /**
     * 根据文章id和用户id查找举报信息
     * @param postId
     * @param userId
     * @return
     */
    @Query("select r from Report r where r.postId =?1 and r.userId =?2")
    Page<Optional<List<Report>>> findByPostIdAndUserId(Integer postId, Integer userId,
                                                       Pageable pageable);

    @Query("select r from Report r where r.postId =?1 and r.userId =?2")
    Optional<List<Report>> findByPostIdAndUserId(Integer postId, Integer userId);

    /**
     * 根据举报原因查找举报信息
     * @param reason
     * @return
     */
    @Query("select r from Report r where r.reason LIKE %?1%")
    Page<Report> findByReason(String reason, Pageable pageable);

    /**
     * 根据举报原因和文章id查找举报信息
     * @param reason
     * @param postId
     * @return
     */
    @Query("select r from Report r where r.reason LIKE %?1% and r.postId =?2")
    Page<Report> findByReasonForPost(String reason, Integer postId,
                                                     Pageable pageable);

    /**
     * 根据状态查找举报信息
     * @param status
     * @return
     */

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    /**
     * 管理员根据状态和文章id查找举报信息
     * @param keyword
     * @param status
     * @param reason
     * @return
     */
    @Query("SELECT r FROM Report r WHERE " +
            "(:keyword IS NULL OR r.description LIKE %:keyword%) AND " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:reason IS NULL OR r.reason = :reason)")
    Page<Report> searchReportsForAdmin(
            @Param("keyword") String keyword,
            @Param("status") ReportStatus status,
            @Param("reason") ReportReason reason,
            Pageable pageable
    );

    @Query("SELECT r FROM Report r WHERE r.userId = :userId AND " +
            "(:keyword IS NULL OR r.description LIKE %:keyword%) AND " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:reason IS NULL OR r.reason = :reason)")
    Page<Report> searchReportsForUser(
            @Param("userId") Integer userId,
            @Param("keyword") String keyword,
            @Param("status") ReportStatus status,
            @Param("reason") ReportReason reason,
            Pageable pageable
    );
}
