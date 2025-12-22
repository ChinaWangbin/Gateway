package com.example.model1.entity;

/**
 * 响应码枚举
 */
public enum ResponseCode {
    // 成功
    SUCCESS(200, "成功"),
    
    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    
    // 服务端错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    
    // 业务相关错误
    BUSINESS_ERROR(1000, "业务异常"),
    PARAMETER_ERROR(1001, "参数校验失败"),
    DATA_NOT_FOUND(1002, "数据不存在");
    
    private final Integer code;
    private final String message;
    
    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return "ResponseCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}