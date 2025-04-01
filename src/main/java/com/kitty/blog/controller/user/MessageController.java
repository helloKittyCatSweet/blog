package com.kitty.blog.controller.user;

import com.kitty.blog.dto.message.MessageStatusUpdate;
import com.kitty.blog.dto.user.LoginResponseDto;
import com.kitty.blog.dto.message.MessageInfo;
import com.kitty.blog.model.message.Message;
import com.kitty.blog.service.MessageService;
import com.kitty.blog.utils.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
     * @param message
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "创建消息", description = "创建消息")
    @PostMapping("/public/create")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "创建成功"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<Boolean>> create(
            @RequestBody Message message) {
        ResponseEntity<Boolean> response = messageService.create(message);
        return Response.createResponse(response,
                HttpStatus.OK, "创建成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 更新消息
     * @param message
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "更新消息", description = "更新消息")
    @PutMapping("/public/update")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "更新成功"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<Boolean>> update(
            @RequestBody Message message) {
        ResponseEntity<Boolean> response = messageService.update(message);
        return Response.createResponse(response,
                HttpStatus.OK, "更新成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }



    /**
     * 读取消息
     * @param isRead
     * @param messageId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "读取消息", description = "根据消息ID，读取消息")
    @PutMapping("/public/read/{messageId}/{isRead}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "读取成功"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<Boolean>> readMessage(
            @PathVariable @Param("isRead") boolean isRead,
            @PathVariable @Param("messageId") Integer messageId) {
        ResponseEntity<Boolean> response = messageService.readMessage(isRead, messageId);
        return Response.createResponse(response,
                HttpStatus.OK, isRead ? "标记读取成功" : "标记未读成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 根据发送者ID，查询消息
     * @param senderId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "查询消息", description = "根据发送者ID，查询消息")
    @GetMapping("/public/find/sender/{senderId}/list")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<List<Message>>> findBySenderId(
            @PathVariable @Param("senderId") Integer senderId) {
        ResponseEntity<List<Message>> response = messageService.findBySenderId(senderId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }


    /**
     * 根据接收者ID，查询消息
     * @param receiverId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "查询消息", description = "根据接收者ID，查询消息")
    @GetMapping("/public/find/receiver/{receiverId}/list")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<List<Message>>> findByReceiverId(
            @PathVariable @Param("receiverId") Integer receiverId) {
        ResponseEntity<List<Message>> response = messageService.findByReceiverId(receiverId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 根据消息内容和发送者ID，查询消息
     * @param content
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "根据消息内容和发送者ID，查询消息", description = "根据消息内容和发送者ID，查询消息")
    @GetMapping("/public/find/sender/{content}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
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
     * @param content
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "查询消息", description = "根据消息内容和接收者ID，查询消息")
    @GetMapping("/public/find/receiver/{content}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
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
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "查询联系人", description = "查询联系人")
    @GetMapping("/public/find/contacted")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<List<MessageInfo>>> findContactedUserNames(
            @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<MessageInfo>> response = messageService.findContactedUserNames(user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 查询对话
     * TODO: 前端在处理的时候，要注意senderId和receiverId的顺序，因为可能是A->B，也可能是B->A
     * @param senderId
     * @param receiverId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "查询对话", description = "查询对话")
    @GetMapping("/public/find/conversation/{senderId}/{receiverId}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
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
     * @param message
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "保存消息", description = "保存消息")
    @PostMapping("/public/save")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<Message>> save(
            @RequestBody Message message) {
        ResponseEntity<Message> response = messageService.save(message);
        return Response.createResponse(response,
                HttpStatus.OK, "保存成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 根据消息ID，查询消息
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_MESSAGE_MANAGER) " +
            "or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询消息", description = "根据消息ID，查询消息")
    @GetMapping("/admin/find/id/{id}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "消息不存在") })
    public ResponseEntity<Response<Message>> findById(
            @PathVariable @Param("id") Integer id) {
        ResponseEntity<Message> response = messageService.findById(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "消息不存在");
    }

    /**
     * 查询所有消息
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_MESSAGE_MANAGER) " +
            "or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有消息", description = "查询所有消息")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<List<Message>>> findAll() {
        ResponseEntity<List<Message>> response = messageService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 删除消息
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or @messageService.hasDeletePermission(#user.id, #id)")
    @Operation(summary = "删除消息", description = "根据消息ID，删除消息")
    @DeleteMapping("/admin/delete/id/{id}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "消息不存在") })
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
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询消息数量", description = "查询消息数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> response = messageService.count();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 查询消息是否存在
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_MESSAGE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询消息是否存在", description = "根据消息ID，查询消息是否存在")
    @GetMapping("/admin/exists/{id}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "消息不存在") })
    public ResponseEntity<Response<Boolean>> existsById(
            @PathVariable @Param("id") Integer id) {
        ResponseEntity<Boolean> response = messageService.existsById(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "消息不存在");
    }
}
