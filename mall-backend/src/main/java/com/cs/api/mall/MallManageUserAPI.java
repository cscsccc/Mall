package com.cs.api.mall;

import com.cs.api.mall.VO.MallUserVO;
import com.cs.api.mall.param.MallUserUpdateParam;
import com.cs.api.mall.param.UserRegisterAndLoginParam;
import com.cs.common.Constants;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToMallUser;
import com.cs.pojo.MallUser;
import com.cs.pojo.MallUserToken;
import com.cs.service.MallUserService;
import com.cs.util.BeanUtil;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MallManageUserAPI {

    private static final Logger logger = LoggerFactory.getLogger(MallManageUserAPI.class);

    @Autowired
    private MallUserService mallUserService;

    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public Result register(@RequestBody UserRegisterAndLoginParam userRegisterParam) {
        if (userRegisterParam == null) {
            return ResultGenerator.genFailResult("参数异常");
        }

        String result = mallUserService.saveUser(userRegisterParam.getLoginName(), userRegisterParam.getPassword());

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);

    }

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public Result login(@RequestBody UserRegisterAndLoginParam userLoginParam) {
        if (userLoginParam == null) {
            return ResultGenerator.genFailResult("参数异常");
        }

        String loginResult = mallUserService.login(userLoginParam.getLoginName(), userLoginParam.getPassword());
        logger.info("loginResult:{}", loginResult);

        if (StringUtils.hasText(loginResult) && loginResult.length() == Constants.TOKEN_LENGTH){
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            return result;
        }

        return ResultGenerator.genFailResult(loginResult);
    }

    @GetMapping("/user/info")
    public Result getUserInfo(@TokenToMallUser MallUserToken mallUserToken) {
        MallUser mallUser = mallUserService.selectByPrimaryKey(mallUserToken.getUserId());
        MallUserVO mallUserVO = new MallUserVO();
        BeanUtil.copyProperties(mallUser, mallUserVO);
        System.out.println(mallUserVO);
        return ResultGenerator.genSuccessResult(mallUserVO);
    }

    @PutMapping("/user/info")
    public Result updateUserInfo(@RequestBody MallUserUpdateParam mallUserUpdateParam, @TokenToMallUser MallUserToken mallUserToken) {
        if (mallUserUpdateParam == null) {
            return ResultGenerator.genFailResult("参数异常");
        }
        Boolean result = mallUserService.updateUserInfoByPrimaryKey(mallUserUpdateParam, mallUserToken.getUserId());
        if (result){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("修改失败");
    }

    @PostMapping("/user/logout")
    public Result logout(@TokenToMallUser MallUserToken mallUserToken) {
        logger.info("logoutUserId:{}", mallUserToken.getUserId());

        Boolean result = mallUserService.logout(mallUserToken.getUserId());
        if (result){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
    }


}
