package com.tanhua.service;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.impl.UserListInfoImplMXY;
import com.tanhua.vo.PageResultMXY;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserListServiceMXY {
    @Autowired
    private UserListInfoImplMXY userListInfoImplMXY;

    public PageResultMXY queryUserLisetInfo(String pagesize, String page, String id, String nickname, String city) {
        Map<String, String> param = new HashMap<>();
        if (ObjectUtil.isAllEmpty(pagesize,page,id,nickname,city)) {
            log.error("查询参数内容为空");
            return null;
        }
        param.put("pagesize",pagesize);
        param.put("page",page);
        param.put("id",id);
        param.put("nickname",nickname);
        param.put("city",city);

        PageResultMXY pageResultMXY = null;
        try {
            pageResultMXY = userListInfoImplMXY.queryUserListInfo(param);
            if (ObjectUtil.isEmpty(pageResultMXY)) {
                log.error("查询用户信息列表为空");
            }
        } catch (Exception e) {
            log.error("查询用户信息列表出错", e);
        }
        return pageResultMXY;
    }
}
