package com.impower.tingshu.album.job;

import cn.hutool.core.collection.CollectionUtil;
import com.impower.tingshu.album.mapper.TrackInfoMapper;
import com.impower.tingshu.album.service.VodService;
import com.impower.tingshu.common.constant.SystemConstant;
import com.impower.tingshu.model.album.TrackInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: atguigu
 * @create: 2024-08-05 15:42
 */
@Slf4j
@Component
public class ReviewTaskResultJob {

    @Autowired
    private TrackInfoMapper trackInfoMapper;
    @Autowired
    private VodService vodService;


    /**
     * 每隔5s查询审核中任务ID获取审核结果，根据审核结果更新审核状态
     * corn表达式：秒 分 时 日 月 周 [年]
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void getAudioReviewTaskResult() {
        log.info("[定时任务]查询审核中任务ID获取审核结果，根据审核结果更新审核状态");
        //1.根据审核状态（审核中）声音列表
        List<TrackInfo> trackInfoList = trackInfoMapper.selectList(new LambdaQueryWrapper<TrackInfo>().eq(TrackInfo::getStatus, SystemConstant.TRACK_STATUS_REVIEW)
                .select(TrackInfo::getId, TrackInfo::getReviewTaskId).last("limit 200"));
        //2.调用腾讯点播平台获取审核任务ID对应审核结果
        if(CollectionUtil.isNotEmpty(trackInfoList)) {
            for (TrackInfo trackInfo : trackInfoList) {
                //2.1 根据审核任务ID查询审核结果
                String suggest = vodService.getReviewTaskResult(trackInfo.getReviewTaskId());
                if(StringUtils.isNotBlank(suggest)) {
                    if ("pass".equals(suggest)) {
                        trackInfo.setStatus(SystemConstant.TRACK_STATUS_PASS);
                    } else if ("block".equals(suggest)) {
                        trackInfo.setStatus(SystemConstant.TRACK_STATUS_NO_PASS);
                    } else if ("review".equals(suggest)) {
                        trackInfo.setStatus(SystemConstant.TRACK_STATUS_REVIEW);
                    }
                    //3.根据审核结果更新审核状态
                    trackInfoMapper.updateById(trackInfo);
                }
            }
        }
    }
}