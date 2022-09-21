package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.PostStoreProfileReq;
import com.example.demo.utils.S3Service;
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
    private final S3Service s3Service;


    @Autowired
    public StoreService(StoreDao storeDao, StoreProvider storeProvider, S3Service s3Service) {
        this.storeDao = storeDao;
        this.storeProvider = storeProvider;
        this.s3Service = s3Service;

    }


    /** 상점명 설정 **/
    public void registerStoreName(int userId, String name) throws BaseException {
        // 가입된 유저인지 확인
        int isUser = storeProvider.checkUserId(userId);
        if (isUser == 0) {
            throw new BaseException(INVALID_JWT);
        }

        // 상점명 중복 확인
        int duplicated = storeProvider.checkDuplicatesStoreName(userId, name);
        if (duplicated == 1){
            throw new BaseException(DUPLICATE_STORE_NAME);
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

    /** 상점 소개 편집 **/
    public void modifyStoreProfile(int userId, PostStoreProfileReq storeProfile) throws BaseException {
        // 가입된 유저인지 확인
        int isUser = storeProvider.checkUserId(userId);
        if (isUser == 0) {
            throw new BaseException(INVALID_JWT);
        }

        // 가입된 유저인 경우, 상점 소개 수정
        if (storeProfile.getProfileImageFile() != null){ // 프로필 이미지를 등록하는 경우
            // 프로필 이미지 저장 후, URL 저장
            String profileImageUrl = s3Service.uploadImage(storeProfile.getProfileImageFile());
            storeProfile.setProfileImageUrl(profileImageUrl);
        }

        try {
            // 상품 소개 수정
            int result = storeDao.updateStoreProfile(userId, storeProfile);
            if (result == 0) {  // 수정 실패
                throw new BaseException(UPDATE_FAIL);
            }
        } catch (BaseException e){

        }
    }
}
