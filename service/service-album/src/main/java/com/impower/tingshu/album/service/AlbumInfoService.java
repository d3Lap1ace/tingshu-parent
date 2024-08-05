package com.impower.tingshu.album.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.impower.tingshu.model.album.AlbumAttributeValue;
import com.impower.tingshu.model.album.AlbumInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.impower.tingshu.query.album.AlbumInfoQuery;
import com.impower.tingshu.vo.album.AlbumAttributeValueVo;
import com.impower.tingshu.vo.album.AlbumInfoVo;
import com.impower.tingshu.vo.album.AlbumListVo;
import com.impower.tingshu.vo.album.AlbumStatVo;

import java.util.List;

public interface AlbumInfoService extends IService<AlbumInfo> {

    /**
     * 当前用户保存专辑
     * @param userId 用户ID
     * @param albumInfo 专辑信息VO对象
     */
    void saveAlbumInfo(Long userId, AlbumInfoVo albumInfo);

    /**
     * 保存专辑统计信息
     *
     * @param albumId  专辑ID
     * @param statType 统计类型
     * @param statNum  统计数值 0401-播放量 0402-订阅量 0403-购买量 0403-评论数'
     */
    void saveAlbumInfoStat(Long albumId, String statType, int statNum);


    /**
     * 查看当前用户专辑分页列表
     * @param pageInfo
     * @param userId
     * @param albumInfoVoQuery
     * @return
     */
    Page<AlbumListVo> getUserAlbumPage(Page<AlbumListVo> pageInfo, Long userId, AlbumInfoQuery albumInfoVoQuery);



    /**
     * 根据专辑ID删除专辑
     * @param id
     * @return
     */
    void removeAlbumInfo(Long id);



    /**
     * 根据专辑ID查询专辑信息（包含标签列表）
     * @param id
     * @return
     */
    AlbumInfo getAlbumInfo(Long id);

    /**
     * 修改专辑信息
     * @param albumInfo
     */
    void updateAlbumInfo(AlbumInfo albumInfo);


    /**
     * 获取当前用户全部专辑列表
     * @param userId
     * @return
     */
    List<AlbumInfo> getUserAllAlbumList(Long userId);

    /**
     * 获取专辑属性值列表
     * @param albumId
     * @return
     */
    List<AlbumAttributeValue> getAlbumAttributeValue(Long albumId);

    /**
     * 根据专辑ID获取专辑统计信息
     * @param albumId
     * @return
     */
    AlbumStatVo getAlbumStatVo(Long albumId);
}
