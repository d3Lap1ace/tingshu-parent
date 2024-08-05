package com.impower.tingshu.album.service.impl;

import com.impower.tingshu.album.config.VodConstantProperties;
import com.impower.tingshu.album.service.VodService;
import com.impower.tingshu.common.util.UploadFileUtil;
import com.impower.tingshu.vo.album.TrackMediaInfoVo;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tms.v20200713.TmsClient;
import com.tencentcloudapi.tms.v20200713.models.TextModerationRequest;
import com.tencentcloudapi.tms.v20200713.models.TextModerationResponse;
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

    @Override
    public void deleteMedia(String mediaFileId) {
        try {
            //1.实例化一个认证对象
            Credential cred = new Credential(vodConstantProperties.getSecretId(), vodConstantProperties.getSecretKey());
            //2.实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, vodConstantProperties.getRegion());
            //3.实例化一个请求对象,每个接口都会对应一个request对象
            DeleteMediaRequest req = new DeleteMediaRequest();
            req.setFileId(mediaFileId);
            //4.返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
            client.DeleteMedia(req);
        } catch (TencentCloudSDKException e) {
            log.error("[专辑服务]删除点播平台文件异常：{}", e);
        }
    }


    /**
     * 对音视频文件发起审核任务
     * @param mediaFileId 文件唯一标识
     * @return 发起审核任务ID，用于查看审核结果
     */
    @Override
    public String reviewTrack(String mediaFileId) {
        try {
            //1.实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            Credential cred = new Credential(vodConstantProperties.getSecretId(), vodConstantProperties.getSecretKey());
            //2.实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, vodConstantProperties.getRegion());
            //3.实例化一个请求对象,每个接口都会对应一个request对象
            ReviewAudioVideoRequest req = new ReviewAudioVideoRequest();
            req.setFileId(mediaFileId);
            //4.发起审核任务，返回的resp是一个ReviewAudioVideoResponse的实例，与请求对象对应
            ReviewAudioVideoResponse resp = client.ReviewAudioVideo(req);
            //5.获取审核任务ID
            if (resp != null) {
                String taskId = resp.getTaskId();
                return taskId;
            }
        } catch (TencentCloudSDKException e) {
            log.error("[点播平台]发起审核任务失败：{}", e);
        }
        return null;
    }

    /**
     * 根据审核任务ID查询审核结果
     *
     * @param mediaFileId 审核任务ID
     * @return 审核结果
     */
    @Override
    public String getReviewTaskResult(String mediaFileId) {
        try {
            //1.实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            Credential cred = new Credential(vodConstantProperties.getSecretId(), vodConstantProperties.getSecretKey());
            //2.实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, vodConstantProperties.getRegion());
            //3.实例化一个请求对象,每个接口都会对应一个request对象
            DescribeTaskDetailRequest req = new DescribeTaskDetailRequest();
            req.setTaskId(mediaFileId);
            //4.查询审核结果 返回的resp是一个DescribeTaskDetailResponse的实例，与请求对象对应
            DescribeTaskDetailResponse resp = client.DescribeTaskDetail(req);
            //5.解析审核结果
            if ("ReviewAudioVideo".equals(resp.getTaskType())) {
                //5.1  判断音视频审核任务是否完成
                if ("FINISH".equals(resp.getStatus())) {
                    //5.2 获取音视频审核结果
                    ReviewAudioVideoTask reviewAudioVideoTask = resp.getReviewAudioVideoTask();
                    if (reviewAudioVideoTask != null) {
                        //5.3 获取审核任务结果
                        ReviewAudioVideoTaskOutput output = reviewAudioVideoTask.getOutput();
                        if (output != null) {
                            //5.4 返回建议结果
                            String suggestion = output.getSuggestion();
                            return suggestion;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("[点播平台]获取审核结果异常：{}", e);
        }
        return null;
    }

    @Override
    public String ScanText(String mediaFileId) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            Credential cred = new Credential(vodConstantProperties.getSecretId(), vodConstantProperties.getSecretKey());
            // 实例化要请求产品的client对象,clientProfile是可选的
            TmsClient client = new TmsClient(cred, vodConstantProperties.getRegion());
            // 实例化一个请求对象,每个接口都会对应一个request对象
            TextModerationRequest req = new TextModerationRequest();
            // 查询审核结果 返回的resp是一个DescribeTaskDetailResponse的实例，与请求对象对应
            TextModerationResponse resp = client.TextModeration(req);
            // 输出json格式的字符串回包
            String jsonString = AbstractModel.toJsonString(resp);
            // TODO 解析文字审核结果
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    @Override
    public String ScanImages(MultipartFile file) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            Credential cred = new Credential(vodConstantProperties.getSecretId(), vodConstantProperties.getSecretKey());
            // 实例化要请求产品的client对象,clientProfile是可选的
            TmsClient client = new TmsClient(cred, vodConstantProperties.getRegion());
            // 实例化一个请求对象,每个接口都会对应一个request对象
            TextModerationRequest req = new TextModerationRequest();
            // 返回的resp是一个TextModerationResponse的实例，与请求对象对应
            TextModerationResponse resp = client.TextModeration(req);
            // TODO 解析图片审核结果
            System.out.println(AbstractModel.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return null;
    }
}
