package com.tanhua.controller;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.common.pojo.VerifyCode;
import com.tanhua.common.utils.VerifyThreadLocal;
import com.tanhua.pojo.DistributionVo;
import com.tanhua.pojo.YearsVo;
import com.tanhua.service.DashboardService;
import com.tanhua.pojo.DashboardStatVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: my-tanhua
 * @description: 后台控制模块
 * @author: xkZhao
 * @Create: 2021-10-22 18:33
 **/
@RestController
@RequestMapping("dashboard")
@Slf4j
public class DashboardController_zxk {
    @Autowired
    private DashboardService dashboardService;

    /**
     * 注册用户分布，行业top、年龄、性别、地区
     *
     * @param sd 开始时间
     * @param ed 结束时间
     * @return
     */
    @GetMapping("distribution")
    public ResponseEntity<Object> distribution(Long sd, Long ed) {
        VerifyCode code = VerifyThreadLocal.get();
        try {
            DistributionVo distributionVo = this.dashboardService.distribution(sd, ed);
            if (ObjectUtil.isNull(distributionVo)) {
                return null;
            }
            return ResponseEntity.ok(distributionVo);
        } catch (Exception e) {
            log.error("注册用户分布，行业top、年龄、性别、地区 出错 admin_id = " + code.getId() +
                    "请求开始时间戳" + sd + ",请求结束时间戳" + ed, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 新增、活跃用户、次日留存率
     *
     * @param sd   开始时间
     * @param ed   结束时间
     * @param type 访问类型，，101 新增 102 活跃用户 103 次日留存率
     * @return
     */
    @GetMapping("users")
    public ResponseEntity<Object> users(Long sd, Long ed, Integer type) {
        VerifyCode code = VerifyThreadLocal.get();
        try {
            YearsVo yearsVo = dashboardService.users(sd, ed, type);
            if (ObjectUtil.isNull(yearsVo)) {
                return null;
            }
            return ResponseEntity.ok(yearsVo);
        } catch (Exception e) {
            log.error("新增、活跃用户、次日留存率 出错 admin_id = " + code.getId() + "," +
                    "请求开始时间戳" + sd + ",请求结束时间戳" + ed + ",操作type码=" + type, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 概要统计信息
     *
     * @return
     */
    @GetMapping("summary")
    public ResponseEntity<DashboardStatVo> summary() {
        VerifyCode code = VerifyThreadLocal.get();
        try {
            DashboardStatVo dashboardStatVo = this.dashboardService.summary();
            if (ObjectUtil.isNull(dashboardStatVo)) {
                return null;
            }
            return ResponseEntity.ok(dashboardStatVo);
        } catch (Exception e) {
            log.error("概要统计信息 admin_id = " + code.getId() + "," +
                    "请求开始时间戳" + System.currentTimeMillis(), e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}

