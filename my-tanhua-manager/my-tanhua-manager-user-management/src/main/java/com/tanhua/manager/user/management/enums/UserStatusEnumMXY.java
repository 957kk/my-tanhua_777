package com.tanhua.manager.user.management.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum UserStatusEnumMXY implements IEnum<Integer> {
    NORMAL(1,"正常"),
    FORZEN(2,"冻结");

    private int value;
    private String desc;

    UserStatusEnumMXY(int value, String desc) {
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
