package com.example.demo.src.product;

import com.example.demo.utils.S3Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
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
    public BaseResponse<PostProductRes> createProduct(@ModelAttribute @Valid PostProductReq postProductReq) throws BaseException, BindException {

        //jwt 인증
        int userId= jwtService.getUserId();
        postProductReq.setUserId(userId);

        List<MultipartFile> images= postProductReq.getImages();

        //empty image validation
        if(images.get(0).isEmpty()) throw new BaseException(EMPTY_IMAGE_ERROR);

        //S3에 이미지 업로드 및 url 반환
        List<String> imageUrls = s3Service.uploadImage(images);

        //상품 등록
        try{
            PostProductRes postProductRes = productService.createProduct(postProductReq, imageUrls);
            return new BaseResponse<>(postProductRes, INSERT_SUCCESS);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 상품 삭제 API
     * [DELETE] /products/:product-id
     * @return BaseResponse
     */
    @ResponseBody
    @DeleteMapping("/{product-id}")
    public BaseResponse deleteProduct(@PathVariable("product-id") int productId) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        //상품 삭제
        try{
            productService.deleteProduct(productId);
            return new BaseResponse<>(DELETE_SUCCESS);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 상제페이지 API
     * [GET] /products/:product-id
     * @return BaseResponse<GetProductRes>
     */
    @ResponseBody
    @GetMapping("/{product-id}")
    public BaseResponse<GetProductRes> getProduct(@PathVariable("product-id") int productId) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();


        try{
            //상품 조회수 증가
            productService.increaseProductView(productId);
            //상품 조회
            GetProductRes getProductRes = productProvider.getProduct(productId);
            return new BaseResponse<>(getProductRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상점 판매상품 조회 API
     * [GET] /products/stores?store-id=스토어id&last-product-id=마지막상품id
     * @return BaseResponse<GetProductListRes>
     */
    @ResponseBody
    @GetMapping("/stores")
    public BaseResponse<GetStoreProductListRes> getProductListByStoreId(@RequestParam(value = "store-id", required = true) int storeId,
                                                                        @RequestParam(value="size",required = false, defaultValue = "-1") Integer size,
                                                                        @RequestParam(value = "last-product-id", required = false, defaultValue = "-1") Integer lastProductId,
                                                                        @RequestParam(value="last-updated-at", required = false)String lastUpdatedAt) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        try{
            //상점 판매상품 조회
            GetStoreProductListRes getStoreProductListRes = productProvider.getStoreProductListByStoreId(userId, storeId, lastUpdatedAt, lastProductId,size);
            return new BaseResponse<>(getStoreProductListRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 홈 화면 추천 상품 조회 API
     * [GET] /products
     * @return BaseResponse<>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetRecommendedProductListRes> getProductList(@RequestParam(value = "last-product-id", required = false, defaultValue = "-1") Integer lastProductId) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        try{
            //추천 상품 조회
            GetRecommendedProductListRes getRecommendedProductListRes = productProvider.getProductList(userId, lastProductId);
            return new BaseResponse<>(getRecommendedProductListRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
