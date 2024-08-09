package com.impower.tingshu.user.service.impl;

import com.impower.tingshu.model.user.VipServiceConfig;
import com.impower.tingshu.user.mapper.VipServiceConfigMapper;
import com.impower.tingshu.user.service.VipServiceConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings({"all"})
public class VipServiceConfigServiceImpl extends ServiceImpl<VipServiceConfigMapper, VipServiceConfig> implements VipServiceConfigService {

	@Autowired
	private VipServiceConfigMapper vipServiceConfigMapper;


	@Override
	public List<VipServiceConfig> getfindAll() {
		List<VipServiceConfig> list = vipServiceConfigMapper.selectList(null);
		return list;
	}
}
