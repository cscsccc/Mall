package com.cs.mapper;

import com.cs.pojo.AdminUserToken;
import com.cs.pojo.MallUserToken;

public interface MallUserTokenMapper {
    MallUserToken selectByPrimaryKey(Long userId);

    int updateByPrimaryKey(MallUserToken mallUserToken);

    int insertByPrimaryKey(MallUserToken mallUserToken);

    MallUserToken selectByToken(String token);

    int deleteByPrimaryKey(Long userId);
}
