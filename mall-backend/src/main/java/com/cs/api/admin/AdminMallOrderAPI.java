package com.cs.api.admin;

import com.cs.api.admin.param.BatchIdParam;
import com.cs.api.mall.VO.MallOrderDetailVO;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.pojo.AdminUserToken;
import com.cs.service.MallOrderService;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminMallOrderAPI {

    private static final Logger logger = LoggerFactory.getLogger(AdminMallOrderAPI.class);

    @Autowired
    private MallOrderService mallOrderService;

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public Result list(@RequestParam(required = false) Integer pageNumber, @RequestParam(required = false) Integer pageSize,
                       @RequestParam(required = false) String orderNo, @RequestParam(required = false) Byte orderStatus, @TokenToAdminUser AdminUserToken aminUser) {
        logger.info("adminUser:{}", aminUser);

        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("参数异常");
        }
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);

        if (StringUtils.hasText(orderNo)){
            params.put("orderNo", orderNo);
        }
        if (orderStatus != null){
            params.put("orderStatus", orderStatus);
        }

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult pageResult = mallOrderService.selectMallOrderPage(pageQueryUtil);
        return ResultGenerator.genSuccessResult(pageResult);
    }

    @GetMapping("/orders/{orderId}")
    public Result<MallOrderDetailVO> getDetail(@PathVariable("orderId") Long orderId, @TokenToAdminUser AdminUserToken aminUser) {
        logger.info("adminUser:{}", aminUser.toString());

        return ResultGenerator.genSuccessResult(mallOrderService.getOrderDetailByOrderId(orderId));
    }

    @RequestMapping(value = "/orders/close", method = RequestMethod.PUT)
    public Result close(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken aminUser) {
        logger.info("adminUser:{}", aminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length<1){
            return ResultGenerator.genFailResult("参数异常");
        }

        String result = mallOrderService.closeOrder(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    // 配货
    @RequestMapping(value = "/orders/checkDone", method = RequestMethod.PUT)
    public Result checkDone(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken aminUser) {
        logger.info("adminUser:{}", aminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length<1){
            return ResultGenerator.genFailResult("参数异常");
        }

        String result = mallOrderService.checkDone(batchIdParam.getIds());

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(result);
    }

    // 配货完成
    @RequestMapping(value = "/orders/checkOut", method = RequestMethod.PUT)
    public Result checkOut(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken aminUser) {
        logger.info("adminUser:{}", aminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length<1){
            return ResultGenerator.genFailResult("参数异常");
        }

        String result = mallOrderService.checkOut(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(result);

    }







}
