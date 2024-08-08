package com.impower.tingshu.account.service;

import com.impower.tingshu.model.account.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

public interface UserAccountService extends IService<UserAccount> {


    /**
     * 初始化账户记录；新增账户变动日志
     * @param mapData
     */
    void saveUserAccount(Map mapData);


    /**
     * 保存账户变动日志
     * @param userId 用户ID
     * @param title 内容
     * @param tradeType 交易类型 1201-充值 1202-锁定 1203-解锁 1204-消费
     * @param amount 金额
     * @param orderNo 订单编号
     */
    void saveUserAccountDetail(Long userId, String title, String tradeType, BigDecimal amount, String orderNo);
}
