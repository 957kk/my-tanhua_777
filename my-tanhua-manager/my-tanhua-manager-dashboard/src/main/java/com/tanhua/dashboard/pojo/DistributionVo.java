package com.tanhua.dashboard.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: my-tanhua
 * @description: 分布vo类
 * @author: xkZhao
 * @Create: 2021-10-23 09:34
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DistributionVo implements Serializable {
    private static final long serialVersionUID = 36249882076318165L;
    /**
     * 行业分布TOP10
     * 最小数量: 10
     * 元素是否都不同: true
     * 最大数量: 10
     * item 类型: object
     */
    private Object[] industryDistribution;
    /**
     * 年龄分布
     * 最小数量: 6
     * 元素是否都不同: true
     * 最大数量: 6
     * item 类型: object
     */
    private Object[] ageDistribution;
    /**
     * 性别分布
     * 最小数量: 2
     * 元素是否都不同: true
     * 最大数量: 2
     * item 类型: object
     */
    private Object[] genderDistribution;
    /**
     * 地区分布
     * 最小数量: 33
     * 元素是否都不同: true
     * 最大数量: 33
     * item 类型: object
     */
    private Object[] localDistribution;
    /**
     * 地区合计
     * 最小数量: 5
     * 元素是否都不同: true
     * 最大数量: 6
     * item 类型: object
     */
    private Object[] localTotal;

}

