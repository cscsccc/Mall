package com.cs.api.admin;

import com.cs.api.admin.param.BatchIdParam;
import com.cs.api.admin.param.IndexConfigAddParam;
import com.cs.api.admin.param.IndexConfigEditParam;
import com.cs.common.IndexConfigTypeEnum;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.pojo.AdminUserToken;
import com.cs.pojo.IndexConfig;
import com.cs.service.IndexConfigService;
import com.cs.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "后台管理系统首页配置模块接口")
public class AdminIndexConfigAPI {

    private static final Logger logger = LoggerFactory.getLogger(AdminIndexConfigAPI.class);

    @Autowired
    private IndexConfigService adminIndexConfigService;

    @RequestMapping(value = "/indexConfigs", method = RequestMethod.GET)
    @ApiOperation(value = "首页配置列表", notes = "首页配置列表")
    public Result indexConfig(@RequestParam(required = false) Integer pageNumber,
                              @RequestParam(required = false) Integer pageSize,
                              @RequestParam(required = false) Integer configType,
                              @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);

        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10){
            return ResultGenerator.genFailResult("参数异常");
        }
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);
        if (indexConfigTypeEnum.equals(IndexConfigTypeEnum.DEFAULT)){
            return ResultGenerator.genFailResult("非法参数");
        }

        Map params = new HashMap( 8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("configType", configType);
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult pageResult = adminIndexConfigService.getIndexConfigPage(pageQueryUtil);

        return ResultGenerator.genSuccessResult(pageResult);
    }

    @RequestMapping(value = "/indexConfigs", method = RequestMethod.POST)
    public Result addIndexConfig(@RequestBody @Valid IndexConfigAddParam indexConfigAddParam,
                                 @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);

        if (indexConfigAddParam == null){
            return ResultGenerator.genFailResult("参数异常");
        }
        IndexConfig indexConfig = new IndexConfig();
        BeanUtil.copyProperties(indexConfigAddParam, indexConfig);

        String result = adminIndexConfigService.addIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    @RequestMapping(value = "/indexConfigs/{id}", method = RequestMethod.GET)
    public Result getIndexConfigEditInfo(@PathVariable("id") Long id,
                                         @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);

        if (id == null || id < 1){
            return ResultGenerator.genFailResult("参数异常");
        }

        IndexConfig indexConfig = adminIndexConfigService.selectIndexConfigById(id);
        if (indexConfig == null){
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(indexConfig);
    }

    @RequestMapping(value = "/indexConfigs", method = RequestMethod.PUT)
    public Result editIndexConfig(@RequestBody @Valid IndexConfigEditParam indexConfigEditParam,
                                  @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);

        if (indexConfigEditParam == null){
            return ResultGenerator.genFailResult("参数异常");
        }
        IndexConfig indexConfig = new IndexConfig();
        BeanUtil.copyProperties(indexConfigEditParam, indexConfig);
        String result = adminIndexConfigService.updateIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(result);

    }

    @RequestMapping(value = "/indexConfigs", method = RequestMethod.DELETE)
    public Result delete(@RequestBody BatchIdParam batchIdParam,
                                    @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);

        if (batchIdParam == null || batchIdParam.getIds().length < 1){
            return ResultGenerator.genFailResult("参数异常");
        }

        if (adminIndexConfigService.deleteIndexConfig(batchIdParam.getIds()) > 0){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult("删除失败");
    }

}
