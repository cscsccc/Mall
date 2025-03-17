package com.cs.service;

import com.cs.api.mall.VO.MallCartItemVO;
import com.cs.api.mall.VO.MallOrderDetailVO;
import com.cs.api.mall.VO.MallOrderItemVO;
import com.cs.api.mall.VO.MallOrderListVO;
import com.cs.pojo.MallUserAddress;
import com.cs.pojo.MallUserToken;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;

import java.util.List;

public interface MallOrderService {

    PageResult selectMallOrderPage(PageQueryUtil pageQueryUtil);

    MallOrderDetailVO getOrderDetailByOrderId(Long orderId);

    String closeOrder(Long[] ids);

    String checkDone(Long[] ids);

    String checkOut(Long[] ids);

    String saveOrder(MallUserToken mallUser, MallUserAddress mallUserAddress, List<MallCartItemVO> mallCartItemVOS);

    String paySuccess(String orderNo, int payType);

    PageResult<List<MallOrderItemVO>> getOrderListPage(PageQueryUtil pageQueryUtil);

    MallOrderDetailVO getOrderDetailByOrderNoAndUserId(String orderNo, Long userId);

    String cancelOrder(String orderNo, Long userId);

    String finishOrder(String orderNo, Long userId);

    String saveSeckillOrder(Long seckillSuccessId, Long userId);
}
