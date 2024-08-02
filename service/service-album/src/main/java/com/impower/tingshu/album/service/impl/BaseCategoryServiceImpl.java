package com.impower.tingshu.album.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.impower.tingshu.album.mapper.BaseCategory1Mapper;
import com.impower.tingshu.album.mapper.BaseCategory2Mapper;
import com.impower.tingshu.album.mapper.BaseCategory3Mapper;
import com.impower.tingshu.album.mapper.BaseCategoryViewMapper;
import com.impower.tingshu.album.service.BaseCategoryService;
import com.impower.tingshu.model.album.BaseCategory1;
import com.impower.tingshu.model.album.BaseCategoryView;
import com.impower.tingshu.vo.album.AlbumListVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({"all"})
public class BaseCategoryServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategoryService {

	@Autowired
	private BaseCategory1Mapper baseCategory1Mapper;

	@Autowired
	private BaseCategory2Mapper baseCategory2Mapper;

	@Autowired
	private BaseCategory3Mapper baseCategory3Mapper;

	@Autowired
	private BaseCategoryViewMapper baseCategoryViewMapper;


	/**
	 * 查询所有1,2,3级分类列表（将子分类封装到categoryChild）
	 */
	@Override
	public List<JSONObject> getBaseCategoryList() {
		// 建立空集合 封装分类JSON数据并返回
		List<JSONObject> jsonObject1List = new ArrayList<>();
		// 查询所有分类视图集合
		List<BaseCategoryView> allCategoryList = baseCategoryViewMapper.selectList(null);
		// 用map储存一级分类视图数据 并 遍历一级分类视图并封装到分类JSON数据
		allCategoryList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id)).forEach((k, v) -> {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("categoryId", k);
			jsonObject.put("categoryName",v);
			jsonObject1List.add(jsonObject);

			// 建立空集合 用map储存2级分类视图数据 并 遍历2级分类视图并封装到分类JSON数据
			ArrayList<JSONObject> jsonObject2List = new ArrayList<>();
			v.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id)).forEach((k2, v2) -> {
				JSONObject jsonObject2 = new JSONObject();
				jsonObject2.put("categoryId", k2);
				jsonObject2.put("categoryName",v2);
				jsonObject2List.add(jsonObject2);

				// 建立空集合 用map储存3级分类视图数据 并 遍历3级分类视图并封装到分类JSON数据
				ArrayList<JSONObject> jsonObjects3List = new ArrayList<>();
				v2.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id)).forEach((k3, v3) -> {
					JSONObject jsonObject3 = new JSONObject();
					jsonObject3.put("categoryId", k3);
					jsonObject3.put("categoryName",v3);
					jsonObjects3List.add(jsonObject3);
				});
				jsonObject2.put("categoryChild", jsonObjects3List);
			});
			jsonObject.put("categoryChild", jsonObject2List);
		});
		return jsonObject1List;
	}
}
