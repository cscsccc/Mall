package com.cs.mapper;

import com.cs.pojo.MallOrderItem;

import java.util.List;

public interface MallOrderItemMapper {

    List<MallOrderItem> selectItemsByOrderId(Long orderId);

    List<MallOrderItem> selectByOrderIds(List<Long> orderIds);

    int insertBatch(List<MallOrderItem> mallOrderItems);

    int insertSelective(MallOrderItem mallOrderItem);
}
