package com.czy.api.domain.dto.base;


import exception.ExceptionEnums;
import exception.AppException;
import json.BaseBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;


import java.io.Serializable;

/**
 * 基本响应类型
 * TODO 带重构，因为javax.servlet.http.HttpServletResponse和org.springframework.http.ResponseEntity不应该出现在webflux中
 * @param <T>
 */
@Slf4j
@Data
public class BaseResponse<T> implements BaseBean, Serializable {

    protected String code = "200";
    protected String message;
    protected T data; // 泛型字段，用于存放具体的数据

    /**
     * 成功响应 code = 200 [200]
     * @param data      数据
     */
    public BaseResponse(T data) {
        this.code = "200";
        this.message = null;
        this.data = data;
    }

    /**
     * 成功响应 code = 200 [200]
     * @param message   错误信息 [成功信息/null]
     * @param data      数据
     */
    public BaseResponse(String message, T data) {
        this.code = "200";
        this.message = message;
        this.data = data;
    }

    /**
     * 指定响应 code, 错误信息, 数据
     * @param code      状态码
     * @param message   错误信息
     * @param data      数据
     */
    public BaseResponse(String code, String message, T data) {
        this.code = code == null ? "200" : code;
        this.message = message;
        this.data = data;
    }

    /**
     * 错误响应
     * @param message   错误信息
     */
    public BaseResponse(String message) {
        this.code = "200";
        this.message = message;
        this.data = null;
    }

    /**
     * ResponseEntity<T>成功响应 ; ResponseEntity<T>会直接在Controller层提交响应
     * @param data 数据
     * @return  ResponseEntity
     * @param <T>       响应类型
     */
    public static <T> ResponseEntity<BaseResponse<T>> getResponseEntitySuccessRE(T data) {
        BaseResponse<T> baseResponse = new BaseResponse<>(data);
        return ResponseEntity.ok(baseResponse);
    }

    public static <T> BaseResponse<T> getResponseEntitySuccess(T data) {
        return new BaseResponse<>(data);
    }

    /**
     * ResponseEntity<T>失败响应 ; ResponseEntity<T>会直接在Controller层提交响应
     * @return  ResponseEntity
     * @param <T>       响应类型
     */
    public static <T> ResponseEntity<BaseResponse<T>> getResponseEntityFail() {
        return ResponseEntity.notFound().build();
    }

    /**
     * 错误响应 + 错误日志
     * @param warningMessage    错误日志（默认响应给前端的错误信息）
     * @param log               日志
     * @return                  BaseResponse
     * @param <T>               响应类型
     */
    public static <T> BaseResponse<T> LogBackError(String warningMessage, org.slf4j.Logger log) {
        log.warn(warningMessage);
        return new BaseResponse<>("400", warningMessage, null);
    }

    public static <T> BaseResponse<T> LogBackError(String warningMessage) {
        return new BaseResponse<>("400", warningMessage, null);
    }

    public static <T> BaseResponse<T> LogBackError(String errorCode, String errorMessage) {
        return new BaseResponse<>(errorCode, errorMessage, null);
    }

    public static <T> BaseResponse<T> LogBackError(ExceptionEnums exceptionEnums) {
        return new BaseResponse<>(exceptionEnums.getCode(), exceptionEnums.getMessage(), null);
    }

    public static <T> BaseResponse<T> LogBackError(AppException appException) {
        return new BaseResponse<>(appException.getErrCode(), appException.getMessage(), null);
    }
}
