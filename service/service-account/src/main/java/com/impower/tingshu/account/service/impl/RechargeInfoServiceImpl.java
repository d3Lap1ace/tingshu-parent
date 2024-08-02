package com.impower.tingshu.account.service.impl;

import com.impower.tingshu.account.mapper.RechargeInfoMapper;
import com.impower.tingshu.account.service.RechargeInfoService;
import com.impower.tingshu.model.account.RechargeInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"all"})
public class RechargeInfoServiceImpl extends ServiceImpl<RechargeInfoMapper, RechargeInfo> implements RechargeInfoService {

	@Autowired
	private RechargeInfoMapper rechargeInfoMapper;

}
