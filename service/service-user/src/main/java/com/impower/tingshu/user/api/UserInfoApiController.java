package com.impower.tingshu.user.api;

import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.model.user.UserInfo;
import com.impower.tingshu.user.service.UserInfoService;
import com.impower.tingshu.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理接口")
@RestController
@RequestMapping("api/user")
@SuppressWarnings({"all"})
public class UserInfoApiController {

	@Autowired
	private UserInfoService userInfoService;

	@Operation(summary = "根据用户ID查询用户信息")
	@GetMapping("/userInfo/getUserInfoVo/{userId}")
	public Result<UserInfoVo> getUserInfoVo(@PathVariable("userId") Long userId) {
		UserInfoVo userInfoVo = userInfoService.getUserInfo(userId);
		return Result.ok(userInfoVo);
	}


	@Operation(summary = "获取用户最近一次播放记录")
	@GetMapping("/userListenProcess/getLatelyTrack")
	public Result getLatelyTrack() {
		return null;
	}
}

