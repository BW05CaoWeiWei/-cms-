package com.caoweiwei.cms.zh;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class KafkaConsumer {

	public static void main(String[] args) {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:spring-kafka-consumer.xml","classpath:spring-beans.xml");

	}
}
