package com.cs.api.admin;

import com.cs.api.admin.param.BatchIdParam;
import com.cs.common.Constants;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.pojo.AdminUserToken;
import com.cs.pojo.MallUser;
import com.cs.service.MallUserService;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MallUserAPI {

    private static final Logger logger = LoggerFactory.getLogger(MallUserAPI.class);

    @Autowired
    private MallUserService mallUserService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public Result list(@RequestParam(required = false) Integer pageNumber,
                       @RequestParam(required = false) Integer pageSize,
                       @RequestParam(required = false) Byte lockedFlag,@TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser = {}", adminUser);

        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10){
            return ResultGenerator.genFailResult("参数异常");
        }

        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (lockedFlag != null) {
            params.put("lockedFlag", lockedFlag);
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);

        PageResult pageResult = mallUserService.selectList(pageQueryUtil);

        return ResultGenerator.genSuccessResult(pageResult);
    }

    // 解除或封禁 用户
    @RequestMapping(value = "/users/{lockedFlag}", method = RequestMethod.PUT)
    public Result updateLockedFlag(@PathVariable("lockedFlag") Byte lockedFlag, @RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser = {}", adminUser);

        if (lockedFlag == null || (lockedFlag != 0 && lockedFlag != 1) || batchIdParam == null || batchIdParam.getIds().length< 1){
            return ResultGenerator.genFailResult("参数异常");
        }
        if(mallUserService.updateLockedFlagByPrimaryKey(lockedFlag, batchIdParam.getIds())){
            return ResultGenerator.genSuccessResult();
        }


        return ResultGenerator.genFailResult("操作失败");
    }

}
