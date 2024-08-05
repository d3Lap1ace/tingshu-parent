package com.impower.tingshu.album.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.impower.tingshu.model.album.TrackInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.impower.tingshu.query.album.TrackInfoQuery;
import com.impower.tingshu.vo.album.TrackListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TrackInfoMapper extends BaseMapper<TrackInfo> {


    /**
     * 条件分页查询声音列表
     *
     * @param pageInfo       分页对象
     * @param trackInfoQuery 查询条件对象
     * @return
     */
    Page<TrackListVo> getUserTrackPage(Page<TrackListVo> pageInfo,@Param("vo") TrackInfoQuery trackInfoQuery);

    Page<TrackListVo> getAlbumTrackPage(Page<TrackListVo> pageInfo, Long albumId);
}
