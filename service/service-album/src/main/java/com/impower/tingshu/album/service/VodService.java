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

    /**
     * 根据id删除文件
     * @param mediaFileId
     */
    void deleteMedia(String mediaFileId);

    /**
     * 对音视频文件发起审核任务
     * @param mediaFileId 文件唯一标识
     * @return 发起审核任务ID，用于查看审核结果
     */
    String reviewTrack(String mediaFileId);


    //查询审核任务结果：https://cloud.tencent.com/document/product/266/33431
    /**
     * 根据审核任务ID查询审核结果
     * @param mediaFileId
     * @return
     */
    String getReviewTaskResult(String mediaFileId);

    String ScanText(String mediaFileId);

    String ScanImages(MultipartFile file);

}
