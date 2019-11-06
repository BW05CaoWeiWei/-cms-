package com.caoweiwei.cms.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 曹伟伟
 *
 * 2019年9月21日
 */
public class Comment implements Serializable {
	
	private static final long serialVersionUID = 5272488365152386382L;
	
	private Integer id;
	private Integer articleId;
	private Integer userId;
	private String userName;
	private String content;
	private Date created;
	
	private String articleTitle;
	
	public String getArticleTitle() {
		return articleTitle;
	}
	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getArticleId() {
		return articleId;
	}
	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	


}
