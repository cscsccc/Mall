package com.cs.config.handler;

import com.cs.common.Constants;
import com.cs.common.MallException;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.config.annotation.TokenToMallUser;
import com.cs.mapper.AdminUserTokenMapper;
import com.cs.mapper.MallUserMapper;
import com.cs.mapper.MallUserTokenMapper;
import com.cs.pojo.AdminUserToken;
import com.cs.pojo.MallUser;
import com.cs.pojo.MallUserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class TokenToMallUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private MallUserTokenMapper mallUserTokenMapper;
    @Autowired
    private MallUserMapper mallUserMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.hasParameterAnnotation(TokenToMallUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory){
        if (parameter.getParameterAnnotation(TokenToMallUser.class) != null) {
            String token = webRequest.getHeader("token");
            System.out.println("token" + token);
            if (token != null && !"".equals(token) && token.length() == Constants.TOKEN_LENGTH) {
                MallUserToken mallUserToken = mallUserTokenMapper.selectByToken(token);
                System.out.println("mallUserToken" + mallUserToken);
                if (mallUserToken == null || mallUserToken.getExpireTime().getTime() <= System.currentTimeMillis()) {
                    MallException.fail(ServiceResultEnum.TOKEN_EXPIRE_ERROR.getResult());
                }
                MallUser mallUser = mallUserMapper.selectByPrimaryKey(mallUserToken.getUserId());
                if (mallUser == null) {
                    MallException.fail(ServiceResultEnum.USER_NULL_ERROR.getResult());
                }
                if (mallUser.getLockedFlag().intValue() == 1){
                    MallException.fail(ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult());
                }

                return mallUserToken;
            }else {
                MallException.fail(ServiceResultEnum.NOT_LOGIN_ERROR.getResult());
            }

        }
        return null;
    }
}
