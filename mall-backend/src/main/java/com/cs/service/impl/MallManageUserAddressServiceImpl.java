package com.cs.service.impl;

import com.cs.api.mall.VO.MallUserAddressVO;
import com.cs.common.MallException;
import com.cs.common.ServiceResultEnum;
import com.cs.mapper.MallManageUserAddressMapper;
import com.cs.pojo.MallUserAddress;
import com.cs.service.MallManageUserAddressService;
import com.cs.util.BeanUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MallManageUserAddressServiceImpl implements MallManageUserAddressService {
    @Autowired
    private MallManageUserAddressMapper mallManageUserAddressMapper;

    @Override
    public MallUserAddress selectDefaultAddressByPrimaryKey(Long userId) {
        return mallManageUserAddressMapper.selectDefaultAddress(userId);
    }

    @Override
    public boolean saveAddress(MallUserAddress mallUserAddress) {
        Date now = new Date();
        if (mallUserAddress.getDefaultFlag().intValue() == 1){
            // 添加默认地址 将原来的修改
            MallUserAddress temp = mallManageUserAddressMapper.selectDefaultAddress(mallUserAddress.getUserId());
            if (temp != null){
                temp.setDefaultFlag((byte) 0);
                temp.setUpdateTime(now);
                if (mallManageUserAddressMapper.updateByPrimaryKeySelective(temp) < 1){
                    MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
                }
            }
        }
        return mallManageUserAddressMapper.saveAddress(mallUserAddress) > 0;
    }

    @Override
    public List<MallUserAddressVO> getMyAddresslist(Long userId) {
        List<MallUserAddress> mallUserAddress = mallManageUserAddressMapper.selectAddressListByUserId(userId);
        List<MallUserAddressVO> mallUserAddressVOS = BeanUtil.copyList(mallUserAddress, MallUserAddressVO.class);
        return mallUserAddressVOS;
    }

    @Override
    public MallUserAddress getDetailByPriamryKey(Long addressId) {
        return mallManageUserAddressMapper.getDetailByAddressId(addressId);
    }
}
