package com.tanhua.dashboard.service;

import cn.hutool.core.date.DateUtil;
import com.tanhua.service.DashboardService;
import com.tanhua.service.DataStatisticsService;
import com.tanhua.vo.RegistrationVo;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-22 20:35
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class DashboardServiceTest {
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private DataStatisticsService dataStatisticsService;
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void dashboardServiceTest(){
       // Integer integer = dashboardService.activePass(1000);

       // DateTime dt=new DateTime().withMillisOfDay(0);
      //  System.out.println(dt.toString("yyyy-MM-dd HH:mm:ss"));
       // Integer integer = dashboardService.newUsers(1);

       // Integer integer = dashboardService.newUsersTodayRate();
        //Integer integer = dashboardService.loginTimes(2);
       //Integer integer = dashboardService.activeUsers(3);
        long l = System.currentTimeMillis()+3600*1000*24;
        System.out.println(l);
      //  Integer retentionRate = dashboardService.getRetentionRate(1634872449129L, 1634958849129L);

       // Integer integer = dashboardService.activePass(7);
      //  Integer integer = dashboardService.getRetentionRate(1634872449129L, 1634974917811L);
       // System.out.println(integer);
       // DistributionVo distribution = dashboardService.distribution(1508830917811L, 1634974917811L);

        boolean sameDay = DateUtil.isSameDay(new Date(1634974917811L), new Date(1634974917221L));
        System.out.println(sameDay);
        DateTime dateTime1 = new DateTime(System.currentTimeMillis());
        System.out.println(dateTime1);
        DateTime dateTime = new DateTime(1635216393347L);
        DateTime dateTime2 = dateTime.withMillisOfDay(0).plusYears(-1);
        DateTime dateTime22 = dateTime.withMillisOfDay(0).plusYears(-1).plusDays(11);
        System.out.println(dateTime22);
        System.out.println(dateTime2.toString().substring(5,10).replace("-","/"));


      /*                                                                                                  //1632539433843L
        TreeMap<Long, DailyDate> map = dataStatisticsService.getdailyDate(System.currentTimeMillis()-10000L, System.currentTimeMillis(), 1);
        Set<Long> longs = map.keySet();
        for (Long aLong : longs) {
            System.out.println(aLong+map.get(aLong).toString());
        }
        ArrayList<DailyDate> objects = new ArrayList<>();*/

       /* TreeMap<Long, RegistrationVo> registrationVo = dataStatisticsService.getLongRegistrationVoMap(1634716479992L,
                System.currentTimeMillis(), 2);
        Set<Long> longs = registrationVo.keySet();
        for (Long aLong : longs) {
            System.out.println(aLong+registrationVo.get(aLong).toString());
        }*/

     /*   for (int i = 0; i < 10; i++) {
            System.out.println(System.currentTimeMillis()-i*(3600*1000*24));
        }*/

      /*  UserInfo userInfo = new UserInfo();
        userInfo.setSex(SexEnum.MAN);
        Integer value = userInfo.getSex().getValue();
        System.out.println(value);
        userInfo.setSex(SexEnum.WOMAN);
        Integer vv = userInfo.getSex().getValue();
        System.out.println(vv);*/
      /*  List<String> allDays = MonthUtil.getAllDays("2021-08-09", "2021-09-02");
        for (String allDay : allDays) {
            System.out.println(allDay);
        }
*/
        //   String ss = FromCityToProvince.findRegion("河南");
       // System.out.println(ss);
    }
}

