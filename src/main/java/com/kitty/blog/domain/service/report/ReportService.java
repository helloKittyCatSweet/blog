package com.kitty.blog.domain.service.report;

import com.kitty.blog.application.dto.report.ReportDto;
import com.kitty.blog.common.constant.ReportReason;
import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.Report;
import com.kitty.blog.common.constant.ReportStatus;
import com.kitty.blog.domain.repository.post.PostRepository;
import com.kitty.blog.domain.repository.ReportRepository;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.infrastructure.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@CacheConfig(cacheNames = "reports")
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReportWorkflowService reportWorkflowService;

    @Transactional
    public ResponseEntity<Boolean> create(Report report) {
        if (!userRepository.existsById(report.getUserId()) ||
                !postRepository.existsById(report.getPostId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        report.setStatus(ReportStatus.PENDING);
        report = reportRepository.save(report);

        try {
            reportWorkflowService.startReportProcess(report);
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("创建举报信息失败", e);
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> update(Report updatedReport) {
        if (!reportRepository.existsById(updatedReport.getReportId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        Report existingReport = (Report) reportRepository.findById(updatedReport.getReportId()).orElseThrow();
        // 按需更新
        try {
            UpdateUtil.updateNotNullProperties(updatedReport, existingReport);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        reportRepository.save(existingReport);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<List<ReportDto>> findByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }

        List<Report> reports = reportRepository.findByUserId(userId).orElse(new ArrayList<>());
        List<ReportDto> reportDtos = reports.stream()
                .map(this::convertToDto)
                .filter(dto -> dto != null)
                .toList();

        return new ResponseEntity<>(reportDtos, HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<Report>> findByArticleId(Integer postId) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(
                    reportRepository.findByArticleId(postId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#reason")
    public ResponseEntity<List<Report>> findByReason(String reason) {
        return new ResponseEntity<>(
                reportRepository.findByReason(reason).orElse(new ArrayList<>()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#reason+#postId")
    public ResponseEntity<List<Report>> findByReasonForPost(String reason, Integer postId) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(
                    reportRepository.findByReasonForPost(reason, postId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    // @Transactional
    // public ResponseEntity<Boolean> changeStatus(Integer reportId, String status)
    // {
    // if (!reportRepository.existsById(reportId)) {
    // return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    // }
    // Report report = (Report) reportRepository.findById(reportId).orElse(null);
    // if (report == null) {
    // return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    // } else {
    // if (isStatusValid(status)) {
    // report.setStatus(status);
    // return new ResponseEntity<>(true, HttpStatus.OK);
    // } else {
    // return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    // }
    // }
    // }

    private boolean isStatusValid(String status) {
        try {
            // 尝试将字符串转为枚举类型，如果成功，则说明是有效的枚举值
            ReportStatus.valueOf(status);
            return true;
        } catch (IllegalArgumentException e) {
            // 如果抛出 IllegalArgumentException，则说明不是合法的枚举值
            return false;
        }
    }

    @Transactional
    public ResponseEntity<List<Report>> findByStatus(ReportStatus status) {
        return new ResponseEntity<>(
                reportRepository.findByStatus(status).orElse(new ArrayList<>()),
                HttpStatus.OK);
    }

    @Transactional
    public boolean hasReportedPost(Integer postId, Integer userId) {
        List<Report> reports = reportRepository.findByPostIdAndUserId(postId, userId).orElse(null);
        return reports != null && !reports.isEmpty();
    }

    /**
     * Auto-generated methods
     */
    @Transactional
    public ResponseEntity<Report> save(Report report) {
        return new ResponseEntity<>((Report) reportRepository.save(report), HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#reportId")
    public ResponseEntity<Report> findById(Integer reportId) {
        return new ResponseEntity<>(
                (Report) reportRepository.findById(reportId).orElse(null),
                HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<List<ReportDto>> findAll() {
        if (reportRepository.count() == 0) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        List<Report> reports = reportRepository.findAll();
        List<ReportDto> reportDtos = reports.stream()
                .map(this::convertToDto)
                .filter(dto -> dto != null)
                .toList();

        return new ResponseEntity<>(reportDtos, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer reportId) {
        if (!existsById(reportId).getBody()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        reportRepository.deleteById(reportId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(reportRepository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer reportId) {
        return new ResponseEntity<>(reportRepository.existsById(reportId), HttpStatus.OK);
    }

    /**
     * 搜索举报信息（根据角色权限）
     */
    @Transactional
    public ResponseEntity<List<ReportDto>> searchReports(Integer userId, String keyword, ReportStatus status,
            ReportReason reason, boolean isAdmin) {
        String username = userRepository.findById(userId).orElse(new User()).getUsername();
        if (username == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        try {
            List<Report> reports;

            if (isAdmin) {
                reports = reportRepository.searchReportsForAdmin(
                        keyword.isEmpty() ? null : keyword,
                        status,
                        reason);
            } else {
                reports = reportRepository.searchReportsForUser(
                        userId,
                        keyword.isEmpty() ? null : keyword,
                        status,
                        reason);
            }

            List<ReportDto> reportDtos = reports.stream()
                    .map(this::convertToDto)
                    .filter(dto -> dto != null)
                    .toList();
            return new ResponseEntity<>(reportDtos, HttpStatus.OK);
        } catch (Exception e) {
            log.error("搜索举报信息失败", e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Boolean> approve(Integer reportId, String comment) {
        try {
            Report report = findById(reportId).getBody();
            if (report == null) {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }

            report.setStatus(ReportStatus.APPROVED);
            report.setComment(comment);
            reportRepository.save(report);

            reportWorkflowService.completeReviewTask(reportId, true, comment);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            log.error("举报信息审批失败", e);
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Boolean> reject(Integer reportId, String comment) {
        try {
            Report report = findById(reportId).getBody();
            if (report == null) {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }

            report.setStatus(ReportStatus.REJECTED);
            report.setComment(comment);
            reportRepository.save(report);

            reportWorkflowService.completeReviewTask(reportId, false, comment);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            log.error("举报信息驳回失败", e);
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 添加映射函数
    private ReportDto convertToDto(Report report) {
        User user = userRepository.findById(report.getUserId()).orElse(null);
        if (user == null)
            return null;

        Post post = postRepository.findById(report.getPostId()).orElse(null);
        if (post == null)
            return null;

        return ReportDto.builder()
                .reportId(report.getReportId())
                .postId(report.getPostId())
                .userId(report.getUserId())
                .reason(report.getReason().getLabel())
                .createdAt(report.getCreatedAt())
                .status(report.getStatus())
                .processInstanceId(report.getProcessInstanceId())
                .comment(report.getComment())
                .username(user.getUsername())
                .postTitle(post.getTitle())
                .description(report.getDescription())
                .build();
    }
}