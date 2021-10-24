package com.tanhua.manager.user.management.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResultMXY implements Serializable {
    private Integer counts;
    private Integer pagesize;
    private Integer pages;
    private Integer page;
    private List<UsersListVOMXY> items;
}
