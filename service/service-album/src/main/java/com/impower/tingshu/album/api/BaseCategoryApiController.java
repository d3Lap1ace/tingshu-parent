package com.impower.tingshu.album.api;

import com.alibaba.fastjson.JSONObject;
import com.impower.tingshu.album.service.BaseCategoryService;
import com.impower.tingshu.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}

