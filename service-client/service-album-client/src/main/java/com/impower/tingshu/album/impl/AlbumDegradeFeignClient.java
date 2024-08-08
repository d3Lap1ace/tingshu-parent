package com.impower.tingshu.album.impl;


import com.impower.tingshu.album.AlbumFeignClient;
import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.model.album.AlbumInfo;
import com.impower.tingshu.model.album.BaseCategory3;
import com.impower.tingshu.model.album.BaseCategoryView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AlbumDegradeFeignClient implements AlbumFeignClient {


    @Override
    public Result<AlbumInfo> getAlbumInfo(Long id) {
        log.error("[专辑服务]提供远程调用方法getAlbumInfo执行服务降级");
        return null;
    }

    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        log.error("[专辑服务]提供远程调用方法getCategoryView执行服务降级");
        return null;
    }



    /**
     * 根据1级分类ID查询该分类置顶7个三级分类列表
     * @param category1Id
     * @return
     */
    @Override
    public Result<List<BaseCategory3>> getTopBaseCategory3(Long category1Id) {
        log.error("[专辑服务]提供远程调用方法getTopBaseCategory3执行服务降级");
        return null;
    }
}
