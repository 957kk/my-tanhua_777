package com.tanhua.common.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * 运营日志枚举
 */
public enum LogRetainedEnum_yt implements IEnum<Integer> {

    DEFAULT(1, "删除内容"),
    UPDATE(2, "修改栏目"),
    PASS(3, "内容审核通过");

    private int value;
    private String desc;

    LogRetainedEnum_yt(int value, String desc) {
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
