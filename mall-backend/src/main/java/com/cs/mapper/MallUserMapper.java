package com.cs.mapper;

import com.cs.api.admin.param.BatchIdParam;
import com.cs.pojo.MallUser;
import com.cs.util.PageQueryUtil;

import java.util.List;

public interface MallUserMapper {


    List<MallUser> selectList(PageQueryUtil pageUtil);

    int getTotalList(PageQueryUtil pageUtil);

    int updateLockedFlagByPrimaryKey(Byte lockedFlag, Long[] ids);

    MallUser selectByLoginName(String loginName);

    int saveUser(MallUser mallUser);

    MallUser selectByLoginNameAndPassword(String loginName, String passwordMd5);

    MallUser selectByPrimaryKey(Long userId);

    int updateUserInfoByPrimaryKey(MallUser mallUser);
}
