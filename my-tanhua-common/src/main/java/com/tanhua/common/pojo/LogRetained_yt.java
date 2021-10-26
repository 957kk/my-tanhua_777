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
@TableName(value = "tb_aaa")
public class LogRetained_yt implements Serializable {
    //主键id
    private  Integer id;
    //操作人
    private String username;
    //ip：可以给固定数据
    private String ip;
    //操作时间
    private Long  time;
    //操作内容  1.删除内容，2.修改栏目，3.内容审核通过
    private String o;
    //操作描述
    private String a;

}
