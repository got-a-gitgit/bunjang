package com.example.demo.src.review.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class PostReviewReq {
    @Positive(message = "별평가를 입력해주세요.")
    private float rating;
    @Size(min = 20, message = "최소 20자 이상 입력해주세요.")
    private String contents;
    private List<MultipartFile> imageList;
}
