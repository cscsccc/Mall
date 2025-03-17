package com.cs.api.mall;

import com.cs.api.mall.VO.MallCartItemVO;
import com.cs.api.mall.param.MallCartAddParam;
import com.cs.api.mall.param.MallCartEditCountParam;
import com.cs.common.MallException;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToMallUser;
import com.cs.pojo.MallUserToken;
import com.cs.service.MallCartService;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MallCartAPI {

    private static final Logger logger = LoggerFactory.getLogger(MallCartAPI.class);

    @Autowired
    private MallCartService mallCartService;

    @RequestMapping(value = "/shop-cart", method = RequestMethod.GET)
    public Result<List<MallCartItemVO>> getCartItemList(@TokenToMallUser MallUserToken mallUser) {
       logger.info("mallUSer:{}", mallUser);
       System.out.println(mallCartService.getCartItemList(mallUser.getUserId()));
       return ResultGenerator.genSuccessResult(mallCartService.getCartItemList(mallUser.getUserId()));
    }

    @RequestMapping(value = "/shop-cart", method = RequestMethod.POST)
    public Result addCart(@RequestBody MallCartAddParam mallCartAddParam, @TokenToMallUser MallUserToken mallUser){
        logger.info("mallUser:{}", mallUser);

        if (mallCartAddParam == null){
            return ResultGenerator.genFailResult("参数异常");
        }

        String result = mallCartService.addCart(mallCartAddParam.getGoodsCount(), mallCartAddParam.getGoodsId(), mallUser.getUserId());


        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(result);
    }
    @RequestMapping(value = "/shop-cart", method = RequestMethod.PUT)
    public Result editCartItemCount(@RequestBody MallCartEditCountParam mallCartEditCountParam, @TokenToMallUser MallUserToken mallUser){
        logger.info("mallUser:{}", mallUser);

        if (mallCartEditCountParam == null){
            return ResultGenerator.genFailResult("参数异常");
        }

        String result = mallCartService.updatecartItemCount(mallCartEditCountParam, mallUser.getUserId());

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(result);
    }

    @DeleteMapping("/shop-cart/{id}")
    public Result deleteCartItem(@PathVariable("id") Long id, @TokenToMallUser MallUserToken mallUser){
        logger.info("mallUser:{}", mallUser);

        if (id == null || id < 1){
            return ResultGenerator.genFailResult("参数异常");
        }

        if (mallCartService.deleteCartItem(id, mallUser.getUserId())){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());

    }

    @GetMapping("/shop-cart/settle")
    public Result<List<MallCartItemVO>> toSettle(Long[] cartItemIds, @TokenToMallUser MallUserToken mallUser){
        logger.info("mallUser:{}", mallUser);
        if (cartItemIds == null || cartItemIds.length < 1){
            return ResultGenerator.genFailResult("参数异常");
        }
        int totalPrice = 0;
        List<MallCartItemVO> mallCartItemVOS = mallCartService.getCartItemListForSettle(cartItemIds);
        if (CollectionUtils.isEmpty(mallCartItemVOS)){
            MallException.fail("参数异常");
        }else {
            for (MallCartItemVO mallCartItemVO : mallCartItemVOS){
                totalPrice += mallCartItemVO.getSellingPrice();
            }
            if (totalPrice < 1){
                MallException.fail("参数异常");
            }
        }

        return ResultGenerator.genSuccessResult(mallCartItemVOS);
    }
}
