package com.impower.tingshu.album.api;

import com.alibaba.fastjson.JSONObject;
import com.impower.tingshu.album.mapper.BaseCategory1Mapper;
import com.impower.tingshu.album.service.BaseCategoryService;
import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.model.album.BaseAttribute;
import com.impower.tingshu.model.album.BaseCategory1;
import com.impower.tingshu.model.album.BaseCategory3;
import com.impower.tingshu.model.album.BaseCategoryView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "分类管理")
@RestController
@RequestMapping(value="/api/album")
@SuppressWarnings({"all"})
public class BaseCategoryApiController {

	@Autowired
	private BaseCategoryService baseCategoryService;


	/**
	 * 查询所有1,2,3级分类列表（将子分类封装到categoryChild）
	 *
	 * @return [{"categoryChild":[{"categoryName":"音乐音效","categoryId":101}],"categoryName":"音乐","categoryId":1}]
	 */
	@Operation(summary = "查询所有1,2,3级分类列表")
	@GetMapping("/category/getBaseCategoryList")
	public Result<List<JSONObject>> getBaseCategoryList() {
		List<JSONObject> categoryAllList = baseCategoryService.getBaseCategoryList();
		return Result.ok(categoryAllList);
	}

	/**
	 * 根据1级分类ID查询关联所有标签列表（标签值）
	 * @param category1Id
	 * @return
	 */
	@Operation(summary = "根据1级分类ID查询关联所有标签列表（标签值）")
	@GetMapping("/category/findAttribute/{category1Id}")
	public Result<List<BaseAttribute>> findAttribute(@PathVariable("category1Id") Long category1Id) {
		List<BaseAttribute> lsit = baseCategoryService.getAttributesByCategory1Id(category1Id);
		return Result.ok(lsit);
	}

	/**
	 * 根据三级分类ID查询分类视图
	 * @param category3Id
	 * @return
	 */
	@Operation(summary = "根据三级分类ID查询分类视图")
	@GetMapping("/category/getCategoryView/{category3Id}")
	public Result<BaseCategoryView> getCategoryView(@PathVariable("category3Id") Long category3Id) {
		BaseCategoryView baseCategoryView = baseCategoryService.getCategoryView(category3Id);
		return Result.ok(baseCategoryView);
	}


	/**
	 * 根据一级分类Id查询三级分类列表
	 */
	@Operation(summary = "根据一级分类Id查询三级分类列表")
	@GetMapping("/category/findTopBaseCategory3/{category1Id}")
	public Result<List<BaseCategory3>> getTopBaseCategory3(@PathVariable("category1Id") Long category1Id) {
		List<BaseCategory3> list = baseCategoryService.getTopBaseCategory3(category1Id);
		return Result.ok(list);
	}

	/**
	 * 根据一级分类id获取全部分类信息
	 */
	@Operation(summary = "根据一级分类id获取全部分类信息")
	@GetMapping("/category/getBaseCategoryList/{category1Id}")
	public Result<JSONObject> getBaseCategoryList(@PathVariable Long category1Id){
		JSONObject jsonObject = baseCategoryService.getBaseCategoryListByCategory1Id(category1Id);
		return Result.ok(jsonObject);
	}


	/**
	 * 查询所有的一级分类信息
	 */
	@Operation(summary = "查询所有的一级分类信息")
	@GetMapping("/category/findAllCategory1")
	public Result<List<BaseCategory1>> findAllCategory1(){
		List<BaseCategory1> list = baseCategoryService.getfindAllCategory1();
		return Result.ok(list);
	}

}






















