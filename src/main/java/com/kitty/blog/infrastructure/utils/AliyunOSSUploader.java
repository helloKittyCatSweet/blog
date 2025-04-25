package com.kitty.blog.infrastructure.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AliyunOSSUploader {
    private static final Logger logger = LoggerFactory.getLogger(AliyunOSSUploader.class);

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    // 调整超时时间配置
    private static final int CONNECTION_TIMEOUT = 20000; // 20秒
    private static final int SOCKET_TIMEOUT = 20000; // 20秒
    private static final int MAX_CONNECTIONS = 1024;
    private static final int MAX_ERROR_RETRY = 5;
    private static final int REQUEST_TIMEOUT = 30000; // 30秒

    // 连续失败计数
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private volatile boolean usingInternalEndpoint = false;

    private String getEffectiveEndpoint() {
        // 如果已经在使用内网endpoint，直接返回
        if (usingInternalEndpoint) {
            return endpoint;
        }

        // 如果连续失败次数超过阈值，尝试切换到内网endpoint
        if (failureCount.get() >= 3) {
            String internalEndpoint = endpoint.replace(".aliyuncs.com", "-internal.aliyuncs.com");
            if (isInAliyunNetwork() && !endpoint.contains("-internal.")) {
                logger.info("Switching to internal endpoint: {}", internalEndpoint);
                usingInternalEndpoint = true;
                return internalEndpoint;
            }
        }
        return endpoint;
    }

    private boolean isInAliyunNetwork() {
        try {
            // 尝试解析内网DNS
            InetAddress.getAllByName("oss-cn-hangzhou-internal.aliyuncs.com");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private OSS createOSSClient() {
        // 创建ClientConfiguration实例
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();

        // 基础连接配置
        conf.setConnectionTimeout(CONNECTION_TIMEOUT);
        conf.setSocketTimeout(SOCKET_TIMEOUT);
        conf.setMaxConnections(MAX_CONNECTIONS);
        conf.setMaxErrorRetry(MAX_ERROR_RETRY);
        conf.setRequestTimeout(REQUEST_TIMEOUT);

        // 开启TCP连接复用
        conf.setSupportCname(true);

        // 使用当前有效的endpoint创建客户端
        String currentEndpoint = getEffectiveEndpoint();
        logger.debug("Creating OSS client with endpoint: {}", currentEndpoint);
        return new OSSClientBuilder().build(currentEndpoint, accessKeyId, accessKeySecret, conf);
    }

    /**
     * 上传文件到阿里云 OSS，添加重试逻辑
     */
    public String uploadFile(File file, String objectName, Integer someId) {
        if (objectName == null || objectName.trim().isEmpty()) {
            objectName = "posts/" + someId + "/" + System.currentTimeMillis() + "-" + file.getName();
        }

        OSS ossClient = null;
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < MAX_ERROR_RETRY) {
            try {
                ossClient = createOSSClient();

                // 创建文件上传请求
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);

                // 上传文件到 OSS
                ossClient.putObject(putObjectRequest);

                // 上传成功，重置失败计数
                failureCount.set(0);
                return generateFileUrl(objectName);

            } catch (Exception e) {
                lastException = e;
                retryCount++;

                // 增加失败计数
                failureCount.incrementAndGet();

                logger.warn("文件上传失败，正在进行第 {} 次重试: {}", retryCount, e.getMessage());

                try {
                    // 指数退避策略，但最大等待时间不超过5秒
                    long sleepTime = Math.min(1000 * (long) Math.pow(2, retryCount - 1), 5000);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("上传被中断", ie);
                }
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        }

        throw new RuntimeException("文件上传失败，已重试 " + retryCount + " 次: " +
                (lastException != null ? lastException.getMessage() : "未知错误"), lastException);
    }

    /**
     * 上传图片到 OSS ( 使用时间戳生成路径 )
     */
    public String uploadImage(File image, Integer someId, String idType) {
        String objectName = "images/";
        switch (idType) {
            case "userId":
                objectName += "user/" + someId + "/";
                deleteFilesInDirectory(objectName);
                break;
            case "postId":
                objectName += "post/" + someId + "/";
                break;
            case "postCover":
                objectName += "post/" + someId + "/cover/";
                deleteFilesInDirectory(objectName);
                break;
            default:
                break;
        }
        objectName += System.currentTimeMillis() + "-" + image.getName();
        return uploadFile(image, objectName, someId);
    }

    /**
     * 上传压缩包到 OSS
     */
    public String uploadZip(File zipFile, Integer someId) {
        String objectName = "archives/" + someId + "/" + System.currentTimeMillis() + "-" + zipFile.getName();
        return uploadFile(zipFile, objectName, someId);
    }

    /**
     * 上传任意文件，同时根据类型分类存储
     *
     * @param file 本地文件
     * @return 文件在 OSS 中的 URL
     */
    public String uploadByFileType(File file, Integer someId, String idType) {
        String objectName;

        if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")
                || file.getName().endsWith(".jpeg")) {
            return uploadImage(file, someId, idType);
        } else if (file.getName().endsWith(".zip") || file.getName().endsWith(".rar")) {
            return uploadZip(file, someId);
        } else {
            return uploadFile(file, "", someId);
        }
    }

    /**
     * 根据 ObjectName 生成访问 URL
     */
    private String generateFileUrl(String objectName) {
        return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }

    /**
     * 删除指定目录下的所有文件
     *
     * @param prefix 文件前缀（目录路径）
     */
    public void deleteFilesInDirectory(String prefix) {
        OSS ossClient = null;
        try {
            ossClient = createOSSClient();

            // 列出目录下的所有文件
            ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request(bucketName)
                    .withPrefix(prefix)
                    .withMaxKeys(1000);
            ListObjectsV2Result result = ossClient.listObjectsV2(listObjectsRequest);
            List<OSSObjectSummary> objectSummaries = result.getObjectSummaries();

            // 删除每个文件
            for (OSSObjectSummary objectSummary : objectSummaries) {
                ossClient.deleteObject(bucketName, objectSummary.getKey());
            }
        } catch (Exception e) {
            throw new RuntimeException("删除文件失败: " + e.getMessage(), e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 删除指定文件
     *
     * @param objectName 完整文件路径（例如：posts/123/456-filename.txt）
     */
    public void deleteFile(String objectName) {
        OSS ossClient = null;
        try {
            ossClient = createOSSClient();
            // 检查文件是否存在
            if (!ossClient.doesObjectExist(bucketName, objectName)) {
                throw new RuntimeException("文件不存在");
            }
            ossClient.deleteObject(bucketName, objectName);
        } catch (Exception e) {
            throw new RuntimeException("删除文件失败: " + e.getMessage(), e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 根据完整URL解析出OSS objectName
     *
     * @param fileUrl 例如：https://bucket-name.oss-cn-beijing.aliyuncs.com/posts/123/456-filename.txt
     * @return objectName 例如：posts/123/456-filename.txt
     */
    public String parseObjectName(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            String path = uri.getPath();
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("无效的OSS文件URL");
        }
    }

    /**
     * 上传用户签名
     */
    public String uploadSignature(File file, Integer userId) {
        String objectName = "signature/" + userId + "/signature.png";
        OSS ossClient = null;
        try {
            ossClient = createOSSClient();

            // 如果已存在则先删除
            if (ossClient.doesObjectExist(bucketName, objectName)) {
                ossClient.deleteObject(bucketName, objectName);
            }

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);
            ossClient.putObject(putObjectRequest);

            return generateFileUrl(objectName);
        } catch (Exception e) {
            throw new RuntimeException("签名上传失败: " + e.getMessage(), e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 获取签名URL
     */
    public String getSignatureUrl(Integer userId) {
        String objectName = "signature/" + userId + "/signature.png";
        if (doesSignatureExist(userId)) {
            return generateFileUrl(objectName);
        }
        return null;
    }

    /**
     * 检查用户签名是否存在
     */
    public boolean doesSignatureExist(Integer userId) {
        String objectName = "signature/" + userId + "/signature.png";
        OSS ossClient = null;
        try {
            ossClient = createOSSClient();
            return ossClient.doesObjectExist(bucketName, objectName);
        } catch (Exception e) {
            throw new RuntimeException("检查用户签名失败: " + e.getMessage(), e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
