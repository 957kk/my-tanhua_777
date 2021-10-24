package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 运营日志
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_LogRetained_yt")
public class LogRetained_yt implements Serializable {
    //主键id
    private  Integer id;
    //操作人
    private String username;
    //ip：可以给固定数据
    private String IP;
    //操作时间
    private Date  time;
    //操作内容  1.删除内容，2.修改栏目，3.内容审核通过
    private Integer operation;
    //操作描述
    private String describe;

}
