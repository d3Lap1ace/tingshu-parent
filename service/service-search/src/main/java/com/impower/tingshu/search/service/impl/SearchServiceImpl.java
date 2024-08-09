package com.impower.tingshu.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.impower.tingshu.album.AlbumFeignClient;
import com.impower.tingshu.model.album.AlbumAttributeValue;
import com.impower.tingshu.model.album.AlbumInfo;
import com.impower.tingshu.model.album.AlbumInfoIndex;
import com.impower.tingshu.model.album.BaseCategoryView;
import com.impower.tingshu.model.search.AttributeValueIndex;
import com.impower.tingshu.search.service.AlbumInfoIndexRepository;
import com.impower.tingshu.search.service.SearchService;
import com.impower.tingshu.user.client.UserFeignClient;
import com.impower.tingshu.vo.user.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@SuppressWarnings({"all"})
public class SearchServiceImpl implements SearchService {

    @Autowired
    private AlbumFeignClient albumFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private AlbumInfoIndexRepository albumInfoIndexRepository;

    /**
     * 将指定专辑ID构建索引库文档对象，将文档保存到索引库
     *
     * @param albumId
     */
    @Override
    public void upperAlbum(Long albumId) {
        //1.封装索引库文档对象
        AlbumInfoIndex albumInfoIndex = new AlbumInfoIndex();
        // 远程调用  获取专辑信息（包含专辑标签列表）封装专辑基本信息
        AlbumInfo albumInfo = albumFeignClient.getAlbumInfo(albumId).getData();
        Assert.notNull(albumInfo, "专辑{}不存在", albumId);
        // 拷贝属性
        BeanUtil.copyProperties(albumInfo, albumInfoIndex);
        // 专辑中包含标签列表单独处理
        List<AlbumAttributeValue> albumAttributeValueVoList = albumInfo.getAlbumAttributeValueVoList();
        if(CollectionUtil.isNotEmpty(albumAttributeValueVoList)){
            List<AttributeValueIndex> indexList = albumAttributeValueVoList.stream().map(albumAttributeValue ->
                    BeanUtil.copyProperties(albumAttributeValue, AttributeValueIndex.class)).collect(Collectors.toList());
            albumInfoIndex.setAttributeValueIndexList(indexList);
        }
        //  远程调用"专辑服务" 获取专辑三级分类ID 封装三级分类ID
        BaseCategoryView categoryView = albumFeignClient.getCategoryView(albumInfo.getCategory3Id()).getData();
        Assert.notNull(categoryView, "分类{}不存在", albumInfo.getCategory3Id());
        albumInfoIndex.setCategory2Id(categoryView.getCategory2Id());
        albumInfoIndex.setCategory1Id(categoryView.getCategory1Id());

        //  TODO 远程调用"专辑服务" 获取专辑统计信息 封装统计信息
        //  随机产生四项统计数值 播放量，订阅量，购买量，评论量
        int num1 = RandomUtil.randomInt(2000, 5000);
        int num2 = RandomUtil.randomInt(100, 1000);
        int num3 = RandomUtil.randomInt(1000, 3000);
        int num4 = RandomUtil.randomInt(500, 1000);
        albumInfoIndex.setPlayStatNum(num1);
        albumInfoIndex.setSubscribeStatNum(num2);
        albumInfoIndex.setBuyStatNum(num3);
        albumInfoIndex.setCommentStatNum(num4);
        //  利用现有统计数值计算热度分值 规则：用户行为*系数 累加
        BigDecimal score1 = new BigDecimal("0.1").multiply(BigDecimal.valueOf(num1));
        BigDecimal score2 = new BigDecimal("0.2").multiply(BigDecimal.valueOf(num2));
        BigDecimal score3 = new BigDecimal("0.3").multiply(BigDecimal.valueOf(num3));
        BigDecimal score4 = new BigDecimal("0.4").multiply(BigDecimal.valueOf(num4));
        BigDecimal hotScore = score1.add(score2).add(score3).add(score4);
        albumInfoIndex.setHotScore(hotScore.doubleValue());

        //  远程调用"用户服务" 获取专辑主播信息 封装主播名称
        UserInfoVo userInfoVo = userFeignClient.getUserInfoVo(albumInfo.getUserId()).getData();
        Assert.notNull(userInfoVo, "主播{}不存在", albumInfo.getUserId());
        albumInfoIndex.setAnnouncerName(userInfoVo.getNickname());

        // 保存专辑索引库文档
//        albumInfoIndexRepository.save(albumInfoIndex);

    }

    @Override
    public void lowerAlbum(Long albumId) {
        return;
    }

    @Override
    public List<Map<String, Object>> getCategory3Top6(Long category1Id) {
        return null;
    }
}
