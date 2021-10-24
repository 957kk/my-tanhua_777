package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@Data
@TableName(value = "tb_admin_log")
public class VerifyLog implements Serializable {
    private Integer id;
    private String username;
    private String log;
    private Long time;
}
