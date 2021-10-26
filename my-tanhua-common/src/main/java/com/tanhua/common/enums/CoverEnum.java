package com.tanhua.common.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * 鉴定结果类型图片
 */
public enum CoverEnum implements IEnum<Integer> {
    OWI(1," https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/owl.png"),
    RABBIT(2,"https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/rabbit.png"),
    FOX(3,"https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/fox.png"),
    LION(4,"https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/lion.png");

    private int value;
    private String desc;

    CoverEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.desc;
    }
    //根据鉴定类型对应枚举id，获取对应鉴定结果类型图片
    public static String getName(int index) {
        for (CoverEnum coverEnum : CoverEnum.values()) {
            if (coverEnum.getIndex() == index) {
                return coverEnum.desc;
            }
        }
        return null;
    }
    // get set 方法
    public String getName() {
        return desc;
    }
    public void setName(String name) {
        this.desc = name;
    }
    public int getIndex() {
        return value;
    }
    public void setIndex(int index) {
        this.value = index;
    }
}
