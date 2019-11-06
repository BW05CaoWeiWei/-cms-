package com.caoweiwei.cms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.caoweiwei.cms.entity.Comment;

/**
 * @author 曹伟伟
 *
 * 2019年9月21日
 */
@Mapper      
public interface CommentMapper {
	
	
	@Insert("INSERT INTO cms_comment (articleId,userId,content,created)"
			+ " values(#{articleId},#{userId},#{content},now()) ")
	int add(Comment commnet);

	@Delete("delete from cms_comment where id=#{cid} and userId=#{userId}")
	int delete(@Param("userId") Integer userId,@Param("cid")  Integer cid);

	@Select("SELECT c.* , u.username as userName "
			+ " from cms_comment c "
			+ " LEFT JOIN cms_user u ON u.id=c.userId "
			+ " where c.articleId=#{value}"
			+ " ORDER BY c.created desc ")
	List<Comment> listByArticle(Integer articleId);

	@Select("SELECT c.* , a.title as articleTitle from cms_comment c "
			+ " LEFT JOIN cms_article a ON  c.articleId = a.id "
			+ " where c.userId=#{value} ")
	List<Comment> listByUser(Integer userId);
	
}
