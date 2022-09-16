package com.example.demo.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String message;
    private final int code;
    private T result;
    private List<validError> error;


    // GET 요청에 성공한 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS_WITH_DATA.isSuccess();
        this.message = SUCCESS_WITH_DATA.getMessage();
        this.code = SUCCESS_WITH_DATA.getCode();
        this.result = result;
    }


    // 요청에 실패한 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }


    // Validation 실패한 경우
    public BaseResponse(List<validError> error){
        this.isSuccess = INVALID_REQUEST.isSuccess();
        this.message = INVALID_REQUEST.getMessage();
        this.code = INVALID_REQUEST.getCode();
        this.error = error;
    }


    // Validation 실패 에러 내역
    @Getter
    @AllArgsConstructor
    public static class validError{
        private String field;
        private String errMessage;
    }
}

