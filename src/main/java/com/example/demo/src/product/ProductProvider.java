package com.example.demo.src.product;


import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /** 상점 판매 상품 목록 조회 **/
    public GetStoreProductListRes getStoreProductListByStoreId(int userId, int storeId, int lastProductId, Integer size) throws BaseException {
        try{
            GetStoreProductListRes getStoreProductListRes = new GetStoreProductListRes();
            List<GetStoreProductRes> productList;

            //무한 스크롤 여부 구분
            if (size == -1) {
                productList = productDao.getWholeProductListByStoreId(userId, storeId);
                getStoreProductListRes.setProductList(productList);
                getStoreProductListRes.setHasNextPage(false);
            } else {
                //첫 조회와 무한스크롤 구분
                if (lastProductId == -1) {
                    productList = productDao.getFirstProductListByStoreId(userId, storeId, size);
                } else {
                    productList = productDao.getProductListByStoreId(userId, storeId, lastProductId,size);
                }

                //다음 페이지 존재 여부 입력
                if (productList.size() == 21) {
                    getStoreProductListRes.setHasNextPage(true);
                    getStoreProductListRes.setProductList(productList.subList(0, 20));
                } else {
                    getStoreProductListRes.setHasNextPage(false);
                    getStoreProductListRes.setProductList(productList);
                }

                //마지막 아이디 입력
                productList = getStoreProductListRes.getProductList();
                int newLastProductId = productList.get(productList.size() - 1).getProductId();
                getStoreProductListRes.setLastProductId(newLastProductId);
            }
            return getStoreProductListRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 홈화면 추천 상품 목록 조회 **/
    public GetRecommendedProductListRes getProductList(int userId, Integer lastProductId) throws BaseException {
        try{
            GetRecommendedProductListRes getRecommendedProductListRes = new GetRecommendedProductListRes();
            List<RecommendedProduct> productList;

            //첫 조회와 무한스크롤 구분
            if (lastProductId == -1) {
                productList = productDao.getFirstProductList(userId);
            } else {
                productList = productDao.getProductList(userId,lastProductId);
            }

            //다음 페이지 존재 여부 입력
            if (productList.size() == 21) {
                getRecommendedProductListRes.setHasNextPage(true);
                getRecommendedProductListRes.setProductList(productList.subList(0, 20));
            } else {
                getRecommendedProductListRes.setHasNextPage(false);
                getRecommendedProductListRes.setProductList(productList);
            }

            //마지막 아이디 입력
            productList = getRecommendedProductListRes.getProductList();
            int newLastProductId = productList.get(productList.size() - 1).getProductId();
            getRecommendedProductListRes.setLastProductId(newLastProductId);

            return getRecommendedProductListRes;
        } catch(Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
