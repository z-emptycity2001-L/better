package com.study.Publish;

import com.study.RocketMQ.Publish.CommonMsgBuilder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class LogoutMsgPublish extends CommonMsgBuilder {
    @Override
    public void sendAsyncMessage(SimpleMailMessage messageBody, String topic) {
        super.sendAsyncMessage(messageBody, topic);
    }
}
