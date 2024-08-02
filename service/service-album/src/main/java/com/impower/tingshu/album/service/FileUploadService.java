package com.impower.tingshu.album.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    /**
     * 图片（封面、头像）文件上传
     * 前端提交文件参数名：file
     *
     * @param multipartFile
     * @return
     */
    String fileUpload(MultipartFile multipartFile);
}