package com.impower.tingshu.user.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.impower.tingshu.common.constant.RedisConstant;
import com.impower.tingshu.common.execption.GuiguException;
import com.impower.tingshu.common.rabbit.constant.MqConst;
import com.impower.tingshu.common.rabbit.service.RabbitService;
import com.impower.tingshu.common.result.ResultCodeEnum;
import com.impower.tingshu.model.user.UserInfo;
import com.impower.tingshu.user.mapper.UserInfoMapper;
import com.impower.tingshu.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.impower.tingshu.vo.user.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Autowired
	private WxMaService wxMaService;
    @Autowired
    private RabbitService rabbitService;
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;

	/**
	 * 微信登录
	 *
	 * @param code 小程序wx.login()获取调用微信服务端临时票据（有效次数一次，有效时间5分钟）
	 * @return {"token":"用户令牌"}
	 */

	@Override
	public Map<String, String> wxLogin(String code) {
		try {
			//1.根据小程序提交code再加appid+appsecret获取微信账户唯一标识 wxOpenId
			WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
			String openid = sessionInfo.getOpenid();
			if(StringUtils.isBlank(openid)){
				throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
			}
			//2.根据微信唯一标识查询用户记录
			UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getWxOpenId, openid));
			//3.判断用户记录是否存在(用户是否绑定过该微信账号)
			if(userInfo == null){
				userInfo = new UserInfo();
				userInfo.setWxOpenId(openid);
				userInfo.setNickname("听友"+ IdUtil.getSnowflakeNextId());
				userInfo.setAvatarUrl("https://thirdwx.qlogo.cn/mmopen/vi_32/hFKeRpRQU4wG…axvke5nueicggowdBricR4pspWbp6dwFtLSCWJKyZGJoQ/132");
				userInfo.setIsVip(0);
				userInfo.setCreateTime(new Date());
				userInfoMapper.insert(userInfo);
				// 隐式初始化账户记录
				HashMap<String, Object> map = new HashMap<>();
				map.put("userId", userInfo.getId());
				map.put("title","首次登录赠送体验金");
				map.put("amount", new BigDecimal("10"));
				map.put("orderNo", "zs"+IdUtil.getSnowflakeNextId());
				// 调用发送消息工具类方法发送消息
				rabbitService.sendMessage(MqConst.EXCHANGE_USER,MqConst.ROUTING_USER_REGISTER, map);
			}
			// 4.为用户生成令牌存入Redis 其中Redis中Key=前缀+token Value=用户基本信息UserInfoVo
			// 生成token
			String token = IdUtil.randomUUID();
			// 构建用户登录Key 形式=user:login:token值
			String key = RedisConstant.USER_LOGIN_KEY_PREFIX + token;
			// 将用户登录信息UserInfoVo存入Redis
			UserInfoVo userInfoVo = BeanUtil.copyProperties(userInfo, UserInfoVo.class);
			redisTemplate.opsForValue().set(key,userInfoVo,RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.SECONDS);
			// 封装token到对象响应给前端
			Map<String, String> map = new HashMap<>();
			map.put("token", token);
			return map;
		} catch (WxErrorException e) {
			log.error("[用户服务]微信登录接口异常：{}", e);
			throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
		}
	}

	/**
	 * 获取当前用户基本信息
	 * @param userId
	 * @return
	 */
	@Override
	public UserInfoVo getUserInfo(Long userId) {
		UserInfo userInfo = userInfoMapper.selectById(userId);
		Assert.notNull(userInfo, "用户{}信息不存在", userId);
		return BeanUtil.copyProperties(userInfo, UserInfoVo.class);
	}

	/**
	 * 更新用户信息
	 * @param userId
	 * @param userInfoVo
	 */
	@Override
	public void updateUser(Long userId, UserInfoVo userInfoVo) {
		//只允许修改基本信息
		UserInfo userInfo = new UserInfo();
		userInfo.setId(userId);
		userInfo.setNickname(userInfoVo.getNickname());
		userInfo.setAvatarUrl(userInfoVo.getAvatarUrl());
		userInfoMapper.updateById(userInfo);
	}
}






















