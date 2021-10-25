package com.tanhua.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.common.mapper.UserLogInfoMapper_zxk;
import com.tanhua.common.pojo.UserLogInfo;
import com.tanhua.vo.LogsVOListMXY;
import com.tanhua.vo.PageResultMXY;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserLogsListMXY {
    @Autowired
    private UserLogInfoMapper_zxk userLogInfoMapper_zxk;

    public PageResultMXY queryUserLogs(Map<String, String> param){
        QueryWrapper<UserLogInfo> query = new QueryWrapper<>();
        if("descending".equals(param.get("sortOrder"))){
            query.orderByDesc("login_time");
        }else {
            query.orderByAsc("login_time");
        }
        Page<UserLogInfo> page = new Page<>();
        page.setCurrent(Convert.toLong(param.get("page")));
        page.setSize(Convert.toLong(param.get("pagesize")));
        query.eq("user_id",Convert.toLong(param.get("uid")));
        IPage<UserLogInfo> infoIPage = userLogInfoMapper_zxk.selectPage(page, query);
        List<UserLogInfo> logInfoList = infoIPage.getRecords();
        List<LogsVOListMXY> listMXIES = new ArrayList<>();
        for (UserLogInfo userLogInfo : logInfoList) {
            LogsVOListMXY logsVOListMXY = new LogsVOListMXY();
            logsVOListMXY.setId(StrUtil.toString(userLogInfo.getId()));
            logsVOListMXY.setLogTime(userLogInfo.getLoginTime());
            logsVOListMXY.setPlace(userLogInfo.getLoginAddress());
            logsVOListMXY.setEquipment(userLogInfo.getLoginDevice());
            listMXIES.add(logsVOListMXY);
        }

        if(CollUtil.isEmpty(listMXIES)){
            return null;
        }
        PageResultMXY pageResultMXY = new PageResultMXY();
        pageResultMXY.setCounts(listMXIES.size());
        pageResultMXY.setPage(Convert.toInt(param.get("page")));
        pageResultMXY.setPagesize(Convert.toInt(param.get("pagesize")));
        pageResultMXY.setPages(Convert.toInt(Math.ceil(listMXIES.size() / Convert.toInt(param.get("pagesize")))) + 1);

        pageResultMXY.setItems(listMXIES);

        return pageResultMXY;

    }
}
