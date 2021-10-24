package com.tanhua.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersListVOMXY {
    private Integer id;
    private String logo;
    private String logoStatus;//头像状态
    private String nickname;
    private String mobile;
    private String sex;
    private Integer age;
    private String occupation;//职业
    private String userStatus;//用户状态是否冻结
    private Long lastActiveTime;//上次登陆时间
    private String city;
}
