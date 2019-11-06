package com.caoweiwei.cms;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caoweiwei.cms.entity.Comment;
import com.caoweiwei.cms.service.CommentService;

/**
 * @author 刘吉飞
 *
 * 2019年9月23日
 */
public class TestComment   extends TestBase{
	
	@Autowired
	CommentService commentService;
	
	@Test
	public void testInsert(){
		
		Comment comment = new Comment();
		comment.setArticleId(20);
		comment.setContent("这是对我的单元测试");
		comment.setUserId(37);
		commentService.post(comment);
		
	}

}
