package com.impower.tingshu.album.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.impower.tingshu.album.mapper.*;
import com.impower.tingshu.album.service.BaseCategoryService;
import com.impower.tingshu.model.album.BaseAttribute;
import com.impower.tingshu.model.album.BaseAttributeValue;
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

	@Autowired
	private BaseAttributeMapper baseAttributeMapper;



	/**
	 * 查询所有1,2,3级分类列表（将子分类封装到categoryChild）
	 */
	@Override
	public List<JSONObject> getBaseCategoryList() {
		// 建立空集合 封装分类JSON数据并返回
		List<JSONObject> jsonObject1List = new ArrayList<>();
		// 查询分类视图集合
		List<BaseCategoryView> allCategoryList = baseCategoryViewMapper.selectList(null);
		// 用map储存一级分类视图数据并遍历 封装到分类JSON数据
		allCategoryList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id)).forEach((k, v) -> {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("categoryId", k);
			jsonObject.put("categoryName",v.get(0).getCategory1Name());
			jsonObject1List.add(jsonObject);

			// 建立空集合 用map储存2级分类视图数据并遍历 封装到分类JSON数据
			ArrayList<JSONObject> jsonObject2List = new ArrayList<>();
			v.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id)).forEach((k2, v2) -> {
				JSONObject jsonObject2 = new JSONObject();
				jsonObject2.put("categoryId", k2);
				jsonObject2.put("categoryName",v2.get(0).getCategory2Name());
				jsonObject2List.add(jsonObject2);

				// 建立空集合 用map储存3级分类视图数据并遍历 封装到分类的JSON数据
				ArrayList<JSONObject> jsonObjects3List = new ArrayList<>();
				v2.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id)).forEach((k3, v3) -> {
					JSONObject jsonObject3 = new JSONObject();
					jsonObject3.put("categoryId", k3);
					jsonObject3.put("categoryName",v3.get(0).getCategory3Name());
					jsonObjects3List.add(jsonObject3);
				});
				// 封装3级分类视图数据
				jsonObject2.put("categoryChild", jsonObjects3List);
			});
			// 封装2级分类视图数据
			jsonObject.put("categoryChild", jsonObject2List);
		});
		return jsonObject1List;
	}

	/**
	 * 根据1级分类ID查询关联所有标签列表（标签值）
	 * @param category1Id
	 * @return
	 */
	@Override
	public List<BaseAttribute> getAttributesByCategory1Id(Long category1Id) {
		return baseAttributeMapper.getAttributesByCategory1Id(category1Id);
	}
}
