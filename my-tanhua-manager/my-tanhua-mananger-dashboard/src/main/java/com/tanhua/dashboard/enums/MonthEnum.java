package com.tanhua.dashboard.enums;

import java.io.Serializable;

/**
 * @program: my-tanhua
 * @description: 月份枚举
 * @author: xkZhao
 * @Create: 2021-10-23 10:14
 **/
public enum MonthEnum implements Serializable {
    /**
     * 一月
     */
    JANUARY(1),
    /**
     * 二月
     */
    FEBRUARY(2),
    /**
     * 三月
     */
    March(3),
    /**
     * 四月
     */
    April(4),
    /**
     * 五月
     */
    May(5),
    /**
     * 六月
     */
    June(6),
    /**
     * 七月
     */
    July(7),
    /**
     * 八月
     */
    August(8),
    /**
     * 九月
     */
    September(9),
    /**
     * 十月
     */
    October(10),
    /**
     * 十一月
     */
    November(11),
    /**
     * 十二月
     */
    December(12)
    ;
    /**
     * 月份
     */
    private final  Integer month;

    MonthEnum(Integer month) {
        this.month = month;
    }

    public Integer getMonth() {
        return month;
    }
}

