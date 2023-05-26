package com.jim.demo.mnio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileUploader {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, XmlParserException, ErrorResponseException {
        String bucketName = "my-bucketname";
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://localhost:9000")
                        .credentials("minioadmin", "minioadmin")
                        .build();
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (found) {

        }
        else {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).region("us-west-1").build());
        }
    }
}
