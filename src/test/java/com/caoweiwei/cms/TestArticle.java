package com.caoweiwei.cms;

import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caoweiwei.cms.entity.Article;
import com.caoweiwei.cms.service.ArticleService;
import com.caoweiwei.common.utils.FileUtil;

/**
 * @author 刘吉飞
 *
 * 2019年9月23日
 */
public class TestArticle  extends TestBase{
	
	@Autowired
	ArticleService arServie;
	
	// D:\高级\复习\src\上传下载
	
	@Test
	public void TestImp(){
		
		int channelId[]={1,2,3,4,5,6,7,8};
		
		List<String> fileList = FileUtil.getFileList("C:\\Users\\zhuzg\\Desktop\\img");
		Random random = new Random();
		for (String string : fileList) {
			
			try {
				Article article = new Article();
				String content;
				content = FileUtil.readFile(string);
				article.setContent(content);
				article.setTitle(string.substring(string.lastIndexOf('\\')+1 , string.lastIndexOf('.')));
				article.setHits(10 + random.nextInt(90));//
				article.setHot(random.nextInt(2));
				article.setUserId(37);
				article.setStatus(1);// 审核通过
				//article.setChannelId(channelId[random.nextInt(7)]);
				article.setChannelId(2);
				article.setCategoryId(4);
				arServie.add(article);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("string :" + string);
				e.printStackTrace();
			}
			
			
		}
	}
	

}
