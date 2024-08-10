package com.wizz.fi.util;


public enum ResultCode implements IErrorCode {
    SUCCESS(200, "success"),
    FAILED(500, "failed"),
    VALIDATE_FAILED(404, "validate failed"),
    UNAUTHORIZED(401, "unauthorized"),

    VERIFY_FAILED(401, "verify failed"),
    FORBIDDEN(403, "forbidden"),

    PARAMETER_ERROR(990000, "invalid parameter"),
    NETWORK_ERROR(990001, "network error, please try again later"),

    S3_UPLOAD_ERROR(9001, "File Upload Error, please retry"),

    JOB_NOT_FOUND(1001, "Job not founded"),

    WORKER_IS_DISABLE(4001, "Worker is disabled"),
    JOB_DIFFICULT_ERROR(4002, "job difficulty must between 4 to 9"),

    JOB_SCHEDULE_DATA_ERROR(4004, "invalid parameter"),

    NO_MORE_COUPON(4005, "Sorry, all coupons have been claimed."),

    STAKE_WRONG_AMOUNT(4006, "invalid stake amount"),

    STAKE_WRONG_RULE(4006, "invalid stake period"),
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
