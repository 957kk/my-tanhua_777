package com.tanhua.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalsVOMXY {
    private String title;
    private String code;
    private Long values;
}
