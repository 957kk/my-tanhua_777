package com.tanhua.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.tanhua.impl.QuanZiListVOImplMXY;
import com.tanhua.vo.PageResultMXY;
import com.tanhua.vo.QuanZiListVOMXY;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class QuanZiListServiceMXY {
    @Autowired
    private QuanZiListVOImplMXY quanZiListVOImplMXY;

    public PageResultMXY queryQuanZiList(String uid, String page, String pagesize, String sortProp, String sortOrder){
        if(StrUtil.isAllEmpty(uid,page,pagesize,sortProp,sortOrder)){
            log.error("查询参数存在null值 uid =" +uid + "---" +"page = " +page +"---"+"pagesize = " +pagesize +"---"+"sortProp = " +sortProp +"---"+"sortOrder = " +sortOrder);
            return null;
        }
        Map<String,String> param = new HashMap<>();
        param.put("uid",uid);
        param.put("page",page);
        param.put("pagesize",pagesize);
        param.put("sortProp",sortProp);
        param.put("sortOrder",sortOrder);
        PageResultMXY pageResultMXY = null;
        try {
            pageResultMXY = quanZiListVOImplMXY.queryQuanZiVOList(param);
        } catch (Exception e) {
            log.error("获取用户动态列表出错，uid = "+ uid,e);
        }

        return pageResultMXY;

    }

    public QuanZiListVOMXY queryQuanZiInfo(String id){
        if(StrUtil.isEmpty(id)){
            log.error("查询动态详情的参数为空");
            return null;
        }
        QuanZiListVOMXY quanZiListVOMXY = quanZiListVOImplMXY.queryQuanZiInfo(id);
        if(ObjectUtil.isEmpty(quanZiListVOMXY)){
            log.error("查询动态详情出错 id="+id);
            return null;
        }
        return quanZiListVOMXY;

    }

    public PageResultMXY queryCommentsList(String messageID,String page,String pagesize,String sortProp,String sortOrder){
        if(StrUtil.isAllEmpty(messageID,page,pagesize,sortProp,sortOrder)){
            log.error("查询参数存在null值 messageID =" +messageID + "---" +"page = " +page +"---"+"pagesize = " +pagesize +"---"+"sortProp = " +sortProp +"---"+"sortOrder = " +sortOrder);
            return null;
        }
        Map<String,String> param = new HashMap<>();
        param.put("messageID",messageID);
        param.put("page",page);
        param.put("pagesize",pagesize);
        param.put("sortProp",sortProp);
        param.put("sortOrder",sortOrder);
        PageResultMXY pageResultMXY = null;
        try {
            pageResultMXY = quanZiListVOImplMXY.queryCommentVOList(param);
        } catch (Exception e) {
            log.error("获取用户动态评论列表出错，uid = "+ messageID,e);
        }

        return pageResultMXY;
    }

    public Map<String, String> setQuanZiTop(String id) {
        if(StrUtil.isEmpty(id)) {
            log.error("圈子id为空");
            return null;
        }

        Map<String,String> result = quanZiListVOImplMXY.setQuanZiTop(new ObjectId(id));


        return result;
    }

    public Map<String, String> setQuanZiUnTop(String id) {
        if(StrUtil.isEmpty(id)) {
            log.error("圈子id为空");
            return null;
        }

        Map<String,String> result = quanZiListVOImplMXY.setQuanZiUntop(new ObjectId(id));


        return result;
    }
}
