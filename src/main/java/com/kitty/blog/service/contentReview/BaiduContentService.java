package com.kitty.blog.service.contentReview;

import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined";
        try {
            String param = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间，客户端可自行缓存，过期后重新获取。
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