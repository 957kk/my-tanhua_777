package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: my-tanhua
 * @description: 用户登录日志表
 * @author: xkZhao
 * @Create: 2021-10-22 15:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("tb_user_loginfo")
public class UserLogInfo implements Serializable {
    private static final long serialVersionUID = 362498820763181265L;
    /**
     * 主键id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 登录客户端ip
     */
    private String loginIp;
    /**
     * 登录所在地址
     */
    private String loginAddress;
    /**
     * 登录时间戳
     */
    private Long loginTime;
}

