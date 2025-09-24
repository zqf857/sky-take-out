package com.sky.config;

import com.sky.properties.MinIOProperties;
import com.sky.utils.MinIOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类,用于创建 MinIOUtil 对象
 */
@Configuration
@Slf4j
public class MinIOConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MinIOUtil minIOUtil(MinIOProperties minIOProperties){
        log.info("开始创建Minio文件上传工具类对象: {}", minIOProperties);
        return new MinIOUtil(minIOProperties.getUrl(),
                minIOProperties.getAccessKey(),
                minIOProperties.getSecretKey(),
                minIOProperties.getBucketName());
    }
}
