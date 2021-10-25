package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_userFreeze_status")
public class UserFreezeMXY implements Serializable {

    private long id;
    private Integer userId;
    private Integer freezingTime;//冻结时间
    private Integer freezingRange;//冻结范围
    private String reasonsForFreezing;//冻结原因
    private String frozenRemarks;//冻结备注
}
