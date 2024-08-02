package com.impower.tingshu.album.service;

import com.alibaba.fastjson.JSONObject;
import com.impower.tingshu.model.album.BaseCategory1;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BaseCategoryService extends IService<BaseCategory1> {


    /**
     * 查询所有1,2,3级分类列表（将子分类封装到categoryChild）
     */
    List<JSONObject> getBaseCategoryList();
}
