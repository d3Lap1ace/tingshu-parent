package com.impower.tingshu.user.service;

import com.impower.tingshu.model.user.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.impower.tingshu.vo.user.UserInfoVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {

    /**
     * 微信登录
     *
     * @param code 小程序wx.login()获取调用微信服务端临时票据（有效次数一次，有效时间5分钟）
     * @return {"token":"用户令牌"}
     */
    Map<String, String> wxLogin(String code);

    /**
     * 获取当前用户基本信息
     * @param userId
     * @return
     */
    UserInfoVo getUserInfo(Long userId);

    /**
     * 更新用户信息
     * @param userId
     * @param userInfoVo
     */
    void updateUser(Long userId, UserInfoVo userInfoVo);



}
