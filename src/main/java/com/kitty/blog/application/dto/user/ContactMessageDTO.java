package com.kitty.blog.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class ContactMessageDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "主题不能为空")
    private String subject;

    @NotBlank(message = "消息内容不能为空")
    private String message;
}