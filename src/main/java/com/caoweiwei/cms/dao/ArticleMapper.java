package  com.caoweiwei.cms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.caoweiwei.cms.entity.Article;

public interface ArticleMapper {


	List<Article> list(@Param("channelId") Integer channelId,
			@Param("catId") Integer cid);

	Article findById(Integer articleId);
	
	int add(Article article);
	
	int insert(Article article);
	
	
	List<Article> listByUser(@Param("userId") Integer userId);

	// 获取审核的文章列表
	List<Article> checkList(Integer status);

	/**
	 * 修改文章的状态
	 * @param id
	 * @param status
	 * @return
	 */
	@Update("update cms_article set status=#{status} , updated=now() where id=#{id}")
	int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

	/**
	 * 获取热门文章
	 * @return
	 */
	List<Article> hotList(@Param("title") String title);

	
	/** 
	 * 获取最新文章
	 * @return
	 */
	List<Article> lastArticles();
	
	
	/**
	 * 设置热门
	 * @param id
	 * @param status
	 * @return
	 */
	
	
	/**
	 * 修改文章的状态
	 * @param id
	 * @param status
	 * @return
	 */
	@Update("update cms_article set hot=#{status} , updated=now() where id=#{id}")
	int updateHot(@Param("id") Integer id, @Param("status") Integer status);	
	
	@Update("update cms_article set title=#{title},content=#{content1},channel_id=#{channelId},category_id=#{categoryId} where id=#{id}")
	int updatea(@Param("id")Integer id,@Param("title") String title,@Param("categoryId") Integer categoryId, @Param("channelId")Integer channelId,@Param("content1") String content1);

	@Update("update cms_article set commentCnt = commentCnt + 1 where id=#{value}")
	void increaseCommentCnt(Integer articleId);

	
	Article getAdj(@Param("cid") Integer channelId, @Param("aid")Integer aId, @Param("flag") int flag);

	/**
	 * 修改数据库中的点击量
	 * @param id
	 * @param hits
	 */
	@Update("update cms_article set hits=#{hits} , updated=now() where id=#{id}")
	void updateHits(@Param("id") Integer id, @Param("hits") Integer hits);

	List<Article> queryArticleAll();
	
}
