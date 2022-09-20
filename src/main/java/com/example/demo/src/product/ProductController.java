package com.example.demo.src.product;

import com.example.demo.utils.S3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ProductProvider productProvider;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final S3Service s3Service;



    /**
     * 상품 등록 API
     * [POST] /products
     * @return BaseResponse<PostProductRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostProductRes> createProduct(@RequestParam("images") List<MultipartFile> multipartFiles, @RequestParam("jsonBody") String jsonBody) throws JsonProcessingException {
        //jwt 인증

        //validation

        //S3에 이미지 업로드 및 url 반환
        List<String> iamgeUrls = s3Service.uploadImage(multipartFiles);

        //JSON 문자열 객체로 mapping
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new SimpleModule());
        PostProductReq postProductReq = objectMapper.readValue(jsonBody, PostProductReq.class);

        //상품 등록
        try{
            PostProductRes postProductRes = productService.createProduct(postProductReq, iamgeUrls);
            return new BaseResponse<>(postProductRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
