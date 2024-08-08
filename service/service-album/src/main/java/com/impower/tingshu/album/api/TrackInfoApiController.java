package com.impower.tingshu.album.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.impower.tingshu.album.service.TrackInfoService;
import com.impower.tingshu.album.service.VodService;
import com.impower.tingshu.common.login.ImpowerLogin;
import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.common.util.AuthContextHolder;
import com.impower.tingshu.model.album.TrackInfo;
import com.impower.tingshu.query.album.TrackInfoQuery;
import com.impower.tingshu.vo.album.TrackInfoVo;
import com.impower.tingshu.vo.album.TrackListVo;
import com.impower.tingshu.vo.album.TrackStatVo;
import com.qcloud.cos.transfer.Transfer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "声音管理")
@RestController
@RequestMapping("api/album")
@SuppressWarnings({"all"})
public class TrackInfoApiController {

	@Autowired
	private TrackInfoService trackInfoService;
	@Autowired
	private VodService vodService;

	/**
	 * 音视频文件上腾讯传点播平台
	 *
	 * @param file
	 * @return
	 */
	@Operation(summary = "音视频文件上传腾讯传点播平台")
	@PostMapping("/trackInfo/uploadTrack")
	public Result<Map<String, String>> uploadTrack(@RequestParam("file") MultipartFile file) {
		Map<String, String> map = vodService.uploadTrack(file);
		return Result.ok(map);
	}


	/**
	 * 保存声音
	 *
	 * @param trackInfoVo
	 * @return
	 */
	@ImpowerLogin
	@Operation(summary = "保存声音")
	@PostMapping("/trackInfo/saveTrackInfo")
	public Result saveTrackInfo(@RequestBody TrackInfoVo trackInfoVo) {
		Long userId = AuthContextHolder.getUserId();
//		Long userId = 1L;
		trackInfoService.saveTrackInfo(userId, trackInfoVo);
		return Result.ok();
	}


	/**
	 *
	 * 条件分页查询声音列表
	 *
	 * @param page
	 * @param limit
	 * @param trackInfoQuery
	 * @return
	 */
	@ImpowerLogin
	@Operation(summary = "条件分页查询声音列表")
	@PostMapping("/trackInfo/findUserTrackPage/{page}/{limit}")
	public Result<Page<TrackListVo>> getUserTrackPage(
			@PathVariable int page,
			@PathVariable int limit,
			@RequestBody TrackInfoQuery trackInfoQuery
	) {
		//1.获取用户ID
		Long userId = AuthContextHolder.getUserId();
		trackInfoQuery.setUserId(userId);
		//2.构建分页对象
		Page<TrackListVo> pageInfo = new Page<>(page, limit);
		//3.调用业务层分页查询
		pageInfo = trackInfoService.getUserTrackPage(pageInfo, trackInfoQuery);
		//4.响应结果
		return Result.ok(pageInfo);
	}


	/**
	 * 查询声音信息
	 * @param id
	 * @return
	 */
	@Operation(summary = "查询声音信息")
	@GetMapping("/trackInfo/getTrackInfo/{id}")
	public Result<TrackInfo> getTrackInfo(@PathVariable Long id) {
		return Result.ok(trackInfoService.getById(id));
	}


	/**
	 * 修改声音信息
	 * @param id
	 * @param trackInfo
	 * @return
	 */
	@ImpowerLogin
	@Operation(summary = "修改声音信息")
	@PutMapping("/trackInfo/updateTrackInfo/{id}")
	public Result updateTrackInfo(@PathVariable("id") Long trackId, @RequestBody TrackInfo trackInfo) {
		trackInfoService.updateTrackInfo(trackId,trackInfo);
		return Result.ok();
	}


	/**
	 * 删除声音信息
	 */
	@ImpowerLogin
	@Operation(summary = "删除声音信息")
	@DeleteMapping("/trackInfo/removeTrackInfo/{id}")
	public Result removeTrackInfo(@PathVariable("id") Long id) {
		trackInfoService.removeTrackInfo(id);
		return Result.ok();
	}


	/**
	 * 获取声音统计信息
	 * @param trackId
	 * @return
	 */
	@Operation(summary = "获取声音统计信息")
	@GetMapping("/trackInfo/getTrackStatVo/{trackId}")
	public Result<TrackStatVo> getTrackStatVo(@PathVariable Long trackId) {
		TrackStatVo trackStatVo = trackInfoService.getTrackStatVoList(trackId);
		return Result.ok(trackStatVo);
	}

	/**
	 * 查询专辑声音分页列表
	 * @param albumId
	 * @param page
	 * @param limit
	 * @return
	 */
	@ImpowerLogin
	@Operation(summary = "查询专辑声音分页列表")
	@GetMapping("/trackInfo/findAlbumTrackPage/{albumId}/{page}/{limit}")
	public Result<Page<TrackListVo>> getAlbumTrackPage(@PathVariable Long albumId,
													   @PathVariable int page,
													   @PathVariable int limit){
		Page<TrackListVo> pageInfo = new Page<>(page, limit);
		pageInfo = trackInfoService.getAlbumTrackPage(pageInfo,albumId);
		return Result.ok(pageInfo);
	}

}

