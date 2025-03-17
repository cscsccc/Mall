package com.cs.service.impl;

import com.cs.api.mall.VO.MallCartItemVO;
import com.cs.api.mall.VO.MallOrderDetailVO;
import com.cs.api.mall.VO.MallOrderItemVO;
import com.cs.api.mall.VO.MallOrderListVO;
import com.cs.common.*;
import com.cs.mapper.*;
import com.cs.pojo.*;
import com.cs.service.MallOrderService;
import com.cs.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MallOrderServiceImpl implements MallOrderService {

    @Autowired
    private MallOrderMapper mallOrderMapper;
    @Autowired
    private MallOrderItemMapper mallOrderItemMapper;
    @Autowired
    private MallGoodsMapper mallGoodsMapper;
    @Autowired
    private MallCartMapper mallCartMapper;
    @Autowired
    private MallOrderAddressMapper mallOrderAddressMapper;
    @Autowired
    private MallSeckillSuccessMapper mallSeckillSuccessMapper;
    @Autowired
    private MallSecKillMapper mallSecKillMapper;
    @Autowired
    private MallManageUserAddressMapper mallManageUserAddressMapper;

    @Override
    public PageResult selectMallOrderPage(PageQueryUtil pageQueryUtil) {
        List<MallOrder> list = mallOrderMapper.selectMallOrderPageList(pageQueryUtil);
        int total = mallOrderMapper.selectTotalMallOrder(pageQueryUtil);

        PageResult pageResult = new PageResult(list, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public MallOrderDetailVO getOrderDetailByOrderId(Long orderId) {
        MallOrder mallOrder = mallOrderMapper.selectMallOrderByOrderId(orderId);
        if (mallOrder == null){
            MallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }

        List<MallOrderItem> mallOrderItems = mallOrderItemMapper.selectItemsByOrderId(orderId);
        if (!CollectionUtils.isEmpty(mallOrderItems)){
            List<MallOrderItemVO> mallOrderItemVOs = BeanUtil.copyList(mallOrderItems, MallOrderItemVO.class);
            MallOrderDetailVO mallOrderDetailVO = new MallOrderDetailVO();
            BeanUtil.copyProperties(mallOrder, mallOrderDetailVO);
            mallOrderDetailVO.setOrderStatusString(MallOrderStatusEnum.getMallOrderStatusEnumByStatus(mallOrderDetailVO.getOrderStatus()).getName());
            mallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(mallOrderDetailVO.getPayType()).getName());
            mallOrderDetailVO.setMallOrderItemVOS(mallOrderItemVOs);

            return mallOrderDetailVO;
        }
        MallException.fail(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
        return null;
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {

        List<MallOrder> orders = mallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));

        StringBuilder errOrderNo = new StringBuilder();
        if (!CollectionUtils.isEmpty(orders)){
            for (MallOrder order : orders){
                if (order.getIsDeleted() == 1) {
                    errOrderNo.append(order.getOrderNo()).append(" ");
                    continue;
                }

                if (order.getOrderStatus() == 4 || order.getOrderStatus() < 0){
                    errOrderNo.append(order.getOrderNo()).append(" ");
                }
            }

            if (!StringUtils.hasText(errOrderNo.toString())){

                // 对ids进行修改订单状态 并恢复库存
                if (mallOrderMapper.closeOrder(Arrays.asList(ids), MallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0 && recoverStockNum(Arrays.asList(ids))){
                    return ServiceResultEnum.SUCCESS.getResult();
                }
                return ServiceResultEnum.DB_ERROR.getResult();
            }
            System.out.println(errOrderNo);
            if (errOrderNo.length() > 0 && errOrderNo.length() < 100){
                return errOrderNo + "订单不能执行关闭操作";
            }
            return errOrderNo + "你选择的订单不能关闭";
        }

        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        List<MallOrder> orders = mallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errOrderNo = "";

        if (!CollectionUtils.isEmpty(orders)){
            for (MallOrder order : orders){
                if (order.getIsDeleted() == 1){
                    errOrderNo += order.getOrderNo() + " ";
                    continue;
                }
                if (order.getOrderStatus() != 1){
                    errOrderNo += order.getOrderNo() + " ";
                }
            }

            if (!StringUtils.hasText(errOrderNo)){
                if (mallOrderMapper.checkDone(Arrays.asList(ids)) > 0 ){
                    return ServiceResultEnum.SUCCESS.getResult();
                }
                else{
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            }

            if (errOrderNo.length() > 0 && errOrderNo.length() < 100){
                return errOrderNo + "订单的状态不是支付成功无法执行出库操作";
            }
            return  errOrderNo + "你选择了太多不是支付成功的订单，无法执行配货完成操作";
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        List<MallOrder> orders = mallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        System.out.println("a" + orders);
        String errOrderNo = "";
        if (!CollectionUtils.isEmpty(orders)){
            for (MallOrder order : orders){
                if (order.getIsDeleted() == 1){
                    errOrderNo += order.getOrderNo() + " ";
                    continue;
                }
                if (order.getOrderStatus() != 2){
                    errOrderNo += order.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errOrderNo)){
                if (mallOrderMapper.checkOut(Arrays.asList(ids)) > 0){
                    return ServiceResultEnum.SUCCESS.getResult();
                }
                return ServiceResultEnum.DB_ERROR.getResult();
            }
            if (errOrderNo.length() > 0 && errOrderNo.length() < 100) {
                return errOrderNo + "订单的状态不是支付成功或配货完成无法执行出库操作";
            } else {
                return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
            }
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(MallUserToken mallUser, MallUserAddress mallUserAddress, List<MallCartItemVO> mallCartItemVOS) {
        List<Long> itemIdList = mallCartItemVOS.stream().map(MallCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = mallCartItemVOS.stream().map(MallCartItemVO::getGoodsId).collect(Collectors.toList());
        List<MallGoods> mallGoods = mallGoodsMapper.selectListByIds(goodsIds);
        // 检查商品是否下架
        List<MallGoods> goodsListDown = mallGoods.stream()
                .filter(mallGood -> mallGood.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListDown)){
            MallException.fail(goodsListDown.get(0).getGoodsName() + "已下架， 无法生成订单");
        }

        // 判断商品库存
        Map<Long, MallGoods> goodsMap = mallGoods.stream().collect(Collectors.toMap(MallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        for (MallCartItemVO mallCartItemVO : mallCartItemVOS) {
            if (!goodsMap.containsKey(mallCartItemVO.getGoodsId())) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            if (mallCartItemVO.getGoodsCount() > goodsMap.get(mallCartItemVO.getGoodsId()).getStockNum()){
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }

        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(mallGoods)){
            if (mallCartMapper.deleteBatchByPrimaryKey(itemIdList) > 0){
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(mallCartItemVOS, StockNumDTO.class);
                for (StockNumDTO stockNumDTO : stockNumDTOS){
                    if (mallGoodsMapper.deductStockNum(stockNumDTO) < 1){
                        MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                    }
                }

                // 生成订单号
                String orderNumber = NumberUtil.genOrderNo();

                MallOrder order = new MallOrder();
                order.setOrderNo(orderNumber);
                String extroInfo = "";
                order.setExtraInfo(extroInfo);
                order.setUserId(mallUser.getUserId());
                int priceTotal = 0;
                for (MallCartItemVO mallCartItemVO : mallCartItemVOS) {
                    priceTotal += mallCartItemVO.getGoodsCount() * mallCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1){
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                order.setTotalPrice(priceTotal);
                // 生成订单啊 保存(主键回填)
                if (mallOrderMapper.insertSelective(order) > 0){
                    // 对订单收货地址进行设置 1 orderId ->1 order address
                    MallOrderAddress mallOrderAddress = new MallOrderAddress();
                    BeanUtil.copyProperties(mallUserAddress, mallOrderAddress);
                    mallOrderAddress.setOrderId(order.getOrderId());

                    List<MallOrderItem> mallOrderItems = new ArrayList<>();
                    for (MallCartItemVO mallCartItemVO : mallCartItemVOS) {
                        MallOrderItem mallOrderItem = new MallOrderItem();
                        BeanUtil.copyProperties(mallCartItemVO, mallOrderItem);
                        mallOrderItem.setOrderId(order.getOrderId());
                        mallOrderItems.add(mallOrderItem);
                    }
                    if (mallOrderItemMapper.insertBatch(mallOrderItems) > 0 && mallOrderAddressMapper.insertSelective(mallOrderAddress)>0){
                        return orderNumber;
                    }
                }
            }
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());

        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        MallOrder mallOrder = mallOrderMapper.selectMallOrderByOrderNo(orderNo);

        if (mallOrder == null){
            return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
        }
        if (mallOrder.getOrderStatus().intValue() != MallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
            return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
        }
        mallOrder.setPayType((byte) payType);
        mallOrder.setOrderStatus((byte) MallOrderStatusEnum.ORDER_PAID.getOrderStatus());
        mallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
        mallOrder.setPayTime(new Date());
        mallOrder.setUpdateTime(new Date());

        if (mallOrderMapper.updateBySelective(mallOrder)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }


        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public PageResult getOrderListPage(PageQueryUtil pageQueryUtil) {
        List<MallOrder> mallOrders = mallOrderMapper.getOrderListPage(pageQueryUtil);
        int total = mallOrderMapper.getTotalList(pageQueryUtil);

        List<MallOrderListVO> mallOrderListVOS = new ArrayList<>();
        mallOrderListVOS = BeanUtil.copyList(mallOrders, MallOrderListVO.class);
        for (MallOrderListVO mallOrderListVO : mallOrderListVOS){
            // 将状态转为string
            mallOrderListVO.setOrderStatusString(MallOrderStatusEnum.getMallOrderStatusEnumByStatus(mallOrderListVO.getOrderStatus()).getName());
        }
        //先获取所有的订单id
        List<Long> orderIds = mallOrders.stream().map(MallOrder::getOrderId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderIds)){
            // 根据订单id查询所有的订单item
            List<MallOrderItem> mallOrderItems = mallOrderItemMapper.selectByOrderIds(orderIds);
            // 根据订单id分组
            Map<Long, List<MallOrderItem>> mallOrderItemMap = mallOrderItems.stream().collect(Collectors.groupingBy(MallOrderItem::getOrderId));
            // 对于每一个订单封装，将订单itemVOS放入
            for (MallOrderListVO mallOrderListVO : mallOrderListVOS){
                if (mallOrderItemMap.containsKey(mallOrderListVO.getOrderId())){
                    List<MallOrderItem> temp = mallOrderItemMap.get(mallOrderListVO.getOrderId());
                    List<MallOrderItemVO> mallOrderItemVOS = BeanUtil.copyList(temp, MallOrderItemVO.class);

                    mallOrderListVO.setMallOrderItemVOS(mallOrderItemVOS);
                }
            }
        }

        PageResult pageResult = new PageResult(mallOrderListVOS, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());

        return pageResult;
    }

    @Override
    public MallOrderDetailVO getOrderDetailByOrderNoAndUserId(String orderNo, Long userId) {
        MallOrderDetailVO mallOrderDetailVO = new MallOrderDetailVO();
        System.out.println("orderNo" + orderNo);
        MallOrder mallOrder = mallOrderMapper.selectMallOrderByOrderNoAnduserId(orderNo, userId);
        if (mallOrder == null){
            MallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        //获取订单项列表
        List<MallOrderItem> mallOrderItems = mallOrderItemMapper.selectItemsByOrderId(mallOrder.getOrderId());
        if (CollectionUtils.isEmpty(mallOrderItems)){
            MallException.fail(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
        }

        List<MallOrderItemVO> mallOrderItemVOS = BeanUtil.copyList(mallOrderItems, MallOrderItemVO.class);

        BeanUtil.copyProperties(mallOrder, mallOrderDetailVO);
        mallOrderDetailVO.setOrderStatusString(MallOrderStatusEnum.getMallOrderStatusEnumByStatus(mallOrder.getOrderStatus()).getName());
        mallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(mallOrder.getPayType()).getName());
        mallOrderDetailVO.setMallOrderItemVOS(mallOrderItemVOS);
        return mallOrderDetailVO;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        MallOrder mallOrder = mallOrderMapper.selectMallOrderByOrderNoAnduserId(orderNo, userId);
        if (mallOrder == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (mallOrder.getOrderStatus().intValue() == MallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
            || mallOrder.getOrderStatus().intValue() == MallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
            || mallOrder.getOrderStatus().intValue() == MallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
            || mallOrder.getOrderStatus().intValue() == MallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()){
            return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
        }

        mallOrder.setOrderStatus((byte) MallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus());
        mallOrder.setUpdateTime(new Date());
        if (mallOrderMapper.updateBySelective(mallOrder) > 0 && recoverStockNum(Collections.singletonList(mallOrder.getOrderId()))){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return  ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        MallOrder mallOrder = mallOrderMapper.selectMallOrderByOrderNoAnduserId(orderNo, userId);
        if (mallOrder == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        if (mallOrder.getOrderStatus() != MallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()){
            return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
        }

        mallOrder.setOrderStatus((byte) MallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
        mallOrder.setUpdateTime(new Date());

        if (mallOrderMapper.updateBySelective(mallOrder) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String saveSeckillOrder(Long seckillSuccessId, Long userId) {
        System.out.println(userId);
        MallSeckillSuccess mallSeckillSuccess = mallSeckillSuccessMapper.findByUserIdAndSeckillSuccessId(userId, seckillSuccessId);
        if (mallSeckillSuccess == null){
            MallException.fail("用户不一致");
        }
        MallSeckill mallSeckill = mallSecKillMapper.findSeckillByPrimaryKey(mallSeckillSuccess.getSeckillId());
        // 保存订单
        MallGoods mallGood = mallGoodsMapper.getMallGoodById(mallSeckill.getGoodsId());
        String orderNo = NumberUtil.genOrderNo();

        MallOrder mallOrder = new MallOrder();
        mallOrder.setOrderNo(orderNo);
        mallOrder.setUserId(userId);
        mallOrder.setTotalPrice(mallSeckill.getSeckillPrice());
        mallOrder.setPayStatus((byte) PayStatusEnum.PAY_ING.getPayStatus());
//        mallOrder.setPayTime(new Date());
        mallOrder.setPayType((byte) PayTypeEnum.NOT_PAY.getPayType());
        mallOrder.setOrderStatus((byte) MallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus());
        String extra_info = "秒杀商品";
        mallOrder.setExtraInfo(extra_info);
        if (mallOrderMapper.insertSelective(mallOrder) < 1){
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }

        MallOrderAddress mallOrderAddress = new MallOrderAddress();
        mallOrderAddress.setOrderId(mallOrder.getOrderId());
        // 找到默认地址
        MallUserAddress mallUserAddress = mallManageUserAddressMapper.selectDefaultAddress(userId);
        BeanUtil.copyProperties(mallUserAddress, mallOrderAddress);
        if (mallOrderAddressMapper.insertSelective(mallOrderAddress) < 1){
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }

        MallOrderItem mallOrderItem = new MallOrderItem();
        mallOrderItem.setOrderId(mallOrder.getOrderId());
        mallOrderItem.setGoodsId(mallGood.getGoodsId());
        mallOrderItem.setGoodsName(mallGood.getGoodsName());
        mallOrderItem.setGoodsCoverImg(mallGood.getGoodsCoverImg());
        mallOrderItem.setSellingPrice(mallSeckill.getSeckillPrice());
        mallOrderItem.setGoodsCount(1);
        if (mallOrderItemMapper.insertSelective(mallOrderItem) < 1){
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }

        return orderNo;
    }

    public Boolean recoverStockNum(List<Long> orderIds) {
        //查询对应的订单项
        List<MallOrderItem> newBeeMallOrderItems = mallOrderItemMapper.selectByOrderIds(orderIds);
        //获取对应的商品id和商品数量并赋值到StockNumDTO对象中
        List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(newBeeMallOrderItems, StockNumDTO.class);
        //执行恢复库存的操作
        for (StockNumDTO stockNumDTO : stockNumDTOS) {
            int updateStockNumResult = mallGoodsMapper.recoverStockNum(stockNumDTO);
            if (updateStockNumResult < 1) {
                MallException.fail(ServiceResultEnum.CLOSE_ORDER_ERROR.getResult());
                return false;
            }
        }
        return true;

    }
}
