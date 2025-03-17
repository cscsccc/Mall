package com.cs.service;

import com.cs.api.mall.VO.MallUserAddressVO;
import com.cs.pojo.MallUserAddress;

import java.util.List;

public interface MallManageUserAddressService {
    MallUserAddress selectDefaultAddressByPrimaryKey(Long userId);

    boolean saveAddress(MallUserAddress mallUserAddress);

    List<MallUserAddressVO> getMyAddresslist(Long userId);

    MallUserAddress getDetailByPriamryKey(Long addressId);
}
