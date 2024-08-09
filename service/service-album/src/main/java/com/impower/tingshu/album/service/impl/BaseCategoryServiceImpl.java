package com.impower.tingshu.album.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.impower.tingshu.album.mapper.*;
import com.impower.tingshu.album.service.BaseCategoryService;
import com.impower.tingshu.common.constant.SystemConstant;
import com.impower.tingshu.common.execption.GuiguException;
import com.impower.tingshu.model.album.*;
import com.impower.tingshu.vo.album.AlbumListVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.v3.core.util.Json;
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

	@Override
	public BaseCategoryView getCategoryView(Long category3Id) {
		return baseCategoryViewMapper.selectById(category3Id);
	}


	/**
	 * 根据一级分类Id查询三级分类列表
	 * @param category1Id
	 * @return
	 */
	@Override
	public List<BaseCategory3> getTopBaseCategory3(Long category1Id) {
		// 根据1级id查找二级
		List<BaseCategory2> category2List = baseCategory2Mapper.selectList(new LambdaQueryWrapper<BaseCategory2>()
				.eq(BaseCategory2::getCategory1Id, category1Id).select(BaseCategory2::getId));
		if(CollectionUtil.isNotEmpty(category2List)){
			// 将basecategory2转为long
			List<Long> category2IdList = category2List.stream().map(BaseCategory2::getId).collect(Collectors.toList());
			// 查询3级 分类ID列表、是否置顶、按照序号进行排序、获取7条分类记录
			List<BaseCategory3> category3List = baseCategory3Mapper.selectList(new LambdaQueryWrapper<BaseCategory3>()
					.in(BaseCategory3::getCategory2Id, category2IdList)
					.eq(BaseCategory3::getIsTop,1)
					.select(BaseCategory3::getId,BaseCategory3::getCreateTime,BaseCategory3::getName,BaseCategory3::getCategory2Id)
					.orderByAsc(BaseCategory3::getOrderNum)
					.last("limit 7"));
			return category3List;
		}
		return null;
	}

	/**
	 * 根据一级分类id获取全部分类信息
	 * @param category1Id
	 * @return
	 */
	@Override
	public JSONObject getBaseCategoryListByCategory1Id(Long category1Id) {
		//1.查询分类视图获取一级分类ID所有视图记录
		List<BaseCategoryView> baseCategoryViewList = baseCategoryViewMapper.selectList(new LambdaQueryWrapper<BaseCategoryView>()
				.eq(BaseCategoryView::getCategory1Id, category1Id));
		if(CollectionUtil.isNotEmpty(baseCategoryViewList)){
			//2.处理1级分类
			BaseCategoryView baseCategoryView = baseCategoryViewList.get(0);
			String category1Name = baseCategoryView.getCategory1Name();
			JSONObject jsonObject1 = new JSONObject();
			jsonObject1.put("categoryId", category1Id);
			jsonObject1.put("categoryName", category1Name);
			//3.处理2级分类
			Map<Long, List<BaseCategoryView>> map2 = baseCategoryViewList
					.stream()
					.collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
			ArrayList<JSONObject> jsonObject2List = new ArrayList<>();
			map2.forEach((k, v) -> {
				JSONObject jsonObject2 = new JSONObject();
				jsonObject2.put("categoryId", k);
				jsonObject2.put("categoryName",v.get(0).getCategory2Name());
				jsonObject2List.add(jsonObject2);

				//4.处理3级分类
				ArrayList<JSONObject> jsonObject3List = new ArrayList<>();
				v.forEach(categoryView -> {
					JSONObject jsonObject3 = new JSONObject();
					jsonObject3.put("categoryId", categoryView.getCategory3Id());
					jsonObject3.put("categoryName", categoryView.getCategory3Name());
					jsonObject3List.add(jsonObject3);
				});
				jsonObject2.put("categoryChild", jsonObject3List);
			});
			jsonObject1.put("categoryChild", jsonObject2List);
			return jsonObject1;
		}
		return null;
	}

	@Override
	public List<BaseCategory1> getfindAllCategory1() {
		List<BaseCategory1> list = baseCategory1Mapper.selectList(null);
		return list;
	}
}
