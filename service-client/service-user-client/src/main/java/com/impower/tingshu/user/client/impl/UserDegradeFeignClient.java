package com.impower.tingshu.user.client.impl;


import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.user.client.UserFeignClient;
import com.impower.tingshu.vo.user.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class UserDegradeFeignClient implements UserFeignClient {

    /**
     * 根据用户ID查询用户/主播基本信息
     * @param userId
     * @return
     */
    @Override
    public Result<UserInfoVo> getUserInfoVo(Long userId) {
        log.error("[用户服务]提供远程调用getUserInfoVo执行服务降级");
        return null;
    }
}
