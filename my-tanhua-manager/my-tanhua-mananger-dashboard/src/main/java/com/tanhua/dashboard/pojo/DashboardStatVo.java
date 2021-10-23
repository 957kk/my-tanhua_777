package com.tanhua.dashboard.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: my-tanhua
 * @description: 后台统计数据vo
 * @author: xkZhao
 * @Create: 2021-10-22 19:18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatVo implements Serializable {
    private static final long serialVersionUID = 362498820763181265L;
    /**
     * 累计用户
     * 最大值: 500
     * 最小值: 0
     */
    private Integer cumulativeUsers;
    /**
     * 过去30天活跃用户
     * 最大值: 500
     * 最小值: 0
     */
    private Integer activePassMonth;
    /**
     * 过去7天活跃用户
     * 最大值: 500
     * 最小值: 0
     */
    private Integer activePassWeek;
    /**
     * 今日新增用户
     * 最大值: 500
     * 最小值: 0
     */
    private Integer newUsersToday;
    /**
     * 今日新增用户涨跌率，单位百分数，正数为涨，负数为跌
     */
    private Integer newUsersTodayRate;
    /**
     * 今日登录次数
     * 最大值: 500
     * 最小值: 0
     */
    private Integer loginTimesToday;
    /**
     * 今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
     */
    private Integer loginTimesTodayRate;
    /**
     * 今日活跃用户
     * 最大值: 500
     * 最小值: 0
     */
    private Integer activeUsersToday;
    /**
     * 今日活跃用户涨跌率，单位百分数，正数为涨，负数为跌
     */
    private Integer activeUsersTodayRate;
    /**
     * 过去7天平均日使用时长，单位秒
     */
    private Integer useTimePassWeek;
    /**
     * 昨日活跃用户
     */
    private Integer activeUsersYesterday;
    /**
     * 昨日活跃用户涨跌率，单位百分数，正数为涨，负数为跌
     */
    private Integer activeUsersYesterdayRate;


}

