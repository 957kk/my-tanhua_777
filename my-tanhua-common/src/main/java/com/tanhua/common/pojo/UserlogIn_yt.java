package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("tb_user_log")
public class UserlogIn_yt implements Serializable {
    private Integer id;
    private String phone;
    private String log;
    private Long time;
}
