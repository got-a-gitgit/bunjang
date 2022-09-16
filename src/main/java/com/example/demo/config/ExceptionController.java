package com.example.demo.config;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponse.validError;


@ControllerAdvice
@ResponseBody
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public void handelException(Exception e){
        e.printStackTrace();
    }



    @ExceptionHandler(BaseException.class)
    public BaseResponse handleBaseException(BaseException e){
        return new BaseResponse<>(e.getStatus());
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<List<validError>> handleValidException(MethodArgumentNotValidException e){

        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        List<validError> validErrors = new ArrayList<>();

        for (FieldError error : errors){
            validErrors.add(new validError(error.getField(), error.getDefaultMessage()));
        }

        return new BaseResponse<>(validErrors);
    }

}
