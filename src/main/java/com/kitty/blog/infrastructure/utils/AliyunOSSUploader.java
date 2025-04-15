package com.kitty.blog.infrastructure.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class AliyunOSSUploader {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    /**
     * 上传文件到阿里云 OSS
     *
     * @param file       本地文件对象
     * @param objectName OSS 中存储的文件路径和名称
     * @return 上传成功的 URL 地址
     */
    public String uploadFile(File file, String objectName, Integer someId) {
        if (objectName == null || objectName.trim().isEmpty()) {
            objectName = "posts/" + someId + "/" + System.currentTimeMillis() + "-" + file.getName();
        }

        OSS ossClient = null;
        try {
            // 创建 OSS 客户端实例
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 创建文件上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);

            // 上传文件到 OSS
            ossClient.putObject(putObjectRequest);
            return generateFileUrl(objectName);
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
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
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

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
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
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
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

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
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
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
