package com.tanhua.api;


import com.tanhua.common.pojo.LogRetained_yt;

import java.util.List;

/**
 * 运营日志的api
 */

public interface LogRetained_yt_Api {
    /**
     * 保存运营日志到数据库
     * @param logRetained_yt
     * @return
     */
    Boolean saveLogRetained(LogRetained_yt logRetained_yt);

    /**
     * 查询所有的日志表
     * @return
     */
    List<LogRetained_yt> queryLogRetained_yt_list();


    /**
     * 根据id查询项目
     * @param id
     * @return
     */
    LogRetained_yt queryLogRetained_ytById (Integer id);

}
