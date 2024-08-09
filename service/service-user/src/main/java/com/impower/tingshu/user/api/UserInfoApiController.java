package com.impower.tingshu.user.api;

import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.model.user.UserInfo;
import com.impower.tingshu.user.service.UserInfoService;
import com.impower.tingshu.user.service.UserPaidTrackService;
import com.impower.tingshu.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理接口")
@RestController
@RequestMapping("api/user")
@SuppressWarnings({"all"})
public class UserInfoApiController {

	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private UserPaidTrackService userPaidTrackService;

	@Operation(summary = "根据用户ID查询用户信息")
	@GetMapping("/userInfo/getUserInfoVo/{userId}")
	public Result<UserInfoVo> getUserInfoVo(@PathVariable("userId") Long userId) {
		UserInfoVo userInfoVo = userInfoService.getUserInfo(userId);
		return Result.ok(userInfoVo);
	}


	@Operation(summary = "获取用户声音列表付费情况")
	@PostMapping("/userInfo/userIsPaidTrack/{userId}/{albumId}")
	public Result userIsPaidTrack(@PathVariable("userId") Long userId, @PathVariable("albumId") Long albumId) {
		int isPaid = userPaidTrackService.getuserIsPaidTrack(userId,albumId);
		return Result.ok(isPaid);
	}


	@Operation(summary = "判断用户是否购买过指定专辑")
	@GetMapping("/userInfo/isPaidAlbum/{albumId}")
	public Result isPaidAlbum(@PathVariable("albumId") Long albumId) {
		return Result.ok();
	}

}

