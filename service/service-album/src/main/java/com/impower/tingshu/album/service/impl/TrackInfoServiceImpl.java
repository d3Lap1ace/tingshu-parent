package com.impower.tingshu.album.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.impower.tingshu.album.mapper.AlbumInfoMapper;
import com.impower.tingshu.album.mapper.TrackInfoMapper;
import com.impower.tingshu.album.mapper.TrackStatMapper;
import com.impower.tingshu.album.service.TrackInfoService;
import com.impower.tingshu.album.service.VodService;
import com.impower.tingshu.common.constant.SystemConstant;
import com.impower.tingshu.common.execption.GuiguException;
import com.impower.tingshu.model.album.AlbumInfo;
import com.impower.tingshu.model.album.TrackInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.impower.tingshu.model.album.TrackStat;
import com.impower.tingshu.query.album.TrackInfoQuery;
import com.impower.tingshu.vo.album.TrackInfoVo;
import com.impower.tingshu.vo.album.TrackListVo;
import com.impower.tingshu.vo.album.TrackMediaInfoVo;
import com.impower.tingshu.vo.album.TrackStatVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class TrackInfoServiceImpl extends ServiceImpl<TrackInfoMapper, TrackInfo> implements TrackInfoService {

	@Autowired
	private TrackInfoMapper trackInfoMapper;
	@Autowired
	private AlbumInfoMapper albumInfoMapper;
	@Autowired
	private VodService vodService;
    @Autowired
    private TrackStatMapper trackStatMapper;

	/**
	 * 保存声音
	 * @param userId      用户ID
	 * @param trackInfoVo 声音VO信息
	 */
	@Override
	public void saveTrackInfo(Long userId, TrackInfoVo trackInfoVo) {
		//1.保存声音信息
		AlbumInfo albumInfo = albumInfoMapper.selectById(trackInfoVo.getAlbumId());
		//1.1 将用户提交VO转为PO对象
		TrackInfo trackInfo = BeanUtil.copyProperties(trackInfoVo, TrackInfo.class);
		trackInfo.setUserId(userId);
		trackInfo.setOrderNum(albumInfo.getIncludeTrackCount() + 1);
		trackInfo.setSource(SystemConstant.TRACK_SOURCE_USER);
		trackInfo.setStatus(SystemConstant.TRACK_STATUS_NO_PASS);
		if(StringUtils.isBlank(trackInfo.getCoverUrl())){
			trackInfo.setCoverUrl(albumInfo.getCoverUrl());
		}

		//1.3 调用腾讯点播平台获取音视频文件详情信息-得到时长、大小、类型
		TrackMediaInfoVo trackMediaInfoVo = vodService.getMediaInfo(trackInfo.getMediaFileId());
		if(trackMediaInfoVo != null){
			trackInfo.setMediaDuration(BigDecimal.valueOf(trackMediaInfoVo.getDuration()));
			trackInfo.setMediaSize(trackMediaInfoVo.getSize());
			trackInfo.setMediaType(trackMediaInfoVo.getType());
		}
		trackInfoMapper.insert(trackInfo);
		Long trackInfoId = trackInfo.getId();

		//2.更新专辑信息（声音数量）
		albumInfo.setIncludeTrackCount(albumInfo.getIncludeTrackCount() + 1);
		albumInfoMapper.updateById(albumInfo);
		//3.新增声音统计信息
		this.saveTrackStat(trackInfoId, SystemConstant.TRACK_STAT_PLAY, 0);
		this.saveTrackStat(trackInfoId, SystemConstant.TRACK_STAT_PRAISE, 0);
		this.saveTrackStat(trackInfoId, SystemConstant.TRACK_STAT_COLLECT, 0);
		this.saveTrackStat(trackInfoId, SystemConstant.TRACK_STAT_COMMENT, 0);
		// 4.调用点播平台发起音频文件审核任务（异步审核）
		String reviewTrackId = vodService.reviewTrack(trackInfo.getMediaFileId());
		trackInfo.setReviewTaskId(reviewTrackId);
		trackInfo.setStatus(SystemConstant.TRACK_STATUS_REVIEW);
		trackInfoMapper.updateById(trackInfo);

	}


	/**
	 * 保存声音统计信息
	 * @param trackId 声音ID
	 * @param statType 统计类型 0701-播放量 0702-收藏量 0703-点赞量 0704-评论数
	 * @param num 数值
	 */
	@Override
	public void saveTrackStat(Long id, String statType, int num) {
		TrackStat trackStat = new TrackStat();
		trackStat.setTrackId(id);
		trackStat.setStatType(statType);
		trackStat.setStatNum(num);
		trackStatMapper.insert(trackStat);
	}


	/**
	 * 条件分页查询声音列表
	 *
	 * @param pageInfo       分页对象
	 * @param trackInfoQuery 查询条件对象
	 * @return
	 */
	@Override
	public Page<TrackListVo> getUserTrackPage(Page<TrackListVo> pageInfo, TrackInfoQuery trackInfoQuery) {
		return trackInfoMapper.getUserTrackPage(pageInfo, trackInfoQuery);
	}


	/**
	 * 修改声音信息
	 * @param trackId
	 * @param trackInfo
	 */
	@Override
	public void updateTrackInfo(Long trackId, TrackInfo trackInfo) {
		//1.判断音频文件是否变更
		TrackInfo oldTrackInfo = trackInfoMapper.selectById(trackId);
		//  根据声音ID查询声音记录得到“旧”的音频文件标识
		if(!oldTrackInfo.getMediaFileId().equals(trackInfo.getMediaFileId())){
			TrackMediaInfoVo mediaInfo = vodService.getMediaInfo(trackInfo.getMediaFileId());
			if(mediaInfo != null){
				trackInfo.setMediaDuration(BigDecimal.valueOf(mediaInfo.getDuration()));
				trackInfo.setMediaSize(mediaInfo.getSize());
				trackInfo.setMediaType(mediaInfo.getType());
				trackInfo.setStatus(SystemConstant.TRACK_STATUS_NO_PASS);
				// Todo 再次进行审核
				String suggestTitle = vodService.scanText(trackInfo.getTrackTitle());
				String suggestIntro = vodService.scanText(trackInfo.getTrackIntro());
				if (!"pass".equals(suggestTitle) || !"pass".equals(suggestIntro)) {
					throw new GuiguException(500, "声音标题或简介内容存在违规！");
				}
			}
			//  从点播平台删除旧的音频文件
			vodService.deleteMedia(oldTrackInfo.getMediaFileId());
		}
		trackInfoMapper.updateById(trackInfo);
	}

	/**
	 * 删除声音信息
	 * @param id
	 */
	@Override
	public void removeTrackInfo(Long id) {
		// 查询要删除的声音实体
		TrackInfo trackInfo = trackInfoMapper.selectById(id);
		// 删除声音表
		trackInfoMapper.deleteById(id);
		// 更新声音表排序 orderNUM
		Integer orderNum = trackInfo.getOrderNum();
		LambdaUpdateWrapper<TrackInfo> wrapper = new LambdaUpdateWrapper<>();
		wrapper.eq(TrackInfo::getId,id)
				.gt(TrackInfo::getOrderNum,orderNum)
				.setSql("order_num = order_num - 1");
		// 删除声音统计表
		trackStatMapper.delete(new LambdaQueryWrapper<TrackStat>().eq(TrackStat::getTrackId,id));
		// 删除专辑中声音的数量
		AlbumInfo albumInfo = albumInfoMapper.selectOne(new LambdaQueryWrapper<AlbumInfo>().eq(AlbumInfo::getId, trackInfo.getAlbumId()));
		albumInfo.setIncludeTrackCount(albumInfo.getIncludeTrackCount() - 1);
		albumInfoMapper.updateById(albumInfo);
		// 删除音频
		vodService.deleteMedia(trackInfo.getMediaFileId());

	}


	/**
	 * 获取声音统计信息
	 * @param trackId
	 * @return
	 */
	@Override
	public TrackStatVo getTrackStatVoList(Long trackId) {
		List<TrackStat> trackStatList = trackStatMapper.selectList(new LambdaQueryWrapper<TrackStat>().eq(TrackStat::getTrackId, trackId));
		TrackStatVo trackStatVo = new TrackStatVo();
		if(trackStatList != null && trackStatList.size() > 0){
			trackStatList.forEach(trackStat -> {
				switch (trackStat.getStatType()){
					case SystemConstant.TRACK_STAT_PLAY: trackStatVo.setPlayStatNum(trackStat.getStatNum());break;
					case SystemConstant.TRACK_STAT_COLLECT: trackStatVo.setCollectStatNum(trackStat.getStatNum());break;
					case SystemConstant.TRACK_STAT_PRAISE: trackStatVo.setPraiseStatNum(trackStat.getStatNum());break;
					case SystemConstant.TRACK_STAT_COMMENT: trackStatVo.setCommentStatNum(trackStat.getStatNum());break;
				}
			});
		}
		return trackStatVo;
	}

	/**
	 * 查询专辑声音分页列表
	 * @param pageInfo
	 * @param albumId
	 * @return
	 */
	@Override
	public Page<TrackListVo> getAlbumTrackPage(Page<TrackListVo> pageInfo, Long albumId) {
		return trackInfoMapper.getAlbumTrackPage(pageInfo,albumId);
	}
}
