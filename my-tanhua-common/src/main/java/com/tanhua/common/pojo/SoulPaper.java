package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 问卷
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoulPaper {
    private String id;//问卷编号
    private String name;//问卷名称
    private String cover;//封面
    private String level;//级别（初级，中级，高级）
    private Integer star;//星别
    @TableField(exist = false)
    private List<SoulQuestions> questions;
    @TableField(exist = false)
    private Integer isLock;//锁状态
    @TableField(exist = false)
    private String reportId;

}


