package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 验证码类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_four_user")
public class VerifyCode implements Serializable {
    private Integer id;
    private String username;
    private String password;
}