package com.tanhua.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.mapper.UserFreezeMapperMXY;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserFreezeMXY;


public class UserFreezenState {


    public static boolean isFreezen(User user, int state,UserFreezeMapperMXY userFreezeMapperMXY) {
        QueryWrapper<UserFreezeMXY> query = new QueryWrapper<>();
        query.eq("user_id",user.getId());
        query.eq("freezing_range",state);
        UserFreezeMXY userFreezeMXY = userFreezeMapperMXY.selectOne(query);
        if(ObjectUtil.isNotNull(userFreezeMXY)){
            //当前系统时间
            long now = System.currentTimeMillis();
            long freezeTime = 0;
            //判断冻结时间
            //冻结三天 60s
            if(userFreezeMXY.getFreezingTime()==1){
                freezeTime = userFreezeMXY.getCreated() + (60*1000);
                //冻结七天 120s
            }else if(userFreezeMXY.getFreezingTime()==2){
                freezeTime = userFreezeMXY.getCreated() + (120*1000);
                //永久冻结 比当前时间多一秒
            }else {
                freezeTime = System.currentTimeMillis()+1000;
            }
            if(freezeTime>now){
                //还处于冻结状态
                return true;
            }

        }
        return false;
    }
}
