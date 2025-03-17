package com.cs.mapper;

import com.cs.pojo.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

public interface AdminUserMapper {
    AdminUser login(@Param("username") String username, @Param("password") String password);

    AdminUser getDetailsById(Long id);

    int updateByIdSelective(AdminUser adminUser);

    AdminUser selectAdminUserByLoginUserName(String loginUserName);

    int updateNameByPrimaryKey(Long id, String loginUserName, String nickName);
}
