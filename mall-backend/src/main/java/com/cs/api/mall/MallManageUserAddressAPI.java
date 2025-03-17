package com.cs.api.mall;

import com.cs.api.mall.VO.MallUserAddressVO;
import com.cs.api.mall.param.SaveMallUserAddressParam;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToMallUser;
import com.cs.pojo.MallUserAddress;
import com.cs.pojo.MallUserToken;
import com.cs.service.MallManageUserAddressService;
import com.cs.util.BeanUtil;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class MallManageUserAddressAPI {
    private static final Logger logger = LoggerFactory.getLogger(MallManageUserAddressAPI.class);

    @Autowired
    private MallManageUserAddressService mallManageUserAddressService;


    @GetMapping("/address/default")
    public Result defaultAddress(@TokenToMallUser MallUserToken mallUser) {
        logger.info("MallUser:{}", mallUser.toString());
        MallUserAddress mallUserAddress = mallManageUserAddressService.selectDefaultAddressByPrimaryKey(mallUser.getUserId());

        return ResultGenerator.genSuccessResult(mallUserAddress);
    }

    @PostMapping("/address")
    public Result saveAddress(@RequestBody SaveMallUserAddressParam saveMallUserAddressParam, @TokenToMallUser MallUserToken mallUser) {
        logger.info("MallUser:{}", mallUser.toString());
        if (saveMallUserAddressParam == null) {
            return ResultGenerator.genFailResult("参数异常");
        }
        MallUserAddress mallUserAddress = new MallUserAddress();
        BeanUtil.copyProperties(saveMallUserAddressParam, mallUserAddress);
        mallUserAddress.setUserId(mallUser.getUserId());
        if (mallManageUserAddressService.saveAddress(mallUserAddress)){
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult("添加失败");
    }

    @GetMapping("/address")
    public Result<List<MallUserAddressVO>> getAddress(@TokenToMallUser MallUserToken mallUser) {
        return ResultGenerator.genSuccessResult(mallManageUserAddressService.getMyAddresslist(mallUser.getUserId()));
    }

    @GetMapping("/address/{id}")
    public Result getAddressDetail(@PathVariable("id") Long id, @TokenToMallUser MallUserToken mallUser) {
        logger.info("MallUser:{}", mallUser.toString());
        if (id == null || id < 1){
            return ResultGenerator.genFailResult("参数异常");
        }
        MallUserAddress mallUserAddress = mallManageUserAddressService.getDetailByPriamryKey(id);
        MallUserAddressVO mallUserAddressVO = new MallUserAddressVO();
        BeanUtil.copyProperties(mallUserAddress, mallUserAddressVO);
        if (!mallUser.getUserId().equals(mallUserAddress.getUserId())){
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }

        return ResultGenerator.genSuccessResult(mallUserAddressVO);
    }

}
