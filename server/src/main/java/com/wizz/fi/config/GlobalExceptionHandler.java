package com.wizz.fi.config;

import com.wizz.fi.exception.ApiException;
import com.wizz.fi.util.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 * Created by macro on 2020/2/27.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public CommonResult handle(HttpServletRequest req, ApiException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        log.error(
                String.format(
                        "business exception, Real IP:{%s}, forward:{%s}, URI:{%s}, stackTrace:{%s}",
                        getRealIP(req),
                        this.getForwardHost(req),
                        req.getRequestURI(),
                        ExceptionUtils.getStackTrace(e)));
        return CommonResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult handleValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        log.error(
                String.format(
                        "business exception, Real IP:{%s}, forward:{%s}, URI:{%s}, stackTrace:{%s}",
                        getRealIP(req),
                        this.getForwardHost(req),
                        req.getRequestURI(),
                        ExceptionUtils.getStackTrace(e)));
        return CommonResult.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public CommonResult handleValidException(HttpServletRequest req, BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        log.error(
                String.format(
                        "business exception, Real IP:{%s}, forward:{%s}, URI:{%s}, stackTrace:{%s}",
                        getRealIP(req),
                        this.getForwardHost(req),
                        req.getRequestURI(),
                        ExceptionUtils.getStackTrace(e)));
        return CommonResult.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public CommonResult handleValidException(HttpServletRequest req, Exception e) {
        log.error(
                String.format(
                        "business exception, Real IP:{%s}, forward:{%s}, URI:{%s}, stackTrace:{%s}",
                        getRealIP(req),
                        this.getForwardHost(req),
                        req.getRequestURI(),
                        ExceptionUtils.getStackTrace(e)));
        return CommonResult.failed("failed");
    }

    private String getRealIP(HttpServletRequest req) {
        if (req.getHeader("X-Real-IP") == null) {
            return req.getRemoteAddr();
        }
        return req.getHeader("X-Real-IP");
    }

    private String getForwardHost(HttpServletRequest req) {
        if (req.getHeader("X-Forwarded-For") == null) {
            return req.getRemoteAddr();
        }
        return req.getHeader("X-Forwarded-For");
    }
}
