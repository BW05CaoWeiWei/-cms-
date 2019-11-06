package com.caoweiwei.cms.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caoweiwei.cms.entity.Article;
import com.caoweiwei.cms.entity.Channel;
import com.caoweiwei.cms.entity.Link;
import com.caoweiwei.cms.service.ArticleService;
import com.caoweiwei.cms.service.ChannelService;
import com.caoweiwei.cms.utils.ESUtils;
import com.caoweiwei.cms.utils.PageUtil;
import com.github.pagehelper.PageInfo;

@Controller
public class IndexController {
	
	private Logger log = Logger.getLogger(IndexController.class);
	
	@Autowired
	ChannelService cService;
	
	@Autowired
	ArticleService articleService ;
	
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	
	@RequestMapping(value= {"/index","/",""},method=RequestMethod.GET)
	public  String index(HttpServletRequest request,
			 @RequestParam( value="pageSize",defaultValue = "5") Integer pageSize,
			 @RequestParam(value="page",defaultValue = "1") Integer pageNum,
			 @RequestParam(defaultValue = "") String key 
			) {
		
		log.info("this is log test");
		
		List<Channel> channels = cService.getChannels();
		request.setAttribute("channels", channels);
		
		//获取热门
		PageInfo<Article> arPage = null;
		if(key == null || "".equals(key.trim())) {
			arPage = articleService.listhots(key,pageNum, pageSize);
		}else{
			//如果有查询条件，则从elasticsearch中查询数据
			AggregatedPage<Article> pageList = (AggregatedPage<Article>) ESUtils.selectObjects(elasticsearchTemplate, Article.class, pageNum - 1, pageSize,
					"id", new String[] {"title"}, key);
			
			//获取查询到的结果
			List<Article> list = pageList.getContent();
			
			//将数据封装到分页对象
			arPage = new PageInfo<Article>(list);
			
			//设置总页数
			arPage.setPages(pageList.getTotalPages());
			
			//设置当前页
			arPage.setPageNum(pageNum);
		}
		
		
		request.setAttribute("pageInfo", arPage);
		
		//获取最新
		List<Article> lastArticles = articleService.last();
		request.setAttribute("lasts", lastArticles);
		
		
		//获取点击量
//		List<Article> hitsArticles = articleService.hit();
//		request.setAttribute("hits", hitsArticles);
		//获取评论数
//		List<Article> lastArticles = articleService.last();
//		request.setAttribute("lasts", lastArticles);
		
		
		//友情链接
		List<Link> links =  new ArrayList<Link>();
		links.add(new Link("http://www.bwie.net","八维好厉害"));
		links.add(new Link("http://www.bwie.org","八维真牛"));
		links.add(new Link("http://www.bwie.com","八维顶呱呱"));
		request.setAttribute("links", links);
		
		String pageString = PageUtil.page(arPage.getPageNum(), arPage.getPages(), "/article/hots?key="+key, arPage.getPageSize());
		request.setAttribute("pageStr", pageString);
		return "index/index";
	}
	
}
