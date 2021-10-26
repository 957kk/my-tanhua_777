package com.tanhua.controller;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.common.pojo.DailyDate;
import com.tanhua.service.DataStatisticsService;
import com.tanhua.vo.RegistrationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TreeMap;

/**
 * @program: my-tanhua_777
 * @description: 数据统计
 * @author: xkZhao
 * @Create: 2021-10-24 21:30
 **/
@RestController
@RequestMapping("data")
public class DataStatisticsController_zxk {
    @Autowired
    private DataStatisticsService dataStatisticsService;

    /**
     * 日数据统计
     *
     * @param sd  开始时间戳
     * @param ed  结束时间戳
     * @param sex 性别   1代表男  2代表女
     * @return
     */
    @GetMapping("dailyDate")
    public ResponseEntity<Object> dailyDate(Long sd, Long ed, Integer sex) {
        TreeMap<Long, DailyDate> map = this.dataStatisticsService.getdailyDate(sd, ed, sex);
        if (ObjectUtil.isNull(map)) {
            return ResponseEntity.ok("请输入正常时间范围，且不为空");
        }
        return ResponseEntity.ok(map);
    }

    /**
     * 注册留存
     *
     * @param sd  开始时间戳
     * @param ed  结束时间戳
     * @param sex 性别   1代表男  2代表女
     * @return
     */
    @GetMapping("registration")
    public ResponseEntity<Object> registrationOfRetained(Long sd, Long ed, Integer sex) {
        TreeMap<Long, RegistrationVo> map = this.dataStatisticsService.getRegistrationVo(sd, ed, sex);

        if(ObjectUtil.isNull(map)){
            return ResponseEntity.ok("请输入正常时间范围，且不为空");
        }
        return ResponseEntity.ok(map);
    }

}

