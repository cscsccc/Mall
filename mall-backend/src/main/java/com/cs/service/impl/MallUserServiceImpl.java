package com.cs.service.impl;

import com.cs.api.admin.param.BatchIdParam;
import com.cs.api.mall.param.MallUserUpdateParam;
import com.cs.common.MallException;
import com.cs.common.ServiceResultEnum;
import com.cs.mapper.MallUserMapper;
import com.cs.mapper.MallUserTokenMapper;
import com.cs.pojo.MallUser;
import com.cs.pojo.MallUserToken;
import com.cs.service.MallUserService;
import com.cs.util.MD5Util;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import com.cs.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MallUserServiceImpl implements MallUserService {

    @Autowired
    private MallUserMapper mallUserMapper;
    @Autowired
    private MallUserTokenMapper mallUserTokenMapper;

    @Override
    public PageResult selectList(PageQueryUtil pageUtil) {
        List<MallUser> list = mallUserMapper.selectList(pageUtil);
        int total = mallUserMapper.getTotalList(pageUtil);

        PageResult pageResult = new PageResult(list, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public boolean updateLockedFlagByPrimaryKey(Byte lockedFlag, Long[] ids) {
        return mallUserMapper.updateLockedFlagByPrimaryKey(lockedFlag, ids) > 0;
    }

    @Override
    public String saveUser(String loginName, String password) {
        // 1.查询是否有相同用户名
        MallUser mallUser = mallUserMapper.selectByLoginName(loginName);
        if (mallUser != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }

        // 2.保存用户
        MallUser tem = new MallUser();
        tem.setLoginName(loginName);
        tem.setPasswordMd5(password);
        tem.setCreateTime(new Date());
        if (mallUserMapper.saveUser(tem) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String password) {
        MallUser mallUser = mallUserMapper.selectByLoginNameAndPassword(loginName, password);

        Date now = new Date();
        Date expireTime = new Date(now.getTime() + 24 * 60 * 60 * 1000);

        if (mallUser != null){
            String token = TokenUtil.getNewtoken(System.currentTimeMillis() + "", mallUser.getUserId());
            MallUserToken mallUserToken = mallUserTokenMapper.selectByPrimaryKey(mallUser.getUserId());
            if (mallUserToken != null){
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);

                if (mallUserTokenMapper.updateByPrimaryKey(mallUserToken) > 0){
                    return token;
                }
            }else{
                mallUserToken = new MallUserToken();
                mallUserToken.setUserId(mallUser.getUserId());
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);

                if (mallUserTokenMapper.insertByPrimaryKey(mallUserToken) > 0){
                    return token;
                }
            }
            return ServiceResultEnum.DB_ERROR.getResult();


        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public MallUser selectByPrimaryKey(Long userId) {
        return mallUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Boolean updateUserInfoByPrimaryKey(MallUserUpdateParam mallUserUpdateParam, Long userId) {
        MallUser mallUser = mallUserMapper.selectByPrimaryKey(userId);
        if (mallUser == null){
            MallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }

        mallUser.setNickName(mallUserUpdateParam.getNickName());
        if (!MD5Util.MD5Encode("", "UTF-8").equals(mallUser.getPasswordMd5())) {
            mallUser.setPasswordMd5(mallUserUpdateParam.getPasswordMd5());
        }
        mallUser.setIntroduceSign(mallUserUpdateParam.getIntroduceSign());

        return mallUserMapper.updateUserInfoByPrimaryKey(mallUser) > 0;
    }

    @Override
    public Boolean logout(Long userId) {
        return mallUserTokenMapper.deleteByPrimaryKey(userId) > 0;
    }
}
