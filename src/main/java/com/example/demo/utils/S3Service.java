package com.example.demo.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
// https://devlog-wjdrbs96.tistory.com/323
// https://nirsa.tistory.com/288
// https://jane514.tistory.com/10
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")    // application.yml
    private String bucket;

    private final AmazonS3 amazonS3;

    String profile = System.getProperty("spring.profiles.active");
    private final String directory = "app_" + profile + "/";


    /** 파일 이름 생성 **/
    public String createFileName(String filename){
        return directory + filename + "-" + System.currentTimeMillis();
    }

    /** 파일 업로드 (1개)**/
    public String uploadImage(MultipartFile multipartFile) {
        String fileName = createFileName(multipartFile.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try {
            // 파일 업로드 후 URL 저장
            amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), objectMetadata);
        } catch (IOException e){
            // 이미지 업로드 에러
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    /** 파일 업로드 (여러개)**/
    public List<String> uploadImage(List<MultipartFile> multipartFile) {
        List<String> imageUrl = new ArrayList<>();

        multipartFile.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            // 파일 크기
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try {
                // 파일 업로드 후 URL 저장
                amazonS3.putObject(bucket, fileName, file.getInputStream(), objectMetadata);
                imageUrl.add(amazonS3.getUrl(bucket, fileName).toString());
            } catch (IOException e){
                // 이미지 업로드 에러
            }
        });

        return imageUrl;
    }

    /** 파일 삭제 **/
    public void deleteImage(String filename){
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, filename));
    }

}
