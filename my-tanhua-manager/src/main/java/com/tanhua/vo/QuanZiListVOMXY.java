package com.tanhua.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuanZiListVOMXY {
    private String id;//圈子id
    private String nickname;//昵称
    private Long userId;//用户id
    private String userLogo;//用户头像
    private Long createDate;//创建时间
    private String text;//文本
    private String state;//状态
    private Integer topState;//置顶状态
    private Integer reportCount;//举报数
    private Integer likeCount;//喜欢数
    private Integer commentCount;//评论数
    private Integer forwardingCount;//转发数
    private String[] medias;//配图
}
