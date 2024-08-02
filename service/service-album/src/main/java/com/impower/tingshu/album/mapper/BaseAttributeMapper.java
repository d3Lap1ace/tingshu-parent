package com.impower.tingshu.album.mapper;

import com.impower.tingshu.model.album.BaseAttribute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseAttributeMapper extends BaseMapper<BaseAttribute> {


    List<BaseAttribute> getAttributesByCategory1Id(Long category1Id);
}
