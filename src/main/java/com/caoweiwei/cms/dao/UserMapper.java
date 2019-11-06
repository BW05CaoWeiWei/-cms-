package com.caoweiwei.cms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.caoweiwei.cms.entity.User;

@Mapper
public interface UserMapper {
	
	int add(User user);
	
	
	int update(User user);
	
	/**
	 *  根据用户名查找  用于登录的时候和  注册用户名称唯一性校验
	 * @param userName
	 * @return
	 */
	@Select("select * from cms_user where username = #{value} limit 1")
	@ResultType(User.class)
	User findByName(String userName);
	
	/**
	 *  获取用户的列表
	 * @param user
	 * @return
	 */
	List<User> select(User user);


	@Select("select * from cms_user where id=#{value}")
	@ResultType(User.class)
	User findById(Integer id);

	
	@Select("select id,username,password,nickname,birthday,gender,"
			+ "locked,create_time as createTime,update_time as updateTime,url,"
			+ "score,role from cms_user "
			+ " where username like concat('%',#{name},'%') "
			+ " order by createTime desc ")
	@ResultType(User.class)
	List<User> queryList(@Param("name") String name);


	/**
	 * 修改用户的锁定状态
	 * @param userId
	 * @param locked
	 * @return
	 */
	@Update("update cms_user set locked=#{locked},update_time=now() WHERE id=#{userId}")
	int updateLocked(@Param("userId") Integer userId, @Param("locked") Integer locked);
	
	
	
	
	
}
