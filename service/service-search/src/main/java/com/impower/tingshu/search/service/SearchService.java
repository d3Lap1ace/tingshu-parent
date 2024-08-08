package com.impower.tingshu.search.service;

import java.util.List;
import java.util.Map;

public interface SearchService {


    /**
     * 将指定专辑ID构建索引库文档对象，将文档保存到索引库
     * @param albumId
     */
    void upperAlbum(Long albumId);


    void lowerAlbum(Long albumId);


    /**
     * 查询1级分类下置顶3级分类热度top6
     * @param category1Id
     * @return
     */
    List<Map<String, Object>> getCategory3Top6(Long category1Id);



}
