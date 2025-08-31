package com.study.RocketMQ.Publish;

import com.study.exception.BusinessException;
import com.study.Eume.ExceptEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

@Slf4j
public class CommonMsgBuilder {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    public void sendAsyncMessage(SimpleMailMessage messageBody, String topic) {
        log.info(topic+" 异步消息开始发送");
        rocketMQTemplate.asyncSend(topic, messageBody, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("异步发送成功: " + sendResult.getMsgId());
            }
            @Override
            public void onException(Throwable throwable) {
                System.err.println("异步发送失败: " + throwable.getMessage());
                throw BusinessException.of(ExceptEnum.ROCKETMQ_MSG_SEND_ERROR);
            }
        });
    }


}
