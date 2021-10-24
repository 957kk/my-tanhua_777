package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户状态表  是否封禁
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_account_status")
public class AccountStatusMXY {

    private Long id;
    private Long userid;
    //冻结时间（1，2，3）
    private Integer status;
    //冻结范围（1，2，3）
    private Integer scope;
    //冻结原因（1，2，3，4）
    private Integer reason;
    private String describes;
    private Long created;
}
