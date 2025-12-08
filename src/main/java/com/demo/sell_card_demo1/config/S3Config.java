package com.demo.sell_card_demo1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                // Dòng này rất quan trọng: Nó cho phép code tự tìm quyền
                // 1. Nếu chạy Local: Tìm trong file ~/.aws/credentials
                // 2. Nếu chạy EC2: Tìm IAM Role đã gắn vào EC2 (bảo mật tuyệt đối)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}