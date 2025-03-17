package com.cs.api.admin;

import com.cs.api.admin.param.BatchIdParam;
import com.cs.api.admin.param.GoodsCategoryAddParam;
import com.cs.api.admin.param.GoodsCategoryEditParam;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.pojo.AdminUserToken;
import com.cs.pojo.GoodsCategory;
import com.cs.service.MallCategoryService;
import com.cs.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "后台管理员分类模块接口")
public class AdminGoodsCategoryAPI {

    private static final Logger logger = LoggerFactory.getLogger(AdminGoodsCategoryAPI.class);

    @Autowired
    private MallCategoryService mallCategoryService;

    @RequestMapping(value = "/categories",method = RequestMethod.GET)
    @ApiOperation(value = "商品分类列表", notes = "根据级别和上级分类id查询")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
                                @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                                @RequestParam(required = false) @ApiParam(value = "分类级别") Integer categoryLevel,
                                @RequestParam(required = false) @ApiParam(value = "上级分类的id") Long parentId,
                                @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser.toString());



        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10 || categoryLevel == null || categoryLevel < 0 || categoryLevel > 3
            || parentId == null || parentId < 0) {
            return ResultGenerator.genFailResult("分页参数异常");
        }

        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("categoryLevel", categoryLevel);
        params.put("parentId", parentId);

        PageQueryUtil pageUtil = new PageQueryUtil(params);

        PageResult pageResult = mallCategoryService.getCategories(pageUtil);

        return ResultGenerator.genSuccessResult(pageResult);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public Result save(@RequestBody GoodsCategoryAddParam goodsCategoryAddParam,
                              @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser.toString());

        GoodsCategory goodsCategory = new GoodsCategory();
        BeanUtil.copyProperties(goodsCategoryAddParam, goodsCategory);

        String result = mallCategoryService.saveCategory(goodsCategory);

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
    public Result getDetail(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser.toString());

        GoodsCategory category = mallCategoryService.selectByPrimaryKey(id);
        if (category == null){
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(category);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.PUT)
    public Result update(@RequestBody GoodsCategoryEditParam goodsCategoryEditParam, @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser.toString());
        GoodsCategory category = new GoodsCategory();
        BeanUtil.copyProperties(goodsCategoryEditParam, category);
        String result = mallCategoryService.updateCategory(category);

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(result);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.DELETE)
    public Result delete(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser.toString());

        if (batchIdParam == null || batchIdParam.getIds().length < 1){
            return ResultGenerator.genFailResult("参数异常");
        }

        if (mallCategoryService.deleteBatch(batchIdParam.getIds())){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult("删除失败。");
    }
}
