package com.example.demo.src.product;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.aspectj.weaver.ast.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class ProductProvider {

    private final ProductDao productDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProductProvider(ProductDao productDao, JwtService jwtService) {
        this.productDao = productDao;
        this.jwtService = jwtService;
    }


    /** 상품 상세 조회 **/
    public GetProductRes getProduct(int productId) throws BaseException {
        try{
            //상품 정보 조회
            GetProductRes getProductRes =  productDao.getProduct(productId);
            //상품 이미지 조회
            List<String> images= productDao.getProductImages(productId);
            getProductRes.setImages(images);
            //상품 태그 조회
            List<String> tags= productDao.getProductTags(productId);
            getProductRes.setTags(tags);

            return getProductRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductListRes getProductListByStoreId(int userId, int storeId, int lastProductId) throws BaseException {
        try{
            GetProductListRes getProductListRes = new GetProductListRes();
            List<ProductListElement> productList;

            //첫 조회와 무한스크롤 구분
            if (lastProductId == -1) {
                productList = productDao.getFirstProductListByStoreId(userId, storeId);
            } else {
                productList = productDao.getProductListByStoreId(userId, storeId,lastProductId);
            }

            //다음 페이지 존재 여부 입력
            if (productList.size() == 21) {
                getProductListRes.setHasNextPage(true);
                getProductListRes.setProductList(productList.subList(0, 20));
            } else {
                getProductListRes.setHasNextPage(false);
                getProductListRes.setProductList(productList);
            }

            //마지막 아이디 입력
            productList = getProductListRes.getProductList();
            int newLastProductId = productList.get(productList.size() - 1).getProductId();
            getProductListRes.setLastProductId(newLastProductId);

            return getProductListRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
