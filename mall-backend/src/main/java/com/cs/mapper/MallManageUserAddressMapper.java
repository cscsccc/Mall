package com.cs.mapper;

import com.cs.api.mall.VO.MallUserAddressVO;
import com.cs.pojo.MallUserAddress;

import java.util.List;

public interface MallManageUserAddressMapper {
    MallUserAddress selectDefaultAddress(Long userId);

    int saveAddress(MallUserAddress mallUserAddress);

    int updateByPrimaryKeySelective(MallUserAddress temp);

    List<MallUserAddress> selectAddressListByUserId(Long userId);

    MallUserAddress getDetailByAddressId(Long addressId);
}
