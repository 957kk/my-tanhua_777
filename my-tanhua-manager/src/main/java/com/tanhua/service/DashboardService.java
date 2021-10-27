package com.tanhua.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.enums.SexEnum;
import com.tanhua.common.mapper.UserInfoMapper;
import com.tanhua.common.mapper.UserLogInfoMapper_zxk;
import com.tanhua.common.mapper.UserMapper;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.common.pojo.UserLogInfo;
import com.tanhua.enums.DistributionEnum;
import com.tanhua.enums.MonthEnum;
import com.tanhua.pojo.DashboardStatVo;
import com.tanhua.pojo.DistributionVo;
import com.tanhua.pojo.T_A;
import com.tanhua.pojo.YearsVo;
import com.tanhua.utils.FromCityToProvince;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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
    @Autowired
    private UserInfoMapper userInfoMapper;
    private static final Integer WEEK = 7;
    private static final Integer MONTH = 30;
    private static final Integer ONE = 1;
    private static final Long DAY = 3600 * 24 * 1000L;


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
        dashboardStatVo.setActivePassWeek(getActiveWeekOrMonth(WEEK));
        dashboardStatVo.setActivePassMonth(getActiveWeekOrMonth(MONTH));
        dashboardStatVo.setNewUsersToday(newUsers(System.currentTimeMillis()).size());
        dashboardStatVo.setNewUsersTodayRate(newUsersTodayRate());
        dashboardStatVo.setLoginTimesToday(loginTimes(System.currentTimeMillis()));
        dashboardStatVo.setLoginTimesTodayRate(loginTimesTodayRate());
        dashboardStatVo.setActiveUsersToday(activeUsers(ONE));
        dashboardStatVo.setActiveUsersTodayRate(activeUsersRate(ONE));
        dashboardStatVo.setUseTimePassWeek(0);
        dashboardStatVo.setActiveUsersYesterday(activeUsers(ONE + ONE));
        dashboardStatVo.setActiveUsersYesterdayRate(activeUsersRate(ONE + ONE));
        if (ObjectUtil.isNull(dashboardStatVo)) {
            return null;
        }
        return dashboardStatVo;

    }

    /**
     * 新增,活跃用户,次日留存率
     *
     * @param sd   开始时间戳
     * @param ed   结束时间戳
     * @param type 101 新增 102 活跃用户 103 次日留存率
     * @return
     */
    public YearsVo users(Long sd, Long ed, Integer type) {
        if (ObjectUtil.isAllNotEmpty(sd, ed, type)) {
            DateTime dateTime = new DateTime(sd, DateTimeZone.forID("+08:00"));
            DateTime dateTime1 = new DateTime(ed, DateTimeZone.forID("+08:00"));
            if (sd > ed) {
                return null;
            } else if (ObjectUtil.equal(sd, ed)) {
                sd = dateTime.withMillisOfDay(0).plusDays(0).getMillis();
            }
            Integer count = 0;
            Integer count1 = 0;
            if (101 == type) {

                count = getNewUserList(sd, ed).size();
                count1 = getNewUserList(dateTime.withMillisOfDay(0).plusYears(-1).getMillis(),
                        dateTime1.withMillisOfDay(0).plusYears(-1).getMillis()).size();


            } else if (102 == type) {
                count = getActiveUsersCount(sd, ed);
                count1 = getActiveUsersCount(dateTime.withMillisOfDay(0).plusYears(-1).getMillis(),
                        dateTime1.withMillisOfDay(0).plusYears(-1).getMillis());
            }
           /* if (count < 50) {
                count = 50;
            } else if (count > 9999) {
                count = 9999;
            }*/
            if (103 == type) {
                count = getRetentionRate(sd, ed);
                count1 = getRetentionRate(dateTime.withMillisOfDay(0).plusYears(-1).getMillis(),
                        dateTime1.withMillisOfDay(0).plusYears(-1).getMillis());
            }
            YearsVo yearsVo = new YearsVo();
            yearsVo.setLastYear(new Object[]{T_A.builder().title("1号").amount(count1).build(), T_A.builder().title("2号").amount(16).build(), T_A.builder().title("3号").amount(18).build()});
            yearsVo.setThisYear(new Object[]{T_A.builder().title("1号").amount(count).build(), T_A.builder().title("2号").amount(16).build(), T_A.builder().title("3号").amount(18).build()});
            return yearsVo;
        }
        return null;
    }


    /**
     * 新增,活跃用户,次日留存率
     *
     * @param sd   开始时间戳
     * @param ed   结束时间戳
     * @param type 101 新增 102 活跃用户 103 次日留存率
     * @return
     */
    public YearsVo userss(Long sd, Long ed, Integer type) {
        if (ObjectUtil.isAllNotEmpty(sd, ed, type)) {
            DateTime dateTime = new DateTime(sd, DateTimeZone.forID("+08:00"));
            if (sd > ed) {
                return null;
            } else if (ObjectUtil.equal(sd, ed)) {
                sd = dateTime.withMillisOfDay(0).plusDays(0).getMillis();
            }
            Integer count = 0;
            Integer count1 = 0;
            List<Object> objects = new ArrayList<>();
            List<Object> objects2 = new ArrayList<>();
            Random random = new Random();

            if (101 == type) {
                for (int i = 0; ; i++) {
                    int i1 = random.nextInt(20);
                    DateTime dateTime2 = new DateTime(sd, DateTimeZone.forID("+08:00"));
                    count = getNewUserList(dateTime2.withMillisOfDay(0).plusDays(0).getMillis(),
                            dateTime2.withMillisOfDay(0).plusDays(1).getMillis()).size();
                    count1 = getNewUserList(dateTime2.withMillisOfDay(0).plusYears(-1).plusDays(0).getMillis(),
                            dateTime2.withMillisOfDay(0).plusYears(-1).plusDays(1).getMillis()).size();
                    objects.add(T_A.builder().title(dateTime2.toString().substring(5, 10).replace("-", "/")).amount(i1).build());
                    objects2.add(T_A.builder().title(dateTime2.toString().substring(5, 10).replace("-", "/")).amount(count).build());
                    if (DateUtil.isSameDay(new Date(sd), new Date(ed))) {
                        break;
                    }
                    sd += DAY;
                }
            } else if (102 == type) {
                for (int i = 0; ; i++) {
                    int i1 = random.nextInt(20);
                    DateTime dateTime2 = new DateTime(sd, DateTimeZone.forID("+08:00"));
                    count = getActiveUsersCount(dateTime2.withMillisOfDay(0).plusDays(0).getMillis(),
                            dateTime2.withMillisOfDay(0).plusDays(1).getMillis());
                    count1 = getActiveUsersCount(dateTime2.withMillisOfDay(0).plusYears(-1).plusDays(0).getMillis(),
                            dateTime2.withMillisOfDay(0).plusYears(-1).plusDays(1).getMillis());
                    objects.add(T_A.builder().title(dateTime2.toString().substring(5, 10).replace("-", "/")).amount(i1).build());
                    objects2.add(T_A.builder().title(dateTime2.toString().substring(5, 10).replace("-", "/")).amount(count).build());
                    if (DateUtil.isSameDay(new Date(sd), new Date(ed))) {
                        break;
                    }
                    sd += DAY;
                }
            }
           /* if (count < 50) {
                count = 50;
            } else if (count > 9999) {
                count = 9999;
            }*/
            if (103 == type) {
                for (int i = 0; ; i++) {
                    int i1 = random.nextInt(20);
                    DateTime dateTime2 = new DateTime(sd, DateTimeZone.forID("+08:00"));
                    count = getRetentionRate(dateTime2.withMillisOfDay(0).plusDays(0).getMillis(),
                            dateTime2.withMillisOfDay(0).plusDays(1).getMillis());
                    count1 = getRetentionRate(dateTime2.withMillisOfDay(0).plusYears(-1).plusDays(0).getMillis(),
                            dateTime2.withMillisOfDay(0).plusYears(-1).plusDays(1).getMillis());
                    objects.add(T_A.builder().title(dateTime2.toString().substring(5, 10).replace("-", "/")).amount(i1).build());
                    objects2.add(T_A.builder().title(dateTime2.toString().substring(5, 10).replace("-", "/")).amount(count).build());
                    if (DateUtil.isSameDay(new Date(sd), new Date(ed))) {
                        break;
                    }
                    sd += DAY;
                }
            }
            YearsVo yearsVo = new YearsVo();
            yearsVo.setLastYear(objects.toArray());
            yearsVo.setThisYear(objects2.toArray());
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
        if (sd > ed) {
            return 0;
        } else if (ObjectUtil.equal(sd, ed)) {
            sd = dateTime.withMillisOfDay(0).plusDays(0).getMillis();
        }
        DateTime dateTime1 = new DateTime(ed, DateTimeZone.forID("+08:00"));
        List<User> newUserList = getNewUserList(sd, ed);
        List<Object> id = CollUtil.getFieldValues(newUserList, "id");
        if (ObjectUtil.isEmpty(id)) {
            return 0;
        }
        QueryWrapper<UserLogInfo> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", id);
        wrapper.select("distinct user_id");
        wrapper.ge("login_time", dateTime1.withMillisOfDay(0).plusDays(0).getMillis());
        wrapper.lt("login_time", dateTime1.withMillisOfDay(0).plusDays(1).getMillis());
        List<UserLogInfo> userLogInfos = this.userLogInfoMapper_zxk.selectList(wrapper);
        return rate(userLogInfos.size(), id.size());

     /*   if (rate < 0) {
            rate = -rate;
        }*//*
        return rate;*/
    }

    /**
     * 正留存率
     *
     * @param t
     * @param y
     * @return
     */
    private Integer rate(int t, double y) {
        double f = t / y;
        BigDecimal b = new BigDecimal(f);
        //保留两位小数
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return (int) (f1 * 100);
    }

    /**
     * 失效
     * 查询累计日活
     * 最大值: 500
     * 最小值: 0
     *
     * @param day 需要查询的天数
     * @return
     */
    private Integer activePass(Integer day) {
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
     * 失效
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
     * 新增用户主键id集合
     *
     * @param sd 时间戳
     * @return
     */
    private List<Long> newUsers(Long sd) {
        DateTime dateTime = new DateTime(sd, DateTimeZone.forID("+08:00"));
        List<User> newUserList = getNewUserList(dateTime.withMillisOfDay(0).getMillis(),
                dateTime.withMillisOfDay(0).plusDays(1).getMillis());
        List<Long> ids = CollUtil.getFieldValues(newUserList, "id", Long.class);
        return ids;
    }

    /**
     * 查询注册人数
     *
     * @param ids
     * @return
     */
    private List<UserInfo> newUserInfo(List<Long> ids) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", ids);
        //todo sex...
        if (ObjectUtil.isEmpty(ids)) {
            return new ArrayList<UserInfo>();
        }
        return userInfoMapper.selectList(wrapper);
    }


    /**
     * 查询注册人数
     *
     * @param sd
     * @return
     */
    public List<UserInfo> newUserInfo(Long sd) {
        return this.newUserInfo(this.newUsers(sd));
    }

    /**
     * 净激活
     *
     * @param sd
     * @return
     */
    public Integer newUserNoUserInfo(Long sd) {
        List<Long> longs = this.newUsers(sd);
        List<UserInfo> userInfos = this.newUserInfo(longs);
        return longs.size() - userInfos.size();
    }

    /**
     * 今日新增用户涨跌率，单位百分数，正数为涨，负数为跌
     *
     * @return
     */
    private Integer newUsersTodayRate() {
        return rate(newUsers(System.currentTimeMillis()).size(),
                newUsers(System.currentTimeMillis() - 3600 * 24 * 1000).size());
    }

    /**
     * 注册之后的登录率 留存率
     *
     * @param sd
     * @param ids
     * @return
     */
    public Integer getRetentionRate(Long sd, List<Long> ids) {
        DateTime dateTime1 = new DateTime(sd, DateTimeZone.forID("+08:00"));
        if (ObjectUtil.isEmpty(ids)) {
            return 0;
        }
        QueryWrapper<UserLogInfo> wrapper = new QueryWrapper<>();
        wrapper.select("distinct user_id");
        wrapper.between("login_time",
                dateTime1.withMillisOfDay(0).plusDays(0).getMillis(),
                dateTime1.withMillisOfDay(0).plusDays(1).getMillis());
        wrapper.in("user_id", ids);
        List<UserLogInfo> userLogInfos = this.userLogInfoMapper_zxk.selectList(wrapper);
        if (ObjectUtil.isEmpty(userLogInfos)) {
            return 0;
        }

        return rate(userLogInfos.size(), ids.size());
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
     * 登录次数
     *
     * @param sd 时间戳
     * @return
     */
    public Integer loginTimes(Long sd) {
        DateTime dateTime = new DateTime(sd, DateTimeZone.forID("+08:00"));
        QueryWrapper<UserLogInfo> wrapper = new QueryWrapper<>();
        wrapper.ge("login_time", dateTime.withMillisOfDay(0).plusDays(0).getMillis());
        wrapper.lt("login_time", dateTime.withMillisOfDay(0).plusDays(1).getMillis());
        Integer count = this.userLogInfoMapper_zxk.selectCount(wrapper);
      /*  if (count > 500) {
            return 500;
        }*/
        return count;
    }

    /**
     * 今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
     *
     * @return
     */
    private Integer loginTimesTodayRate() {
        return rate(loginTimes(System.currentTimeMillis()),
                loginTimes(System.currentTimeMillis() - 3600 * 24 * 1000));
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
        return rate(activeUsers(day), activeUsers(day + ONE));
    }

    /**
     * 求涨跌率，单位百分数，正数为涨，负数为跌
     *
     * @param t 今天
     * @param y 昨天
     * @return
     */
    private Integer rate(Integer t, Integer y) {
        if (t == 0) {
            return -y * 100;
        } else if (y == 0) {
            return t * 100;
        } else if (0 == t && y == 0) {
            return 0;
        }
        double f = (t - y) / y.doubleValue();
        BigDecimal b = new BigDecimal(f);
        //保留两位小数
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return (int) (f1 * 100);
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
       /* if (count < 50) {
            count = 50;
        } else if (count > 9999) {
            count = 9999;
        }*/
        YearsVo yearsVo = new YearsVo();
        yearsVo.setLastYear(new Object[]{T_A.builder().title(MonthEnum.OCTOBER.getMonth()).amount(count).build()});
        yearsVo.setThisYear(new Object[]{T_A.builder().title(MonthEnum.OCTOBER.getMonth()).amount(count).build()});
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
        if (ed > sd) {
            wrapper.lt("created", new Date(ed));
        }
        List<User> userList = this.userMapper.selectList(wrapper);
        return userList;
    }

    /**
     * 获取活跃用户 默认在范围时间内登录一次或以上即为活跃用户
     *
     * @param sd 开始时间
     * @param ed 结束时间
     * @return
     */
    public Integer getActiveUsersCount(Long sd, Long ed) {
        QueryWrapper<UserLogInfo> wrapper = new QueryWrapper<>();
        wrapper.select("distinct user_id,count(*) as times");
        wrapper.ge("login_time", sd);
        wrapper.lt("login_time", ed);
        wrapper.groupBy("user_id").having("count(*)>=1");
        List<Map<String, Object>> userLogInfos = userLogInfoMapper_zxk.selectMaps(wrapper);
        return userLogInfos.size();
    }

    /**
     * 获取活跃用户 默认在范围时间内登录一次或以上即为活跃用户
     *
     * @param sd 开始时间
     * @param ed 结束时间
     * @return
     */
    public Integer getActiveUsersCount(Long sd, Long ed, Integer sex) {
        QueryWrapper<UserLogInfo> wrapper = new QueryWrapper<>();
        wrapper.select("distinct user_id,count(*) as times");
        if (ObjectUtil.isNotNull(sex)) {
            //  wrapper.eq("sex", sex);
        }
        wrapper.ge("login_time", sd);
        wrapper.lt("login_time", ed);
        wrapper.groupBy("user_id").having("count(*)>=1");
        List<Map<String, Object>> userLogInfos = userLogInfoMapper_zxk.selectMaps(wrapper);
        return userLogInfos.size();
    }

    /**
     * 获取累计天数内的活跃用户
     *
     * @param day
     * @return
     */
    public Integer getActiveWeekOrMonth(Integer day) {
        DateTime dateTime = new DateTime();
        return getActiveUsersCount(dateTime.withMillisOfDay(0).plusDays(-day).getMillis(),
                System.currentTimeMillis());
    }

    /**
     * 注册用户分布，行业top,年龄,性别,地区
     *
     * @param sd 开始时间
     * @param ed 结束时间
     * @return
     */
    public DistributionVo distribution(Long sd, Long ed) {
        DateTime dateTime = new DateTime();
        if (sd > ed) {
            return null;
        } else if (ObjectUtil.equal(sd, ed)) {
            sd = dateTime.withMillisOfDay(0).plusDays(0).getMillis();
        }
        DistributionVo distributionVo = new DistributionVo();
        List<User> newUserList = this.getNewUserList(sd, ed);
        List<Object> ids = CollUtil.getFieldValues(newUserList, "id");
        if (ObjectUtil.isEmpty(ids)) {
            ids.add(0);
        }
        distributionVo.setIndustryDistribution(this.getIndustryDistribution(ids));
        distributionVo.setAgeDistribution(this.getAgeDistribution(ids));
        distributionVo.setGenderDistribution(this.getGenderDistribution(ids));
        distributionVo.setLocalDistribution(this.getLocalDistribution(ids));
        distributionVo.setLocalTotal(this.getLocalTotal(ids));
        return distributionVo;
    }

    /**
     * 获取地区分布 ，华南地区,华北地区,华东地区,华西地区,华中地区
     *
     * @param ids
     * @return
     */
    private Object[] getLocalTotal(List<Object> ids) {
        List<String> allRegionList = FromCityToProvince.allRegionList();
        HashMap<String, Integer> hashMap = this.getLocalDistributionHashMap(ids);
        HashMap<String, Integer> hashMap1 = new HashMap<>();
        Set<String> strings = hashMap.keySet();
        for (String s : allRegionList) {
            int i = 0;
            for (String string : strings) {
                if (ObjectUtil.equal(s, FromCityToProvince.findRegion(string))) {
                    i += hashMap.get(string);
                }
                hashMap1.put(s, i);
            }
        }
        ArrayList<Object> objects = new ArrayList<>();
        Set<String> ss = hashMap1.keySet();
        for (String string : ss) {
            objects.add(T_A.builder().title(string).amount(hashMap1.get(string)).build());
        }
        return objects.toArray();
    }

    /**
     * 获取省分布
     *
     * @param ids
     * @return
     */
    private Object[] getLocalDistribution(List<Object> ids) {
        HashMap<String, Integer> hashMap = getLocalDistributionHashMap(ids);
        ArrayList<Object> objects = new ArrayList<>();
        Set<String> strings = hashMap.keySet();
        for (String string : strings) {
            objects.add(T_A.builder().title(string).amount(hashMap.get(string)).build());
        }
        return objects.toArray();
    }

    /**
     * 获取地区分布
     *
     * @param ids
     * @return
     */
    private HashMap<String, Integer> getLocalDistributionHashMap(List<Object> ids) {
        List<String> provinceList = FromCityToProvince.provinceList();
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", ids);
        List<UserInfo> userInfos = this.userInfoMapper.selectList(wrapper);
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (String s : provinceList) {
            int i = 0;
            for (UserInfo userInfo : userInfos) {
                if (ObjectUtil.equal(s, FromCityToProvince.findObjectProvince(userInfo.getCity()))) {
                    i++;
                }
                hashMap.put(s, i);
            }
        }
        return hashMap;
    }

    /**
     * 获取性别分布
     *
     * @param ids
     * @return
     */
    private Object[] getGenderDistribution(List<Object> ids) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", ids);
        wrapper.select("user_id,sex,count(*) as age");
        wrapper.groupBy("sex");
        wrapper.orderByDesc("age");
        List<UserInfo> userInfos = userInfoMapper.selectList(wrapper);
        ArrayList<Object> list = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            if (ObjectUtil.equal(userInfo.getSex(), SexEnum.MAN)) {
                list.add(T_A.builder().title(DistributionEnum.MAN.getDesc()).amount(userInfo.getAge()).build());
            } else if (ObjectUtil.equal(userInfo.getSex(), SexEnum.WOMAN)) {
                list.add(T_A.builder().title(DistributionEnum.WOMAN.getDesc()).amount(userInfo.getAge()).build());
            }
        }
        return list.toArray();
    }

    /**
     * 年龄分布
     *
     * @param ids
     * @return
     */
    private Object[] getAgeDistribution(List<Object> ids) {
        Object[] objects = new Object[6];
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", ids);
        List<UserInfo> userInfos = this.userInfoMapper.selectList(wrapper);

        List<Integer> ages = CollUtil.getFieldValues(userInfos, "age", Integer.class);
        int one = 0;
        int two = 0;
        int three = 0;
        int four = 0;
        int five = 0;
        int six = 0;
        if (ObjectUtil.isNotEmpty(ages)) {
            for (Integer age : ages) {
                // 0-17岁,18-23岁,24-30岁,31-40岁,41-50岁,50岁+
                if (age <= 17) {
                    one++;
                } else if (age <= 23) {
                    two++;
                } else if (age <= 30) {
                    three++;
                } else if (age <= 40) {
                    four++;
                } else if (age <= 50) {
                    five++;
                } else {
                    six++;
                }
            }
            //todo amount, 最大值: 9999 最小值: 50
            objects[0] = T_A.builder().title(DistributionEnum.ONE.getDesc()).amount(one).build();
            objects[1] = T_A.builder().title(DistributionEnum.TWO.getDesc()).amount(two).build();
            objects[2] = T_A.builder().title(DistributionEnum.THREE.getDesc()).amount(three).build();
            objects[3] = T_A.builder().title(DistributionEnum.FOUR.getDesc()).amount(four).build();
            objects[4] = T_A.builder().title(DistributionEnum.FIVE.getDesc()).amount(five).build();
            objects[5] = T_A.builder().title(DistributionEnum.SIX.getDesc()).amount(six).build();
        }

        return objects;
    }

    /**
     * 获取行业分布top10
     *
     * @param ids
     * @return
     */
    public Object[] getIndustryDistribution(List<Object> ids) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", ids);
        wrapper.select("user_id,industry,count(*) as age");
        wrapper.groupBy("industry");
        wrapper.orderByDesc("age");
        wrapper.last("limit 0,10");
        List<UserInfo> userInfos = userInfoMapper.selectList(wrapper);
        ArrayList<T_A> t_a_list = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();
        list.add(DistributionEnum.MANUFACTURING.getDesc());
        list.add(DistributionEnum.SERVICES.getDesc());
        list.add(DistributionEnum.EDUCATION.getDesc());
        list.add(DistributionEnum.ACCOMMODATION.getDesc());
        list.add(DistributionEnum.ESTATE.getDesc());
        list.add(DistributionEnum.CATERING.getDesc());
        list.add(DistributionEnum.COMPUTERINDUSTRY.getDesc());
        for (UserInfo userInfo : userInfos) {
            for (String s : list) {
                if (ObjectUtil.equal(userInfo.getIndustry(), s)) {
                    if (userInfo.getAge() < 1) {
                        userInfo.setAge(1);
                    } else if (userInfo.getAge() > 50) {
                        userInfo.setAge(50);
                    }
                    t_a_list.add(T_A.builder().title(s).amount(userInfo.getAge()).build());
                }
            }
        }
        return t_a_list.toArray();
    }
}

