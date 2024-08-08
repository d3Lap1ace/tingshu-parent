package com.impower.tingshu.album.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.impower.tingshu.album.mapper.AlbumAttributeValueMapper;
import com.impower.tingshu.album.mapper.AlbumInfoMapper;
import com.impower.tingshu.album.mapper.AlbumStatMapper;
import com.impower.tingshu.album.mapper.TrackInfoMapper;
import com.impower.tingshu.album.service.AlbumInfoService;
import com.impower.tingshu.album.service.VodService;
import com.impower.tingshu.common.constant.SystemConstant;
import com.impower.tingshu.common.execption.GuiguException;
import com.impower.tingshu.model.album.AlbumAttributeValue;
import com.impower.tingshu.model.album.AlbumInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.impower.tingshu.model.album.AlbumStat;
import com.impower.tingshu.model.album.TrackInfo;
import com.impower.tingshu.query.album.AlbumInfoQuery;
import com.impower.tingshu.vo.album.AlbumAttributeValueVo;
import com.impower.tingshu.vo.album.AlbumInfoVo;
import com.impower.tingshu.vo.album.AlbumListVo;
import com.impower.tingshu.vo.album.AlbumStatVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class AlbumInfoServiceImpl extends ServiceImpl<AlbumInfoMapper, AlbumInfo> implements AlbumInfoService {

	@Autowired
	private AlbumInfoMapper albumInfoMapper;
	@Autowired
	private AlbumAttributeValueMapper albumAttributeValueMapper;
	@Autowired
	private AlbumStatMapper albumStatMapper;
	@Autowired
	private TrackInfoMapper trackInfoMapper;
	@Autowired
	private VodService vodService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveAlbumInfo(Long userId, AlbumInfoVo albumInfovo) {
		// 保存专辑表
		AlbumInfo albumInfo = BeanUtil.copyProperties(albumInfovo,AlbumInfo.class);
		// 手动设置其他属性赋值
		albumInfo.setUserId(userId);
		albumInfo.setTracksForFree(5);
		albumInfo.setStatus(SystemConstant.ALBUM_STATUS_NO_PASS);
		// 执行保存专辑，得到专辑ID
		albumInfoMapper.insert(albumInfo);
		Long albumId = albumInfo.getId();
		// 保存专辑标签
		List<AlbumAttributeValueVo> albumAttributeValueVoList = albumInfovo.getAlbumAttributeValueVoList();
		if (CollectionUtil.isNotEmpty(albumAttributeValueVoList)) {
			albumAttributeValueVoList.forEach(albumAttributeValueVo -> {
				// 将专辑统计VO转为PO对象
				AlbumAttributeValue albumAttributeValue = BeanUtil.copyProperties(albumAttributeValueVo, AlbumAttributeValue.class);
				// 关联专辑ID
				albumAttributeValue.setAlbumId(albumId);
				// 保存专辑标签
				albumAttributeValueMapper.insert(albumAttributeValue);
			});
		}
		// 初始化专辑统计信息
		this.saveAlbumInfoStat(albumId,SystemConstant.ALBUM_STAT_PLAY, 0);
		this.saveAlbumInfoStat(albumId, SystemConstant.ALBUM_STAT_SUBSCRIBE, 0);
		this.saveAlbumInfoStat(albumId, SystemConstant.ALBUM_STAT_BUY, 0);
		this.saveAlbumInfoStat(albumId, SystemConstant.ALBUM_STAT_COMMENT, 0);

		// 校验内容是否安全
		String suggestTitle = vodService.scanText(albumInfo.getAlbumTitle());
		String suggestIntro = vodService.scanText(albumInfo.getAlbumIntro());
		if ("pass".equals(suggestTitle) && "pass".equals(suggestIntro)) {
			//专辑标题内容审核无误修改为审核通过
			albumInfo.setStatus(SystemConstant.ALBUM_STATUS_PASS);
			albumInfoMapper.updateById(albumInfo);
			return;
		}
		throw new GuiguException(500, "专辑标题或内容存在违规！title:{},intro:{},suggestTitle,suggestIntro");

	}
	/**
	 * 保存专辑统计信息
	 *
	 * @param albumId  专辑ID
	 * @param statType 统计类型
	 * @param statNum  统计数值 0401-播放量 0402-订阅量 0403-购买量 0403-评论数'
	 */
	@Override
	public void saveAlbumInfoStat(Long albumId, String statType, int statNum) {

		AlbumStat albumStat = new AlbumStat();
		albumStat.setAlbumId(albumId);
		albumStat.setStatType(statType);
		albumStat.setStatNum(statNum);
		albumStatMapper.insert(albumStat);
	}


	/**
	 * 查看当前用户专辑分页列表
	 * @param pageInfo
	 * @param userId
	 * @param albumInfoVoQuery
	 * @return
	 */
	@Override
	public Page<AlbumListVo> getUserAlbumPage(Page<AlbumListVo> pageInfo, Long userId, AlbumInfoQuery albumInfoQuery) {
		return albumInfoMapper.getUserAlbumPage(pageInfo, userId, albumInfoQuery);
	}

	/**
	 * 根据专辑ID删除专辑
	 * 1.判断该专辑是否关联声音
	 * 2.删除专辑记录
	 * 3.删除统计记录
	 * 4.删除专辑标签记录
	 *
	 * @param id
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeAlbumInfo(Long id) {
		// 1.判断该专辑是否关联声音
		Long count = trackInfoMapper.selectCount(new LambdaQueryWrapper<TrackInfo>().eq(TrackInfo::getAlbumId, id));
		if (count > 0) {
			// 删除关联声音
			log.info("start delete track info");
			trackInfoMapper.delete(new LambdaQueryWrapper<TrackInfo>().eq(TrackInfo::getAlbumId,id));
			log.info("over delete track info");
		}
		// 2.删除专辑记录
		AlbumInfo albumInfo = albumInfoMapper.selectById(id);
		if (albumInfo != null) {
			albumInfoMapper.deleteById(id);
		}else {
			throw new GuiguException(500,"album info not exist");
		}
		// 3.删除统计记录
		albumStatMapper.delete(new LambdaQueryWrapper<AlbumStat>().eq(AlbumStat::getAlbumId, id));
		// 4.删除专辑标签记录
		albumAttributeValueMapper.delete(new LambdaQueryWrapper<AlbumAttributeValue>().eq(AlbumAttributeValue::getAlbumId, id));


	}


	/**
	 * 根据专辑ID查询专辑信息（包含标签列表）
	 *
	 * @param id
	 * @return
	 */
	@Override
	public AlbumInfo getAlbumInfo(Long id) {
		// 根据id查询相应的专辑实体
		AlbumInfo albumInfo = albumInfoMapper.selectById(id);

		// // TODO 只有下架的的专辑才能修改
		if(albumInfo == null) {
			// 根据专辑id查询专辑标签列表
			List<AlbumAttributeValue> albumAttributeValuesList = albumAttributeValueMapper.selectList(new LambdaQueryWrapper<AlbumAttributeValue>()
					.eq(AlbumAttributeValue::getAlbumId, id));
			albumInfo.setAlbumAttributeValueVoList(albumAttributeValuesList);
		}
		return albumInfo;
	}

	/**
	 * 修改专辑信息
	 *
	 * @param albumInfo
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateAlbumInfo(AlbumInfo albumInfo) {
		// 修改专辑表信息
		albumInfoMapper.updateById(albumInfo);
		// 修改专辑标签关系记录
		List<AlbumAttributeValue> albumAttributeValueVoList = albumInfo.getAlbumAttributeValueVoList();
		// 删除该专辑下旧关联的标签记录
		albumAttributeValueMapper.delete(new LambdaQueryWrapper<AlbumAttributeValue>().eq(AlbumAttributeValue::getAlbumId, albumInfo.getId()));
		// 新增专辑标签记录
		if(CollectionUtil.isNotEmpty(albumAttributeValueVoList)){
			albumAttributeValueVoList.forEach(albumAttributeValue -> {
				// 关联专辑ID
				albumAttributeValue.setAlbumId(albumInfo.getId());
				// 保存专辑标签关系
				albumAttributeValueMapper.insert(albumAttributeValue);
			});
		}
		// 修改后内容是否安全
		String suggestTitle = vodService.scanText(albumInfo.getAlbumTitle());
		String suggestIntro = vodService.scanText(albumInfo.getAlbumIntro());
		if ("pass".equals(suggestTitle) && "pass".equals(suggestIntro)) {
			//专辑标题内容审核无误修改为审核通过
			albumInfo.setStatus(SystemConstant.ALBUM_STATUS_PASS);
			albumInfoMapper.updateById(albumInfo);
			return;
		}
		throw new GuiguException(500, "专辑标题或内容存在违规！");

	}

	/**
	 * 获取当前用户全部专辑列表
	 * @param userId
	 * @return
	 */
	@Override
	public List<AlbumInfo> getUserAllAlbumList(Long userId) {
		List<AlbumInfo> list = albumInfoMapper.selectList(new LambdaQueryWrapper<AlbumInfo>()
				.eq(AlbumInfo::getUserId, userId)
				.select(AlbumInfo::getId, AlbumInfo::getAlbumTitle)
				.last("limit 200")
				.orderByDesc(AlbumInfo::getUserId));
		return list;
	}

	/**
	 * 获取专辑属性值列表
	 * @param albumId
	 * @return
	 */
	@Override
	public List<AlbumAttributeValue> getAlbumAttributeValue(Long albumId) {
		List<AlbumAttributeValue> list = albumAttributeValueMapper.selectList(new LambdaQueryWrapper<AlbumAttributeValue>()
				.eq(AlbumAttributeValue::getAlbumId, albumId));
		return list;
	}

	/**
	 * 根据专辑ID获取专辑统计信息
	 * @param albumId
	 * @return
	 */
	@Override
	public AlbumStatVo getAlbumStatVo(Long albumId) {
		List<AlbumStat> albumStatList = albumStatMapper.selectList(new LambdaQueryWrapper<AlbumStat>().eq(AlbumStat::getAlbumId, albumId));
		AlbumStatVo albumStatVo = new AlbumStatVo();
		albumStatVo.setAlbumId(albumId);
		albumStatList.forEach(albumStat -> {
			switch (albumStat.getStatType()) {
				case SystemConstant.ALBUM_STAT_PLAY: albumStatVo.setPlayStatNum(albumStat.getStatNum()); break;
				case SystemConstant.ALBUM_STAT_SUBSCRIBE:albumStatVo.setSubscribeStatNum(albumStat.getStatNum()); break;
				case SystemConstant.ALBUM_STAT_BUY:albumStatVo.setBuyStatNum(albumStat.getStatNum()); break;
				case SystemConstant.ALBUM_STAT_COMMENT:albumStatVo.setCommentStatNum(albumStat.getStatNum()); break;
			}
		});
		return null;
	}
}
