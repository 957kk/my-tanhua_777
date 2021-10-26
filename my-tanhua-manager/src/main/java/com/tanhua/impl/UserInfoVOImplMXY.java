package com.tanhua.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.enums.SexEnum;
import com.tanhua.common.mapper.UserFreezeMapperMXY;
import com.tanhua.common.mapper.UserInfoMapper;
import com.tanhua.common.mapper.UserLogInfoMapper_zxk;
import com.tanhua.common.mapper.UserMapper;
import com.tanhua.common.pojo.*;
import com.tanhua.dubbo.server.api.UserLikeApi;
import com.tanhua.vo.UserInfoVOMXY;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class UserInfoVOImplMXY {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserLogInfoMapper_zxk userLogInfoMapper_zxk;
    @Autowired
    private UserFreezeMapperMXY userFreezeMapperMXY;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Reference(version = "1.0.0")
    private UserLikeApi userLikeApi;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static final String USER_FREEZE_PREFX = "USER_FREEZE_";

    private static final String FREEZESPEECH3 = "冻结发言3天";
    private static final String FREEZESPEECH7 = "冻结发言7天";
    private static final String FREEZESPEECHFOREVER = "永久冻结发言";

    private static final String FREEZETHELOGIN3 = "冻结登录3天";
    private static final String FREEZETHELOGIN7 = "冻结登录7天";
    private static final String FREEZETHELOGINFOREVER = "永久冻结登录";

    private static final String FREEZINGRELEASEDYNAMICS3 = "冻结发布动态3天";
    private static final String FREEZINGRELEASEDYNAMICS7 = "冻结发布动态7天";
    private static final String FREEZINGRELEASEDYNAMICSFOREVER = "永久冻结发布动态";

    public UserInfoVOMXY queryUserInfoVO(String userId) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
        UserInfoVOMXY userInfoVOMXY = new UserInfoVOMXY();
        userInfoVOMXY.setAge(userInfo.getAge());
        userInfoVOMXY.setCity(userInfo.getCity().split("-")[0]);
        userInfoVOMXY.setId(Convert.toInt(userInfo.getUserId()));
        userInfoVOMXY.setLogo(userInfo.getLogo());
        userInfoVOMXY.setNickname(userInfo.getNickName());
        userInfoVOMXY.setOccupation(null);
        userInfoVOMXY.setSex(SexEnum.MAN == userInfo.getSex() ? "男" : "女");
        userInfoVOMXY.setIncome(Convert.toInt(userInfo.getIncome()));
        userInfoVOMXY.setCreated(userInfo.getCreated().getTime());
        userInfoVOMXY.setTags(userInfo.getTags());
        userInfoVOMXY.setPersonalSignature("我就是我");
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("id", userId);
        User user = userMapper.selectOne(query);
        userInfoVOMXY.setMobile(user.getMobile());
        QueryWrapper<UserFreezeMXY> wapper = new QueryWrapper<>();
        wapper.eq("user_id", userInfo.getUserId());
        UserFreezeMXY userFreezeMXY = userFreezeMapperMXY.selectOne(wapper);
        if (ObjectUtil.isEmpty(userFreezeMXY)) {
            userInfoVOMXY.setUserStatus(1 + "");
        } else {
            userInfoVOMXY.setUserStatus(2 + "");
        }
        QueryWrapper<UserLogInfo> query1 = new QueryWrapper<>();
        query1.eq("user_id", Convert.toLong(userId));
        query1.orderByDesc("login_time");
        UserLogInfo userLogInfo = userLogInfoMapper_zxk.selectOne(query1);
        if (ObjectUtil.isNotNull(userLogInfo)) {
            userInfoVOMXY.setLastActiveTime(userLogInfo.getLoginTime());
            userInfoVOMXY.setLastLoginLocation(userLogInfo.getLoginAddress());
        }
        userInfoVOMXY.setCountBeLiked(Convert.toInt(userLikeApi.queryFanCount(Convert.toLong(userId))));
        userInfoVOMXY.setCountLiked(Convert.toInt(userLikeApi.queryLikeCount(Convert.toLong(userId))));
        userInfoVOMXY.setCountMatching(Convert.toInt(userLikeApi.queryMutualLikeCount(Convert.toLong(userId))));

        return userInfoVOMXY;
    }


    public Map<String, String> userFreeze(UserFreezeMXY userFreezeMXY) {
        int result = -1;
        Map<String, String> flag = new HashMap<>();
        //查询用户冻结状态，如果没有则新增，如果有则修改
        QueryWrapper<UserFreezeMXY> query = new QueryWrapper<>();
        query.eq("user_id", userFreezeMXY.getUserId());
        UserFreezeMXY freezeMXY = userFreezeMapperMXY.selectOne(query);
        if (ObjectUtil.isEmpty(freezeMXY)) {
            if (ObjectUtil.isEmpty(freezeMXY)) {
                userFreezeMXY.setCreated(System.currentTimeMillis());
                result = userFreezeMapperMXY.insert(userFreezeMXY);
            } else {
                freezeMXY.setFreezingTime(userFreezeMXY.getFreezingTime());
                freezeMXY.setFreezingRange(userFreezeMXY.getFreezingRange());
                freezeMXY.setFrozenRemarks(userFreezeMXY.getFrozenRemarks());
                freezeMXY.setReasonsForFreezing(userFreezeMXY.getReasonsForFreezing());
                freezeMXY.setCreated(System.currentTimeMillis());
                result = userFreezeMapperMXY.updateById(freezeMXY);
            }
            if (result != 1) {
                flag.put("message", "操作失败");
                return flag;
            }
            //将用户的冻结状态存储在redis中
            String redisKey = USER_FREEZE_PREFX + userFreezeMXY.getUserId();
            try {
                switch (userFreezeMXY.getFreezingTime()) {
                    case 1: {
                        if (userFreezeMXY.getFreezingRange() == 1) {
                            //设置冻结登录三天
                            redisTemplate.opsForValue().set(redisKey, userFreezeMXY.getFreezingRange() + "",
                                    Duration.ofSeconds(10));
                            sendRocketMQ(FREEZETHELOGIN3, "对" + userFreezeMXY.getUserId() + "进行了" + FREEZETHELOGIN3);
                        } else if (userFreezeMXY.getFreezingRange() == 2) {
                            //设置冻结发言三天
                            redisTemplate.opsForValue().set(redisKey, userFreezeMXY.getFreezingRange() + "",
                                    Duration.ofSeconds(10));
                            sendRocketMQ(FREEZESPEECH3, "对" + userFreezeMXY.getUserId() + "进行了" + FREEZESPEECH3);
                        } else {
                            //设置冻结发布动态三天
                            redisTemplate.opsForValue().set(redisKey, userFreezeMXY.getFreezingRange() + "",
                                    Duration.ofSeconds(10));
                            sendRocketMQ(FREEZINGRELEASEDYNAMICS3, "对" + userFreezeMXY.getUserId() + "进行了" + FREEZINGRELEASEDYNAMICS3);

                        }
                    }
                    break;
                    case 2: {
                        if (userFreezeMXY.getFreezingRange() == 1) {
                            //设置冻结登录7天
                            redisTemplate.opsForValue().set(redisKey, userFreezeMXY.getFreezingRange() + "",
                                    Duration.ofSeconds(30));
                            sendRocketMQ(FREEZETHELOGIN7, "对" + userFreezeMXY.getUserId() + "进行了" + FREEZETHELOGIN7);
                        } else if (userFreezeMXY.getFreezingRange() == 2) {
                            //设置冻结发言7天
                            redisTemplate.opsForValue().set(redisKey, userFreezeMXY.getFreezingRange() + "",
                                    Duration.ofSeconds(30));
                            sendRocketMQ(FREEZESPEECH7, "对" + userFreezeMXY.getUserId() + "进行了" + FREEZESPEECH7);
                        } else {
                            //设置冻结发布动态7天
                            redisTemplate.opsForValue().set(redisKey, userFreezeMXY.getFreezingRange() + "",
                                    Duration.ofSeconds(30));
                            sendRocketMQ(FREEZINGRELEASEDYNAMICS7, "对" + userFreezeMXY.getUserId() + "进行了" + FREEZINGRELEASEDYNAMICS7);
                        }
                    }
                    break;
                    case 3: {
                        if (userFreezeMXY.getFreezingRange() == 1) {
                            //设置冻结登录永久
                            redisTemplate.opsForValue().set(redisKey, userFreezeMXY.getFreezingRange() + "",
                                    Duration.ofSeconds(100));
                            sendRocketMQ(FREEZETHELOGINFOREVER, "对" + userFreezeMXY.getUserId() + "进行了" + FREEZETHELOGINFOREVER);
                        } else if (userFreezeMXY.getFreezingRange() == 2) {
                            //设置冻结发言永久
                            redisTemplate.opsForValue().set(redisKey, userFreezeMXY.getFreezingRange() + "",
                                    Duration.ofSeconds(100));
                            sendRocketMQ(FREEZESPEECHFOREVER, "对" + userFreezeMXY.getUserId() + "进行了" + FREEZESPEECHFOREVER);
                        } else {
                            //设置冻结发布动态永久
                            redisTemplate.opsForValue().set(redisKey, userFreezeMXY.getFreezingRange() + "",
                                    Duration.ofSeconds(100));
                            sendRocketMQ(FREEZINGRELEASEDYNAMICSFOREVER, "对" + userFreezeMXY.getUserId() + "进行了" + FREEZINGRELEASEDYNAMICSFOREVER);
                        }
                    }
                    break;
                    default:
                        return null;
                }
            } catch (Exception e) {
                log.error("用户封禁状态存入redis失败 userid = " + userFreezeMXY.getUserId());
                flag.put("message", "操作失败");
            }
        }
        flag.put("message", "操作成功");
        return flag;
    }


    /**
     * 发送rocketmq信息操作内容
     *
     * @param o
     * @param a
     */
    private void sendRocketMQ(String o, String a) {
        // VerifyCode code = VerifyThreadLocal.get();
        LogRetained_yt logR = new LogRetained_yt();
        logR.setTime(System.currentTimeMillis());
        // logR.setUsername(code.getUsername());
        logR.setUsername("admin");
        logR.setIp("192.168.200.10");
        logR.setO(o);
        logR.setA(a);
        this.rocketMQTemplate.convertAndSend("tanhua", logR);
    }

    public Map<String, String> userUFreeze(Map<String, String> param) {
        Map<String, String> result = new HashMap<>();
        //查询redis中是否还有该用户的冻结状态

        String redisKey = USER_FREEZE_PREFX + param.get("userId");
        if (redisTemplate.hasKey(redisKey)) {
            redisTemplate.delete(redisKey);
        }
        QueryWrapper<UserFreezeMXY> query = new QueryWrapper<>();
        query.eq("user_id", param.get("userId"));
        userFreezeMapperMXY.delete(query);
        result.put("message", "操作成功");
        sendRocketMQ(FREEZINGRELEASEDYNAMICSFOREVER, "对" + param.get("userId") + "进行了" + "解冻");

        return result;
    }

}
