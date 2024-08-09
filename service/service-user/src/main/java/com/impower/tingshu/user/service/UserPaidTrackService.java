package com.impower.tingshu.user.service;

import com.impower.tingshu.model.user.UserPaidTrack;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserPaidTrackService extends IService<UserPaidTrack> {

    /**
     * 获取用户声音列表付费情况
     * @param userId
     * @param albumId
     * @return
     */
    int getuserIsPaidTrack(Long userId, Long albumId);
}
