package com.cs.api.admin;

import com.cs.api.admin.param.BatchIdParam;
import com.cs.api.admin.param.GoodsAddParam;
import com.cs.api.admin.param.GoodsEditParam;
import com.cs.common.Constants;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.pojo.AdminUserToken;
import com.cs.pojo.GoodsCategory;
import com.cs.pojo.MallGoods;
import com.cs.service.MallGoodsService;
import com.cs.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "后台管理系统商品信息模块")
public class AdminGoodsInfoAPI {

    private static final Logger logger = LoggerFactory.getLogger(AdminGoodsInfoAPI.class);

    @Autowired
    private MallGoodsService mallGoodsService;

    //  添加商品
    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    public Result save(@RequestBody @Valid GoodsAddParam goodsAddParam, @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);
        MallGoods mallGoods = new MallGoods();
        BeanUtils.copyProperties(goodsAddParam, mallGoods);
        String result = mallGoodsService.saveGood(mallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    // 展示商品
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ApiOperation(value = "商品列表", notes = "可根据名称和上架状态筛选")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "商品名称") String goodsName,
                       @RequestParam(required = false) @ApiParam(value = "上架状态 0-上架 1-下架") Integer goodsSellStatus,
                       @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);

        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10){
            return ResultGenerator.genFailResult("分页参数异常");
        }

        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);

        if (StringUtils.hasText(goodsName)){
            params.put("goodsName", goodsName);
        }

        if (goodsSellStatus != null){
            params.put("goodsSellStatus", goodsSellStatus);
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);

        PageResult pageResult = mallGoodsService.getMallGoodsPage(pageUtil);

        return ResultGenerator.genSuccessResult(pageResult);
    }

    //点击修改后 在添加商品页面 自动填入所修改的商品信息
    @RequestMapping(value = "/goods/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取单条商品信息", notes = "根据 id 查询")
    public Result getOneById(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);

        Map goodsInfo = new HashMap(8);
        MallGoods mallGood = mallGoodsService.getOneById(id);
        System.out.println("mallGood:" + mallGood);
        if (mallGood == null){
            return ResultGenerator.genFailResult(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }

        goodsInfo.put("goods", mallGood);

        // 获取分类的信息以填补在添加商品的输入框内
        GoodsCategory thirdCategory;
        GoodsCategory secondCategory;
        GoodsCategory firstCategory;

        thirdCategory = mallGoodsService.getCategoryById(mallGood.getGoodsCategoryId());
        if (thirdCategory != null){
            goodsInfo.put("thirdCategory", thirdCategory);
            secondCategory = mallGoodsService.getCategoryById(thirdCategory.getParentId());
            if (secondCategory != null){
                goodsInfo.put("secondCategory", secondCategory);
                firstCategory = mallGoodsService.getCategoryById(secondCategory.getParentId());
                if (firstCategory != null){
                    goodsInfo.put("firstCategory", firstCategory);
                }
            }
        }
        return ResultGenerator.genSuccessResult(goodsInfo);
    }

    // 修改商品状态（0-上架 1-下架）
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改商品状态")
    public Result updateStatus(@RequestBody BatchIdParam batchIdParam, @PathVariable("sellStatus") int sellStatus,
                               @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);
        if (batchIdParam == null || batchIdParam.getIds().length < 1){
            return ResultGenerator.genFailResult("参数异常");
        }

        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN){
            return ResultGenerator.genFailResult("状态异常");
        }

        if (mallGoodsService.updateBatchSellStatus(batchIdParam.getIds(), sellStatus) > 0){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("修改失败");
    }

    // 修改商品
    @RequestMapping(value = "/goods", method = RequestMethod.PUT)
    public Result update(@RequestBody @Valid GoodsEditParam goodsEditParam, @TokenToAdminUser AdminUserToken adminUser){
        logger.info("adminUser:{}", adminUser);

        MallGoods goods = new MallGoods();
        BeanUtil.copyProperties(goodsEditParam, goods);
        String result = mallGoodsService.updateMallGoods(goods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genSuccessResult(result);
    }


}
