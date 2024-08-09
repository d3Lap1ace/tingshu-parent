package com.impower.tingshu.user.api;

import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.model.user.VipServiceConfig;
import com.impower.tingshu.user.service.VipServiceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "VIP服务配置管理接口")
@RestController
@RequestMapping("api/user")
@SuppressWarnings({"all"})
public class VipServiceConfigApiController {

	@Autowired
	private VipServiceConfigService vipServiceConfigService;

	@Operation(summary = "获取全部VIP会员服务配置信息")
	@GetMapping("/vipServiceConfig/findAll")
	public Result<List<VipServiceConfig>> findAll() {
		List<VipServiceConfig> vipServiceConfig = vipServiceConfigService.getfindAll();
		return Result.ok(vipServiceConfig);
	}

}

