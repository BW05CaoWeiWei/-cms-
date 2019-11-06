package com.caoweiwei.cms;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/** 
* @author 作者:曹伟伟
* @version 创建时间：2019年10月30日 上午11:21:47
* 类功能说明 
*/
public class KafkaConsumer {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:spring-beans.xml","classpath:spring-kafka-consumer.xml");
	}
}
