package com.example.demo.src.product;



import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
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


    public PostProductRes createProduct(PostProductReq postProductReq, List<String> productImages) throws BaseException {
        try{
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
}
