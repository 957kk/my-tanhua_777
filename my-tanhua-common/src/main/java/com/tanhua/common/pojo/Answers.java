package com.tanhua.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *作答
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Answers {
    private String questionId;//试题编号
    private String optionId;//选项编号
}
