package com.tanhua.enums;

import java.io.Serializable;

/**
 * @program: my-tanhua
 * @description: distribution枚举类
 * @author: xkZhao
 * @Create: 2021-10-23 15:48
 **/
public enum DistributionEnum implements Serializable {
    /**
     * 制造
     */
    MANUFACTURING("制造"),
    /**
     * 服务
     */
    SERVICES("服务"),
    /**
     * 地产
     */
    ESTATE("地产"),
    /**
     * 住宿
     */
    ACCOMMODATION("住宿"),
    /**
     * 教育
     */
    EDUCATION("教育"),
    /**
     * 餐饮
     */
    CATERING("餐饮"),
    /**
     * 计算机行业
     */
    COMPUTERINDUSTRY("计算机行业"),




    /**
     * 0-17岁
     */
    ONE("0-17岁"),
    /**
     * 18-23岁
     */
    TWO("18-23岁"),
    /**
     * 24-30岁
     */
    THREE("24-30岁"),
    /**
     * 31-40岁
     */
    FOUR("31-40岁"),
    /**
     * 41-50岁
     */
    FIVE("41-50岁"),
    /**
     * 50岁+
     */
    SIX("50岁+"),


    /**
     * 男
     */
    MAN("男性用户"),

    /**
     * 女
     */
    WOMAN("女性用户"),

    //上海,河北,山西,内蒙古,辽宁,吉林,黑龙江,江苏,浙江,安徽,
    //// 福建,江西,山东,河南,湖北,湖南,广东,广西,海南,四川,贵州,
    // 云南,西藏,陕西,甘肃,青海,宁夏,新疆,北京,天津,重庆,香港,澳门

    BEIJING("北京"),
    SHANGHAI("上海"),
    GUANGZHOU("广州"),
    SHENZHEN("深圳"),
    HENAN("河南"),
    ZHEJIANG("浙江"),
    JIANGSU("江苏"),
    HEBEI("河北"),
    FUJIAN("福建"),
    JIANGXI("江西"),
    SHANDONG("山东"),

// 华南地区,华北地区,华东地区,华西地区,华中地区
    /**
     * 华南
     */
    SouthChina("华南地区"),
    /**
     * 华北
     */
    NorthChina("华北地区"),
    /**
     * 华东
     */
    EastChina("华东地区"),
    /**
     * 华西
     */
    WestChina("华西地区"),
    /**
     * 华中
     */
    CentralChina("华中地区")
    ;
    /**
     * 描述
     */
    private String desc;

    DistributionEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
