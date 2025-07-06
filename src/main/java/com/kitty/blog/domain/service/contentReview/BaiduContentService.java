//package com.kitty.blog.domain.service.contentReview;
//
//import jakarta.annotation.PostConstruct;
//import okhttp3.*;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.net.ssl.*;
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
///**
// * 文本审核接口
// */
//@Service
//public class BaiduContentService {
//    private static final Logger logger = LoggerFactory.getLogger(BaiduContentService.class);
//
//    @Value("${baidu.content.apiKey}")
//    private String API_KEY;
//
//    @Value("${baidu.content.secretKey}")
//    private String SECRET_KEY;
//
//    private static final OkHttpClient HTTP_CLIENT = createOkHttpClient();
//
//    private String accessToken;
//
//    private static final int MAX_TEXT_LENGTH = 6000; // 设置安全字符长度限制，文档里写的是约6666字
//
//    private static final long MIN_INTERVAL = 1000; // 设置最小请求间隔，防止频繁请求，也是文档里的限制
//
//    private long lastRequestTime = 0; // 上一次请求的时间戳
//
//    private static final int MAX_RETRIES = 3;
//    private static final int RETRY_DELAY_MS = 1000;
//
//    private static OkHttpClient createOkHttpClient() {
//        try {
//            // 创建一个信任所有证书的 TrustManager
//            final TrustManager[] trustAllCerts = new TrustManager[] {
//                    new X509TrustManager() {
//                        @Override
//                        public void checkClientTrusted(X509Certificate[] chain, String authType)
//                                throws CertificateException {
//                        }
//
//                        @Override
//                        public void checkServerTrusted(X509Certificate[] chain, String authType)
//                                throws CertificateException {
//                        }
//
//                        @Override
//                        public X509Certificate[] getAcceptedIssuers() {
//                            return new X509Certificate[] {};
//                        }
//                    }
//            };
//
//            // 创建 SSLContext 并使用上面的 TrustManager
//            final SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//
//            // 创建 SSLSocketFactory
//            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            // 配置 OkHttpClient
//            return new OkHttpClient.Builder()
//                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
//                    .hostnameVerifier((hostname, session) -> true)
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .retryOnConnectionFailure(true)
//                    .build();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create OkHttpClient", e);
//        }
//    }
//
//    @PostConstruct
//    public void init() throws IOException {
//        this.accessToken = getAccessToken();
//        refreshAccessToken();
//    }
//
//    /**
//     * 定时任务，每30天执行一次，刷新access_token
//     */
//    @Scheduled(cron = "0 0 0 1/29 * ?") //
//    public void refreshAccessToken() {
//        try {
//            this.accessToken = getAccessToken();
//        } catch (IOException e) {
//            logger.error("Failed to refresh access token", e);
//        }
//    }
//
//    public String checkText(String text) {
//        if (text == null || text.isEmpty()) {
//            return "文本为空";
//        }
//
//        try {
//            // 分段处理长文本
//            List<String> textSegments = splitText(text);
//
//            for (String segment : textSegments) {
//                // 控制请求频率
//                controlRequestRate();
//
//                String result = sendRequestWithRetry(segment);
//                if (!"合规".equals(result)) {
//                    // 如果任何一段不合规，直接返回结果
//                    return result;
//                }
//            }
//
//            return "合规";
//        } catch (Exception e) {
//            logger.error("Content check failed", e);
//            return null;
//        }
//    }
//
//    private List<String> splitText(String text) {
//        List<String> segments = new ArrayList<>();
//        int length = text.length();
//        int startIndex = 0;
//
//        while (startIndex < length) {
//            // 计算当前段的最大可能结束位置
//            int endIndex = Math.min(startIndex + MAX_TEXT_LENGTH, length);
//
//            // 如果不是文本末尾，寻找合适的断句点
//            if (endIndex < length) {
//                // 在最大长度范围内向前查找最近的句子结束符
//                int tempEnd = endIndex;
//                while (tempEnd > startIndex) {
//                    char c = text.charAt(tempEnd - 1);
//                    // 中文常用句子结束符：。！？；
//                    if (c == '。' || c == '！' || c == '？' || c == '；' ||
//                            c == '.' || c == '!' || c == '?' || c == ';' ||
//                            c == '\n' || c == '\r') {
//                        endIndex = tempEnd;
//                        break;
//                    }
//                    tempEnd--;
//                }
//                // 如果没找到合适的断句点，但当前段已经接近最大长度
//                if (tempEnd == startIndex) {
//                    // 使用原始的endIndex，保证不会死循环
//                    endIndex = Math.min(startIndex + MAX_TEXT_LENGTH, length);
//                }
//            }
//            // 添加当前段落
//            segments.add(text.substring(startIndex, endIndex));
//            startIndex = endIndex;
//        }
//        return segments;
//    }
//
//    private void controlRequestRate() throws InterruptedException {
//        long currentTime = System.currentTimeMillis();
//        long interval = currentTime - lastRequestTime;
//        if (interval < MIN_INTERVAL) {
//            Thread.sleep(MIN_INTERVAL - interval);
//        }
//        lastRequestTime = System.currentTimeMillis();
//    }
//
//    private String sendRequestWithRetry(String text) throws IOException {
//        int retries = 0;
//        while (retries < MAX_RETRIES) {
//            try {
//                return sendRequest(text);
//            } catch (IOException e) {
//                retries++;
//                if (retries == MAX_RETRIES) {
//                    throw e;
//                }
//                logger.warn("Request failed, attempt {} of {}", retries, MAX_RETRIES, e);
//                try {
//                    Thread.sleep(RETRY_DELAY_MS * retries);
//                } catch (InterruptedException ie) {
//                    Thread.currentThread().interrupt();
//                    throw new IOException("Request interrupted", ie);
//                }
//            }
//        }
//        throw new IOException("Failed after " + MAX_RETRIES + " attempts");
//    }
//
//    private String sendRequest(String text) throws IOException {
//        // 请求url
//        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined";
//        try {
//            String param = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
//            String accessToken = this.accessToken;
//
//            Request request = new Request.Builder()
//                    .url(url + "?access_token=" + accessToken)
//                    .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), param))
//                    .build();
//
//            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String responseBody = response.body().string();
//                    logger.debug("Baidu API response: {}", responseBody);
//
//                    JSONObject jsonResponse = new JSONObject(responseBody);
//                    String conclusion = jsonResponse.getString("conclusion");
//
//                    // 如果是不合规，检查具体原因
//                    if ("不合规".equals(conclusion)) {
//                        JSONArray data = jsonResponse.getJSONArray("data");
//                        for (int i = 0; i < data.length(); i++) {
//                            JSONObject item = data.getJSONObject(i);
//                            JSONArray hits = item.getJSONArray("hits");
//                            for (int j = 0; j < hits.length(); j++) {
//                                JSONObject hit = hits.getJSONObject(j);
//                                JSONArray details = hit.getJSONArray("details");
//                                // 如果是"联系方式-网址"导致的不合规，返回"合规"
//                                for (int k = 0; k < details.length(); k++) {
//                                    if ("联系方式-网址".equals(details.getString(k))) {
//                                        return "合规";
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    return conclusion;
//                } else {
//                    throw new IOException("Unexpected code " + response);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 从用户的AK，SK生成鉴权签名（Access Token）
//     *
//     * @return 鉴权签名（Access Token）
//     * @throws IOException IO异常
//     */
//    private String getAccessToken() throws IOException {
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "");
//        Request request = new Request.Builder()
//                .url("https://aip.baidubce.com/oauth/2.0/token?client_id=" + API_KEY +
//                        "&client_secret=" + SECRET_KEY + "&grant_type=client_credentials")
//                .post(body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Accept", "application/json")
//                .build();
//
//        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string(); // 读取响应内容并存储在变量中
//                return new JSONObject(responseBody).getString("access_token");
//            } else {
//                throw new IOException("Unexpected code " + response);
//            }
//        }
//    }
//
//    // public static void main(String[] args) {
//    // BaiduContentService contentService = new BaiduContentService();
//    // contentService.API_KEY = "tZvDGGlR83jubJsuksqnYObj";
//    // contentService.SECRET_KEY = "7qZr8Y1lTzm6wEUteYU8u6W4KXKZkctL";
//    // String text = "爱你";
//    // contentService.refreshAccessToken();
//    // System.out.println(contentService.checkText(text));
//    // }
//}