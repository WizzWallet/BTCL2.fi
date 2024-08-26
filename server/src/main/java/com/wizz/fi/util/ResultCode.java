package com.wizz.fi.util;


public enum ResultCode implements IErrorCode {
    SUCCESS(200, "success"),
    FAILED(500, "failed"),
    VALIDATE_FAILED(404, "validate failed"),
    UNAUTHORIZED(401, "unauthorized"),

    VERIFY_FAILED(401, "verify failed"),
    FORBIDDEN(403, "forbidden"),

    PARAMETER_ERROR(990000, "invalid parameter"),

    ORDER_NOT_FOUND(10001, "order not found"),
    ORDER_STATUS_INCORRECT(10002, "order status incorrect"),
    STAKE_TOKEN_AMOUNT_NOT_ENOUGH(10003, "stake token amount not enough"),
    ORDINAL_NOT_FOUND(10004, "ordinal not found"),
    ORDINAL_STATUS_INCORRECT(10006, "ordinal status incorrect"),
    ORDER_ORDINAL_NOT_FOUND(10005, "order ordinal not found"),
    ORDER_ORDINAL_STATUS_INCORRECT(10006, "order ordinal status incorrect"),
    ;


    private final long code;
    private final String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
