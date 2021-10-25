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
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserFreezeMXY;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.common.pojo.UserLogInfo;
import com.tanhua.dubbo.server.api.UserLikeApi;
import com.tanhua.vo.UserInfoVOMXY;
import lombok.extern.slf4j.Slf4j;
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
    private RedisTemplate<String,String> redisTemplate;
    @Reference(version = "1.0.0")
    private UserLikeApi userLikeApi;

    private static final String USER_FREEZE_PREFX = "USER_FREEZE_";

    public UserInfoVOMXY queryUserInfoVO(String userId){
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
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
        query.eq("id",userId);
        User user = userMapper.selectOne(query);
        userInfoVOMXY.setMobile(user.getMobile());
        QueryWrapper<UserFreezeMXY> wapper = new QueryWrapper<>();
        wapper.eq("user_id",userInfo.getUserId());
        UserFreezeMXY userFreezeMXY = userFreezeMapperMXY.selectOne(wapper);
        if(ObjectUtil.isEmpty(userFreezeMXY)){
            userInfoVOMXY.setUserStatus(1+"");
        }else {
            userInfoVOMXY.setUserStatus(2+"");
        }
        QueryWrapper<UserLogInfo> query1 = new QueryWrapper<>();
        query1.eq("user_id",Convert.toLong(userId));
        query1.orderByDesc("login_time");
        UserLogInfo userLogInfo = userLogInfoMapper_zxk.selectOne(query1);
        userInfoVOMXY.setLastActiveTime(userLogInfo.getLoginTime());
        userInfoVOMXY.setLastLoginLocation(userLogInfo.getLoginAddress());
        userInfoVOMXY.setCountBeLiked(Convert.toInt(userLikeApi.queryFanCount(Convert.toLong(userId))));
        userInfoVOMXY.setCountLiked(Convert.toInt(userLikeApi.queryLikeCount(Convert.toLong(userId))));
        userInfoVOMXY.setCountMatching(Convert.toInt(userLikeApi.queryMutualLikeCount(Convert.toLong(userId))));

        return userInfoVOMXY;
    }

    public Map<String, String> userFreeze(UserFreezeMXY userFreezeMXY) {
        int result = -1;
        Map<String,String> flag = new HashMap<>();
        //查询用户冻结状态，如果没有则新增，如果有则修改
        QueryWrapper<UserFreezeMXY> query = new QueryWrapper<>();
        query.eq("user_id",userFreezeMXY.getUserId());
        UserFreezeMXY freezeMXY = userFreezeMapperMXY.selectOne(query);
        if(ObjectUtil.isEmpty(freezeMXY)){
            result = userFreezeMapperMXY.insert(userFreezeMXY);
        }else {
            freezeMXY.setFreezingTime(userFreezeMXY.getFreezingTime());
            freezeMXY.setFreezingRange(userFreezeMXY.getFreezingRange());
            freezeMXY.setFrozenRemarks(userFreezeMXY.getFrozenRemarks());
            freezeMXY.setReasonsForFreezing(userFreezeMXY.getReasonsForFreezing());
            result = userFreezeMapperMXY.updateById(freezeMXY);
        }
        if(result!=1){
            flag.put("message","操作失败");
            return flag;
        }
        //将用户的冻结状态存储在redis中
        String redisKey = USER_FREEZE_PREFX + userFreezeMXY.getUserId();
        try {
            switch (userFreezeMXY.getFreezingTime()){
                case 1:{
                    if(userFreezeMXY.getFreezingRange()==1){
                        //设置冻结登录三天
                        redisTemplate.opsForValue().set(redisKey,userFreezeMXY.getFreezingRange()+"",Duration.ofSeconds(10));
                    }else if(userFreezeMXY.getFreezingRange()==2){
                        //设置冻结发言三天
                        redisTemplate.opsForValue().set(redisKey,userFreezeMXY.getFreezingRange()+"",Duration.ofSeconds(10));
                    }else {
                        //设置冻结发布动态三天
                        redisTemplate.opsForValue().set(redisKey,userFreezeMXY.getFreezingRange()+"",Duration.ofSeconds(10));
                    }
                }
                break;
                case 2:{
                    if(userFreezeMXY.getFreezingRange()==1){
                        //设置冻结登录7天
                        redisTemplate.opsForValue().set(redisKey,userFreezeMXY.getFreezingRange()+"",Duration.ofSeconds(30));
                    }else if(userFreezeMXY.getFreezingRange()==2){
                        //设置冻结发言7天
                        redisTemplate.opsForValue().set(redisKey,userFreezeMXY.getFreezingRange()+"",Duration.ofSeconds(30));
                    }else {
                        //设置冻结发布动态7天
                        redisTemplate.opsForValue().set(redisKey,userFreezeMXY.getFreezingRange()+"",Duration.ofSeconds(30));
                    }
                }
                break;
                case 3:{
                    if(userFreezeMXY.getFreezingRange()==1){
                        //设置冻结登录永久
                        redisTemplate.opsForValue().set(redisKey,userFreezeMXY.getFreezingRange()+"",Duration.ofSeconds(100));
                    }else if(userFreezeMXY.getFreezingRange()==2){
                        //设置冻结发言永久
                        redisTemplate.opsForValue().set(redisKey,userFreezeMXY.getFreezingRange()+"",Duration.ofSeconds(100));
                    }else {
                        //设置冻结发布动态永久
                        redisTemplate.opsForValue().set(redisKey,userFreezeMXY.getFreezingRange()+"",Duration.ofSeconds(100));
                    }
                }
                break;
                default:
                    return null;
            }
        } catch (Exception e) {
            log.error("用户封禁状态存入redis失败 userid = "+userFreezeMXY.getUserId());
            flag.put("message","操作失败");
        }

        flag.put("message","操作成功");
        return flag;
    }

    public Map<String, String> userUFreeze(Map<String, String> param) {
        Map<String,String> result = new HashMap<>();
        //查询redis中是否还有该用户的冻结状态

        String redisKey = USER_FREEZE_PREFX + param.get("userId");
        if(redisTemplate.hasKey(redisKey)){
             redisTemplate.delete(redisKey);
        }
        QueryWrapper<UserFreezeMXY> query = new QueryWrapper<>();
        query.eq("user_id",param.get("userId"));
        userFreezeMapperMXY.delete(query);
        result.put("message","操作成功");
        return result;
    }
}
