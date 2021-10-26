package com.tanhua.common.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报告
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoulReport {
    private Integer id;
    @TableField(exist = false)
    private String conclusion;//鉴定结果
    @TableField(exist = false)
    private String cover;//鉴定图像
    private Integer score;//分数
    private String userId;//用户id
    private Integer reportTypeId;//报告id
    private String paperId;//问卷id
}
