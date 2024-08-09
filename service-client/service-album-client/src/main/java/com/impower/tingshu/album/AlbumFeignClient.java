package com.impower.tingshu.album;

import com.impower.tingshu.album.impl.AlbumDegradeFeignClient;
import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.model.album.AlbumInfo;
import com.impower.tingshu.model.album.BaseCategory3;
import com.impower.tingshu.model.album.BaseCategoryView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * <p>
 * 专辑模块远程调用Feign接口
 * </p>
 *
 * @author atguigu
 */
@FeignClient(value = "service-album", fallback = AlbumDegradeFeignClient.class)
public interface AlbumFeignClient {


    /**
     * 根据专辑ID查询专辑信息（包含标签列表）
     *
     * @param id
     * @return
     */
    @GetMapping("/albumInfo/getAlbumInfo/{id}")
    public Result<AlbumInfo> getAlbumInfo(@PathVariable Long id);


    /**
     * 根据三级分类ID查询分类视图
     * @param category3Id
     * @return
     */
    @GetMapping("/category/getCategoryView/{category3Id}")
    public Result<BaseCategoryView> getCategoryView(@PathVariable Long category3Id);


    /**
     * 根据1级分类ID查询该分类置顶7个三级分类列表
     * @param category1Id
     * @return
     */
    @GetMapping("/category/findTopBaseCategory3/{category1Id}")
    public Result<List<BaseCategory3>> getTopBaseCategory3(@PathVariable Long category1Id);

}
