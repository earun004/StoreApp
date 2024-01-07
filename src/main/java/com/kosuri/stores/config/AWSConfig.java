package com.kosuri.stores.config;

import com.kosuri.stores.dao.AdminSecurityEntity;
import com.kosuri.stores.dao.AdminSecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Optional;

@Configuration
public class AWSConfig {

    @Autowired
    private AdminSecurityRepository adminSecurityRepository;

    private String awsAccessKey;

    private String awsSecretKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        Optional<AdminSecurityEntity> adminSecurityOptional = adminSecurityRepository.findById(1);
        if (adminSecurityOptional.isPresent()){
            AdminSecurityEntity adminSecurity = adminSecurityOptional.get();
             awsAccessKey = adminSecurity.getAwsAccessKey();
             awsSecretKey = adminSecurity.getAwsSecretKey();
        }

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(region))
                .build();
    }


}
