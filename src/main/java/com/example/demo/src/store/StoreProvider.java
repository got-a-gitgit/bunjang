package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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

}
