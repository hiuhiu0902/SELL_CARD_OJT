package com.demo.sell_card_demo1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    @Value("${aws.s3.region}")
    private String region;
    @Autowired
    S3Client s3Client;

    public String getUrl(String s3Key){
        if(s3Key == null || s3Key.isEmpty()){
            return null;
        }
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
    }
    public String uploadFile(String folderName, String id, InputStream inputStream, long size, String extension) {
        String fileName = UUID.randomUUID().toString() + "." + extension;
        // Cấu trúc: folder/id/filename.jpg (VD: products/1/abc.jpg)
        String key = folderName + "/" + id + "/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/" + extension)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, size));
        return key;
    }
}
