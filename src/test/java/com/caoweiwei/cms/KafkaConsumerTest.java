package com.caoweiwei.cms;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class KafkaConsumerTest {

	public static void main(String[] args) {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:spring-kafka-consumer.xml","classpath:spring-beans.xml");

	}
}
