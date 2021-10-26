package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 试题
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoulQuestions {
    private String id;//试题编号
    private String question;
    private String pid;//问卷id
    @TableField(exist = false)
    private List<SoulOptions> options;
}
