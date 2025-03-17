package com.cs.listener;

import com.cs.common.*;
import com.cs.mapper.*;
import com.cs.pojo.*;
import com.cs.service.MallOrderService;
import com.cs.util.BeanUtil;
import com.cs.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class OrderListener {
    @Autowired
    private MallOrderMapper mallOrderMapper;
    @Autowired
    private MallManageUserAddressMapper mallManageUserAddressMapper;
    @Autowired
    private MallOrderAddressMapper mallOrderAddressMapper;
    @Autowired
    private MallOrderItemMapper mallOrderItemMapper;
    @Autowired
    private MallSecKillMapper mallSecKillMapper;
    @Autowired
    private MallGoodsMapper mallGoodsMapper;
    @Autowired
    private MallOrderService mallOrderService;

    private static final Logger logger = LoggerFactory.getLogger(OrderListener.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = "createOrder.queue"),
            exchange = @Exchange(name = "seckill.direct", type = ExchangeTypes.DIRECT),
            key = "createOrder",
            arguments = @Argument(name = "x-queue-mode", value = "lazy")
    ))
    public void createSeckillOrder(Map<String, Long> createOrderMap){
        if (createOrderMap == null){
            logger.error("消费者获取订单信息失败");
            return;
        }
        Long seckillId = createOrderMap.get("seckillId");
        Long userId = createOrderMap.get("userId");
        MallSeckill mallSeckill = mallSecKillMapper.findSeckillByPrimaryKey(seckillId);
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
        logger.info("创建成功");

        rabbitTemplate.convertAndSend("delay.direct", "delay", mallOrder.getOrderId(), new MessagePostProcessor() {

            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setDelayLong(30 * 60 * 1000L);
                return message;
            }
        });
        logger.info("设置30分钟未支付 关闭订单");
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = "delay.queue", durable = "true"),
                                exchange = @Exchange(name = "delay.direct", delayed = "true"),
                                key = "delay",
                                arguments = @Argument(name = "x-queue-mode", value = "lazy")))
    public void cancelOrderForOutTime(Long orderId){
        Long[] orderIdArray = new Long[]{orderId};
        String result = mallOrderService.closeOrder(orderIdArray);
        logger.info("订单关闭情况：{}", result);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = "test.queue1"),
            exchange = @Exchange(name = "amq.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "blue"},
            arguments = @Argument(name = "x-queue-mode", value = "lazy")
    ))
    public void listenQueue4(String msg){
        System.out.println(msg);
    }
}
