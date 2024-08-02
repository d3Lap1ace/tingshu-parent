package com.impower.tingshu.user.service.impl;

import com.impower.tingshu.model.user.UserPaidTrack;
import com.impower.tingshu.user.mapper.UserPaidAlbumMapper;
import com.impower.tingshu.user.mapper.UserPaidTrackMapper;
import com.impower.tingshu.user.service.UserPaidTrackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"all"})
public class UserPaidTrackServiceImpl extends ServiceImpl<UserPaidTrackMapper, UserPaidTrack> implements UserPaidTrackService {

	@Autowired
	private UserPaidAlbumMapper userPaidAlbumMapper;

}
