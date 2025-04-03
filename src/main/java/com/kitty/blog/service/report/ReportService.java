package com.kitty.blog.service.report;

import com.kitty.blog.model.User;
import com.kitty.blog.model.Report;
import com.kitty.blog.constant.ReportStatus;
import com.kitty.blog.repository.PostRepository;
import com.kitty.blog.repository.ReportRepository;
import com.kitty.blog.repository.UserRepository;
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
@CacheConfig(cacheNames = "reports")
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @Transactional
    public ResponseEntity<Boolean> create(Report report) {
        if (!userRepository.existsById(report.getUserId()) ||
                !postRepository.existsById(report.getPostId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        report.setStatus(ReportStatus.PENDING.name());
        reportRepository.save(report);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> update(Report updatedReport) {
        if (!reportRepository.existsById(updatedReport.getReportId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        Report existingReport =
                (Report) reportRepository.findById(updatedReport.getReportId()).orElseThrow();
        // 按需更新
        try {
            UpdateUtil.updateNotNullProperties(updatedReport, existingReport);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        existingReport.setStatus(ReportStatus.PENDING.name());
        reportRepository.save(existingReport);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<List<Report>> findByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(
                    reportRepository.findByUserId(userId).orElse(new ArrayList<>()),
                    HttpStatus.OK
            );
        }
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

    @Transactional
    public ResponseEntity<Boolean> changeStatus(Integer reportId, String status) {
        if (!reportRepository.existsById(reportId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        Report report = (Report) reportRepository.findById(reportId).orElse(null);
        if (report == null) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } else {
            if (isStatusValid(status)) {
                report.setStatus(status);
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
            }
        }
    }

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
    public ResponseEntity<List<Report>> findByStatus(String status) {
        if (isStatusValid(status)) {
            return new ResponseEntity<>(
                    reportRepository.findByStatus(status).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
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
    public ResponseEntity<List<Report>> findAll() {
        if (reportRepository.count() == 0) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(reportRepository.findAll(), HttpStatus.OK);
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

}
