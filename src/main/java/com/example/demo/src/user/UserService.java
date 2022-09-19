package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.src.user.model.UserInfoRes;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.FAIL_EMAIL_LOGIN;
import static com.example.demo.config.BaseResponseStatus.FAIL_SOCIAL_LOGIN;

@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService, ObjectMapper objectMapper) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }


    /** 로그인 승인 처리 **/
    public PostLoginRes approvalUser(int userId, boolean created){
        String jwt = jwtService.createJwt(userId);

        return new PostLoginRes(jwt, created);
    }

    /** 이메일로 로그인 **/
    @Transactional(rollbackFor = Exception.class)
    public PostLoginRes loginByEmail(String email) throws BaseException{
        int userId;
        boolean isCreated;

        try{
        // 등록된 유저인지 확인
        UserInfoRes userInfo = userProvider.checkUserEmail(email);

        if (userInfo == null){ // 미등록 유저
            // 회원가입
            userId = userDao.insertUser(email);
            isCreated = true;
        } else {
            userId = userInfo.getUserId();
            isCreated = false;
        }

        // 로그인
        return approvalUser(userId, isCreated);

        } catch (Exception e){
            logger.error("Email Login Fail", e);
            throw new BaseException(FAIL_EMAIL_LOGIN);
        }
    }

    /** 카카오로 로그인 **/
    @Transactional(rollbackFor = Exception.class)
    public PostLoginRes loginByKakao(String accessToken) throws BaseException{
        int userId;
        boolean isCreated;

        try{
            // accessToken을 이용하여 유저 정보 추출
            String kakaoUserInfo = userProvider.getKakaoUserInfo(accessToken);
            JsonNode jsonNode = objectMapper.readTree(kakaoUserInfo);
            String email = jsonNode.get("kakao_account").get("email").asText();

            // 등록된 유저인지 확인
            UserInfoRes userInfo = userProvider.checkUserEmail(email);

            if (userInfo == null){ // 미등록 유저
                // 회원가입 및 sns 연동
                userId = userDao.insertUser(email);
                isCreated = true;
                userDao.updateSNSFlag(userId);
                userDao.insertSNSInfo(userId, 1);

            } else { // 등록 유저
                userId = userInfo.getUserId();  // 유저 식별번호
                isCreated = false;
                char snsFlag = userInfo.getSnsFlag().charAt(0); // sns 연동 여부

                // 이메일만 가입한 유저는 sns 연동 처리
                if (snsFlag == 'N'){
                    userDao.updateSNSFlag(userId);
                    userDao.insertSNSInfo(userId, 1);
                }
            }

            // 로그인
            return approvalUser(userId, isCreated);

        } catch (Exception e) {
            logger.error("Kakao Login Fail", e);
            throw new BaseException(FAIL_SOCIAL_LOGIN);
        }
    }



}
