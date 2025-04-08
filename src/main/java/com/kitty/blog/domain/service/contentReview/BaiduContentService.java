package com.kitty.blog.domain.service.contentReview;

import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 文本审核接口
 */
@Service

public class BaiduContentService {

    @Value("${baidu.content.apiKey}")
    private String API_KEY;

    @Value("${baidu.content.secretKey}")
    private String SECRET_KEY;

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    private String accessToken;

    private static final int MAX_TEXT_LENGTH = 6000; // 设置安全字符长度限制，文档里写的是约6666字

    private static final long MIN_INTERVAL = 1000; // 设置最小请求间隔，防止频繁请求，也是文档里的限制

    private long lastRequestTime = 0; // 上一次请求的时间戳

    @PostConstruct
    public void init() throws IOException {
        this.accessToken = getAccessToken();
        refreshAccessToken();
    }

    /**
     * 定时任务，每30天执行一次，刷新access_token
     */
    @Scheduled(cron = "0 0 0 1/29 * ?") //
    public void refreshAccessToken() {
        try {
            this.accessToken = getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String checkText(String text) {
        if (text == null || text.isEmpty()){
            return "文本为空";
        }

        try{
            // 分段处理长文本
            List<String> textSegments = splitText(text);

            for (String segment : textSegments){
                // 控制请求频率
                controlRequestRate();

                String result = sendRequest(segment);
                if (!"合规".equals(result)){
                    // 如果任何一段不合规，直接返回结果
                    return result;
                }
            }

            return "合规";
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private List<String> splitText(String text) {
        List<String> segments = new ArrayList<>();
        int length = text.length();
        int startIndex = 0;

        while (startIndex < length) {
            // 计算当前段的最大可能结束位置
            int endIndex = Math.min(startIndex + MAX_TEXT_LENGTH, length);

            // 如果不是文本末尾，寻找合适的断句点
            if (endIndex < length) {
                // 在最大长度范围内向前查找最近的句子结束符
                int tempEnd = endIndex;
                while (tempEnd > startIndex) {
                    char c = text.charAt(tempEnd - 1);
                    // 中文常用句子结束符：。！？；
                    if (c == '。' || c == '！' || c == '？' || c == '；' ||
                            c == '.' || c == '!' || c == '?' || c == ';' ||
                            c == '\n' || c == '\r') {
                        endIndex = tempEnd;
                        break;
                    }
                    tempEnd--;
                }
                // 如果没找到合适的断句点，但当前段已经接近最大长度
                if (tempEnd == startIndex) {
                    // 使用原始的endIndex，保证不会死循环
                    endIndex = Math.min(startIndex + MAX_TEXT_LENGTH, length);
                }
            }
            // 添加当前段落
            segments.add(text.substring(startIndex, endIndex));
            startIndex = endIndex;
        }
        return segments;
    }

    private void controlRequestRate() throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - lastRequestTime;
        if (interval < MIN_INTERVAL) {
            Thread.sleep(MIN_INTERVAL - interval);
        }
        lastRequestTime = System.currentTimeMillis();
    }

    private String sendRequest(String text) throws IOException {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined";
        try {
            String param = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，
            // 线上环境access_token有过期时间，客户端可自行缓存，过期后重新获取。
            String accessToken = this.accessToken;

            // 构建请求
            Request request = new Request.Builder()
                    .url(url + "?access_token=" + accessToken)
                    .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), param))
                    .build();

            // 发送请求
            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string(); //
                    System.out.println(responseBody);
                    return new JSONObject(responseBody).getString("conclusion");
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    private String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?client_id=" + API_KEY +
                        "&client_secret=" + SECRET_KEY + "&grant_type=client_credentials")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string(); // 读取响应内容并存储在变量中
                return new JSONObject(responseBody).getString("access_token");
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

//    public static void main(String[] args) {
//        BaiduContentService contentService = new BaiduContentService();
//        contentService.API_KEY = "tZvDGGlR83jubJsuksqnYObj";
//        contentService.SECRET_KEY = "7qZr8Y1lTzm6wEUteYU8u6W4KXKZkctL";
//        String text = "爱你";
//        contentService.refreshAccessToken();
//        System.out.println(contentService.checkText(text));
//    }
}