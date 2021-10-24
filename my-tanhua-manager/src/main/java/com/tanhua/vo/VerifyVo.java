package com.tanhua.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class VerifyVo implements Serializable {
    private String uid;
    private String username;
    private String avatar;
}
