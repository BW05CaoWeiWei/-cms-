package com.caoweiwei.cms;
/** 
* @author 作者:曹伟伟
* @version 创建时间：2019年10月25日 上午9:21:39
* 类功能说明 
*/

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.caoweiwei.cms.entity.Article;
import com.caoweiwei.cms.service.ArticleService;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-beans.xml")
public class QueryArticleAllTest {
	
	@Resource
	private ArticleService articleService;
	
	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;
	
	@Test
	public void add() {
		List<Article> list= articleService.queryArticleAll();
		
		for (Article article : list) {
			IndexQuery indexQuery = new IndexQuery();
			indexQuery.setId(article.getId().toString());
			indexQuery.setObject(article);
			elasticsearchTemplate.index(indexQuery);
		}
		System.out.println("存入成功");
	}
}
