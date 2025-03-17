package com.cs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableRabbit
@SpringBootApplication
@MapperScan("com.cs.mapper")
public class MallBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallBackendApplication.class, args);
	}

	@Bean
	public MessageConverter messageConverter() {
		Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
		jsonMessageConverter.setCreateMessageIds(true);
		return jsonMessageConverter;
	}

}
