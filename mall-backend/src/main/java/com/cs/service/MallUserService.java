package com.cs.service;

import com.cs.api.admin.param.BatchIdParam;
import com.cs.api.mall.param.MallUserUpdateParam;
import com.cs.pojo.MallUser;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;

public interface MallUserService {
    PageResult selectList(PageQueryUtil pageUtil);

    boolean updateLockedFlagByPrimaryKey(Byte lockedFlag, Long[] ids);

    String saveUser(String loginName, String password);

    String login(String loginName, String password);

    MallUser selectByPrimaryKey(Long userId);

    Boolean updateUserInfoByPrimaryKey(MallUserUpdateParam mallUserUpdateParam, Long userId);

    Boolean logout(Long userId);
}
