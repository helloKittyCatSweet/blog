package com.kitty.blog.application.dto.message;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class SystemMessageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String message;
    private String targetRole;
    private Integer senderId;
    private String senderName;
    private List<String> senderRoles;
    private LocalDate createdAt;
    private boolean isRead;
}
