package com.dfire.test.util;

import com.dfire.soa.bizconf.bean.security.User;
import com.dfire.soa.bizconf.service.IUserService;

import java.util.List;

/**
 * Created by majianfeng on 16/1/26.
 */
public class UserUtil {

    public String getOperatorId(IUserService userService, String entityId) {
        List<User> userList = userService.queryUserByEntityId(entityId).getModel();
        String operatorId = "";
        String username = "ADMIN"; //默认用admin去执行操作
        for (User user : userList) {
            if (username.equals(user.getUserName())) {
                operatorId = user.getId();
            }
        }
        return operatorId;
    }
}
