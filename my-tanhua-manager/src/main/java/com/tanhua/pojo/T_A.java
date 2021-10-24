package com.tanhua.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: my-tanhua
 * @description: title&&amount
 * @author: xkZhao
 * @Create: 2021-10-23 09:00
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class T_A implements Serializable {
    private static final long serialVersionUID = 362498820763181265L;
    /**
     * 日期
     */
    private String title;
    /**
     *数量
     */
    private Integer amount;
}

