package com.kitty.blog.domain.service.report;

import com.kitty.blog.common.constant.ReportStatus;
import com.kitty.blog.domain.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ReportWorkflowService {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ReportService reportService;

    public void startReportProcess(Report report) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("reportId", report.getReportId());
        variables.put("userId", report.getUserId());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "reportReviewProcess",
                String.valueOf(report.getReportId()),
                variables);

        // 更新举报记录的流程实例ID
        report.setProcessInstanceId(processInstance.getId());
        reportService.update(report);
    }

    public void completeReviewTask(Integer reportId, boolean approved, String comment) {
        Report report = reportService.findById(reportId).getBody();
        if (report == null) {
            log.error("找不到举报记录: reportId={}", reportId);
            throw new IllegalArgumentException("找不到举报记录");
        }

        // 检查流程实例ID是否存在
        if (report.getProcessInstanceId() == null) {
            log.error("举报记录没有关联的流程实例: reportId={}", reportId);
            throw new IllegalStateException("举报记录没有关联的流程实例");
        }

        Task task = taskService.createTaskQuery()
                .processInstanceId(report.getProcessInstanceId())
                .singleResult();

        // 检查任务是否存在
        if (task == null) {
            log.error("找不到相关的审核任务: reportId={}, processInstanceId={}",
                    reportId, report.getProcessInstanceId());
            throw new IllegalStateException("找不到相关的审核任务");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approved);
        variables.put("comment", comment);

        try {
            taskService.complete(task.getId(), variables);

            // 更新举报状态
            report.setStatus(approved ? ReportStatus.APPROVED : ReportStatus.REJECTED);
            report.setComment(comment);
            reportService.update(report);
        } catch (Exception e) {
            log.error("完成审核任务失败: reportId={}, taskId={}", reportId, task.getId(), e);
            throw new RuntimeException("完成审核任务失败", e);
        }
    }
}