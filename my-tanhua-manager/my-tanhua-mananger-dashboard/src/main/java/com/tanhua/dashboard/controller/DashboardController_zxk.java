package com.tanhua.dashboard.controller;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.dashboard.pojo.DistributionVo;
import com.tanhua.dashboard.pojo.YearsVo;
import com.tanhua.dashboard.service.DashboardService;
import com.tanhua.dashboard.pojo.DashboardStatVo;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DashboardController_zxk {
    @Autowired
    private DashboardService dashboardService;

    /**
     * 注册用户分布，行业top、年龄、性别、地区
     * @param sd 开始时间
     * @param ed 结束时间
     * @return
     */
    @GetMapping("distribution")
    public ResponseEntity<Object> distribution(Long sd,Long ed){
        DistributionVo distributionVo= this.dashboardService.distribution(sd,ed);
        if(ObjectUtil.isNull(distributionVo)){
            return null;
        }
        return ResponseEntity.ok(distributionVo);
    }

    /**
     * 新增、活跃用户、次日留存率
     * @param sd 开始时间
     * @param ed 结束时间
     * @param type 访问类型，，101 新增 102 活跃用户 103 次日留存率
     * @return
     */
    @GetMapping("users")
    public ResponseEntity<Object> users(Long sd, Long ed, Integer type){
        YearsVo yearsVo=dashboardService.users(sd,ed,type);
        if(ObjectUtil.isNull(yearsVo)){
            return null;
        }
        return ResponseEntity.ok(yearsVo);
    }

    /**
     * 概要统计信息
     * @return
     */
    @GetMapping("summary")
    public ResponseEntity<DashboardStatVo> summary(){
        DashboardStatVo dashboardStatVo=this.dashboardService.summary();
        if(ObjectUtil.isNull(dashboardStatVo)){
            return null;
        }
        return ResponseEntity.ok(dashboardStatVo);
    }

}

