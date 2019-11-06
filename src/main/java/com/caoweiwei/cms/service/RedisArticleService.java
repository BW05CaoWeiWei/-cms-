package com.caoweiwei.cms.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import com.caoweiwei.cms.entity.Article;
/** 
* @author 作者:曹伟伟
* @version 创建时间：2019年10月30日 上午9:52:09
* 类功能说明 
*/
@Service
public class RedisArticleService implements RedisArticle{
	
	@Resource
	private RedisTemplate<String, Article> redisTemplate;
	
	@Resource
	private KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void save(List<Article> articles) {
		// TODO Auto-generated method stub
		ListOperations<String, Article> opsForList = redisTemplate.opsForList();
		
		opsForList.rightPushAll("articles", articles);
		
		System.out.println("存储完毕");
		
		kafkaTemplate.sendDefault("articles");
	}
	
//	public void save(List<Article> articles) {
//		ListOperations<String, Article> opsForList = redisTemplate.opsForList();
//		
//		opsForList.rightPushAll("articles", articles);
//		
//		System.out.println("存储完毕");
//		
//		kafkaTemplate.sendDefault("articles");
//		
//	}
	
}
