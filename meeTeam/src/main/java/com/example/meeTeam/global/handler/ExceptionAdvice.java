package com.example.meeTeam.global.handler;


import com.example.meeTeam.global.exception.BaseException;
import com.example.meeTeam.global.exception.BaseResponse;
import com.example.meeTeam.global.exception.codes.ErrorCode;
import com.example.meeTeam.global.exception.codes.reason.Reason;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice {

    /**
     * 바인딩 에러 처리
     * @param e
     * @return
     */
    @ExceptionHandler
    public ResponseEntity<Object> validation(MethodArgumentNotValidException e) {
        log.error(e.getBindingResult().getFieldErrors().get(0).toString());
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        BaseResponse<?> baseResponse = BaseResponse.onFailure(ErrorCode.BINDING_ERROR.getCode(), message, null);
        return handleExceptionInternal(baseResponse);
    }

    /**
     * 서버 에러
     * @param e
     * @return
     */
    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e) {
        log.error(e.getMessage());
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        BaseResponse<?> baseResponse = BaseResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), null);
        return handleExceptionInternal(baseResponse);
    }

    /**
     * 클라이언트 에러
     * @param generalException
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> onThrowException(BaseException generalException) {
        Reason.ReasonDto errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
        BaseResponse<?> baseResponse = BaseResponse.onFailure(errorReasonHttpStatus.getCode(), errorReasonHttpStatus.getMessage(), null);
        return handleExceptionInternal(baseResponse);
    }

    private ResponseEntity<Object> handleExceptionInternal(BaseResponse<?> response) {
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCommonStatus) {
        BaseResponse<?> baseResponse = BaseResponse.onFailure(errorCommonStatus.getCode(), errorCommonStatus.getMessage(), null);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }
}