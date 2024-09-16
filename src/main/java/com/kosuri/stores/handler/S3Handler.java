package com.kosuri.stores.handler;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;


@Service
public class S3Handler {

    private final S3Client s3Client;

    public S3Handler(S3Client s3Client) {
        this.s3Client = s3Client;
    }


}
