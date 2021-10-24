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
    JANUARY("1月"),
    /**
     * 二月
     */
    FEBRUARY("2月"),
    /**
     * 三月
     */
    MARCH("3月"),
    /**
     * 四月
     */
    APRIL("4月"),
    /**
     * 五月
     */
    MAY("5月"),
    /**
     * 六月
     */
    JUNE("6月"),
    /**
     * 七月
     */
    JULY("7月"),
    /**
     * 八月
     */
    AUGUST("8月"),
    /**
     * 九月
     */
    SEPTEMBER("9月"),
    /**
     * 十月
     */
    OCTOBER("10月"),
    /**
     * 十一月
     */
    NOVEMBER("11月"),
    /**
     * 十二月
     */
    DECEMBER("12月")
    ;
    /**
     * 月份
     */
    private final  String month;

    MonthEnum(String month) {
        this.month = month;
    }

    public String getMonth() {
        return month;
    }
}

