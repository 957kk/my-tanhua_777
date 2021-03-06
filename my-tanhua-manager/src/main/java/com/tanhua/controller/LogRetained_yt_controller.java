package com.tanhua.controller;

import cn.hutool.core.util.StrUtil;
import com.tanhua.common.pojo.LogRetained_yt;
import com.tanhua.common.utils.NoAuthorization;
import com.tanhua.service.LogRetained_yt_ApiImpl;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("log")
@RocketMQMessageListener(topic = "tanhua", consumerGroup = "tanhua")
public class LogRetained_yt_controller implements RocketMQListener<LogRetained_yt> {

    @Autowired
    LogRetained_yt_ApiImpl logRetained_yt_ApiImpl;



    @PostMapping("save")
    @NoAuthorization
    public Boolean saveLogRetained(@RequestBody Map<String, Object> param){
        LogRetained_yt logRetained_yt = new LogRetained_yt();
      // logRetained_yt.setId(2);
        logRetained_yt.setUsername(StrUtil.toString( param.get("username")));
        logRetained_yt.setIp(StrUtil.toString( param.get("ip")));
       // logRetained_yt.setTime(new Date());
        logRetained_yt.setO(StrUtil.toString (param.get("Operation")) );
        logRetained_yt.setA(StrUtil.toString( param.get("Describe")));

        Boolean data = this.logRetained_yt_ApiImpl.saveLogRetained(logRetained_yt);
        return data;
    }

    @PostMapping("findall")
    @NoAuthorization
    public  List<LogRetained_yt> findAll(){

        List<LogRetained_yt> all = logRetained_yt_ApiImpl.findAll();
        for (LogRetained_yt logRetained_yt : all) {
            System.out.println(logRetained_yt);
        }

        return all;
    }

    /**
     * 根据id查询
     * @return
     */

    @PostMapping("findbyid")
    @NoAuthorization
    public LogRetained_yt findById(@RequestBody Map<String, Object> param){


        LogRetained_yt logRetained_yt =   logRetained_yt_ApiImpl.findById(Integer.parseInt((String) param.get("id")));


        return logRetained_yt;
    }


    /**
     * mq接收实体类进行封装
     * @param logRetained_yt
     */
    @Override
    public void onMessage(LogRetained_yt logRetained_yt) {
      this.logRetained_yt_ApiImpl.saveLogRetained(logRetained_yt);
    }
}
