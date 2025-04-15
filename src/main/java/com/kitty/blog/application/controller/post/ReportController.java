package com.kitty.blog.application.controller.post;

import com.kitty.blog.application.dto.report.ReportDto;
import com.kitty.blog.common.constant.ReportReason;
import com.kitty.blog.common.constant.ReportStatus;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.model.Report;
import com.kitty.blog.domain.service.report.ReportService;
import com.kitty.blog.domain.service.report.ReportWorkflowService;
import com.kitty.blog.infrastructure.utils.Response;
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

    /**
     * 创建报告
     * @param report
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据用户ID查询报告列表", description = "根据用户ID查询报告列表")
    @GetMapping("/public/find/user/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Response<List<ReportDto>>> findByUserId
            (@AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<ReportDto>> response = reportService.findByUserId(user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "用户不存在");
    }

    /**
     * 根据文章ID查询报告列表
     * @param postId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
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

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据状态查询报告列表")
    @GetMapping("/admin/find/status/{status}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Report>>> findByStatus
            (@PathVariable(value = "status") ReportStatus status) {
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有报告列表", description = "查询所有报告列表")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<ReportDto>>> findAll() {
        ResponseEntity<List<ReportDto>> response = reportService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据报告ID删除报告
     * @param reportId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据报告ID删除报告", description = "根据报告ID删除报告")
    @DeleteMapping("/public/delete/id/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "报告不存在")
    })
    public ResponseEntity<Response<Boolean>> deleteById(@PathVariable(value = "id") Integer reportId) {
        ResponseEntity<Boolean> response = reportService.deleteById(reportId);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.NOT_FOUND, "报告不存在");
    }

    /**
     * 统计报告数量
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
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

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "审核举报")
    @PostMapping("/admin/review/{reportId}")
    public ResponseEntity<Response<Boolean>> reviewReport(
            @PathVariable Integer reportId,
            @RequestParam boolean approved,
            @RequestParam String comment) {
        try {
            // 改用 reportService 的方法
            ResponseEntity<Boolean> result = approved ?
                    reportService.approve(reportId, comment) :
                    reportService.reject(reportId, comment);

            return Response.createResponse(result,
                    HttpStatus.OK, "审核完成",
                    HttpStatus.INTERNAL_SERVER_ERROR, "审核失败");
        } catch (Exception e) {
            log.error("审核失败", e);
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "审核失败: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "提交举报")
    @PostMapping("/public/submit")
    public ResponseEntity<Response<Boolean>> submitReport(@RequestBody Report report) {
        try {
            // 直接使用 reportService.create
            ResponseEntity<Boolean> result = reportService.create(report);
            return Response.createResponse(result,
                    HttpStatus.OK, "举报提交成功",
                    HttpStatus.BAD_REQUEST, "举报提交失败");
        } catch (Exception e) {
            log.error("举报提交失败", e);
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "举报提交失败: " + e.getMessage());
        }
    }


    /**
     * 搜索举报信息
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "搜索举报信息", description = "根据关键字、状态和原因搜索举报信息")
    @GetMapping("/public/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<ReportDto>>> searchReports(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportReason reason,
            @RequestParam(required = true) boolean isAdmin,
            @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<ReportDto>> response = reportService.searchReports(
                user.getId(),
                keyword != null ? keyword : "",
                status,
                reason,
                isAdmin
        );
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }
}
