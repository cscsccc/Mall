package com.cs.api.mall;

import com.cs.common.Constants;
import com.cs.config.annotation.TokenToMallUser;
import com.cs.pojo.MallGoods;
import com.cs.pojo.MallUserToken;
import com.cs.service.MallGoodsService;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MallGoodsAPI {

    @Autowired
    private MallGoodsService mallGoodsService;

    @RequestMapping(value = "/goods/detail/{goodsId}", method = RequestMethod.GET)
    public Result getDetail(@PathVariable("goodsId") Long goodsId) {
        if (goodsId < 1){
            return ResultGenerator.genFailResult("参数异常");
        }
        return ResultGenerator.genSuccessResult(mallGoodsService.getDetail(goodsId));
    }

    @GetMapping("/search")
    public Result search(@RequestParam(required = false) @ApiParam(value = "搜索关键字") String keyword,
                         @RequestParam(required = false) @ApiParam(value = "分类id") Long goodsCategoryId,
                         @RequestParam(required = false) @ApiParam(value = "orderBy") String orderBy,
                         @RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
                         @TokenToMallUser MallUserToken mallUserToken) {

        Map params = new HashMap();
        params.put("page", pageNumber);
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        params.put("goodsCategoryId", goodsCategoryId);
        if (StringUtils.hasText(keyword)) {
            params.put("keyWord", keyword);
        }
        if (StringUtils.hasText(orderBy)) {
            params.put("orderBy", orderBy);
        }
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult result = mallGoodsService.searchMallGoods(pageQueryUtil);

        return ResultGenerator.genSuccessResult(result);
    }




}
