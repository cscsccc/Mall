package com.cs.mapper;

import com.cs.pojo.AdminUserToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminUserTokenMapper {

    AdminUserToken getTokenByPrimaryId(Long userId);

    int insertSelective(AdminUserToken adminUserToken);

    int updateByPrimaryIdSelective(AdminUserToken adminUserToken);

    AdminUserToken getDetailByToken(String token);

    Integer deleteById(Long adminUserId);
}
