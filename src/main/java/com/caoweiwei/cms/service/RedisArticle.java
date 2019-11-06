package com.caoweiwei.cms.service;
/** 
* @author 作者:曹伟伟
* @version 创建时间：2019年10月30日 下午12:51:11
* 类功能说明 
*/

import java.util.List;


import com.caoweiwei.cms.entity.Article;

public interface RedisArticle {
	
	void save(List<Article> articles);
	
	
	
}
