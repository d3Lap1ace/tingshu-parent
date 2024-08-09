package com.impower.tingshu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
	@Autowired
	private UserPaidTrackMapper userPaidTrackMapper;

	@Override
	public int getuserIsPaidTrack(Long userId, Long albumId) {
		LambdaQueryWrapper<UserPaidTrack> userPaidTrackLambdaQueryWrapper = new LambdaQueryWrapper<>();
		userPaidTrackLambdaQueryWrapper.eq(UserPaidTrack::getUserId, userId);
		userPaidTrackLambdaQueryWrapper.eq(UserPaidTrack::getAlbumId, albumId);
		Long count = userPaidTrackMapper.selectCount(userPaidTrackLambdaQueryWrapper);
		if (count > 0){
			return 57;
		}else {
			return 56;
		}
	}
}
