package com.impower.tingshu.album.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.impower.tingshu.album.service.AlbumInfoService;
import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.common.util.AuthContextHolder;
import com.impower.tingshu.model.album.AlbumInfo;
import com.impower.tingshu.query.album.AlbumInfoQuery;
import com.impower.tingshu.vo.album.AlbumInfoVo;
import com.impower.tingshu.vo.album.AlbumListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
	@Operation(summary = "当前用户保存专辑")
	@PostMapping("/albumInfo/saveAlbumInfo")
	public Result saveAlbumInfo(@RequestBody AlbumInfoVo albumInfo) {
		Long userId = AuthContextHolder.getUserId();
		albumInfoService.saveAlbumInfo(userId,albumInfo);
		return Result.ok();
	}



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

	@Operation(summary = "修改专辑")
	@PutMapping("/albumInfo/updateAlbumInfo/{id}")
	public Result updateAlbumInfoById(@PathVariable("id") Long id,@RequestBody AlbumInfo albumInfo){
		albumInfoService.updateAlbumInfo(albumInfo);
		return Result.ok();
	}

}

