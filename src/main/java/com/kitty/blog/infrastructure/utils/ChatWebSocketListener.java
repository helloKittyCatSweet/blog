package com.kitty.blog.infrastructure.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kitty.blog.infrastructure.config.system.XunfeiConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class ChatWebSocketListener extends WebSocketListener {
    @Getter
    private static String lastResponse = "";
    private final String prompt;
    private final StringBuilder responseBuilder = new StringBuilder();
    private final CountDownLatch latch;
    private final XunfeiConfig xunfeiConfig;

    public ChatWebSocketListener(String prompt, CountDownLatch latch, XunfeiConfig xunfeiConfig) {
        this.prompt = prompt;
        this.latch = latch;
        this.xunfeiConfig = xunfeiConfig;
    }

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {
        log.info("WebSocket opened");
        try {
            JSONObject requestJson = new JSONObject();

            // header参数
            JSONObject header = new JSONObject();
            header.put("app_id", xunfeiConfig.getAppId());
            header.put("uid", "1234");
            // 添加patch_id参数
            if (xunfeiConfig.getPatchId() != null && !xunfeiConfig.getPatchId().isEmpty()) {
                JSONArray patchIdArray = new JSONArray();
                patchIdArray.add(xunfeiConfig.getPatchId());
                header.put("patch_id", patchIdArray);
            }

            // parameter参数
            JSONObject parameter = new JSONObject();
            JSONObject chat = new JSONObject();
            // 使用配置的domain
            chat.put("domain", xunfeiConfig.getDomain());
            chat.put("temperature", 0.5);
            chat.put("max_tokens", 2048);
            parameter.put("chat", chat);

            // payload参数
            JSONObject payload = new JSONObject();
            JSONObject message = new JSONObject();
            JSONArray text = new JSONArray();

            // 添加当前问题
            JSONObject roleContent = new JSONObject();
            roleContent.put("role", "user");
            roleContent.put("content", prompt);
            text.add(roleContent);

            message.put("text", text);
            payload.put("message", message);

            requestJson.put("header", header);
            requestJson.put("parameter", parameter);
            requestJson.put("payload", payload);

            log.info("Sending message: {}", requestJson);
            webSocket.send(requestJson.toString());
        } catch (Exception e) {
            log.error("发送消息失败", e);
            lastResponse = "发送消息失败：" + e.getMessage();
            latch.countDown();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        log.info("收到响应: {}", text);
        try {
            JSONObject response = JSON.parseObject(text);
            JSONObject header = response.getJSONObject("header");
            if (header != null) {
                Integer code = header.getInteger("code");
                String message = header.getString("message");
                Integer status = header.getInteger("status");

                if (code != null && code != 0) {
                    log.error("API返回错误: code={}, message={}", code, message);
                    lastResponse = "API调用失败：" + message;
                    latch.countDown();
                    return;
                }

                JSONObject payload = response.getJSONObject("payload");
                if (payload != null) {
                    JSONObject choices = payload.getJSONObject("choices");
                    if (choices != null) {
                        JSONArray texts = choices.getJSONArray("text");
                        if (texts != null && !texts.isEmpty()) {
                            JSONObject textObj = texts.getJSONObject(0);
                            String content = textObj.getString("content");
                            if (content != null) {
                                // 累积响应内容
                                responseBuilder.append(content);
                            }
                        }
                    }
                }

                // 当状态为2时，表示所有响应已完成，设置最终结果
                if (status != null && status == 2) {
                    lastResponse = responseBuilder.toString();
                    latch.countDown();
                }
            }
        } catch (Exception e) {
            log.error("处理响应失败", e);
            lastResponse = "处理响应失败：" + e.getMessage();
            latch.countDown();
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
        log.error("WebSocket连接失败", t);
        lastResponse = "连接失败：" + t.getMessage();
        latch.countDown();
    }
}