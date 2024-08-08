package com.impower.tingshu.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.impower.tingshu.album.AlbumFeignClient;
import com.impower.tingshu.model.album.AlbumInfo;
import com.impower.tingshu.model.album.AlbumInfoIndex;
import com.impower.tingshu.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@SuppressWarnings({"all"})
public class SearchServiceImpl implements SearchService {

    @Autowired
    private AlbumFeignClient albumFeignClient;
    /**
     * 将指定专辑ID构建索引库文档对象，将文档保存到索引库
     *
     * @param albumId
     */
    @Override
    public void upperAlbum(Long albumId) {
        //1.封装索引库文档对象
        AlbumInfoIndex albumInfoIndex = new AlbumInfoIndex();
        // 远程调用  获取专辑信息（包含专辑标签列表）封装专辑基本信息
        AlbumInfo albumInfo = albumFeignClient.getAlbumInfo(albumId).getData();
        Assert.notNull(albumInfo, "专辑{}不存在", albumId);
        // 拷贝属性
        BeanUtil.copyProperties(albumInfo, albumInfoIndex);


    }

    @Override
    public void lowerAlbum(Long albumId) {
        return;
    }

    @Override
    public List<Map<String, Object>> getCategory3Top6(Long category1Id) {
        return null;
    }
}
