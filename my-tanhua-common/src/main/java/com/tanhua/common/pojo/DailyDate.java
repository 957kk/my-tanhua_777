package com.tanhua.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * @program: my-tanhua_777
 * @description: 日数据vo
 * @author: xkZhao
 * @Create: 2021-10-24 21:49
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyDate implements Serializable {
    private static final long serialVersionUID = 362498820763181265L;

    /**
     * 日期
     */
    private DateTime date;
    /**
     * 净激活数
     */
    private Integer activeCount;
    /**
     * 注册人数
     */
    private Integer registrationCount;
    /**
     * 登录次数
     */
    private Integer loginCount;
    /**
     * 活跃人数
     */
    private Integer activePersons;

}

