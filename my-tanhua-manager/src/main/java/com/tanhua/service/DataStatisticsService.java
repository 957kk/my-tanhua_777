package com.tanhua.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.common.pojo.DailyDate;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.pojo.T_A;
import com.tanhua.vo.RegistrationVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * @program: my-tanhua_777
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-24 21:42
 **/
@Service
public class DataStatisticsService {
    @Autowired
    private DashboardService dashboardService;

    /**
     * 一天86,400,000毫秒
     */
    private static final Long DAY = 86400000L;

    /**
     * 获取日数据
     *
     * @param sd
     * @param ed
     * @param sex
     * @return
     */
    public TreeMap<Long, DailyDate> getdailyDate(Long sd, Long ed, Integer sex) {
        if (ObjectUtil.isAllNotEmpty(sd, ed)) {
            if (sd > ed) {
                return null;
            } /*else if (ObjectUtil.equal(sd, ed) || DateUtil.isSameDay(new Date(sd), new Date(ed))) {
                TreeMap<Long, DailyDate> map = new TreeMap<>();
                DailyDate dailyDate1 = new DailyDate();
                sd = dateTime.withMillisOfDay(0).plusDays(0).getMillis();
                dailyDate1.setDate(new DateTime(sd, DateTimeZone.forID("+08:00")));
                dailyDate1.setActivePersons(dashboardService.getActiveUsersCount(sd, ed, sex));
                dailyDate1.setActiveCount(dashboardService.newUserNoUserInfo(sd));
                dailyDate1.setRegistrationCount(dashboardService.newUserInfo(sd));
                dailyDate1.setLoginCount(dashboardService.loginTimes(sd));
                map.put(sd, dailyDate1);
                return map;
            }*/ else {
                return getLongDailyDateMap(sd, ed, sex);
            }
        }
        return null;
    }

    /**
     * 获取  TreeMap<Long, DailyDate>类型的map 对DailyDate进行数据封装
     * @param sd 开始时间戳
     * @param ed 结束时间戳
     * @param sex 性别
     * @return
     */
    private TreeMap<Long, DailyDate> getLongDailyDateMap(Long sd, Long ed, Integer sex) {
        TreeMap<Long, DailyDate> map1 = new TreeMap<>();
        for (int i = 0; ; i++) {
            DailyDate dailyDate1 = new DailyDate();
            dailyDate1.setDate(new DateTime(sd, DateTimeZone.forID("+08:00")));
            dailyDate1.setActivePersons(dashboardService.getActiveUsersCount
                    (new DateTime(sd, DateTimeZone.forID("+08:00")).withMillisOfDay(0).plusDays(0).getMillis(),
                            new DateTime(sd, DateTimeZone.forID("+08:00")).withMillisOfDay(0).plusDays(1).getMillis(), sex));
            dailyDate1.setActiveCount(dashboardService.newUserNoUserInfo(sd));
            dailyDate1.setRegistrationCount(dashboardService.newUserInfo(sd).size());
            dailyDate1.setLoginCount(dashboardService.loginTimes(sd));
            map1.put(sd, dailyDate1);
            if (DateUtil.isSameDay(new Date(sd), new Date(ed))) {
                break;
            }
            sd += DAY;
        }
        return map1;
    }

    public TreeMap<Long, RegistrationVo> getRegistrationVo(Long sd, Long ed, Integer sex) {
        if (ObjectUtil.isAllNotEmpty(sd, ed)) {
            if (sd > ed) {
                return null;
            } else {
                RegistrationVo registrationVo = new RegistrationVo();





            }
        }

        return null;
    }

    /**
     * 获取  TreeMap<Long, RegistrationVo>类型的map 对RegistrationVo进行数据封装
     * @param sd 开始时间戳
     * @param ed 结束时间戳
     * @param sex 性别
     * @return
     */
    public TreeMap<Long, RegistrationVo> getLongRegistrationVoMap(Long sd, Long ed, Integer sex) {
        TreeMap<Long, RegistrationVo> map1 = new TreeMap<>();
        for (int i = 0; ; i++) {
            RegistrationVo registrationVo = new RegistrationVo();
            DailyDate dailyDate1 = new DailyDate();
            //todo
            registrationVo.setDate(new DateTime(sd, DateTimeZone.forID("+08:00")));
            List<UserInfo> userInfos = dashboardService.newUserInfo(sd);
            registrationVo.setRegistrationCount(userInfos.size());
            Object[] objects=new Object[8];
            List<Long> ids = CollUtil.getFieldValues(userInfos, "userId", Long.class);
            Long st=sd;
            for (int j = 0; j < objects.length; j++) {
                if(st>System.currentTimeMillis()||ObjectUtil.isEmpty(ids)){
                    objects[j] = T_A.builder().title(1 + j + "").amount(0).build();
                }else {
                    objects[j] = T_A.builder().title(1 + j + "").amount(dashboardService.getRetentionRate(st, ids)).build();
                }if(j==objects.length-2){
                    st+=23*DAY;
                }else {
                    st+=DAY;
                }
            }
            registrationVo.setRate(objects);
            map1.put(sd, registrationVo);
            if (DateUtil.isSameDay(new Date(sd), new Date(ed))) {
                break;
            }
            sd += DAY;
        }
        return map1;
    }

}

