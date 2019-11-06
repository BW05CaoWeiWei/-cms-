package com.caoweiwei.cms;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.caoweiwei.cms.entity.Article;
import com.caoweiwei.cms.entity.Category;
import com.caoweiwei.cms.entity.Channel;
import com.caoweiwei.cms.service.CategoryService;
import com.caoweiwei.cms.service.ChannelService;
import com.caoweiwei.cms.utils.RandomUtil;
import com.caoweiwei.common.utils.FileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-beans.xml","classpath:spring-kafka-producer.xml"})
public class KafkaProducerTest {

	@Resource
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Resource
	private ChannelService channelService;
	
	@Resource
	private CategoryService categoryService;
	
	
	@Test
	public void sendMsg() throws Exception {
		//读取多个文件
		List<String> fileList = FileUtil.getFileList("D:\\1705DJsoup");
		
		//读取文件内容
		for (String file_path : fileList) {
			
			//获取到文件内容
			String content = FileUtil.readFile(file_path);
			
			//获取到文章的名称   将\和.txt去掉
			String title = file_path.substring(
					file_path.lastIndexOf("\\") + 1,
					file_path.lastIndexOf("."));
			
			//创建实体类对象
			Article article = new Article();
			
			article.setTitle(title);		//名称
			article.setContent(content);	//内容
			
//			(4)在文本内容中截取前140个字作为摘要。（2分）
			if(content.length() <= 140) {
				article.setRemark(content);
			}else {
				article.setRemark(content.substring(0, 140));
			}
			
//			(5)“点击量”和“是否热门”、“频道”字段要使用随机值。（2分）
			Random random = new Random();
			
			//点击量
			article.setHits(random.nextInt(10000));
			
			//是否热门 0 1
			article.setHot(random.nextInt(2));
			
			//频道
			List<Channel> channels = channelService.getChannels();
			
			//获取随机频道
			Channel channel = channels.get(random.nextInt(channels.size()));
			
			article.setChannelId(channel.getId());
			
			//分类
			List<Category> categories = categoryService.getCategoryByChId(channel.getId());
			
			if(categories != null && categories.size() > 0) {
				//根据随机下标，获取对应的数据
				Category category = categories.get(random.nextInt(categories.size()));
				
				article.setCategoryId(category.getId());
			}
			
			
//			(6)文章发布日期从2019年1月1日模拟到今天。（2分）   -2
//			Calendar calendar = Calendar.getInstance();
//			calendar.set(2019, 0, 1);
//			
//			long start_date = calendar.getTimeInMillis();
//			long end_date = new Date().getTime();
//			
//			//时间差
//			long zj_date = end_date-start_date;
//			
//			//获取随机时间数据
//			long random_date = random.nextLong();
//			
//			long abs_date = Math.abs(zj_date - random_date); 
//			
//			Date created = new Date(start_date + abs_date);
//			
//			article.setCreated(created);
//			
//			Date updated = new Date(start_date + abs_date + abs_date);
//			article.setUpdated(updated);
			
			//随机时间
			Date created = RandomUtil.randomDate("2019-01-01", "2019-10-24");
			article.setCreated(created);
			
			//获取8小时以后的时间
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(created);
			
			calendar.add(Calendar.HOUR, 8);
			Date updated = calendar.getTime();
			article.setUpdated(updated );
			
//			(7)其它的字段随便模拟。
			article.setStatus(1);
			
			article.setPicture("2.jpg");
			
			//将article对象装换为json字符串
			Gson gson = new Gson();
			String article_json = gson.toJson(article);
			
			//将json字符串发送的kafka
			kafkaTemplate.sendDefault(article_json);
		}
		System.out.println("发送完毕");
	}
}
