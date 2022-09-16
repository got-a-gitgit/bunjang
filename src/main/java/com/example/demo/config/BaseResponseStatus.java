package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS_WITH_DATA(true, 1000, "요청에 성공하였습니다."),
    SUCCESS_WITH_NO_DATA(true, 1001, "일치하는 데이터가 없습니다."),
    DELETE_SUCCESS(true, 1010, "삭제되었습니다."),
    UPDATE_SUCCESS(true, 1020, "수정되었습니다."),



    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "잘못된 요청입니다."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 회원의 접근입니다."),
    INVALID_REQUEST(false, 2009, "데이터 유효성 검증 실패"),


    // [POST] /users
    USERS_EMPTY_EMAIL(false, 2010, "이메일을 입력하세요."),





    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    INACTIVATE_USER(false, 3016, "비활성화된 회원입니다."),





    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "DB 오류"),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패했습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패했습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패했습니다."),



    /**
     * 5000 : 소셜 로그인 오류
     */
    FAIL_SOCIAL_LOGIN(false, 5000, "소셜 로그인에 실패했습니다."),
    KAKAO_GET_USERINFO_FAIL(false, 5101, "카카오 사용자 정보 조회에 실패했습니다.");


    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
