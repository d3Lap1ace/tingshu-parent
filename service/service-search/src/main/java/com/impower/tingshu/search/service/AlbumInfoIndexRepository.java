package com.impower.tingshu.search.service;

import com.impower.tingshu.model.search.AlbumInfoIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AlbumInfoIndexRepository extends ElasticsearchRepository<AlbumInfoIndex, Long> {
    //文档基本CRUD操作
//    AlbumInfoIndex save(AlbumInfoIndex albumInfoIndex);

}