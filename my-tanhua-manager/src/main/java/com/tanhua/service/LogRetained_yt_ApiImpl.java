package com.tanhua.service;


import com.tanhua.api.LogRetained_yt_Api;

import com.tanhua.common.enums.LogRetainedEnum_yt;
import com.tanhua.common.mapper.LogRetained_yt_Mapper;
import com.tanhua.common.pojo.LogRetained_yt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogRetained_yt_ApiImpl implements LogRetained_yt_Api {

    @Autowired
    LogRetained_yt_Mapper logRetained_yt_mapper;

    /**
     * 将对象写入数据库
     *
     * @param logRetained_yt
     * @return
     */
    @Override
    public Boolean saveLogRetained(LogRetained_yt logRetained_yt) {
        System.out.println(logRetained_yt);


        int insert = (int) logRetained_yt_mapper.insert(logRetained_yt);
        if (insert != 0) {
            return true;
        }
        return false;
    }

    /**
     * 将数据全部返回到前台
     *
     * @return
     */
    @Override
    public List<LogRetained_yt> queryLogRetained_yt_list() {

        List<LogRetained_yt> logRetained_yts = this.logRetained_yt_mapper.selectList(null);
        for (LogRetained_yt logRetained_yt : logRetained_yts) {
            System.out.println(logRetained_yt);
        }
        return logRetained_yts;
    }

    /**
     * 根据id返回个人数据
     * 查看功能
     *
     * @param id
     * @return
     */
    @Override
    public LogRetained_yt queryLogRetained_ytById(Integer id) {
        LogRetained_yt logRetained_yt = this.logRetained_yt_mapper.selectById(id);
        System.out.println(logRetained_yt);
        System.out.println(LogRetainedEnum_yt.DEFAULT);
        return logRetained_yt;
    }

    public List<LogRetained_yt> findAll() {
        return logRetained_yt_mapper.selectList(null);
    }

    public LogRetained_yt findById(Integer id) {
        return logRetained_yt_mapper.selectById(id);
    }
}
