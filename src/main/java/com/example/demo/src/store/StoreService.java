package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class StoreService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;
    private final StoreProvider storeProvider;

    @Autowired
    public StoreService(StoreDao storeDao, StoreProvider storeProvider) {
        this.storeDao = storeDao;
        this.storeProvider = storeProvider;
    }


    /** 상점명 설정 **/
    public void registerStoreName(int userId, String name) throws BaseException {
        // 가입된 유저인지 확인
        int isUser = storeProvider.checkUserId(userId);
        if (isUser == 0) {
            throw new BaseException(INVALID_JWT);
        }

        // 가입된 유저라면 상점 등록
        try {
            int result = storeDao.insertStoreName(userId, name);
            if (result == 0) {  // 등록 실패
                throw new BaseException(INSERT_FAIL);
            }
        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        } catch (Exception e){
            throw new BaseException(INSERT_FAIL);
        }
    }
}
