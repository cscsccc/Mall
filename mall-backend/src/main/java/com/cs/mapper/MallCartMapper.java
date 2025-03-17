package com.cs.mapper;

import com.cs.pojo.MallCartItem;

import java.util.List;

public interface MallCartMapper {
    List<MallCartItem> selectByUserId(Long userId, int number);

    MallCartItem selectByUserIdAndGoodsId(Long userId, Long goodsId);

    int saveCart(MallCartItem temp);

    int selectCountByUserId(Long userId);

    MallCartItem selectByPrimaryKey(Long cartItemId);

    int updateByPrimaryKeySelective(MallCartItem mallCartItem);

    int deleteByPrimaryKey(Long cartItemId);

    List<MallCartItem> selectByPrimaryKeyForSettle(Long[] cartItemIds);

    List<MallCartItem> selectByUserIDAndCartItems(List<Long> cartItemIds, Long userId);

    int deleteBatchByPrimaryKey(List<Long> itemIdList);

    List<MallCartItem> selectByOrderId(Long orderId);
}
