package com.impower.tingshu.album.service.impl;

import com.impower.tingshu.album.config.VodConstantProperties;
import com.impower.tingshu.album.service.VodService;
import com.impower.tingshu.common.util.UploadFileUtil;
import com.impower.tingshu.vo.album.TrackMediaInfoVo;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class VodServiceImpl implements VodService {

    @Autowired
    private VodConstantProperties vodConstantProperties;

    @Autowired
    private VodUploadClient vodUploadClient;

    /**
     * 音视频文件上腾讯传点播平台
     *
     * @param file
     * @return {"mediaFileId":"点播平台文件唯一标识", "mediaUrl":"在线播放地址"}
     */
    @Override
    public Map<String, String> uploadTrack(MultipartFile file) {
        try {
            //1.将音频文件上传到点播平台
            //1.1 将用户上传文件保存到临时目录下
            String uploadTempPath = UploadFileUtil.uploadTempPath(vodConstantProperties.getTempPath(), file);
            //1.2 构造上传请求对象
            VodUploadRequest vodUploadRequest = new VodUploadRequest();
            //1.3 设置媒体本地上传路径
            vodUploadRequest.setMediaFilePath(uploadTempPath);
            //1.4 调用上传方法
            VodUploadResponse uploadResponse = vodUploadClient.upload(vodConstantProperties.getRegion(), vodUploadRequest);
            //2.得到上传后文件在线播放地址及文件唯一标识
            if (uploadResponse != null) {
                String mediaUrl = uploadResponse.getMediaUrl();
                String fileId = uploadResponse.getFileId();
                Map<String, String> map = new HashMap<>();
                map.put("mediaFileId", fileId);
                map.put("mediaUrl", mediaUrl);
                return map;
            }
            return null;
        } catch (Exception e) {
            log.error("[专辑服务]上传音视频文件到腾讯点播平台异常：{}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public TrackMediaInfoVo getMediaInfo(String mediaFileId) {
        try {
            //1.实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey
            Credential cred = new Credential(vodConstantProperties.getSecretId(), vodConstantProperties.getSecretKey());
            //2.实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, vodConstantProperties.getRegion());
            //3.实例化一个请求对象,每个接口都会对应一个request对象
            DescribeMediaInfosRequest req = new DescribeMediaInfosRequest();
            String[] fileIds1 = {mediaFileId};
            req.setFileIds(fileIds1);
            //4.请求点播平台获取媒体文件详情
            DescribeMediaInfosResponse resp = client.DescribeMediaInfos(req);
            if (resp != null) {
                MediaInfo mediaInfo = resp.getMediaInfoSet()[0];
                if (mediaInfo != null) {
                    //4.1 获取音频基本信息得到 文件类型
                    MediaBasicInfo basicInfo = mediaInfo.getBasicInfo();
                    //4.2 获取音频元数据 得到大小，时长
                    MediaMetaData metaData = mediaInfo.getMetaData();
                    TrackMediaInfoVo trackMediaInfoVo = new TrackMediaInfoVo();
                    trackMediaInfoVo.setType(basicInfo.getType());
                    trackMediaInfoVo.setSize(metaData.getSize());
                    trackMediaInfoVo.setDuration(metaData.getDuration());
                    return trackMediaInfoVo;
                }
            }
        } catch (Exception e) {
            log.error("[专辑服务]获取腾讯点播平台音频详情异常：{}", e);
        }
        return null;
    }
}
