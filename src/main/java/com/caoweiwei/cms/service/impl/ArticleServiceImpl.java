package com.caoweiwei.cms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import com.caoweiwei.cms.dao.ArticleMapper;
import com.caoweiwei.cms.entity.Article;
import com.caoweiwei.cms.service.ArticleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


@Service
public class ArticleServiceImpl implements ArticleService{
	
	@Autowired
	ArticleMapper articleMapper;
	
	@Autowired
	private RedisTemplate<String, Article> redisTemplate;

	@Override
	public int post(Article article) {
		// TODO Auto-generated method stub
		return articleMapper.add(article);
	}

	@Override
	public PageInfo<Article> list(Integer pageNum,Integer channelId, Integer cid) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, 5);
		List<Article> articles =   articleMapper.list(channelId,cid);
		return new PageInfo(articles);
	}

	@Override
	public Article findById(Integer articleId) {
		// TODO Auto-generated method stub
		return articleMapper.findById(articleId);
		
	}

	@Override
	public int update(Article article) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int logicDelete(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int logicDeleteBatch(Integer[] ids) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int add(Article article) {
		// TODO Auto-generated method stub
		return articleMapper.add(article);
	}

	@Override
	public PageInfo<Article> getByUserId(Integer id, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<Article> pageInfo = new PageInfo<Article>(articleMapper.listByUser(id));
		
		return pageInfo;
	}

	@Override
	public PageInfo<Article> checkList(Integer status, int pageNumber, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNumber, pageSize);
		List<Article> articles=  articleMapper.checkList(status);
		
		return new PageInfo<Article>(articles);
	}

	@Override
	public int check(Integer id, Integer status) {
		// TODO Auto-generated method stub
		return articleMapper.updateStatus(id,status);
	}

	/**
	 * 查询热点文章
	 */
	@Override
	public PageInfo<Article> listhots(String title,Integer pageNum, Integer pageSize) {

		
		System.out.println("title   is ============ " + title);
		List<Article> articles= null;
		
		//获取redis操作list集合的对象
		ListOperations<String, Article> opsForList = redisTemplate.opsForList();
		
		//先查询redis中是否存储热点文章，
		if(redisTemplate.hasKey("hots_list")) {
			//如果存储，则直接取出；
			articles = opsForList.range("hots_list",(pageNum - 1) * pageSize, pageNum * pageSize - 1);
//			articles = opsForList.range("hots_list",0 ,-1);
			
		}else {
			//如果没有，则查询数据库，并将数据存储到redis中。
			//查询数据库
			List<Article> hotList = articleMapper.hotList(title);
			
			PageHelper.startPage(pageNum, pageSize);
			articles =  articleMapper.hotList(title);
			
			//存入redis
			opsForList.rightPushAll("hots_list", hotList);
		}
		
		System.out.println(articles);
		PageInfo<Article> pageInfo = new PageInfo<Article>(articles);
		//获取总条数
		int totils = opsForList.size("hots_list").intValue();
		//计算总页数
		int pages = totils / pageSize;
		
		//如果有余数，则总页数+1
		if(totils % pageSize > 0) {
			pages ++;
		}
		
		System.out.println("总页数"+pages);
		//设置总页数
		pageInfo.setPages(pages);
		//设置当前页
		pageInfo.setPageNum(pageNum);
		
		return pageInfo;
	}

	/**
	 * 获取最新文章
	 */
	@Override
	public List<Article> last() {

		List<Article> list = null;
		
		//获取redis操作list集合的对象
		ListOperations<String, Article> opsForList = redisTemplate.opsForList();
		
		//先查询redis中是否存储最新文章
		if(redisTemplate.hasKey("last_list")) {
			//如果存储，则直接取出；
			list = opsForList.range("last_list", 0, -1);
		}else {
			//如果没有，则查询数据库，并将数据存储到redis中。
			list = articleMapper.lastArticles();
			opsForList.rightPushAll("last_list", list);
		}
		return list;
	}

	@Override
	public int setHot(Integer id, Integer status) {
		// TODO Auto-generated method stub
		return articleMapper.updateHot(id,status);
	}

	@Override
	public int updatea(Integer id, String title, Integer categoryId,
			Integer channelId, String content1) {
		return articleMapper.updatea(id, title, categoryId, channelId, content1);
	}

	@Override
	public Article findByLastId(Integer channelId, Integer aId) {
		// TODO Auto-generated method stub
		return articleMapper.getAdj(channelId, aId,2);
	}

	@Override
	public Article findByPreId(Integer channelId, Integer aId) {
		// TODO Auto-generated method stub
		return  articleMapper.getAdj(channelId, aId,1);
	}

	/**
	 * 获取点击量最多的10个文章
	 */
	
	@Override
	public List<Article> hit() {
		
		List<Article> article_list = new ArrayList<Article>();
		
		ZSetOperations<String, Article> opsForZSet = redisTemplate.opsForZSet();
		//获取前10，从大到小，按照分数
//		Set<TypedTuple<Article>> reverseRangeWithScores = opsForZSet.reverseRangeWithScores("article_hits", 0, 9);
//		//遍历数据，存放的list中
//		if(reverseRangeWithScores != null) {
//			for (TypedTuple<Article> typedTuple : reverseRangeWithScores) {
//				Article article = typedTuple.getValue();
//				article_list.add(article);
//			}
//		}
		
		
		Set<Article> reverseRange = opsForZSet.reverseRange("article_hits", 0, -1);
		if(reverseRange != null && reverseRange.size() > 0) {
			for (Article article : reverseRange) {
				article_list.add(article);
			}
		}
		return article_list;
	}

	/**
	 * 访问文章时增加点击量
	 * @param article
	 */
	@Override
	public void addHits(Article article) {
		//存放到数据库中
		articleMapper.updateHits(article.getId(), article.getHits() + 1);
		
		//存放到redis中
//		redisTemplate.hasKey("article_hits");
		
		//获取sortedset 数据类型的操作对象
		ZSetOperations opsForZSet = redisTemplate.opsForZSet();
		opsForZSet.add("article_hits", article, article.getHits() + 1);
	}

	/**
	 * 导入数据时使用
	 */
	@Override
	public void insert(Article article) {
		articleMapper.insert(article);
	}

	@Override
	public List<Article> queryArticleAll() {
		// TODO Auto-generated method stub
		return articleMapper.queryArticleAll();
	}

}
