package com.tanhua.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.tanhua.common.pojo.UserFreezeMXY;
import com.tanhua.impl.UserInfoVOImplMXY;
import com.tanhua.vo.UserInfoVOMXY;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class UserInfoVOServiceMXY {
    @Autowired
    private UserInfoVOImplMXY userInfoVOImplMXY;

    public UserInfoVOMXY queryUserInfoVO(String userId) {

        if (StrUtil.isEmpty(userId)) {
            return null;
        }
        UserInfoVOMXY userInfoVOMXY = null;
        try {
            userInfoVOMXY = userInfoVOImplMXY.queryUserInfoVO(userId);
        } catch (Exception e) {
            log.error("查询用户详细信息失败 userId = " + userId, e);
        }
        return userInfoVOMXY;
    }

    public Map<String, String> userFreeze(UserFreezeMXY userFreezeMXY) {
        if(ObjectUtil.isEmpty(userFreezeMXY)) {
            log.error("用户冻结参数为空");
            throw new RuntimeException("用户冻结参数为空");
        }

        Map<String,String> result = userInfoVOImplMXY.userFreeze(userFreezeMXY);


        return result;
    }

    public Map<String, String> userUnFreeze(Map<String, String> param) {
        if(ObjectUtil.isEmpty(param)) {
            log.error("用户解冻参数为空");
            throw new RuntimeException("用户解冻参数为空");
        }

        Map<String,String> result = userInfoVOImplMXY.userUFreeze(param);


        return result;
    }
}
