package com.caoweiwei.cms.service;

import com.caoweiwei.cms.entity.User;
import com.caoweiwei.cms.entity.UserVo;
import com.github.pagehelper.PageInfo;

/**
 * 
 * @author 曹伟伟
 *
 */
public interface UserService {
	
	/**
	 * 
	 * @return
	 */
	User login(User user);
	/**
	 * 
	 * @return
	 */
	User register(User user);
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	User findByName(String name);
	
	/**
	 * 
	 * @param uservo
	 * @return
	 */
	User query(UserVo  uservo);
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	int update(User user);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	User findById(Integer id);
	
	/**
	 * 根据用户名模糊查询
	 * @param pageNumber
	 * @param pageSize
	 * @param name
	 * @return
	 */
	PageInfo<User> search(int pageNumber, int pageSize, String name);
	
	/**
	 * 
	 * @param id  用户的id
	 * @param locked 是否锁定  1 表示锁定 0 表示不锁定
	 * @return
	 */
	int updateLocked(Integer id, Integer locked);
	
	
	
}
