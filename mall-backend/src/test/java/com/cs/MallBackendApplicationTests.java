package com.cs;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class MallBackendApplicationTests {

//	@Test
//	void contextLoads() {
//	}
//
//	@Autowired
//	private RabbitTemplate rabbitTemplate;
//
//	@Test
//	void convertMessage(){
//		Long orderId =  1L;
//		rabbitTemplate.convertAndSend("delay.direct", "delay", orderId, new MessagePostProcessor() {
//
//			@Override
//			public Message postProcessMessage(Message message) throws AmqpException {
//				message.getMessageProperties().setDelayLong(10000L);
//				return message;
//			}
//		});
//		System.out.println("发送成功!");
//	}

}
