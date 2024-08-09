package com.impower.tingshu.search.receiver;

import com.impower.tingshu.common.rabbit.constant.MqConst;
import com.impower.tingshu.search.service.SearchService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @classname tingshu-parent
 * @Auther d3Lap1ace
 * @Time 9/8/2024 16:57 周五
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Slf4j
@Component
public class SearchReceiver {
    @Autowired
    private SearchService searchService;

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = MqConst.EXCHANGE_ALBUM,durable ="true",autoDelete = "false"),
    value = @Queue(value = MqConst.QUEUE_ALBUM_UPPER,durable = "true",autoDelete = "false"),key = "MqConst.ROUTING_ALBUM_UPPER"))
    public void albumUpper(Long albumId, Message message, Channel channel) {
        if (albumId != null) {
            log.info("[搜索服务]监听到专辑上架消息：{}", albumId);
            searchService.upperAlbum(albumId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     *
     * 监听专辑下架队列，完成专辑下架
     * @param albumId 专辑ID
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_ALBUM, durable = "true", autoDelete = "true"),
            value = @Queue(value = MqConst.QUEUE_ALBUM_LOWER, durable = "true", autoDelete = "true"),
            key = MqConst.ROUTING_ALBUM_LOWER
    ))
    public void albumLower(Long albumId, Message message, Channel channel) {
        if (albumId != null) {
            log.info("[搜索服务]监听到专辑下架消息：{}", albumId);
            searchService.lowerAlbum(albumId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }




}
