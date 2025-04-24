package com.kitty.blog.application.controller.user;

import com.kitty.blog.application.dto.user.ContactMessageDTO;
import com.kitty.blog.domain.service.contact.ContactService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin
@Tag(name = "Contact", description = "Contact API")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/send")
    public DeferredResult<ResponseEntity<Response<String>>> sendMessage(
            @Valid @RequestBody ContactMessageDTO message) {
        DeferredResult<ResponseEntity<Response<String>>> deferredResult = new DeferredResult<>(65000L); // 65 seconds
                                                                                                        // timeout

        contactService.sendContactMessage(message)
                .thenApply(result -> ResponseEntity.ok(new Response<>(200, "success", result)))
                .orTimeout(60, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    String errorMessage = throwable.getMessage() != null
                            ? throwable.getMessage()
                            : "发送消息失败，请稍后重试";
                    return ResponseEntity.ok(new Response<>(500, errorMessage, null));
                })
                .thenAccept(deferredResult::setResult)
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.ok(new Response<>(500, "服务器处理超时", null)));
                    return null;
                });

        return deferredResult;
    }
}