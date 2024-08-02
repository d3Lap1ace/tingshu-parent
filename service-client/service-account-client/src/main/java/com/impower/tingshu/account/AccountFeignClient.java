package com.impower.tingshu.account;

import com.impower.tingshu.account.impl.AccountDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * <p>
 * 账号模块远程调用API接口
 * </p>
 *
 * @author atguigu
 */
@FeignClient(value = "service-account", fallback = AccountDegradeFeignClient.class)
public interface AccountFeignClient {

}
