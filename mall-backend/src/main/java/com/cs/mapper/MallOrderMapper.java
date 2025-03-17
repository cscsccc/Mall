package com.cs.mapper;

import com.cs.common.MallOrderStatusEnum;
import com.cs.pojo.MallOrder;
import com.cs.util.PageQueryUtil;

import java.util.List;

public interface MallOrderMapper {

    List<MallOrder> selectMallOrderPageList(PageQueryUtil pageQueryUtil);

    int selectTotalMallOrder(PageQueryUtil pageQueryUtil);

    MallOrder selectMallOrderByOrderId(Long orderId);

    List<MallOrder> selectByPrimaryKeys(List<Long> list);

    int closeOrder(List<Long> orderIds, int orderStatus);

    int checkDone(List<Long> list);

    int checkOut(List<Long> list);

    int insertSelective(MallOrder order);

    MallOrder selectMallOrderByOrderNo(String orderNo);

    int updateBySelective(MallOrder mallOrder);

    List<MallOrder> getOrderListPage(PageQueryUtil pageQueryUtil);

    int getTotalList(PageQueryUtil pageQueryUtil);

    MallOrder selectMallOrderByOrderNoAnduserId(String orderNo, Long userId);
}
