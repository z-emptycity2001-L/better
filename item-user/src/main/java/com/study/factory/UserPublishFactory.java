package com.study.factory;

import com.study.Publish.LogoutMsgPublish;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
public class UserPublishFactory {
    private static LogoutMsgPublish logoutMsgPublish;

    public static LogoutMsgPublish getLogoutMsgPublish() {
        return logoutMsgPublish;
    }
}
