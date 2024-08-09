package com.impower.tingshu.album.service;

import com.alibaba.fastjson.JSONObject;
import com.impower.tingshu.model.album.BaseAttribute;
import com.impower.tingshu.model.album.BaseCategory1;
import com.baomidou.mybatisplus.extension.service.IService;
import com.impower.tingshu.model.album.BaseCategory3;
import com.impower.tingshu.model.album.BaseCategoryView;

import java.util.List;

public interface BaseCategoryService extends IService<BaseCategory1> {


    /**
     * 查询所有1,2,3级分类列表（将子分类封装到categoryChild）
     */
    List<JSONObject> getBaseCategoryList();


    /**
     * 根据1级分类ID查询关联所有标签列表（标签值）
     * @param category1Id
     * @return
     */
    List<BaseAttribute> getAttributesByCategory1Id(Long category1Id);


    /**
     * 根据三级分类ID查询分类视图
     * @param category3Id
     * @return
     */
    BaseCategoryView getCategoryView(Long category3Id);


    /**
     * 根据一级分类Id查询三级分类列表
     * @param category1Id
     * @return
     */
    List<BaseCategory3> getTopBaseCategory3(Long category1Id);


    /**
     * 根据一级分类id获取全部分类信息
     * @param category1Id
     * @return
     */
    JSONObject getBaseCategoryListByCategory1Id(Long category1Id);

    /**
     * 查询所有的一级分类信息
     * @return
     */
    List<BaseCategory1> getfindAllCategory1();

}
