package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportVo implements Serializable {

    private String conclusion;//鉴定结果
    private String cover;//鉴定图像
    private List<DimensionsVo> dimensions;//维度
    private List<SimilarYouVo> similarYou;//相似的用户
}
