package com.wizz.fi.sso;


import com.wizz.fi.dao.pojo.LoginUser;
import com.wizz.fi.util.MyAssert;
import com.wizz.fi.util.ResultCode;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(LoginUser.class) &&
                parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        LoginUser user = (LoginUser) webRequest.getAttribute(SSOConstant.CURRENT_SSO_USER, RequestAttributes.SCOPE_REQUEST);

        MyAssert.isTrue(user != null, ResultCode.UNAUTHORIZED);

        return user;
    }
}

