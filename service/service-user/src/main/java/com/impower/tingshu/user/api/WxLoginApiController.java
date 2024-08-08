package com.impower.tingshu.user.api;

import com.impower.tingshu.common.login.ImpowerLogin;
import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.common.util.AuthContextHolder;
import com.impower.tingshu.model.user.UserInfo;
import com.impower.tingshu.user.service.UserInfoService;
import com.impower.tingshu.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "微信授权登录接口")
@RestController
@RequestMapping("/api/user/wxLogin")
@Slf4j
public class WxLoginApiController {

    @Autowired
    private UserInfoService userInfoService;


    /**
     * 微信登录
     *
     * @param code 小程序wx.login()获取调用微信服务端临时票据（有效次数一次，有效时间5分钟）
     * @return {"token":"用户令牌"}
     */
    @Operation(summary = "微信登录")
    @GetMapping("/wxLogin/{code}")
    public Result<Map<String,String>> wxLogin(@PathVariable String code) {
        Map<String,String> map = userInfoService.wxLogin(code);
        return Result.ok(map);
    }

    /**
     * 获取当前用户基本信息
     *
     * @return
     */
    @ImpowerLogin
    @Operation(summary = "获取当前用户基本信息")
    @GetMapping("/getUserInfo")
    public Result<UserInfoVo> getUserInfo() {
        Long userId = AuthContextHolder.getUserId();
        UserInfoVo userInfo = userInfoService.getUserInfo(userId);
        return Result.ok(userInfo);
    }

    /**
     * 更新用户基本信息
     *
     * @param userInfoVo
     * @return
     */
    @ImpowerLogin
    @Operation(summary = "更新用户基本信息")
    @PostMapping("/updateUser")
    public Result updateUser(@RequestBody UserInfoVo userInfoVo) {
        Long userId = AuthContextHolder.getUserId();
        userInfoService.updateUser(userId, userInfoVo);
        return Result.ok();
    }
}
