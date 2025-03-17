package com.cs.api.admin;

import com.cs.api.admin.param.BatchIdParam;
import com.cs.api.admin.param.CarouselAddParam;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.pojo.AdminUserToken;
import com.cs.pojo.Carousels;
import com.cs.service.CarouselService;
import com.cs.util.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminCarouselAPI {

    private static final Logger logger = LoggerFactory.getLogger(AdminCarouselAPI.class);

    @Autowired
    private CarouselService carouselService;

    @RequestMapping(value = "/carousels", method = RequestMethod.GET)
    @ApiOperation(value = "轮播图列表", notes = "轮播图列表")
    public Result getCarousels(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
                               @RequestParam(required = false) @ApiParam(value = "页数") Integer pageSize,
                               @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser);
        System.out.println(pageNumber);

        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10){
            return ResultGenerator.genFailResult("分页参数出错");
        }

        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);

        PageResult pageResult = carouselService.getCarousels(pageQueryUtil);

        return ResultGenerator.genSuccessResult(pageResult);
    }

    @RequestMapping(value = "/carousels", method = RequestMethod.POST)
    @ApiOperation(value = "添加轮播图")
    public Result addCarousel(@RequestBody CarouselAddParam params,
                              @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser);

        if (params == null){
            return ResultGenerator.genFailResult("参数异常");
        }
        Carousels carousel = new Carousels();
        BeanUtil.copyProperties(params, carousel);
        String result = carouselService.insertCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(result);
    }

    @RequestMapping(value = "/carousels", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除轮播图")
    public Result deletByBatchId(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);

        if (batchIdParam == null || batchIdParam.getIds().length < 1){
            return ResultGenerator.genFailResult("删除失败");
        }
        String result = carouselService.updateByBatchId(batchIdParam.getIds());
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(result);
    }
}
