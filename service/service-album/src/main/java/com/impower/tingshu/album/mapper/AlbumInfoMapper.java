package com.impower.tingshu.album.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.impower.tingshu.model.album.AlbumInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.impower.tingshu.query.album.AlbumInfoQuery;
import com.impower.tingshu.vo.album.AlbumListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AlbumInfoMapper extends BaseMapper<AlbumInfo> {

    Page<AlbumListVo> getUserAlbumPage(Page<AlbumListVo> pageInfo, @Param("userId") Long userId, AlbumInfoQuery albumInfoQuery);

}
