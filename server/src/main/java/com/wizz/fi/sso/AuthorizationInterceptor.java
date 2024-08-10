package com.wizz.fi.sso;


import com.wizz.fi.dao.enums.Chain;
import com.wizz.fi.dao.pojo.LoginUser;
import com.wizz.fi.util.MyAssert;
import com.wizz.fi.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

@Component
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public AuthorizationInterceptor() {

    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        } else {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            UnAuthorization unAuthorization = method.getAnnotation(UnAuthorization.class);
            if (unAuthorization != null) {
                return true;
            } else {
                String authHeader = request.getHeader("Authorization");
                MyAssert.notBlank(authHeader, ResultCode.UNAUTHORIZED);

                String authToken = authHeader.substring("Bearer ".length());// The part after "Bearer "
                final Map<String, Object> claims = jwtTokenUtil.getClaimsFromToken(authToken);

                MyAssert.notNull(claims, ResultCode.UNAUTHORIZED);

                LoginUser loginUser = new LoginUser();
                if (claims.containsKey("user_address")) {
                    loginUser.setUserAddress(String.valueOf(claims.get("user_address")));
                }
                if (claims.containsKey("chain")) {
                    loginUser.setChain(Chain.valueOf(String.valueOf(claims.get("chain"))));
                }
                if (claims.containsKey("pubkey")) {
                    loginUser.setPubkey(String.valueOf(claims.get("pubkey")));
                }

                request.setAttribute(SSOConstant.CURRENT_SSO_USER, loginUser);
                return true;
            }
        }
    }
}
