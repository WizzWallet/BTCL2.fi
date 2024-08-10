package com.wizz.fi.util;

import com.wizz.fi.exception.ApiException;
import org.apache.commons.lang3.StringUtils;


public class MyAssert {
    public static void notNull(Object object, IErrorCode errorType) {
        if (object == null) {
            throw new ApiException(errorType);
        }
    }

    public static void notNull(Object object, IErrorCode errorType, String message) {
        if (object == null) {
            throw new ApiException(errorType, message);
        }
    }

    public static void notBlank(String str, IErrorCode errorType) {
        if (StringUtils.isBlank(str)) {
            throw new ApiException(errorType);
        }
    }

    public static void notBlank(String str, IErrorCode errorType, String message) {
        if (StringUtils.isBlank(str)) {
            throw new ApiException(errorType, message);
        }
    }

    public static void isTrue(boolean condition, IErrorCode errorType) {
        if (!condition) {
            throw new ApiException(errorType);
        }
    }

    public static void isTrue(boolean condition, IErrorCode errorType, String message) {
        if (!condition) {
            throw new ApiException(errorType, message);
        }
    }

    public static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }

}
