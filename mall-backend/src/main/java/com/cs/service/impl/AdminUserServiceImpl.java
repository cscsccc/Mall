package com.cs.service.impl;

import com.cs.common.ServiceResultEnum;
import com.cs.mapper.AdminUserMapper;
import com.cs.mapper.AdminUserTokenMapper;
import com.cs.pojo.AdminUser;
import com.cs.pojo.AdminUserToken;
import com.cs.util.NumberUtil;
import com.cs.util.ResultGenerator;
import com.cs.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class AdminUserServiceImpl implements com.cs.service.AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private AdminUserTokenMapper adminUserTokenMapper;

    @Override
    public String login(String username, String password) {
        AdminUser loginAdminUser = adminUserMapper.login(username, password);
        if (loginAdminUser != null) {
            // 修改token
            String token = getNewtoken(System.currentTimeMillis()+ "", loginAdminUser.getAdminUserId());
            // 根据id查找token
            AdminUserToken adminUserToken = adminUserTokenMapper.getTokenByPrimaryId(loginAdminUser.getAdminUserId());
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + 2 * 24 * 3600 * 1000);//过期时间 48 小时

            if (adminUserToken == null) {
                adminUserToken = new AdminUserToken();
                adminUserToken.setAdminUserId(loginAdminUser.getAdminUserId());
                adminUserToken.setToken(token);
                adminUserToken.setUpdateTime(now);
                adminUserToken.setExpireTime(expireTime);

                // 新增一条 adminUserToken 数据
                if (adminUserTokenMapper.insertSelective(adminUserToken) > 0){
                    return token;
                }
            }
            else {
                adminUserToken.setToken(token);
                adminUserToken.setUpdateTime(now);
                adminUserToken.setExpireTime(expireTime);

                // 更新
                if (adminUserTokenMapper.updateByPrimaryIdSelective(adminUserToken) > 0){
                    return token;
                }
            }


        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public AdminUser getDetailsById(Long adminUserId) {
        return adminUserMapper.getDetailsById(adminUserId);
    }

    @Override
    public boolean updatePasswordById(Long adminUserId, String originalPassword, String newPassword) {
        // 先查询是否存在此用户
        AdminUser adminUser = adminUserMapper.getDetailsById(adminUserId);
        if (adminUser == null) {
            return false;
        }
        // 对比旧密码是否一致
        if (!adminUser.getLoginPassword().equals(originalPassword)) {
            return false;
        }

        //设置新密码并删除token
        adminUser.setLoginPassword(newPassword);
        return adminUserMapper.updateByIdSelective(adminUser) > 0 && adminUserTokenMapper.deleteById(adminUser.getAdminUserId()) > 0;

    }

    @Override
    public boolean logout(Long adminUserId) {
        if (adminUserTokenMapper.deleteById(adminUserId) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String updateAdminUserName(Long id, String loginUserName, String nickName) {
        // 1.查找用户
        AdminUser adminUser = adminUserMapper.getDetailsById(id);
        if (adminUser == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        // 2.查找是否有同用户名
        AdminUser temp = adminUserMapper.selectAdminUserByLoginUserName(loginUserName);
        if (temp != null && !Objects.equals(temp.getAdminUserId(), id)){
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }

        //3.进行更新
        if (adminUserMapper.updateNameByPrimaryKey(id, loginUserName, nickName)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    public static String getNewtoken(String token, Long adminUserId) {
        String newtoken = token + adminUserId + NumberUtil.genRandomNum(6);
        return SystemUtil.genToken(newtoken);
    }
}
