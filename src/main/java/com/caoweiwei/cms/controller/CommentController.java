package com.caoweiwei.cms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caoweiwei.cms.entity.Comment;
import com.caoweiwei.cms.entity.User;
import com.caoweiwei.cms.service.CommentService;
import com.caoweiwei.cms.utils.ConstantFinal;
import com.caoweiwei.cms.utils.PageUtil;
import com.github.pagehelper.PageInfo;

/**
 * @author 曹伟伟
 *
 * 2019年9月21日
 */
@Controller
@RequestMapping("commnent")
public class CommentController {
	
	@Autowired
	CommentService commentService;
	
	@PostMapping("del")
	@ResponseBody
	public String del(HttpServletRequest request,HttpServletResponse response,
			Integer id){
		response.setContentType("text/html;charset=utf-8");
		User user =(User) request.getSession().getAttribute(ConstantFinal.USER_SESSION_KEY);
		if(user==null){
			return "false";
		               }
		int result = commentService.del(user.getId(),id);
		if(result>0)
			return "success";
		else {
			return "对不起。您发表失败。请稍后再试";
		}
	}
	
	@PostMapping("post")
	@ResponseBody
	public String post(HttpServletRequest request,HttpServletResponse response,Comment comment){
		response.setContentType("text/html;charset=utf-8");
		User user =(User) request.getSession().getAttribute(ConstantFinal.USER_SESSION_KEY);
		if(user==null){
			return "您没有登录，不能发表评论";
		               }
		comment.setUserId(user.getId());
		int result = commentService.post(comment);
		if(result>0)
			return "success";
		else {
			return "对不起。您发表失败。请稍后再试";
		}
	}
	
	@GetMapping("getlist")
	public String getlist(HttpServletRequest request,
			Integer articleId,@RequestParam(defaultValue="1") Integer page,
			@RequestParam(defaultValue="3") Integer pageSize){
		
		PageInfo<Comment> commentsByArticle = commentService.getCommentsByArticle(articleId, page, pageSize);
		System.out.println(commentsByArticle);
		String pagestr = PageUtil.page(commentsByArticle.getPageNum(),
				commentsByArticle.getPages(), "/commnent/getlist?articleId="+articleId, 
				commentsByArticle.getPageSize());
		request.setAttribute("commenPage", commentsByArticle);
		request.setAttribute("pagestr", pagestr);
		return "index/comment/list";
		
	}
	
	@GetMapping("getmylist")
	public String getmylist(HttpServletRequest request,
			@RequestParam(defaultValue="1") Integer page,
			@RequestParam(defaultValue="3") Integer pageSize){
		
		User loingUser = (User)request.getSession().getAttribute(ConstantFinal.USER_SESSION_KEY);
		if(loingUser==null)
			return "redirect:/user/login";
		
		PageInfo<Comment> commentsByArticle = commentService.getCommentsByUser(loingUser.getId(), page, pageSize);
		System.out.println(commentsByArticle);
		String pagestr = PageUtil.page(commentsByArticle.getPageNum(),
				commentsByArticle.getPages(), "/commnent/getmylist", 
				commentsByArticle.getPageSize());
		request.setAttribute("commenPage", commentsByArticle);
		request.setAttribute("pagestr", pagestr);
		return "my/comment/list";
		
	}
	
	
	
	
	


}
