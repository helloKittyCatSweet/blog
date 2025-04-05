package com.kitty.blog.controller.user;

import com.kitty.blog.dto.message.MessageUserInfo;
import com.kitty.blog.dto.user.LoginResponseDto;
import com.kitty.blog.model.Message;
import com.kitty.blog.application.service.MessageService;
import com.kitty.blog.utils.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "消息管理", description = "消息管理相关接口")
@RestController
@RequestMapping("/api/user/message")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 创建消息
     *
     * @param message
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "创建消息", description = "创建消息")
    @PostMapping("/public/create")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Boolean>> create(
            @RequestBody Message message) {
        ResponseEntity<Boolean> response = messageService.create(message);
        return Response.createResponse(response,
                HttpStatus.OK, "创建成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 更新消息
     *
     * @param message
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "更新消息", description = "更新消息")
    @PutMapping("/public/update")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Boolean>> update(
            @RequestBody Message message) {
        ResponseEntity<Boolean> response = messageService.update(message);
        return Response.createResponse(response,
                HttpStatus.OK, "更新成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 读取一个用户的所有消息
     *
     * @param receiverId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "读取消息", description = "根据消息ID，读取消息")
    @PutMapping("/public/read/{receiverId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "读取成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Boolean>> readMessage(
            @PathVariable @Param("receiverId") Integer receiverId,
            @AuthenticationPrincipal LoginResponseDto user) {
        // 前一个是发送方，后一个是接收方
        ResponseEntity<Boolean> response = messageService.readMessage(receiverId, user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "标记读取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "标记未读", description = "根据消息ID，标记未读")
    @PutMapping("/public/unread/{receiverId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "标记未读成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Boolean>> unReadMessage(
            @PathVariable @Param("receiverId") Integer receiverId,
            @AuthenticationPrincipal LoginResponseDto user) {
        // 前一个是发送方，后一个是接收方
        ResponseEntity<Boolean> response = messageService.unReadMessage(receiverId, user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "标记未读成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 根据发送者ID，查询消息
     *
     * @param senderId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "查询消息", description = "根据发送者ID，查询消息")
    @GetMapping("/public/find/sender/{senderId}/list")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<List<Message>>> findBySenderId(
            @PathVariable @Param("senderId") Integer senderId) {
        ResponseEntity<List<Message>> response = messageService.findBySenderId(senderId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }


    /**
     * 根据接收者ID，查询消息
     *
     * @param receiverId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "查询消息", description = "根据接收者ID，查询消息")
    @GetMapping("/public/find/receiver/{receiverId}/list")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<List<Message>>> findByReceiverId(
            @PathVariable @Param("receiverId") Integer receiverId) {
        ResponseEntity<List<Message>> response = messageService.findByReceiverId(receiverId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 根据消息内容和发送者ID，查询消息
     *
     * @param content
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "根据消息内容和发送者ID，查询消息", description = "根据消息内容和发送者ID，查询消息")
    @GetMapping("/public/find/sender/{content}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<List<Message>>> findByContentForSender(
            @PathVariable @Param("content") String content,
            @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<Message>> response = messageService.
                findByContentForSender(content, user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 根据消息内容和接收者ID，查询消息
     *
     * @param content
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "查询消息", description = "根据消息内容和接收者ID，查询消息")
    @GetMapping("/public/find/receiver/{content}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<List<Message>>> findByContentForReceiver(
            @PathVariable @Param("content") String content,
            @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<Message>> response = messageService.
                findByContentForReceiver(content, user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 查询联系人
     *
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "查询联系人", description = "查询联系人")
    @GetMapping("/public/find/contacted")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<List<MessageUserInfo>>> findContactedUserNames(
            @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<MessageUserInfo>> response = messageService.findContactedUserNames(user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 查询对话
     * TODO: 前端在处理的时候，要注意senderId和receiverId的顺序，因为可能是A->B，也可能是B->A
     *
     * @param senderId
     * @param receiverId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "查询对话", description = "查询对话")
    @GetMapping("/public/find/conversation/{senderId}/{receiverId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<List<Message>>> findConversation(
            @PathVariable @Param("senderId") Integer senderId,
            @PathVariable @Param("receiverId") Integer receiverId) {
        ResponseEntity<List<Message>> response = messageService.findConversation(senderId, receiverId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 保存消息
     *
     * @param message
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "保存消息", description = "保存消息")
    @PostMapping("/public/save")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Message>> save(
            @RequestBody Message message) {
        ResponseEntity<Message> response = messageService.save(message);
        return Response.createResponse(response,
                HttpStatus.OK, "保存成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 根据消息ID，查询消息
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询消息", description = "根据消息ID，查询消息")
    @GetMapping("/admin/find/id/{id}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "消息不存在")})
    public ResponseEntity<Response<Message>> findById(
            @PathVariable @Param("id") Integer id) {
        ResponseEntity<Message> response = messageService.findById(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "消息不存在");
    }

    /**
     * 查询所有消息
     *
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有消息", description = "查询所有消息")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<List<Message>>> findAll() {
        ResponseEntity<List<Message>> response = messageService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 删除消息
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or @messageService.hasDeletePermission(#user.id, #id)")
    @Operation(summary = "删除消息", description = "根据消息ID，删除消息")
    @DeleteMapping("/admin/delete/id/{id}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "消息不存在")})
    public ResponseEntity<Response<Boolean>> deleteById(
            @PathVariable @Param("id") Integer id,
            @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<Boolean> response = messageService.deleteById(id);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.NOT_FOUND, "消息不存在");
    }

    /**
     * 查询消息数量
     *
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询消息数量", description = "查询消息数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> response = messageService.count();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 查询消息是否存在
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询消息是否存在", description = "根据消息ID，查询消息是否存在")
    @GetMapping("/admin/exists/{id}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "消息不存在")})
    public ResponseEntity<Response<Boolean>> existsById(
            @PathVariable @Param("id") Integer id) {
        ResponseEntity<Boolean> response = messageService.existsById(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "消息不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "分页查询消息列表", description = "支持按接收者名称、内容和时间范围搜索")
    @GetMapping("/public/page")
    public ResponseEntity<Response<Page<Message>>> findMessagePage(
            @RequestParam(required = false) String receiverName,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal LoginResponseDto user) {

        Pageable pageable = PageRequest.of(page, size);
        ResponseEntity<Page<Message>> response = messageService.findMessagePage(
                receiverName, content, startDate, endDate, user.getId(), pageable);

        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 批量删除消息
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "批量删除消息")
    @DeleteMapping("/public/batch")
    public ResponseEntity<Response<Void>> batchDelete(
            @RequestBody List<Integer> messageIds,
            @AuthenticationPrincipal LoginResponseDto user) {

        ResponseEntity<Void> response = messageService.batchDelete(messageIds, user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "删除失败");
    }
}
