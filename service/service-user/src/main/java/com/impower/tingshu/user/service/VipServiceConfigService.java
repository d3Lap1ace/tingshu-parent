package com.impower.tingshu.user.service;

import com.impower.tingshu.model.user.VipServiceConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface VipServiceConfigService extends IService<VipServiceConfig> {


    /**
     * VIP服务配置管理接口
     * @return
     */
    List<VipServiceConfig> getfindAll();

}
