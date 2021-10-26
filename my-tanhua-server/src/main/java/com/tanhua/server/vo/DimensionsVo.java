package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *维度
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionsVo implements Serializable {
    private String key;//维度项（外向，判断，抽象，理性）
    private String value;//维度值
}