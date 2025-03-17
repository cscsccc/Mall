package com.cs.config.handler;

import com.cs.common.Constants;
import com.cs.common.MallException;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.mapper.AdminUserTokenMapper;
import com.cs.pojo.AdminUserToken;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class TokenToAdminUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private AdminUserTokenMapper adminUserTokenMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.hasParameterAnnotation(TokenToAdminUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory){
        if (parameter.getParameterAnnotation(TokenToAdminUser.class) != null) {
            String token = webRequest.getHeader("token");
            System.out.println("token" + token);
            if (token != null && !"".equals(token) && token.length() == Constants.TOKEN_LENGTH) {
                AdminUserToken adminUserToken = adminUserTokenMapper.getDetailByToken(token);
                System.out.println("adminUserToken" + adminUserToken);
                if (adminUserToken == null) {
                    MallException.fail(ServiceResultEnum.ADMIN_NOT_LOGIN_ERROR.getResult());
                } else if (adminUserToken.getExpireTime().getTime() <= System.currentTimeMillis()) {
                    MallException.fail(ServiceResultEnum.ADMIN_TOKEN_EXPIRE_ERROR.getResult());
                }
                return adminUserToken;
            }else {
                MallException.fail(ServiceResultEnum.ADMIN_NOT_LOGIN_ERROR.getResult());
            }

        }
        return null;
    }
}
