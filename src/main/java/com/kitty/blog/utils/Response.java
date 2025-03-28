package com.kitty.blog.utils;

import com.kitty.blog.model.Favorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class Response<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int status;
    private String message;
    private T data;

    public Response(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters and setters

    public static class Builder<T> {
        private int status;
        private String message;
        private T data;

        public Builder<T> status(int status) {
            this.status = status;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Response<T> build() {
            return new Response<>(status, message, data);
        }
    }

    public static <T> ResponseEntity<Response<T>> createResponse(
            ResponseEntity<T> serviceResponse,
            HttpStatus successStatus, String successMessage,
            HttpStatus errorStatus, String errorMessage) {
        if (serviceResponse.getStatusCode().is2xxSuccessful()) {
            // 请求成功
            Response<T> response = new Response<>(successStatus.value(),
                    successMessage, serviceResponse.getBody());
            return new ResponseEntity<>(response, successStatus); // 使用正确的 HTTP 状态码
        } else {
            // 请求失败
            Response<T> response = new Response<>(errorStatus.value(), errorMessage, null);
            return new ResponseEntity<>(response, errorStatus); // 返回非 2xx 的 HTTP 状态码
        }
    }

    // 成功响应的构造函数
    public static <T> ResponseEntity<Response<T>> ok(T data) {
        return new ResponseEntity<>(new Response<>(200, "success", data), HttpStatus.OK);
    }

    // 成功响应的构造函数
    public static <T> ResponseEntity<Response<T>> ok(T data, String message) {
        return new ResponseEntity<>(new Response<>(200, message, data), HttpStatus.OK);
    }

    // 错误响应的构造函数
    public static <T> ResponseEntity<Response<T>> error(String message) {
        return new ResponseEntity<>(new Response<>(500, message, null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> ResponseEntity<Response<T>> error(HttpStatus status, String message) {
        return new ResponseEntity<>(new Response<>(status.value(), message, null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
