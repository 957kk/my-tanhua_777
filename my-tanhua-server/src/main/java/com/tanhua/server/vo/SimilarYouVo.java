package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 与你相似
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimilarYouVo implements Serializable {
    private Integer id;//用户编号
    private String avatar;//头像
}