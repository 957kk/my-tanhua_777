package com.tanhua.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum LogoStatusEnumMXY implements IEnum<Integer> {
    PASS(1,"ιθΏ"),
    REFUSE(2,"ζη»");

    private int value;
    private String desc;

    LogoStatusEnumMXY(int value, String desc) {
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
}
