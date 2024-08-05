package com.impower.tingshu.album.service;

import com.impower.tingshu.vo.album.TrackMediaInfoVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface VodService {

    /**
     * 音视频文件上腾讯传点播平台
     *
     * @param file
     * @return {"mediaFileId":"点播平台文件唯一标识", "mediaUrl":"在线播放地址"}
     */
    Map<String, String> uploadTrack(MultipartFile file);

    /**
     * 根据点播平台文件唯一标识获取音视频文件详情信息
     * @param mediaFileId
     * @return
     */
    TrackMediaInfoVo getMediaInfo(String mediaFileId);
}
