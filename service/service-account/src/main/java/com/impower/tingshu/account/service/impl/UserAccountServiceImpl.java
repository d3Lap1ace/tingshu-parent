package com.impower.tingshu.account.service.impl;

import com.impower.tingshu.account.mapper.UserAccountDetailMapper;
import com.impower.tingshu.account.mapper.UserAccountMapper;
import com.impower.tingshu.account.service.UserAccountService;
import com.impower.tingshu.common.constant.SystemConstant;
import com.impower.tingshu.model.account.UserAccount;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.impower.tingshu.model.account.UserAccountDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

	@Autowired
	private UserAccountMapper userAccountMapper;

	@Autowired
	private UserAccountDetailMapper userAccountDetailMapper;

	/**
	 * 初始化账户记录；新增账户变动日志
	 * @param mapData {"userId",1,"title":"","amount":10,"orderNo":"cz001"}
	 */
	@Override
	public void saveUserAccount(Map mapData) {
		//1.获取从MQ中获取到参数
		Long userId = (Long) mapData.get("userId");
		String title = (String) mapData.get("title");
		BigDecimal amount = (BigDecimal) mapData.get("amount");
		String orderNo = (String) mapData.get("orderNo");

		//2.保存账户记录
		UserAccount userAccount = new UserAccount();
		userAccount.setUserId(userId);
		userAccount.setTotalAmount(amount);
		userAccount.setAvailableAmount(amount);
		userAccount.setTotalIncomeAmount(amount);
		userAccountMapper.insert(userAccount);

		//3.新增账户变动日志
		this.saveUserAccountDetail(userId,title, SystemConstant.ACCOUNT_TRADE_TYPE_DEPOSIT,amount,orderNo);
	}

	/**
	 * 保存账户变动日志
	 * @param userId 用户ID
	 * @param titile 内容
	 * @param tradeType 交易类型 1201-充值 1202-锁定 1203-解锁 1204-消费
	 * @param amount 金额
	 * @param orderNo 订单编号
	 */
	@Override
	public void saveUserAccountDetail(Long userId, String title, String tradeType, BigDecimal amount, String orderNo) {
		UserAccountDetail userAccountDetail = new UserAccountDetail();
		userAccountDetail.setUserId(userId);
		userAccountDetail.setTitle(title);
		userAccountDetail.setTradeType(tradeType);
		userAccountDetail.setAmount(amount);
		userAccountDetail.setOrderNo(orderNo);
		userAccountDetailMapper.insert(userAccountDetail);
	}
}
