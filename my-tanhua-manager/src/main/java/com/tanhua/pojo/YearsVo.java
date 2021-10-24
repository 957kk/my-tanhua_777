package com.tanhua.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: my-tanhua
 * @description: users返回类
 * @author: xkZhao
 * @Create: 2021-10-23 09:11
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YearsVo implements Serializable {
    private static final long serialVersionUID = 362498820763181265L;
    /**
     * 今年
     */
    private Object[] thisYear;
    /**
     * 去年
     */
    private Object[] lastYear;
}

