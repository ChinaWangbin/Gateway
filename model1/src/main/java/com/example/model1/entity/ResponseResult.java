package com.example.model1.entity;

import java.io.Serializable;
import com.example.model1.entity.ResponseCode;

/**
 * 通用响应结果封装类
 */
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    public ResponseResult() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public ResponseResult(boolean success, Integer code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    // 成功响应的静态方法
    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(true, ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), null);
    }
    
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(true, ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }
    
    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<>(true, ResponseCode.SUCCESS.getCode(), message, data);
    }
    
    // 失败响应的静态方法
    public static <T> ResponseResult<T> error() {
        return new ResponseResult<>(false, ResponseCode.INTERNAL_SERVER_ERROR.getCode(), ResponseCode.INTERNAL_SERVER_ERROR.getMessage(), null);
    }
    
    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(false, ResponseCode.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }
    
    public static <T> ResponseResult<T> error(ResponseCode responseCode) {
        return new ResponseResult<>(false, responseCode.getCode(), responseCode.getMessage(), null);
    }
    
    public static <T> ResponseResult<T> error(Integer code, String message) {
        return new ResponseResult<>(false, code, message, null);
    }
    
    // Getter 和 Setter 方法
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ResponseResult{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}