package com.example.demo.src.product;


import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Transactional(rollbackFor = Exception.class)
@Service
public class ProductService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductDao productDao;
    private final ProductProvider productProvider;
    private final JwtService jwtService;


    @Autowired
    public ProductService(ProductDao productDao, ProductProvider productProvider, JwtService jwtService) {
        this.productDao = productDao;
        this.productProvider = productProvider;
        this.jwtService = jwtService;

    }


    /**
     * 상품 등록
     **/
    public PostProductRes createProduct(PostProductReq postProductReq, List<String> productImages) throws BaseException {
        try {
            //상품 등록
            int productId = productDao.createProduct(postProductReq);

            //태그 등록
            List<Integer> tagIds = productDao.createTags(postProductReq.getTags());

            //상품-태그 등록
            int updatedRowsNumber = productDao.createProductTags(productId, tagIds);

            //상품 이미지 등록
            int updatedImagesNum = productDao.createProductImages(productId, productImages);

            return new PostProductRes(productId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상품 삭제
     **/
    public void deleteProduct(int productId) throws BaseException {
        try {
            //상품 삭제
            int deletedRow = productDao.deleteProduct(productId);
            if (deletedRow == 0) {
                throw new BaseException(NON_EXISTENT_PRODUCT);
            }
        } catch (BaseException baseException) {
            throw baseException;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 조회수 증가
     **/
    public void increaseProductView(int productId) throws BaseException {
        try {
            //상품 조회수 1증가
            productDao.increaseProductView(productId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상품 판매 상태 변경
     **/
    public FetchProductStatusRes updateProductStatus(int productId, String status) throws BaseException {
        try {
            productDao.updateStatus(productId, status);
            return new FetchProductStatusRes(productId, status);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상품 수정
     **/
    public void updateProduct(int productId, PutProductReq putProductReq, List<String> productImages) throws BaseException {
        try {
            //상품 이미지 삭제
            if (putProductReq.getDeletedImageList().size()!=0) {
                productDao.deleteProductImage(putProductReq.getDeletedImageList());
            }
            //태그 등록
            List<Integer> tagIds=null;
            if (putProductReq.getTags().size()!=0) {
                 tagIds= productDao.createTags(putProductReq.getTags());
            }
            
            //기존 태그 삭제
            if (tagIds == null) {
                //태그가 없는 경우 -> 상품-태그 전체 삭제
                int deletedRowsNumber = productDao.deleteAllProductTags(productId);
            } else {
                //제거된 상품-태그 삭제
                int deletedRowsNumber = productDao.deleteProductTags(productId,tagIds);
            }

            if (tagIds != null) {
                //상품-태그 등록
                int updatedRowsNumber = productDao.createProductTags(productId, tagIds);
            }

            //상품 이미지 등록
            if (productImages != null) {
                int updatedImagesNum = productDao.createProductImages(productId, productImages);
            }
            //상품 수정
            productDao.updateProduct(productId, putProductReq);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
