package com.caoweiwei.cms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caoweiwei.cms.dao.ChannelMapper;
import com.caoweiwei.cms.entity.Channel;
import com.caoweiwei.cms.service.ChannelService;

@Service
public class ChannelServiceImpl implements ChannelService{

	@Autowired
	ChannelMapper channelMapper;
	
	@Override
	public List<Channel> getChannels() {
		// TODO Auto-generated method stub
		return channelMapper.getChannels();
	}
	

}
