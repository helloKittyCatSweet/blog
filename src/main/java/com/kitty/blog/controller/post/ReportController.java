package com.kitty.blog.controller.post;

import com.kitty.blog.dto.user.LoginResponseDto;
import com.kitty.blog.model.Report;
import com.kitty.blog.service.report.ReportService;
import com.kitty.blog.service.report.ReportWorkflowService;
import com.kitty.blog.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "举报管理", description = "报告相关接口")
@RestController
@RequestMapping("/api/post/report")
@CrossOrigin
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportWorkflowService reportWorkflowService;

    /**
     * 创建报告
     * @param report
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "创建报告", description = "创建报告")
    @PostMapping("/public/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> create
            (@RequestBody Report report) {
        ResponseEntity<Boolean> response = reportService.create(report);
        return Response.createResponse(response,
                HttpStatus.OK, "创建成功",
                HttpStatus.BAD_REQUEST, "请求参数错误");
    }

    /**
     * 更新报告
     * @param updatedReport
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "更新报告", description = "更新报告")
    @PutMapping("/public/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "报告不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> update(@RequestBody Report updatedReport) {
        ResponseEntity<Boolean> response = reportService.update(updatedReport);
        return Response.createResponse(response,
                HttpStatus.OK, "更新成功",
                HttpStatus.BAD_REQUEST, "请求参数错误");
    }

    /**
     * 根据用户ID查询报告列表
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "根据用户ID查询报告列表", description = "根据用户ID查询报告列表")
    @GetMapping("/public/find/user/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Response<List<Report>>> findByUserId
            (@AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<Report>> response = reportService.findByUserId(user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "用户不存在");
    }

    /**
     * 根据文章ID查询报告列表
     * @param postId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or @reportService.hasReportedPost(#postId, #user.id)")
    @Operation(summary = "根据文章ID查询报告列表", description = "根据文章ID查询报告列表")
    @GetMapping("/admin/find/article/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    public ResponseEntity<Response<List<Report>>> findByArticleId
            (@PathVariable(value = "postId") Integer postId,
             @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<Report>> response = reportService.findByArticleId(postId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "文章不存在");
    }

    /**
     * 根据原因查询报告列表
     * @param reason
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据原因查询报告列表", description = "根据原因查询报告列表")
    @GetMapping("/admin/find/reason/{reason}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Report>>> findByReason
            (@RequestParam(value = "reason") String reason) {
        ResponseEntity<List<Report>> response = reportService.findByReason(reason);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

//    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role)ROLE_MESSAGE_MANAGER)" +
//            " or hasRole(T(com.kitty.blog.constant.Role)ROLE_SYSTEM_ADMINISTRATOR)" +
//            " ")
//    @Operation(summary = "根据原因和文章ID查询报告列表", description = "根据原因和文章ID查询报告列表")
//    @GetMapping("/public/find/reason/post/{reason}/{postId}")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "查询成功"),
//            @ApiResponse(responseCode = "500", description = "服务器内部错误")
//    })
//    public ResponseEntity<Response<List<Report>>> findByReasonForPost
//            (@PathVariable(value = "reason") String reason,
//             @PathVariable(value = "postId") Integer postId) {
//        ResponseEntity<List<Report>> response = reportService.findByReasonForPost(reason, postId);
//        return Response.createResponse(response,
//                HttpStatus.OK, "查询成功",
//                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
//    }
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
        " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "修改报告状态")
    @PutMapping("/admin/change/status/{id}/{status}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "报告不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> changeStatus
            (@PathVariable(value = "id") Integer reportId,
             @PathVariable(value = "status") String status) {
        ResponseEntity<Boolean> response = reportService.changeStatus(reportId, status);
        return Response.createResponse(response,
                HttpStatus.OK, "修改成功",
                HttpStatus.BAD_REQUEST, "请求参数错误");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据状态查询报告列表")
    @GetMapping("/admin/find/status/{status}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Report>>> findByStatus
            (@PathVariable(value = "status") String status) {
        ResponseEntity<List<Report>> response = reportService.findByStatus(status);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }


    /**
     * 保存报告
     * @param report
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "保存报告", description = "保存报告")
    @PostMapping("/public/save")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Report>> save
            (@RequestBody Report report) {
        ResponseEntity<Report> response = reportService.save(report);
        return Response.createResponse(response,
                HttpStatus.OK, "保存成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据报告ID查询报告
     * @param reportId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据报告ID查询报告", description = "根据报告ID查询报告")
    @GetMapping("/admin/find/id/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "报告不存在")
    })
    public ResponseEntity<Response<Report>> findById
            (@PathVariable(value = "id") Integer reportId) {
        ResponseEntity<Report> response = reportService.findById(reportId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "报告不存在");
    }

    /**
     * 查询所有报告列表
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有报告列表", description = "查询所有报告列表")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Report>>> findAll() {
        ResponseEntity<List<Report>> response = reportService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据报告ID删除报告
     * @param reportId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or @reportService.hasReportedPost(#postId, #user.id)")
    @Operation(summary = "根据报告ID删除报告", description = "根据报告ID删除报告")
    @DeleteMapping("/delete/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "报告不存在")
    })
    public ResponseEntity<Response<Boolean>> deleteById
            (@PathVariable(value = "id") Integer reportId,
             @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<Boolean> response = reportService.deleteById(reportId);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.NOT_FOUND, "报告不存在");
    }

    /**
     * 统计报告数量
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "统计报告数量", description = "统计报告数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> count = reportService.count();
        return Response.createResponse(count,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 判断报告是否存在
     * @param reportId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "判断报告是否存在", description = "判断报告是否存在")
    @GetMapping("/admin/exist/id/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "存在"),
            @ApiResponse(responseCode = "404", description = "不存在")
    })
    public ResponseEntity<Response<Boolean>> existsById
            (@PathVariable(value = "id") Integer reportId) {
        ResponseEntity<Boolean> response = reportService.existsById(reportId);
        return Response.createResponse(response,
                HttpStatus.OK, "存在",
                HttpStatus.NOT_FOUND, "不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "审核举报")
    @PostMapping("/admin/review/{reportId}")
    public ResponseEntity<Response<Boolean>> reviewReport(
            @PathVariable Integer reportId,
            @RequestParam boolean approved,
            @RequestParam String comment) {
        try {
            reportWorkflowService.completeReviewTask(reportId, approved, comment);
            return Response.ok(true, "审核完成");
        } catch (Exception e) {
            log.error("审核失败", e);
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "审核失败: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "提交举报")
    @PostMapping("/public/submit")
    public ResponseEntity<Response<Boolean>> submitReport(@RequestBody Report report) {
        try {
            ResponseEntity<Report> savedReport = reportService.save(report);
            if (savedReport.getStatusCode() == HttpStatus.OK && savedReport.getBody() != null) {
                reportWorkflowService.startReportProcess(savedReport.getBody());
                return Response.ok(true, "举报提交成功");
            }
            return Response.error(HttpStatus.BAD_REQUEST, "举报提交失败");
        } catch (Exception e) {
            log.error("举报提交失败", e);
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "举报提交失败: " + e.getMessage());
        }
    }
}
