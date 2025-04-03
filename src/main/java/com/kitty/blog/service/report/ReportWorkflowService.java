//package com.kitty.blog.service.report;
//
//import com.kitty.blog.model.Report;
//import lombok.extern.slf4j.Slf4j;
//import org.activiti.engine.RuntimeService;
//import org.activiti.engine.TaskService;
//import org.activiti.engine.runtime.ProcessInstance;
//import org.activiti.engine.task.Task;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class ReportWorkflowService {
//    @Autowired
//    private RuntimeService runtimeService;
//
//    @Autowired
//    private TaskService taskService;
//
//    @Autowired
//    private ReportService reportService;
//
//    public void startReportProcess(Report report) {
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("reportId", report.getReportId());
//        variables.put("userId", report.getUserId());
//
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//                "reportReviewProcess",
//                String.valueOf(report.getReportId()),
//                variables
//        );
//
//        // 更新举报记录的流程实例ID
//        report.setProcessInstanceId(processInstance.getId());
//        reportService.update(report);
//    }
//
//    public void completeReviewTask(Integer reportId, boolean approved, String comment) {
//        Report report = reportService.findById(reportId).getBody();
//        Task task = taskService.createTaskQuery()
//                .processInstanceId(report.getProcessInstanceId())
//                .singleResult();
//
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("approved", approved);
//        variables.put("comment", comment);
//
//        taskService.complete(task.getId(), variables);
//
//        // 更新举报状态
//        report.setStatus(approved ? "APPROVED" : "REJECTED");
//        report.setComment(comment);
//        reportService.update(report);
//    }
//}