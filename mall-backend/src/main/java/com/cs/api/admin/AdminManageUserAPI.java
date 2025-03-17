package com.cs.api.admin;

import com.cs.api.admin.param.AdminLoginPrarm;
import com.cs.api.admin.param.AdminUpdatePasswordParam;
import com.cs.api.admin.param.UpdateAdminNameParam;
import com.cs.api.admin.param.UpdateAdminPasswordParam;
import com.cs.common.Constants;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.pojo.AdminUser;
import com.cs.pojo.AdminUserToken;
import com.cs.service.AdminUserService;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.scanner.Constant;
import javax.validation.Valid;

@RestController
@Api(value = "v1", tags = "后台管理系统管理员模块接口")
public class AdminManageUserAPI {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(AdminManageUserAPI.class);

    @Autowired
    private AdminUserService adminUserService;

    @RequestMapping(value = "/adminUser/login", method = RequestMethod.POST)
    public Result adminLogin(@RequestBody @Valid AdminLoginPrarm adminLoginPrarm) {
        String loginResult = adminUserService.login(adminLoginPrarm.getUserName(), adminLoginPrarm.getPasswordMd5());
        logger.info("manage login api, adminName:{}, loginResult:{}", adminLoginPrarm.getUserName(), loginResult);
        if (StringUtils.hasText(loginResult) && loginResult.length() == Constants.TOKEN_LENGTH) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            System.out.println(result);
            return result;
        }
        return ResultGenerator.genFailResult(loginResult);
    }

    // 通过token解析当前用户的信息
    @RequestMapping(value = "/adminUser/profile", method = RequestMethod.GET)
    public Result adminProfile(@TokenToAdminUser AdminUserToken adminUserToken) {
        logger.info("adminUser信息:" + adminUserToken.toString());
        AdminUser adminUser = adminUserService.getDetailsById(adminUserToken.getAdminUserId());
        if (adminUser != null) {
            adminUser.setLoginPassword("******");
            Result result = ResultGenerator.genSuccessResult();
            result.setData(adminUser);
            return result;
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }

    // 修改密码功能
    @RequestMapping(value = "/adminUser/password", method = RequestMethod.PUT)
    public Result updatePassword(@RequestBody @Valid AdminUpdatePasswordParam adminUpdatePasswordParam, @TokenToAdminUser AdminUserToken adminUserToken) {
        logger.info("adminUser:{}", adminUserToken.toString());

        if (adminUserService.updatePasswordById(adminUserToken.getAdminUserId(), adminUpdatePasswordParam.getOriginalPassword(), adminUpdatePasswordParam.getNewPassword())){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
    }

    // 退出登录（注销）
    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public Result logout(@TokenToAdminUser AdminUserToken adminUserToken) {
        logger.info("adminUser:{}", adminUserToken.toString());

        if (adminUserService.logout(adminUserToken.getAdminUserId())){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
    }

    //修改用户名 或昵称
    @RequestMapping(value = "/adminUser/name", method = RequestMethod.PUT)
    public Result uodateAdminUserName(@RequestBody @Valid UpdateAdminNameParam updateAdminNameParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (updateAdminNameParam == null){
            return ResultGenerator.genFailResult("参数异常");
        }

        String result = adminUserService.updateAdminUserName(adminUser.getAdminUserId(),updateAdminNameParam.getLoginUserName(), updateAdminNameParam.getNickName());

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(result);
    }
}
