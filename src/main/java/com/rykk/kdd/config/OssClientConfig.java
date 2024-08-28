package com.rykk.kdd.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.rykk.kdd.constant.FileConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云对象存储客户端
 *
 */
@Configuration
@ConfigurationProperties(prefix = "oss.client")
@Data
public class OssClientConfig {

    /**
     * accessKey
     */
    private String accessKeyID;

    /**
     * secretKey
     */
    private String accessKeySecret;

    /**
     * 区域
     */
    private String region;

    @Bean
    public OSS cosClient() {
        // 根据用户身份信息(accessKeyID, accessKeySecret)创建访问凭证
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyID, accessKeySecret);
        // 返回oss客户端
        // 创建OSSClient实例。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(FileConstant.OSS_ENDPOINT)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();
        return ossClient;
    }
}