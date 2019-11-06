package com.caoweiwei.cms.service;

import java.util.List;

import com.caoweiwei.cms.entity.Category;

public interface CategoryService {

	List<Category> getCategoryByChId(Integer cid);

}
