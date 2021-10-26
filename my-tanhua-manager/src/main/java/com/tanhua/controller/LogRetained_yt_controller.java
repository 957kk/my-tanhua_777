package com.tanhua.controller;

import cn.hutool.core.util.StrUtil;
import com.tanhua.common.pojo.LogRetained_yt;
import com.tanhua.service.LogRetained_yt_ApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("log")
public class LogRetained_yt_controller {

    @Autowired
    LogRetained_yt_ApiImpl logRetained_yt_ApiImpl;



    @PostMapping("save")
    public Boolean saveLogRetained(@RequestBody Map<String, Object> param){
        LogRetained_yt logRetained_yt = new LogRetained_yt();
      // logRetained_yt.setId(2);
        logRetained_yt.setUsername(StrUtil.toString( param.get("username")));
        logRetained_yt.setIp(StrUtil.toString( param.get("ip")));
        logRetained_yt.setTime(new Date());
        logRetained_yt.setO(StrUtil.toString (param.get("Operation")) );
        logRetained_yt.setA(StrUtil.toString( param.get("Describe")));

        Boolean data = this.logRetained_yt_ApiImpl.saveLogRetained(logRetained_yt);
        return data;
    }

    @PostMapping("findall")
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
    public LogRetained_yt findById(@RequestBody Map<String, Object> param){


        LogRetained_yt logRetained_yt =   logRetained_yt_ApiImpl.findById(Integer.parseInt((String) param.get("id")));
        System.out.println(logRetained_yt);

        return logRetained_yt;
    }

}
