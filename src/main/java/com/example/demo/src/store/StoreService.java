package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.PatchStoreProfileReq;
import com.example.demo.src.store.model.PatchStoreProfileRes;
import com.example.demo.utils.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public String registerStoreName(int userId, String name) throws BaseException {
        // 상점명 중복 확인
        int duplicated = storeProvider.checkDuplicatesStoreName(userId, name);
        if (duplicated == 1){
            throw new BaseException(DUPLICATE_STORE_NAME);
        }

        try {
            int result = storeDao.insertStoreName(userId, name);
            if (result == 0) {  // 등록 실패
                throw new BaseException(INSERT_FAIL);
            }
            return name;

        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        } catch (Exception e){
            logger.error("RegisterStoreName Error", e);
            throw new BaseException(INSERT_FAIL);
        }
    }

    /** 상점 소개 편집 **/
    public PatchStoreProfileRes modifyStoreProfile(int userId, PatchStoreProfileReq storeProfile) throws BaseException {
        // 프로필 이미지 정보
        String originImageUrl = storeProfile.getOriginImageUrl();    // 기존의 이미지
        MultipartFile newImageFile = storeProfile.getNewImageFile(); // 업로드한 이미지
        String profileImageUrl;

        // 프로필 이미지 수정
        if (newImageFile != null) { // 새로운 프로필 이미지를 등록하는 경우
            profileImageUrl = s3Service.updateImage(originImageUrl, newImageFile);
        } else {    // 기존의 이미지를 삭제하는 경우
            if (originImageUrl != null){
                s3Service.deleteImage(originImageUrl);
            }
            profileImageUrl = null;
        }

        // DB에 저장할 이미지 Url 설정
        storeProfile.setOriginImageUrl(profileImageUrl);

        try {
            // 상점 소개 수정
            int result = storeDao.updateStoreProfile(userId, storeProfile);
            if (result == 0) {  // 수정 실패
                throw new BaseException(UPDATE_FAIL);
            }
            return storeProvider.getStoreProfile(userId);

        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        } catch (Exception e){
            logger.error("ModifyStoreProfile Error", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 상점 팔로우/언팔로우 **/
    public void modifyFollowing(int userId, int followId) throws BaseException {
        // 유효한 (팔로우)유저인지 확인
        int isUser = storeProvider.checkUserId(followId);
        if (isUser == 0) {
            throw new BaseException(INVALID_ACCESS);
        }

        // 팔로우 관계 처리
        try {
           int result = storeDao.insertFollowing(userId, followId);
           if (result == 0){    // 팔로우(언팔로우) 실패
               throw new BaseException(FAIL_FOLLOW_STORE);
           }
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        } catch (Exception e){
            logger.error("Following Error", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
