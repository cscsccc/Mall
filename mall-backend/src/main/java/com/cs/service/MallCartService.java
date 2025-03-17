package com.cs.service;

import com.cs.api.mall.VO.MallCartItemVO;
import com.cs.api.mall.param.MallCartEditCountParam;

import java.util.List;

public interface MallCartService {
    List<MallCartItemVO> getCartItemList(Long userId);

    String addCart(Integer goodsCount, Long goodsId, Long userId);

    String updatecartItemCount(MallCartEditCountParam mallCartEditCountParam, Long userId);

    boolean deleteCartItem(Long id, Long userId);

    List<MallCartItemVO> getCartItemListForSettle(Long[] cartItemIds);

    List<MallCartItemVO> findCartItemListForSettle(List<Long> cartItemIds, Long userId);

}
