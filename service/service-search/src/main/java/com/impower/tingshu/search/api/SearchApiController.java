package com.impower.tingshu.search.api;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.impower.tingshu.common.result.Result;
import com.impower.tingshu.model.album.BaseCategoryView;
import com.impower.tingshu.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.directory.SearchResult;
import java.util.List;
import java.util.Map;

@Tag(name = "搜索专辑管理")
@RestController
@RequestMapping("api/search")
@SuppressWarnings({"all"})
public class SearchApiController {

    @Autowired
    private SearchService searchService;


    /**
     * 仅用于接口测试-指定专辑保存到索引库
     *
     * @param albumId
     * @return
     */
    @Operation(summary = "仅用于接口测试-指定专辑保存到索引库")
    @GetMapping("/albumInfo/upperAlbum/{albumId}")
    public Result upperAlbum(@PathVariable Long albumId) {
        searchService.upperAlbum(albumId);
        return Result.ok();
    }


    /**
     * 仅用于接口测试-将指定专辑从索引库删除
     * @param albumId
     * @return
     */
    @Operation(summary = "仅用于接口测试-将指定专辑从索引库删除")
    @GetMapping("/albumInfo/lowerAlbum/{albumId}")
    public Result lowerAlbum(@PathVariable Long albumId) {
        searchService.lowerAlbum(albumId);
        return Result.ok();
    }

    /**
     * 查询1级分类下置顶3级分类热度top6
     * @param category1Id
     * @return
     */
    @Operation(summary = "查询1级分类下置顶3级分类热度top6")
    @GetMapping("/albumInfo/channel/{category1Id}")
    public Result<List<Map<String, Object>>> getCategory3Top6(@PathVariable Long category1Id){
        List<Map<String, Object>> list = searchService.getCategory3Top6(category1Id);
        return Result.ok(list);
    }



}

