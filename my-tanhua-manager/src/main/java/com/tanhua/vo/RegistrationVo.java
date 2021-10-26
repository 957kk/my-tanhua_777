package com.tanhua.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * @program: my-tanhua_777
 * @description: 注册留存Vo
 * @author: xkZhao
 * @Create: 2021-10-25 14:29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationVo implements Serializable {
    private static final long serialVersionUID = 362498820763181265L;

    /**
     * 日期
     */
    private DateTime date;
    /**
     * 注册人数
     */
    private Integer registrationCount;

    /**
     * 留存率
     */
    private Object[]  rate;

}

