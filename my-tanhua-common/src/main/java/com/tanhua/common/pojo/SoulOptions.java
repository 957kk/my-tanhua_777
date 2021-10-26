package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 选项
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoulOptions {
    private String id;//选项编号
    private String options;
    @TableField(exist = false)
    private String option;//选项
    private String qid;//问题id
    private Integer score;
}
