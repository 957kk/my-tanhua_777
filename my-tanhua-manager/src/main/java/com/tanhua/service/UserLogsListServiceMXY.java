package com.tanhua.service;

import cn.hutool.core.util.StrUtil;
import com.tanhua.impl.UserLogsListMXY;
import com.tanhua.vo.PageResultMXY;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserLogsListServiceMXY {
    @Autowired
    private UserLogsListMXY userLogsListMXY;

    public PageResultMXY queryUserLogsList(String uid, String page, String pagesize, String sortProp, String sortOrder){
        if(StrUtil.isAllEmpty(uid,page,pagesize,sortProp,sortOrder)){
            log.error("查询参数存在null值 uid =" +uid + "---" +"page = " +page +"---"+"pagesize = " +pagesize +"---"+"sortProp = " +sortProp +"---"+"sortOrder = " +sortOrder);
            return null;
        }
        Map<String,String> param = new HashMap<>();
        param.put("uid",uid);
        param.put("page",page);
        param.put("pagesize",pagesize);
        param.put("sortProp",sortProp);
        param.put("sortOrder",sortOrder);
        PageResultMXY pageResultMXY = null;
        try {
            pageResultMXY = userLogsListMXY.queryUserLogs(param);
        } catch (Exception e) {
            log.error("获取用户登录记录出错，uid = "+ uid,e);
        }

        return pageResultMXY;

    }
}
