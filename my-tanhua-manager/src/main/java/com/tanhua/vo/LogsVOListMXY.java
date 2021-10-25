package com.tanhua.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogsVOListMXY implements Serializable {
    private String id;
    private Long logTime;
    private String place;
    private String equipment;
}
