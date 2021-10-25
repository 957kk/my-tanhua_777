package com.tanhua.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideosVOListMXY implements Serializable {
    private String id;//视频id
    private String nickname;//昵称
    private Long userId;//用户id
    private Long createDate;
    private String videoUrl;//视频访问地址
    private String picUrl;//封面地址
    private Integer reportCount;//举报数
    private Integer likeCount;//喜欢数
    private Integer commentCount;//评论数
    private Integer forwardingCount;//转发数
}
