package com.impower.tingshu.album.api;

import cn.hutool.core.lang.Holder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.impower.tingshu.album.service.AlbumInfoService;
import com.impower.tingshu.common.login.ImpowerLogin;
import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.common.util.AuthContextHolder;
import com.impower.tingshu.model.album.AlbumAttributeValue;
import com.impower.tingshu.model.album.AlbumInfo;
import com.impower.tingshu.query.album.AlbumInfoQuery;
import com.impower.tingshu.vo.album.AlbumAttributeValueVo;
import com.impower.tingshu.vo.album.AlbumInfoVo;
import com.impower.tingshu.vo.album.AlbumListVo;
import com.impower.tingshu.vo.album.AlbumStatVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.http.impl.cookie.BasicSecureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "专辑管理")
@RestController
@RequestMapping("api/album")
@SuppressWarnings({"all"})
public class AlbumInfoApiController {

	@Autowired
	private AlbumInfoService albumInfoService;

	/**
	 * 当前用户保存专辑
	 * @param albumInfo
	 * @return
	 */
	@ImpowerLogin
	@Operation(summary = "当前用户保存专辑")
	@PostMapping("/albumInfo/saveAlbumInfo")
	public Result saveAlbumInfo(@RequestBody AlbumInfoVo albumInfo) {
		Long userId = AuthContextHolder.getUserId();
		albumInfoService.saveAlbumInfo(userId,albumInfo);
		return Result.ok();
	}


	@ImpowerLogin
	@Operation(summary = "查看当前用户专辑分页列表")
	@PostMapping("/albumInfo/findUserAlbumPage/{page}/{limit}")
	public Result<Page<AlbumListVo>> findUserAlbumPage(@PathVariable int page,
													   @PathVariable int limit,
													   @RequestBody AlbumInfoQuery albumInfoVoQuery) {
		Long userId = AuthContextHolder.getUserId();
		Page<AlbumListVo> pageInfo = new Page<>(page, limit);
		pageInfo = albumInfoService.getUserAlbumPage(pageInfo,userId,albumInfoVoQuery);
		return Result.ok(pageInfo);
	}

	@ImpowerLogin
	@Operation(summary = "根据ID删除专辑")
	@DeleteMapping("/albumInfo/removeAlbumInfo/{id}")
	public Result removeAlbumInfo(@PathVariable Long id) {
		Long userId = AuthContextHolder.getUserId();
		albumInfoService.removeAlbumInfo(id);
		return Result.ok();
	}

	@Operation(summary = "根据ID查询专辑信息")
	@GetMapping("/albumInfo/getAlbumInfo/{id}")
	public Result<AlbumInfo> getAlbumInfo(@PathVariable Long id) {
		return Result.ok(albumInfoService.getAlbumInfo(id));
	}
	@ImpowerLogin
	@Operation(summary = "修改专辑")
	@PutMapping("/albumInfo/updateAlbumInfo/{id}")
	public Result updateAlbumInfoById(@PathVariable("id") Long id,@RequestBody AlbumInfo albumInfo){
		albumInfoService.updateAlbumInfo(albumInfo);
		return Result.ok();
	}
	@ImpowerLogin
	@Operation(summary = "获取当前用户全部专辑列表")
	@GetMapping("/albumInfo/findUserAllAlbumList")
	public Result<List<AlbumInfo>> findUserAllAlbumList(){
		Long userId = AuthContextHolder.getUserId();
		List<AlbumInfo> list =  albumInfoService.getUserAllAlbumList(userId);
		return Result.ok(list);
	}

	@Operation(summary = "获取专辑属性值列表")
	@GetMapping("/albumInfo/findAlbumAttributeValue/{albumId}")
	public Result<List<AlbumAttributeValue>> findAlbumAttributeValue(@PathVariable Long albumId){
		List<AlbumAttributeValue> list = albumInfoService.getAlbumAttributeValue(albumId);
		return Result.ok(list);
	}

	@Operation(summary = "根据专辑ID获取专辑统计信息")
	@GetMapping("/albumInfo/getAlbumStatVo/{albumId}")
	public Result<AlbumStatVo> getAlbumStatVo(@PathVariable Long albumId){
		AlbumStatVo albumStatVo = albumInfoService.getAlbumStatVo(albumId);
		return Result.ok(albumStatVo);
	}

}

