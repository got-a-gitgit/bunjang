package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.*;
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

    /** 상점의 거래내역(판매) 조회 **/
    public List<TradeInfo> getSales(int userId, String type) throws BaseException {
        // 전체 조회 시
        if (type.equals("All")){
            type = "t.status";
        }

        try {
            return storeDao.selectSales(userId, type);
        } catch (Exception e){
            logger.error("GetWishes", e);
            throw new BaseException(FAIL_GET_SALES);
        }
    }

    /** 상점의 거래내역(구매) 조회 **/
    public List<TradeInfo> getPurchases(int userId, String type) throws BaseException {
        // 전체 조회 시
        if (type.equals("All")){
            type = "t.status";
        }

        try {
            return storeDao.selectPurchases(userId, type);
        } catch (Exception e){
            logger.error("GetWishes", e);
            throw new BaseException(FAIL_GET_PURCHASE);
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

    // 페이징 처리
//    /** 상점의 거래내역(판매) 조회 **/
//    public GetSalesRes getSales(int userId, int tradeId, String date, int size) throws BaseException {
//        try {
//            List<TradeInfo> tradeInfo = storeDao.selectSales(userId, tradeId, date, size);
//            int lastTradeId = 0;
//            String lastTradeDate = "";
//
//            // 다음 페이지 여부
//            boolean hasNextPage = true;
//            if (tradeInfo.size() != size + 1) {
//                hasNextPage = false;
//            }
//            if (hasNextPage){
//                tradeInfo.remove(size);  // 마지막 데이터 삭제
//                lastTradeId = tradeInfo.get(size-1).getTradeId();       // 마지막 데이터 거래 상품 Id
//                lastTradeDate = tradeInfo.get(size-1).getTradeDate();  // 마지막 데이터 거래 날짜
//            }
//
//            return new GetSalesRes(tradeInfo, hasNextPage, lastTradeId, lastTradeDate);
//        } catch (Exception e){
//            logger.error("GetWishes", e);
//            throw new BaseException(FAIL_GET_SALES);
//        }
//    }
}
