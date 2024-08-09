package com.impower.tingshu.user.api;

import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.user.service.UserListenProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户声音播放进度管理接口")
@RestController
@RequestMapping("api/user")
@SuppressWarnings({"all"})
public class UserListenProcessApiController {

	@Autowired
	private UserListenProcessService userListenProcessService;


	/**
	 * 获取用户最近一次播放记录
	 * @return
	 */
	@Operation(summary = "获取用户最近一次播放记录")
	@GetMapping("/userListenProcess/getLatelyTrack")
	public Result getLatelyTrack() {
		return null;
	}
}

