package com.kitty.blog.domain.service.post.abstractContent;

import com.kitty.blog.infrastructure.config.system.XunfeiConfig;
import com.kitty.blog.infrastructure.utils.ChatWebSocketListener;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class XunfeiService {

    private final XunfeiConfig xunfeiConfig;
    private final OkHttpClient client;

    public XunfeiService(XunfeiConfig xunfeiConfig) {
        this.xunfeiConfig = xunfeiConfig;
        this.client = new OkHttpClient.Builder().build();
    }

    public String chat(String prompt) {
        try {
            log.info("开始调用讯飞API，prompt: {}", prompt);

            // 1. 构建鉴权url
            String authUrl = getAuthUrl(xunfeiConfig.getHostUrl(), xunfeiConfig.getApiKey(), xunfeiConfig.getApiSecret());
            log.info("生成的认证URL: {}", authUrl);

            // 2. 将http url转换为websocket url
            String wsUrl = authUrl.replace("http://", "ws://").replace("https://", "wss://");

            CountDownLatch latch = new CountDownLatch(1);
            ChatWebSocketListener listener = new ChatWebSocketListener(prompt, latch, xunfeiConfig);

            // 3. 创建websocket连接
            WebSocket webSocket = client.newWebSocket(
                    new Request.Builder().url(wsUrl).build(),
                    listener
            );

            // 4. 等待响应完成
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            if (!completed) {
                log.warn("等待响应超时");
                webSocket.close(1000, "超时关闭");
                return "请求超时，请稍后重试";
            }

            String response = ChatWebSocketListener.getLastResponse();
            log.info("获取到响应: {}", response);
            webSocket.close(1000, "正常关闭");
            return response;
        } catch (Exception e) {
            log.error("讯飞星辰调用失败", e);
            return "模型调用失败：" + e.getMessage();
        }
    }


    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        // 1. 处理URL
        String newUrl = hostUrl.replace("ws://", "http://").replace("wss://", "https://");
        URL url = new URL(newUrl);

        // 2. 生成RFC1123格式的时间戳
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());

        // 3. 拼接待加密字符串
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";

        // 4. HMAC-SHA256加密
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));

        // 5. 生成signature
        String signature = Base64.getEncoder().encodeToString(hexDigits);

        // 6. 组装authorization原始字符串
        String authorization_origin = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
                apiKey, "hmac-sha256", "host date request-line", signature);

        // 7. 将authorization进行Base64编码
        String authorization = Base64.getEncoder().encodeToString(authorization_origin.getBytes(StandardCharsets.UTF_8));

        // 8. 组装最终URL
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath()))
                .newBuilder()
                .addQueryParameter("authorization", authorization)
                .addQueryParameter("date", date)
                .addQueryParameter("host", url.getHost())
                .build();

        // 9. 转换为WebSocket URL
        return httpUrl.toString().replace("https://", "wss://");
    }
}