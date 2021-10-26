package com.tanhua.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.common.pojo.UserFreezeMXY;
import com.tanhua.service.*;
import com.tanhua.vo.PageResultMXY;
import com.tanhua.vo.QuanZiListVOMXY;
import com.tanhua.vo.UserInfoVOMXY;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage")
@Slf4j
public class UserListInfoControllerMXY {
    @Autowired
    private UserListServiceMXY userListServiceMXY;
    @Autowired
    private UserInfoVOServiceMXY userInfoVOServiceMXY;
    @Autowired
    private QuanZiListServiceMXY quanZiListServiceMXY;

    @Autowired
    private VideosListServiceMXY videosListServiceMXY;
    @Autowired
    private UserLogsListServiceMXY userLogsListServiceMXY;

    /**
     * 用户管理列表
     *
     * @param pagesize 显示条数
     * @param page     显示第几页
     * @param id       模糊查询id
     * @param nickname 模糊查询昵称
     * @param city     条件查询城市
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity<PageResultMXY> queryUserList(String pagesize, String page, String id, String nickname, String city) {
        PageResultMXY pageResultMXY = null;
        try {
            pageResultMXY = userListServiceMXY.queryUserLisetInfo(pagesize, page, id, nickname, city);
            if (ObjectUtil.isEmpty(pageResultMXY)) {
                return null;
            }
        } catch (Exception e) {
            log.error("查询用户信息列表出错", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().body(pageResultMXY);
    }

    /**
     * 用户基本资料
     *
     * @param userID 用户id
     * @return
     */
    @GetMapping("/users/{userID}")
    public ResponseEntity<UserInfoVOMXY> queryUserList(@PathVariable("userID") String userID) {

        try {
            UserInfoVOMXY userInfoVOMXY = userInfoVOServiceMXY.queryUserInfoVO(userID);
            if (ObjectUtil.isNotEmpty(userInfoVOMXY)) {
                return ResponseEntity.ok(userInfoVOMXY);
            }
        } catch (Exception e) {
            log.error("查询用户详细信息出错 userID = " + userID, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 用户圈子列表
     *
     * @param uid       用户id
     * @param page      当前页
     * @param pagesize  展示条数
     * @param sortProp  排序字段
     * @param sortOrder 正序/倒叙
     * @return
     */
    //@GetMapping("/messages")
    public ResponseEntity<PageResultMXY> queryQuanZiList(String uid, String page, String pagesize, String sortProp, String sortOrder) {
        try {
            PageResultMXY pageResultMXY = quanZiListServiceMXY.queryQuanZiList(uid, page, pagesize, sortProp, sortOrder);
            if (ObjectUtil.isNotEmpty(pageResultMXY)) {
                return ResponseEntity.ok(pageResultMXY);
            }
        } catch (Exception e) {
            log.error("查询用户圈子列表出错 uid = " + uid, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 用户视频列表
     *
     * @param uid       用户id
     * @param page      当前页
     * @param pagesize  展示条数
     * @param sortProp  排序字段
     * @param sortOrder 正序/倒叙
     * @return
     */
    @GetMapping("/videos")
    public ResponseEntity<PageResultMXY> queryVideosList(String uid, String page, String pagesize, String sortProp, String sortOrder) {
        try {
            PageResultMXY pageResultMXY = videosListServiceMXY.queryQVideosList(uid, page, pagesize, sortProp, sortOrder);
            if (ObjectUtil.isNotEmpty(pageResultMXY)) {
                return ResponseEntity.ok(pageResultMXY);
            }
        } catch (Exception e) {
            log.error("查询用户列表信息出错 uid = " + uid, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 用户登录记录
     *
     * @param uid       用户id
     * @param page      当前页
     * @param pagesize  展示条数
     * @param sortProp  排序字段
     * @param sortOrder 正序/倒叙
     * @return
     */
    @GetMapping("/logs")
    public ResponseEntity<PageResultMXY> queryUserLogsList(String uid, String page, String pagesize, String sortProp, String sortOrder) {
        try {
            PageResultMXY pageResultMXY = userLogsListServiceMXY.queryUserLogsList(uid, page, pagesize, sortProp, sortOrder);
            if (ObjectUtil.isNotEmpty(pageResultMXY)) {
                return ResponseEntity.ok(pageResultMXY);
            }
        } catch (Exception e) {
            log.error("查询用户登录记录出错 uid = " + uid, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询动态详情
     * @param publishId
     * @return
     */
    @GetMapping("/messages/{publishId}")
    public ResponseEntity<QuanZiListVOMXY> queryQuanZiInfo(@PathVariable("publishId") String publishId) {
        try {
            QuanZiListVOMXY quanZiListVOMXY = quanZiListServiceMXY.queryQuanZiInfo(publishId);
            if (ObjectUtil.isNotEmpty(quanZiListVOMXY)) {
                return ResponseEntity.ok(quanZiListVOMXY);
            }
        } catch (Exception e) {
            log.error("查询用户登录记录出错 publishId = " + publishId, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询用户动态评论列表
     * @param messageID
     * @param page
     * @param pagesize
     * @param sortProp
     * @param sortOrder
     * @return
     */
    @GetMapping("/messages/comments")
    public ResponseEntity<PageResultMXY> queryQuanZiInfo(String messageID, String page, String pagesize, String sortProp, String sortOrder) {
        try {
            PageResultMXY pageResultMXY = quanZiListServiceMXY.queryCommentsList(messageID,page,pagesize,sortProp,sortOrder);
            if (ObjectUtil.isNotEmpty(pageResultMXY)) {
                return ResponseEntity.ok(pageResultMXY);
            }
        } catch (Exception e) {
            log.error("查询用户登录记录出错 messageID = " + messageID, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 圈子置顶
     * @param id
     * @return
     */
    @PostMapping("/messages/{id}/top")
    public ResponseEntity<Map<String,String>> quanZiTop(@PathVariable("id") String id){
        Map<String,String> result = new HashMap<>();
        try {
            result = quanZiListServiceMXY.setQuanZiTop(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("圈子置顶失败 id" + id,e);
        }
        result.put("message","操作失败");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 圈子取消置顶
     * @param id
     * @return
     */
    @PostMapping("/messages/{id}/untop")
    public ResponseEntity<Map<String,String>> quanZiUnUntop(@PathVariable("id") String id){
        Map<String,String> result = new HashMap<>();
        try {
            result = quanZiListServiceMXY.setQuanZiUnTop(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("圈子取消置顶失败 id" + id,e);
        }
        result.put("message","操作失败");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 用户冻结操作
     * @param userFreezeMXY
     * @return
     */
    @PostMapping("/users/freeze")
    public ResponseEntity<Map<String,String>> userFreeze(@RequestBody UserFreezeMXY userFreezeMXY){
        Map<String,String> result = new HashMap<>();
        try {
            result = userInfoVOServiceMXY.userFreeze(userFreezeMXY);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("用户冻结失败 userId" + userFreezeMXY.getUserId(),e);
        }
        result.put("message","操作失败");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
    /**
     * 用户解冻操作
     * @param param
     * @return
     */
    @PostMapping("/users/unfreeze")
    public ResponseEntity<Map<String,String>> userFreeze(@RequestBody Map<String,String> param){
        Map<String,String> result = new HashMap<>();
        try {
            result = userInfoVOServiceMXY.userUnFreeze(param);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("用户冻结失败 userId" + param.get("userId"),e);
        }
        result.put("message","操作失败");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     *  圈子审核
     *

     * @return
     */
    @GetMapping("/messages")

    public ResponseEntity<PageResultMXY> quanZiListCheck(String uid,String id,String pagesize,String page,String sd,String ed,String state,String sortProp, String sortOrder){
        try {
            PageResultMXY pageResultMXY = quanZiListServiceMXY.quanZiListCheck(uid,id,pagesize,page,sd,ed,state,sortProp,sortOrder);
            if (ObjectUtil.isNotEmpty(pageResultMXY)) {
                return ResponseEntity.ok(pageResultMXY);
            }
        } catch (Exception e) {
            log.error("查询圈子状态记录出错 state = " + state , e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 圈子审核通过
     * @param ids
     * @return
     */
    @PostMapping("/messages/pass")
    public ResponseEntity<Map<String,String>> quanZiPass(@RequestBody List<String> ids){
        try {
            Map<String, String> result = quanZiListServiceMXY.quanZiPass(ids);
            if (CollUtil.isNotEmpty(result)) {
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            log.error("查询审核通过出错 ids = " + ids , e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 圈子审核驳回
     * @param ids
     * @return
     */
    @PostMapping("/messages/reject")
    public ResponseEntity<Map<String,String>> quanZiReject(@RequestBody List<String> ids){
        try {
            Map<String, String> result = quanZiListServiceMXY.quanZiReject(ids);
            if (CollUtil.isNotEmpty(result)) {
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            log.error("查询审核驳回出错 ids = " + ids , e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 圈子状态撤回
     * @param ids
     * @return
     */
    @PostMapping("/messages/revocation")
    public ResponseEntity<Map<String,String>>  quanZiRevocation(@RequestBody List<String> ids){
        try {
            Map<String, String> result = quanZiListServiceMXY.quanZiRevocation(ids);
            if (CollUtil.isNotEmpty(result)) {
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            log.error("查询审核撤销出错 ids = " + ids , e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
