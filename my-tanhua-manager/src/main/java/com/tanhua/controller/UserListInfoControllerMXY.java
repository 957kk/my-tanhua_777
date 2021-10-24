package com.tanhua.controller;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.service.UserListServiceMXY;
import com.tanhua.vo.PageResultMXY;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("manage")
@Slf4j
public class UserListInfoControllerMXY {
    @Autowired
    private UserListServiceMXY userListServiceMXY;
    @GetMapping("users")
    public ResponseEntity<PageResultMXY> queryUserList(String pagesize, String page, String id, String nickname, String city){
        PageResultMXY pageResultMXY = null;
        try {
            pageResultMXY = userListServiceMXY.queryUserLisetInfo(pagesize,page,id,nickname,city);
            if(ObjectUtil.isEmpty(pageResultMXY)){
                return null;
            }
        } catch (Exception e) {
            log.error("查询用户信息列表出错");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().body(pageResultMXY);
    }
}
