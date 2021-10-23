package com.tanhua.dashboard.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.mapper.UserLogInfoMapper_zxk;
import com.tanhua.common.mapper.UserMapper;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserLogInfo;
import com.tanhua.dashboard.enums.MonthEnum;
import com.tanhua.dashboard.pojo.DashboardStatVo;
import com.tanhua.dashboard.pojo.T_A;
import com.tanhua.dashboard.pojo.YearsVo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description: 后台统计业务
 * @author: xkZhao
 * @Create: 2021-10-22 19:31
 **/
@Service
public class DashboardService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserLogInfoMapper_zxk userLogInfoMapper_zxk;
    private static final Integer WEEK = 7;
    private static final Integer MONTH = 30;
    private static final Integer DAY = 1;


    /**
     * 概要统计
     *
     * @return
     */
    public DashboardStatVo summary() {
        DashboardStatVo dashboardStatVo = new DashboardStatVo();
        /*
         * 查询累计用户
         */
        Integer integer = userMapper.selectCount(null);
        dashboardStatVo.setCumulativeUsers(integer);
        //todo
        dashboardStatVo.setActivePassWeek(activePass(WEEK));
        //todo
        dashboardStatVo.setActivePassMonth(activePass(MONTH));
        dashboardStatVo.setNewUsersToday(newUsers(DAY));
        dashboardStatVo.setNewUsersTodayRate(newUsersTodayRate());
        dashboardStatVo.setLoginTimesToday(DAY);
        dashboardStatVo.setLoginTimesTodayRate(loginTimesTodayRate());
        dashboardStatVo.setActiveUsersToday(activeUsers(DAY));
        dashboardStatVo.setActiveUsersTodayRate(activeUsersRate(DAY));
        dashboardStatVo.setUseTimePassWeek(0);
        dashboardStatVo.setActiveUsersYesterday(activeUsers(DAY + DAY));
        dashboardStatVo.setActiveUsersYesterdayRate(activeUsersRate(DAY + DAY));
        if (ObjectUtil.isNull(dashboardStatVo)) {
            return null;
        }
        return dashboardStatVo;

    }

    /**
     * 新增、活跃用户、次日留存率
     *
     * @param sd   开始时间戳
     * @param ed   结束时间戳
     * @param type 101 新增 102 活跃用户 103 次日留存率
     * @return
     */
    public YearsVo users(Long sd, Long ed, Integer type) {
        if (ObjectUtil.isAllNotEmpty(sd, ed, type)) {
            Integer count = 0;
            if (101 == type) {
                count = getNewUserList(sd, ed).size();
            } else if (102 == type) {
                count = getActiveUsersCount(sd, ed);
            }
            if (count < 50) {
                count = 50;
            } else if (count > 9999) {
                count = 9999;
            }
            if (103 == type) {
                count = getRetentionRate(sd, ed);
            }
            YearsVo yearsVo = new YearsVo();
            yearsVo.setLastYear(new Object[]{T_A.builder().title(MonthEnum.October.getMonth() + "月").amount(count).build()});
            yearsVo.setThisYear(new Object[]{T_A.builder().title(MonthEnum.October.getMonth() + "月").amount(count).build()});
            return yearsVo;
        }
        return null;
    }
    /**
     * 获取留存率
     *
     * @param sd 开始时间
     * @param ed 结束时间
     * @return
     */
    public Integer getRetentionRate(Long sd, Long ed) {
        DateTime dateTime = new DateTime();
        List<User> newUserList = getNewUserList(sd, ed);
        List<Object> id = CollUtil.getFieldValues(newUserList, "id");
        QueryWrapper<UserLogInfo> wrapper = new QueryWrapper<>();
        wrapper.select("distinct user_id");
        wrapper.ge("login_time", dateTime.withMillisOfDay(0).plusDays(0).getMillis());
        wrapper.lt("login_time", dateTime.withMillisOfDay(0).plusDays(1).getMillis());
        wrapper.in("user_id", id);
        List<UserLogInfo> userLogInfos = this.userLogInfoMapper_zxk.selectList(wrapper);
        Integer rate = this.rate(userLogInfos.size(), id.size());
        if (rate < 0) {
            rate = -rate;
        }
        return rate;
    }

    /**
     * 查询累计日活
     * 最大值: 500
     * 最小值: 0
     *
     * @param day 需要查询的天数
     * @return
     */
    public Integer activePass(Integer day) {
        DateTime dateTime = new DateTime();
        Long max = 0L;
        Long min = 0L;
        Integer count = 0;
        for (Integer i = 0; i < day; i++) {
            max = dateTime.withMillisOfDay(0).plusDays(1 - i).getMillis();
            min = dateTime.withMillisOfDay(0).plusDays(-i).getMillis();
            QueryWrapper<UserLogInfo> wrapper = new QueryWrapper<>();
            wrapper.select("distinct user_id");
            wrapper.ge("login_time", min).lt("login_time", max);
            count += userLogInfoMapper_zxk.selectCount(wrapper);
        }
        if (count > 500) {
            return 500;
        }
        return count;
    }

    /**
     * 新增用户
     * 最大值: 500
     * 最小值: 0
     *
     * @param day 今天，1  昨天，2
     * @return
     */
    private Integer newUsers(Integer day) {
        DateTime dateTime = new DateTime();
        Integer count = 0;
        if (1 == day) {
            count = getNewUserList(dateTime.withMillisOfDay(0).getMillis(),
                    System.currentTimeMillis()).size();
        }
        if (2 == day) {
            count = getNewUserList(dateTime.withMillisOfDay(0).plusDays(-1).getMillis(),
                    dateTime.withMillisOfDay(0).getMillis()).size();
        }
        if (count > 500) {
            return 500;
        }
        return count;
    }

    /**
     * 今日新增用户涨跌率，单位百分数，正数为涨，负数为跌
     *
     * @return
     */
    private Integer newUsersTodayRate() {
        return rate(newUsers(DAY), newUsers(DAY + DAY));
    }

    /**
     * 登录次数
     *
     * @param day 今天，1  昨天，2
     * @return
     */
    private Integer loginTimes(Integer day) {
        DateTime dateTime = new DateTime();
        QueryWrapper<UserLogInfo> wrapper = new QueryWrapper<>();
        if (1 == day) {
            wrapper.ge("login_time", dateTime.withMillisOfDay(0).getMillis());
        } else {
            wrapper.ge("login_time", dateTime.withMillisOfDay(0).plusDays(1 - day).getMillis());
            wrapper.lt("login_time", dateTime.withMillisOfDay(0).plusDays(2 - day).getMillis());
        }
        Integer count = this.userLogInfoMapper_zxk.selectCount(wrapper);
        if (count > 500) {
            return 500;
        }
        return count;
    }

    /**
     * 今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
     *
     * @return
     */
    private Integer loginTimesTodayRate() {
        return rate(loginTimes(DAY), loginTimes(DAY + DAY));
    }

    /**
     * 日活跃用户
     * 最大值: 500
     * 最小值: 0
     *
     * @param day 1，今天   2，昨天   3，前天
     * @return
     */
    private Integer activeUsers(Integer day) {
        DateTime dateTime = new DateTime();
        Integer count = 0;
        if (1 == day) {
            count = getActiveUsersCount(dateTime.withMillisOfDay(0).getMillis(), System.currentTimeMillis());
        } else {
            count = getActiveUsersCount(dateTime.withMillisOfDay(0).plusDays(1 - day).getMillis(),
                    dateTime.withMillisOfDay(0).plusDays(2 - day).getMillis());
        }
        if (count > 500) {
            return 500;
        }
        return count;
    }

    /**
     * 活跃用户涨跌率，单位百分数，正数为涨，负数为跌
     *
     * @param day 1,今天涨跌   2,昨天涨跌
     * @return
     */
    private Integer activeUsersRate(Integer day) {
        return rate(activeUsers(day), activeUsers(day + DAY));
    }

    /**
     * 求涨跌率，单位百分数，正数为涨，负数为跌
     *
     * @param t 第一天数量
     * @param y 前一天数量
     * @return
     */
    private Integer rate(Integer t, Integer y) {
        double f = t.doubleValue() / y.doubleValue();
        BigDecimal b = new BigDecimal(f);
        //保留两位小数
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (t > y) {
            return (int) (f1 * 100);
        }
        return -(int) (f1 * 100);
    }

    /**
     * 失效
     * 获取日期内活跃用户  102
     *
     * @param sd 开始时间戳
     * @param ed 结束时间戳
     * @return
     */
    private YearsVo activeUsers(Long sd, Long ed) {
        Integer count = getActiveUsersCount(sd, ed);
        if (count < 50) {
            count = 50;
        } else if (count > 9999) {
            count = 9999;
        }
        YearsVo yearsVo = new YearsVo();
        yearsVo.setLastYear(new Object[]{T_A.builder().title(MonthEnum.October.getMonth() + "月").amount(count).build()});
        yearsVo.setThisYear(new Object[]{T_A.builder().title(MonthEnum.October.getMonth() + "月").amount(count).build()});
        return yearsVo;
    }

    /**
     * 失效
     * 范围新增用户 101
     *
     * @param sd 开始时间戳
     * @param ed 结束时间戳
     * @return
     */
    private YearsVo NewUsers(Long sd, Long ed) {
        Integer count = getNewUserList(sd, ed).size();
        if (count < 50) {
            count = 50;
        } else if (count > 9999) {
            count = 9999;
        }
        YearsVo yearsVo = new YearsVo();
        yearsVo.setLastYear(new Object[]{T_A.builder().title(MonthEnum.October.getMonth() + "月").amount(count).build()});
        yearsVo.setThisYear(new Object[]{T_A.builder().title(MonthEnum.October.getMonth() + "月").amount(count).build()});
        return yearsVo;
    }

    /**
     * 范围新增用户
     *
     * @param sd 开始时间戳
     * @param ed 结束时间戳
     * @return
     */
    private List<User> getNewUserList(Long sd, Long ed) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ge("created", new Date(sd));
        wrapper.lt("created", new Date(ed));
        List<User> userList = this.userMapper.selectList(wrapper);
        return userList;
    }

    /**
     * 获取活跃用户
     *
     * @param sd 开始时间
     * @param ed 结束时间
     * @return
     */
    private Integer getActiveUsersCount(Long sd, Long ed) {
        QueryWrapper<UserLogInfo> wrapper = new QueryWrapper<>();
        wrapper.select("user_id,count(*) as times");
        wrapper.ge("login_time", sd);
        wrapper.lt("login_time", ed);
        wrapper.groupBy("user_id").having("count(*)>1");
        List<Map<String, Object>> userLogInfos = userLogInfoMapper_zxk.selectMaps(wrapper);
        return userLogInfos.size();
    }

}

