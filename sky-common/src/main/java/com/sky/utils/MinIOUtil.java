package com.sky.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

@Data
@AllArgsConstructor
@Slf4j
public class MinIOUtil {

    private String url;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    /**
     * 文件上传
     *
     * @param bytes      文件字节数组
     * @param objectName Minio中的对象名
     * @return 文件访问路径
     */
    public String upload(byte[] bytes, String objectName) {
        try {
            // 创建 MinioClient
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(url)
                    .credentials(accessKey, secretKey)
                    .build();

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                            .contentType("application/octet-stream") // 可根据文件类型修改
                            .build()
            );

            // Minio 访问路径规则 http://url/bucketName/objectName
            String fileUrl = String.format("%s/%s/%s", url, bucketName, objectName);

            log.info("文件上传到: {}", fileUrl);

            return fileUrl;

        } catch (MinioException e) {
            log.error("上传到 MinIO 出错: {}", e.getMessage(), e);
            throw new RuntimeException("Minio 文件上传失败", e);
        } catch (Exception e) {
            log.error("上传到 MinIO 出现异常: {}", e.getMessage(), e);
            throw new RuntimeException("Minio 文件上传失败", e);
        }
    }
}
