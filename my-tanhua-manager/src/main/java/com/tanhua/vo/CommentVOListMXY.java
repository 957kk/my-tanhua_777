package com.tanhua.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVOListMXY implements Serializable {

    private String id;//评论id
    private String nickname;//昵称
    private Long userId;
    private String content;//评论内容
    private Long createDate;
}
