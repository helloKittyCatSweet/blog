package com.kitty.blog.domain.service.report;

import com.kitty.blog.domain.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ReportWorkflowService {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 启动举报审核流程
     */
    void startReportProcess(Report report) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("reportId", report.getReportId());
            variables.put("userId", report.getUserId());
            variables.put("postId", report.getPostId());
            variables.put("reason", report.getReason());
            variables.put("description", report.getDescription());

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                    "reportReviewProcess",
                    String.valueOf(report.getReportId()),
                    variables);

            report.setProcessInstanceId(processInstance.getId());
            log.info("举报工作流启动成功: reportId={}, processInstanceId={}",
                    report.getReportId(), processInstance.getId());
        } catch (Exception e) {
            log.error("启动举报工作流失败: reportId={}", report.getReportId(), e);
            throw new RuntimeException("启动举报工作流失败", e);
        }
    }

    /**
     * 完成审核任务
     */
    public void completeReviewTask(Integer reportId, boolean approved, String comment) {
        try {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceBusinessKey(String.valueOf(reportId))
                    .singleResult();

            if (processInstance == null) {
                log.error("找不到流程实例: reportId={}", reportId);
                throw new IllegalStateException("找不到流程实例");
            }

            Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getId())
                    .singleResult();

            if (task == null) {
                log.error("找不到审核任务: reportId={}, processInstanceId={}",
                        reportId, processInstance.getId());
                throw new IllegalStateException("找不到审核任务");
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("approved", approved);
            variables.put("comment", comment);
            variables.put("reviewTime", new Date());

            taskService.complete(task.getId(), variables);
            log.info("举报审核任务完成: reportId={}, approved={}", reportId, approved);
        } catch (Exception e) {
            log.error("完成审核任务失败: reportId={}", reportId, e);
            throw new RuntimeException("完成审核任务失败", e);
        }
    }
}