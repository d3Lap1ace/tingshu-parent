package com.impower.tingshu;

import com.impower.tingshu.search.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = ServiceSearchApplication.class)
public class BatchImportTest {
    @Autowired
    private SearchService searchService;

    @Test
    public void testBatchImport() {
        for (long i = 0; i < 1614; i++) {
            try {
                searchService.upperAlbum(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}