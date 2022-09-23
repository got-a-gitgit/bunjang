package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.GetFollowRes;
import com.example.demo.src.store.model.GetStoreInfoRes;
import com.example.demo.src.store.model.PatchStoreProfileRes;
import com.example.demo.src.store.model.ProductInfo;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class StoreProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;
    private final JwtService jwtService;

    @Autowired
    public StoreProvider(StoreDao storeDao, JwtService jwtService) {
        this.storeDao = storeDao;
        this.jwtService = jwtService;
    }

    /** 등록된 유저인지 확인 **/
    public int checkUserId(int userId) throws BaseException{
        try {
            return storeDao.selectUserId(userId);
        } catch (Exception e){
            logger.error("CheckUserId Error", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 상점명 중복 확인 **/
    public int checkDuplicatesStoreName(int userId, String name) throws BaseException {
        try {
            return storeDao.selectStoreName(userId, name);
        } catch (Exception e){
            logger.error("CheckUserId Error", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 상점 프로필 조회 **/
    public PatchStoreProfileRes getStoreProfile(int userid) throws BaseException {
        try {
            return storeDao.selectStoreProfile(userid);
        } catch (Exception e){
            logger.error("GetStoreProfile Error", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 상점 정보 조회 **/
    public GetStoreInfoRes getStoreInfo(int storeId) throws BaseException {
        // 유효한 유저인지 확인
        int isUser = checkUserId(storeId);
        if (isUser == 0) {
            throw new BaseException(INVALID_ACCESS);
        }

        try {
            return storeDao.selectStoreInfo(storeId);
        } catch (Exception e){
            logger.error("GetStoreProfile Error", e);
            throw new BaseException(FAIL_GET_STOREINFO);
        }
    }

    /** 상점의 팔로워 목록 조회 **/
    public List<GetFollowRes> getFollowers(int storeId, int lastId) throws BaseException {
        // 유효한 유저인지 확인
        int isUser = checkUserId(storeId);
        if (isUser == 0) {
            throw new BaseException(INVALID_ACCESS);
        }
        try {
            return storeDao.selectFollowers(storeId, lastId);
        } catch (Exception e) {
            logger.error("GetStoreProfile Error", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 상점의 팔로잉 목록 조회 **/
    public List<GetFollowRes> getFollowings(int storeId, int lastId) throws BaseException {
        // 유효한 유저인지 확인
        int isUser = checkUserId(storeId);
        if (isUser == 0) {
            throw new BaseException(INVALID_ACCESS);
        }

        try {
            List<GetFollowRes> result = storeDao.selectFollowings(storeId, lastId);
            for (GetFollowRes store : result){
                List<ProductInfo> products = storeDao.selectProductsByStore(store.getUserId());
                store.setProductInfoList(products);
            }
            return result;
        } catch (Exception e) {
            logger.error("GetStoreProfile Error", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }





}
