package com.impower.tingshu.album.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.impower.tingshu.model.album.TrackInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.impower.tingshu.query.album.TrackInfoQuery;
import com.impower.tingshu.vo.album.TrackInfoVo;
import com.impower.tingshu.vo.album.TrackListVo;

public interface TrackInfoService extends IService<TrackInfo> {

    /**
     * 保存声音
     * @param userId 用户ID
     * @param trackInfoVo 声音VO信息
     */
    void saveTrackInfo(Long userId, TrackInfoVo trackInfoVo);


    /**
     * 保存声音统计信息
     * @param id
     * @param statType
     * @param num
     */
    void saveTrackStat(Long id, String statType, int num);

    /**
     * 条件分页查询声音列表
     * @param pageInfo 分页对象
     * @param trackInfoQuery 查询条件对象
     * @return
     */
    Page<TrackListVo> getUserTrackPage(Page<TrackListVo> pageInfo, TrackInfoQuery trackInfoQuery);


    /**
     * 修改声音信息
     * @param trackId
     * @param trackInfo
     * @return
     */
    void updateTrackInfo(Long trackId,TrackInfo trackInfo);

    /**
     * 删除声音信息
     * @param id
     */
    void removeTrackInfo(Long id);
}
