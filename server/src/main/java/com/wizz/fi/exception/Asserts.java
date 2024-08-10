package com.wizz.fi.exception;


import com.wizz.fi.util.IErrorCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 断言处理类，用于抛出各种API异常
 */
public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }

    public static void notNull(Object object, IErrorCode errorType) {
        if (object == null) {
            throw new ApiException(errorType);
        }
    }

    public static void notBlank(String str, IErrorCode errorType) {
        if (StringUtils.isBlank(str)) {
            throw new ApiException(errorType);
        }
    }

    public static void isTrue(boolean condition, IErrorCode errorType) {
        if (!condition) {
            throw new ApiException(errorType);
        }
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new ApiException(message);
        }
    }
}
