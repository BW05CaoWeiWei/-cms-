package com.caoweiwei.cms.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.druid.sql.PagerUtils;
import com.caoweiwei.cms.entity.Article;
import com.caoweiwei.cms.entity.Category;
import com.caoweiwei.cms.entity.Channel;
import com.caoweiwei.cms.entity.User;
import com.caoweiwei.cms.service.ArticleService;
import com.caoweiwei.cms.service.CategoryService;
import com.caoweiwei.cms.service.ChannelService;
import com.caoweiwei.cms.utils.ConstantFinal;
import com.caoweiwei.cms.utils.ESUtils;
import com.caoweiwei.cms.utils.PageUtil;
import com.github.pagehelper.PageInfo;

/**
 * 
 * @author 曹伟伟
 *
 */
@Controller
@RequestMapping("article")
public class ArticleController {
	
	private Logger log = Logger.getLogger(ArticleController.class);
	

	@Autowired
	private ChannelService channelService;

	@Autowired
	private CategoryService catService;

	@Autowired
	ArticleService articleService;
	
	
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private RedisTemplate<String, Article> redisTemplate;
	
	/**
	 * 
	 * @param request
	 * @param cid
	 *            文章的分类Id
	 * @return
	 */
	@RequestMapping("listbyCatId")
	public String getListByCatId(HttpServletRequest request, 
			@RequestParam(defaultValue = "0") Integer channelId,
			@RequestParam(defaultValue = "0") Integer catId,
			@RequestParam(defaultValue = "1",value="page") Integer pageNum) {
		
		
		
		PageInfo<Article> arPage = articleService.list(pageNum, channelId, catId);
		request.setAttribute("pageInfo", arPage);
		
		String pageString = PageUtil.page(arPage.getPageNum(), arPage.getPages(), "/article/listbyCatId?catId="+catId+"&channelId="+channelId, arPage.getPageSize());
		request.setAttribute("pageStr", pageString);
		
		return "index/article/list";
	}
	
	
	@RequestMapping("hots")
	public String hots(HttpServletRequest request, 
			 @RequestParam(defaultValue = "") String key ,
			 @RequestParam( value="pageSize",defaultValue = "5") Integer pageSize,
			 @RequestParam(value="page",defaultValue = "1") Integer pageNum) {
		
//		PageInfo<Article> arPage = articleService.listhots(key,pageNum, pageSize);
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
		
		String pageString = PageUtil.page(arPage.getPageNum(), arPage.getPages(), "/article/hots?key="+key, arPage.getPageSize());
		request.setAttribute("pageStr", pageString);
		
		return "index/article/list";
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("toPublish")
	public String toPublish() {

		return "my/article/publish";
	}

	@RequestMapping("publish")
	@ResponseBody
	public boolean publish(HttpServletRequest request,
			@RequestParam("file") MultipartFile img, Article article) 
					throws IllegalStateException, IOException {

		 CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		    // 设置编码
		    commonsMultipartResolver.setDefaultEncoding("utf-8");
		    
		    
		 // 判断是否有文件上传
		if (commonsMultipartResolver.isMultipart(request) && img != null
				&& img.getOriginalFilename()!=null && !"".equals(img.getOriginalFilename().trim())
			) {
			log.debug("img777  is " + img );
			// 获取原文件的名称
			String oName = img.getOriginalFilename();
			log.debug("oName is " + oName );
			
			// 得到扩展名
			String suffixName = oName.substring(oName.lastIndexOf('.'));
			// 新的文件名称
			String newName = UUID.randomUUID() + suffixName;
			img.transferTo(new File("D:\\pic\\" + newName));//另存
			article.setPicture(newName);//
		}
		User loginUser = (User)request.getSession().getAttribute(ConstantFinal.USER_SESSION_KEY);
		if(loginUser==null)
			return false;
		article.setUserId(loginUser.getId());
		int result = articleService.add(article);
		return result > 0;
		
	
	}

	/**
	 * 获取所有的频道
	 * 
	 * @return
	 */
	@RequestMapping("getAllChn")
	@ResponseBody
	public List<Channel> getAllChn() {
		List<Channel> channels = channelService.getChannels();
		return channels;
	}

	/**
	 * 获取某个频道下的所有的分类
	 * 
	 * @return
	 */
	@RequestMapping("getCatsByChn")
	@ResponseBody
	public List<Category> getCatsByChn(Integer channelId) {
		List<Category> cats = catService.getCategoryByChId(channelId);
		return cats;
	}

	/**
	 * 
	 * @param request
	 * @param aId
	 *            文章的id
	 * @return
	 */
	@RequestMapping("getDetail")
	public String getDetail(HttpServletRequest request, Integer aId) {

		Article article = articleService.findById(aId);
		System.out.println("article " + article);
		
		//增加点击量
		articleService.addHits(article);
		
		request.setAttribute("article", article);
		
		// 获取上一页 和下一页
		Article  lastArticle  = articleService.findByLastId(article.getChannelId(), aId);
		Article  preArticle  = articleService .findByPreId(article.getChannelId() , aId);
		//<nav aria-label="Page navigation example"><ul class="pagination"><li class="page-item"><a class="page-link" href="javascript:void(0)" data="/article/hots?key=&amp;page=1" aria-label="Previous">  <span aria-hidden="true">«</span></a></li><li class="page-item"><a class="page-link" href="javascript:void(0)" data="/article/hots?key=&amp;page=1">1</a></li><li class="page-item"><a class="page-link" href="javascript:void(0)" data="/article/hots?key=&amp;page=1" aria-label="Next"><span aria-hidden="true">»</span>﷯</a></li></ul></nav>
		
		StringBuilder sb = new StringBuilder("");
		if(preArticle == null) {
			sb.append("已经是第一页");
			
		}else {
			sb.append("<a  href='/article/getDetail?aId="+preArticle.getId()+"'>上一页</a>");
		}
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		
		if(lastArticle == null) {
			sb.append("已经是最后一页");
			
		}else {
			sb.append("<a  href='/article/getDetail?aId="+lastArticle.getId()+"'>下一页</a>");
		} 
		System.out.println("sb is " + sb);
		request.setAttribute("adjStr", sb.toString());
		
		return "index/article/detail";
	}
//	/**
//	 * 
//	 * @param request
//	 * @param aId
//	 *            文章的id
//	 * @return
//	 */
//	@RequestMapping("getDetail")
//	public String getDetail(HttpServletRequest request, Integer aId) {
//		
//		Article article = articleService.findById(aId);
//		System.out.println("article " + article);
//		request.setAttribute("article", article);
//		
//		// 获取上一页 和下一页
//		Article  lastArticle  = articleService.findByLastId(article.getChannelId(), aId);
//		Article  preArticle  = articleService .findByPreId(article.getChannelId() , aId);
//		//<nav aria-label="Page navigation example"><ul class="pagination"><li class="page-item"><a class="page-link" href="javascript:void(0)" data="/article/hots?key=&amp;page=1" aria-label="Previous">  <span aria-hidden="true">«</span></a></li><li class="page-item"><a class="page-link" href="javascript:void(0)" data="/article/hots?key=&amp;page=1">1</a></li><li class="page-item"><a class="page-link" href="javascript:void(0)" data="/article/hots?key=&amp;page=1" aria-label="Next"><span aria-hidden="true">»</span>﷯</a></li></ul></nav>
//		
//		StringBuilder sb = new StringBuilder("");
//		if(preArticle == null) {
//			sb.append("已经是第一页");
//			
//		}else {
//			sb.append("<a  href='/article/getDetail?aId="+preArticle.getId()+"'>上一页</a>");
//		}
//		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//		
//		if(lastArticle == null) {
//			sb.append("已经是最后一页");
//			
//		}else {
//			sb.append("<a  href='/article/getDetail?aId="+lastArticle.getId()+"'>下一页</a>");
//		} 
//		System.out.println("sb is " + sb);
//		request.setAttribute("adjStr", sb.toString());
//		
//		return "index/article/detail";
//	}
	

	public ChannelService getChannelService() {
		return channelService;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}
	
	@GetMapping("listMyArticle")
	public String listMyArticle(HttpServletRequest request,
			@RequestParam(value="page",defaultValue= "1") int pageNum,
			@RequestParam(defaultValue= "5") int pageSize) {
		
		// 获取当前登陆的用户
		User currUser = (User)request.getSession().getAttribute(ConstantFinal.USER_SESSION_KEY);
		if(currUser==null)  return "my/article/list";
		
		PageInfo<Article> articlePage = articleService.getByUserId(currUser.getId(),pageNum,pageSize);
		System.out.println("articlePage is "  + articlePage);
		request.setAttribute("myarticles", articlePage);
		
		String pageStr = PageUtil.page(articlePage.getPageNum(), articlePage.getPages(), "/article/listMyArticle", articlePage.getPageSize());
		
		request.setAttribute("pageStr", pageStr);
		
		return "my/article/list";
	}
	
	/**
	 * 
	 * @param request
	 * @param status  文章的状态  0 待审  1 已经审核通过  2 审核未通过
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@GetMapping("checkList")
	public String checkList(HttpServletRequest request,
			@RequestParam(defaultValue="0")  Integer status,
			@RequestParam(defaultValue="1",value="page") int pageNumber ,
			@RequestParam(defaultValue="5")  int pageSize) {
		
		PageInfo<Article> arPage =  articleService.checkList(status,pageNumber,pageSize);
		request.setAttribute("articles", arPage);
		
		String pageString = PageUtil.page2(arPage.getPageNum(), arPage.getPages(), "/article/checkList", arPage.getPageSize(),status);
		request.setAttribute("pageStr", pageString);
		request.setAttribute("status", status);
		
		return "admin/article/checkList";
		
	}
	
	
	/**
	 * 审核的
	 * @param request
	 * @param id
	 * @return
	 */
	@GetMapping("get")
	public String getCheckDetail(HttpServletRequest request,Integer id) {
		
		Article article = articleService.findById(id);
		request.setAttribute("article", article);
		return "admin/article/detail";
	}
	
	/**
	 *  审核文章  
	 * @param request
	 * @param id  文章的id
	 * @param status  1 通过   2 不通过
	 * @return
	 */
	@PostMapping("pass")
	@ResponseBody
	public boolean pass(HttpServletRequest request,Integer id,Integer status) {
		
		int result = articleService.check(id,status);
		
		//如果审核通过，则将其添加到elasticsearch中
		if(status == 1) {
			IndexQuery query = new IndexQuery();
			query.setId(id.toString());
			query.setObject(articleService.findById(id));
			elasticsearchTemplate.index(query );
			
			//如果要同步最新文章
			//删除redis中对应的键值对，让后访问首页的时候，redis会自动同步mysql中数据
			redisTemplate.delete("last_list");
		}
		
		return result>0;
		
	}
	
	@PostMapping("sethot")
	@ResponseBody
	public boolean sethot(HttpServletRequest request,Integer id,Integer status) {
		
		int result = articleService.setHot(id,status);
		
		//设置热门文章时删除redis中的数据
			if(status == 1) {
				redisTemplate.delete("hots_list");
			}
		return result>0;
		
	}
	
	
	
	@RequestMapping("toUpdate")
	public String toUpdate(HttpServletRequest request,Integer id) {
		Article article = articleService.findById(id);
		request.setAttribute("article", article);
		request.setAttribute("content1", article.getContent());
		return "my/article/update";	
	}

	@RequestMapping("update")
	@ResponseBody
	public boolean Update(Integer id ,String title,String content1,Integer channelId,Integer categoryId) {
		return articleService.updatea(id,title,categoryId,channelId,content1)>0;
	}
}
