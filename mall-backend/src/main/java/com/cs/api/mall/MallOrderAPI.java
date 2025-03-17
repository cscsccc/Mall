package com.cs.api.mall;

import com.cs.api.mall.VO.MallCartItemVO;
import com.cs.api.mall.param.SaveOrderParam;
import com.cs.common.Constants;
import com.cs.common.MallException;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToMallUser;
import com.cs.mapper.MallCartMapper;
import com.cs.pojo.MallOrder;
import com.cs.pojo.MallUserAddress;
import com.cs.pojo.MallUserToken;
import com.cs.service.MallCartService;
import com.cs.service.MallManageUserAddressService;
import com.cs.service.MallOrderService;
import com.cs.util.MD5Util;
import com.cs.util.PageQueryUtil;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import io.swagger.annotations.ApiParam;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class MallOrderAPI {

    private static final Logger logger = LoggerFactory.getLogger(MallOrderAPI.class);
    @Autowired
    private MallManageUserAddressService mallManageUserAddressService;
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private MallCartService mallCartService;


    @PostMapping("/saveOrder")
    public Result saveOrder(@RequestBody SaveOrderParam param, @TokenToMallUser MallUserToken mallUser) {
        logger.info("mallUser:{}", mallUser);
        if (param == null || param.getAddressId() == null || param.getCartItemIds() == null || param.getCartItemIds().length < 1){
            MallException.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        logger.info("mallCartService" + mallCartService);

        List<MallCartItemVO> mallCartItemVOS = mallCartService.findCartItemListForSettle(Arrays.asList(param.getCartItemIds()), mallUser.getUserId());
        if (CollectionUtils.isEmpty(mallCartItemVOS)){
            MallException.fail("参数异常");
        }
        int priceTotal = 0;
        for (MallCartItemVO mallCartItemVO : mallCartItemVOS) {
            priceTotal += mallCartItemVO.getGoodsCount() * mallCartItemVO.getSellingPrice();
        }
        if (priceTotal < 1){
            MallException.fail("价格异常");
        }
        MallUserAddress mallUserAddress = mallManageUserAddressService.getDetailByPriamryKey(param.getAddressId());
        if (!mallUserAddress.getUserId().equals(mallUser.getUserId())){
            ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }

        //保存订单 返回单号
        String saveOrderResult = mallOrderService.saveOrder(mallUser, mallUserAddress, mallCartItemVOS);
        System.out.println("orderNo:" + saveOrderResult);
        Result result = ResultGenerator.genSuccessResult();
        result.setData(saveOrderResult);
        return result;
    }

    @GetMapping("/paySuccess")
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType, @TokenToMallUser MallUserToken mallUser) {
        logger.info("mallUser:{}", mallUser);

        String payResult = mallOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(payResult);
    }

    @GetMapping("/order")
    public Result getOrderList(@RequestParam(required = false) Integer pageNumber, @ApiParam(value = "订单状态:0.待支付 1.待确认 2.待发货 3:已发货 4.交易成功") @RequestParam(required = false) Integer status, @TokenToMallUser MallUserToken mallUser) {
        logger.info("mallUser:{}", mallUser);
        if (pageNumber == null || pageNumber < 1){
            pageNumber = 1;
        }
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        params.put("orderStatus", status);
        params.put("userId", mallUser.getUserId());

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(mallOrderService.getOrderListPage(pageQueryUtil));
    }

    @GetMapping("/order/{orderNo}")
    public Result getOrderDetail(@PathVariable("orderNo") String orderNo, @TokenToMallUser MallUserToken mallUser) {
        logger.info("mallUser:{}", mallUser);

        if (orderNo == null){
            return ResultGenerator.genFailResult(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        System.out.println(orderNo);
        return ResultGenerator.genSuccessResult(mallOrderService.getOrderDetailByOrderNoAndUserId(orderNo, mallUser.getUserId()));
    }

    @PutMapping("/order/{orderNo}/cancel")
    @Transactional
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, @TokenToMallUser MallUserToken mallUser) {
        logger.info("mallUser:{}", mallUser);
        if (orderNo == null){
            return ResultGenerator.genFailResult(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        String result = mallOrderService.cancelOrder(orderNo, mallUser.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    @PutMapping("/order/{orderNo}/finish")
    public Result finishOrder(@PathVariable("orderNo") String orderNo, @TokenToMallUser MallUserToken mallUser){
        String result = mallOrderService.finishOrder(orderNo, mallUser.getUserId());

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(result);
    }

    @PostMapping("/saveSeckillOrder/{seckillSuccessId}/{seckillSecretKey}")
    public Result saveSeckillOrder(@PathVariable Long seckillSuccessId, @PathVariable String seckillSecretKey, @TokenToMallUser MallUserToken mallUser) {
        if (seckillSecretKey == null || !seckillSecretKey.equals(MD5Util.MD5Encode(seckillSuccessId + Constants.SECKILL_ORDER_SALT, "UTF-8"))){
            MallException.fail("下单商品不合法");
        }
        String orderNo = mallOrderService.saveSeckillOrder(seckillSuccessId, mallUser.getUserId());

        Result result = ResultGenerator.genSuccessResult();
        result.setData(orderNo);
        return result;
    }
}
