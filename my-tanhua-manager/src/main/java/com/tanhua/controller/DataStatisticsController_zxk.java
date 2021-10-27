package com.tanhua.controller;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.common.pojo.DailyDate;
import com.tanhua.common.pojo.VerifyCode;
import com.tanhua.common.utils.NoAuthorization;
import com.tanhua.common.utils.VerifyThreadLocal;
import com.tanhua.service.DataStatisticsService;
import com.tanhua.vo.RegistrationVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@Slf4j
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
    @NoAuthorization
    public ResponseEntity<Object> dailyDate(Long sd, Long ed, Integer sex) {
        VerifyCode code = VerifyThreadLocal.get();
        try {
            TreeMap<Long, DailyDate> map = this.dataStatisticsService.getdailyDate(sd, ed, sex);
            if (ObjectUtil.isNull(map)) {
                return ResponseEntity.ok("请输入正常时间范围，且不为空");
            }
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            log.error("注册留存 出错 admin_id = " + code.getId() +
                    "请求开始时间戳" + sd + ",请求结束时间戳" + ed, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    @NoAuthorization
    public ResponseEntity<Object> registrationOfRetained(Long sd, Long ed, Integer sex) {
        VerifyCode code = VerifyThreadLocal.get();
        try {
            TreeMap<Long, RegistrationVo> map = this.dataStatisticsService.getRegistrationVo(sd, ed, sex);
            if (ObjectUtil.isNull(map)) {
                return ResponseEntity.ok("请输入正常时间范围，且不为空");
            }
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            log.error("注册留存 出错 admin_id = " + code.getId() +
                    "请求开始时间戳" + sd + ",请求结束时间戳" + ed, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}

