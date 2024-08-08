package com.impower.tingshu.account.receiver;

import cn.hutool.core.collection.CollectionUtil;
import com.impower.tingshu.account.service.UserAccountService;
import com.impower.tingshu.common.rabbit.constant.MqConst;
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

import java.util.Map;

/**
 * @classname tingshu-parent
 * @Auther d3Lap1ace
 * @Time 6/8/2024 18:31 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Slf4j
@Component
public class AccountReceiver {

    @Autowired
    private UserAccountService userAccountService;

    /**
     * 监听到注册业务数据，为用户隐式初始化账户信息
     * @param mapData 初始化账户相关数据 {"userId",1,"title":"","amount":10,"orderNo":"cz001"}
     * @param message 消息对象
     * @param channel 连接对象
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = MqConst.EXCHANGE_ACCOUNT,durable = "true"),
            value = @Queue(value = MqConst.QUEUE_USER_REGISTER,durable = "true"),
    key = MqConst.ROUTING_USER_REGISTER))
    public void registerAndInitAccount(Map mapData, Message message, Channel channel){
        if(CollectionUtil.isNotEmpty(mapData)){
            log.info("[账户服务]监听到用户首次注册初始化账户消息：{}", mapData);
            userAccountService.saveUserAccount(mapData);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }












}
