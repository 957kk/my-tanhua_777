package com.tanhua.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVOMXY implements Serializable {
    private Integer id;
    private String nickname;
    private String mobile;
    private String sex;
    private String personalSignature;//个性签名
    private Integer age;
    private Integer countBeLiked;//被喜欢人数
    private Integer countLiked;//喜欢认输吧
    private Integer countMatching;//配对人数
    private Integer income;//收入
    private String occupation;//职业
    private String userStatus;//用户状态
    private Long created;//注册时间
    private String city;
    private Long lastActiveTime;//最近活跃时间
    private String lastLoginLocation;//最近登陆地
    private String logo;
    private String tags;
}
