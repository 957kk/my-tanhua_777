package com.tanhua.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.tanhua.common.pojo.LogRetained_yt;
import com.tanhua.common.pojo.VerifyCode;
import com.tanhua.common.utils.VerifyThreadLocal;
import com.tanhua.impl.QuanZiListVOImplMXY;
import com.tanhua.vo.PageResultMXY;
import com.tanhua.vo.QuanZiListVOMXY;
import com.tanhua.vo.TotalsVOMXY;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class QuanZiListServiceMXY {
    @Autowired
    private QuanZiListVOImplMXY quanZiListVOImplMXY;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public PageResultMXY queryQuanZiList(String uid, String page, String pagesize, String sortProp, String sortOrder) {
        if (StrUtil.isAllEmpty(uid, page, pagesize, sortProp, sortOrder)) {
            log.error("查询参数存在null值 uid =" + uid + "---" + "page = " + page + "---" + "pagesize = " + pagesize + "---" + "sortProp = " + sortProp + "---" + "sortOrder = " + sortOrder);
            return null;
        }
        Map<String, String> param = new HashMap<>();
        param.put("uid", uid);
        param.put("page", page);
        param.put("pagesize", pagesize);
        param.put("sortProp", sortProp);
        param.put("sortOrder", sortOrder);
        PageResultMXY pageResultMXY = null;
        try {
            pageResultMXY = quanZiListVOImplMXY.queryQuanZiVOList(param);
        } catch (Exception e) {
            log.error("获取用户动态列表出错，uid = " + uid, e);
        }

        return pageResultMXY;

    }

    public QuanZiListVOMXY queryQuanZiInfo(String id) {
        if (StrUtil.isEmpty(id)) {
            log.error("查询动态详情的参数为空");
            return null;
        }
        QuanZiListVOMXY quanZiListVOMXY = quanZiListVOImplMXY.queryQuanZiInfo(id);
        if (ObjectUtil.isEmpty(quanZiListVOMXY)) {
            log.error("查询动态详情出错 id=" + id);
            return null;
        }
        return quanZiListVOMXY;

    }

    public PageResultMXY queryCommentsList(String messageID, String page, String pagesize, String sortProp, String sortOrder) {
        if (StrUtil.isAllEmpty(messageID, page, pagesize, sortProp, sortOrder)) {
            log.error("查询参数存在null值 messageID =" + messageID + "---" + "page = " + page + "---" + "pagesize = " + pagesize + "---" + "sortProp = " + sortProp + "---" + "sortOrder = " + sortOrder);
            return null;
        }
        Map<String, String> param = new HashMap<>();
        param.put("messageID", messageID);
        param.put("page", page);
        param.put("pagesize", pagesize);
        param.put("sortProp", sortProp);
        param.put("sortOrder", sortOrder);
        PageResultMXY pageResultMXY = null;
        try {
            pageResultMXY = quanZiListVOImplMXY.queryCommentVOList(param);
        } catch (Exception e) {
            log.error("获取用户动态评论列表出错，uid = " + messageID, e);
        }

        return pageResultMXY;
    }

    public Map<String, String> setQuanZiTop(String id) {
        if (StrUtil.isEmpty(id)) {
            log.error("圈子id为空");
            return null;
        }

        Map<String, String> result = quanZiListVOImplMXY.setQuanZiTop(new ObjectId(id));


        return result;
    }

    public Map<String, String> setQuanZiUnTop(String id) {
        if (StrUtil.isEmpty(id)) {
            log.error("圈子id为空");
            return null;
        }

        Map<String, String> result = quanZiListVOImplMXY.setQuanZiUntop(new ObjectId(id));


        return result;
    }

    public PageResultMXY quanZiListCheck(String uid, String id, String pagesize, String page, String sd, String ed, String state, String sortProp, String sortOrder) {
        if (StrUtil.isAllEmpty(uid, id, pagesize, page, sd, ed, state, sortProp, sortOrder)) {
            log.error("查询圈子状态参数为空");
            return null;
        }
        Map<String, String> param = new HashMap<>();
        param.put("uid", uid);
        param.put("id", id);
        param.put("pagesize", pagesize);
        param.put("page", page);
        param.put("sd", sd);
        param.put("ed", ed);
        param.put("state", state);
        param.put("sortProp", sortProp);
        param.put("sortOrder", sortOrder);
        PageResultMXY pageResultMXY = new PageResultMXY();
        if (StrUtil.isNotEmpty(uid)) {
            pageResultMXY = queryQuanZiList(uid, page, pagesize, sortProp, sortOrder);
        } else {
            pageResultMXY = quanZiListVOImplMXY.quanZiListCheck(param);
            if (ObjectUtil.isEmpty(pageResultMXY)) {
                log.error("查询圈子状态出错");
                return null;
            }
            List<TotalsVOMXY> totals = new ArrayList<>();
            //根据圈子状态，查询圈子总条数
            //全部 0
            TotalsVOMXY totalsVOMXY0 = new TotalsVOMXY();
            totalsVOMXY0.setCode("all");
            totalsVOMXY0.setTitle("全部");
            param.put("state", "all");
            totalsVOMXY0.setValues(quanZiListVOImplMXY.getCount(param));
            totals.add(0, totalsVOMXY0);
            //待审核 2
            TotalsVOMXY totalsVOMXY1 = new TotalsVOMXY();
            totalsVOMXY1.setCode("3");
            totalsVOMXY1.setTitle("待审核");
            param.put("state", "3");
            totalsVOMXY1.setValues(quanZiListVOImplMXY.getCount(param));
            totals.add(1, totalsVOMXY1);
            //已通过 3
            TotalsVOMXY totalsVOMXY2 = new TotalsVOMXY();
            totalsVOMXY2.setCode("2");
            totalsVOMXY2.setTitle("已通过");
            param.put("state", "2");
            totalsVOMXY2.setValues(quanZiListVOImplMXY.getCount(param));
            totals.add(2, totalsVOMXY2);
            //已拒绝 4
            TotalsVOMXY totalsVOMXY3 = new TotalsVOMXY();
            totalsVOMXY3.setCode("4");
            totalsVOMXY3.setTitle("已拒绝");
            param.put("state", "4");
            totalsVOMXY3.setValues(quanZiListVOImplMXY.getCount(param));
            totals.add(3, totalsVOMXY3);
            pageResultMXY.setTotals(totals);
        }

        return pageResultMXY;
    }


    /**
     * 圈子审核通过
     *
     * @param ids
     * @return
     */
    public Map<String, String> quanZiPass(List<String> ids) {
        VerifyCode code = VerifyThreadLocal.get();
        if (ids.size() == 0) {
            log.error("圈子审核参数为空");
            return null;
        }
        List<ObjectId> idList = new ArrayList<>();
        for (String id : ids) {
            idList.add(new ObjectId(id));
        }
        Map<String, String> map = quanZiListVOImplMXY.quanZiPass(idList);
        for (String id : ids) {
            String o="内容审核通过";
            String a= code.getUsername()+"对圈子"+id+"进行了内容审核---"+o;
            this.sendRocketMQ(o,a);
        }
        map.put("message", "操作成功");
        return map;
    }

    /**
     * 圈子审核驳回
     *
     * @param ids
     * @return
     */
    public Map<String, String> quanZiReject(List<String> ids) {
        VerifyCode code = VerifyThreadLocal.get();
        if (ids.size() == 0) {
            log.error("圈子审核参数为空");
            return null;
        }
        List<ObjectId> idList = new ArrayList<>();
        for (String id : ids) {
            idList.add(new ObjectId(id));
        }
        Map<String, String> map = quanZiListVOImplMXY.quanZiReject(idList);
        map.put("message", "操作成功");
        for (String id : ids) {
            String o="内容审核拒绝";
            String a= code.getUsername()+"对圈子"+id+"进行了内容审核---"+o;
            this.sendRocketMQ(o,a);
        }
        return map;
    }

    /**
     * 圈子状态撤销
     *
     * @param ids
     * @return
     */
    public Map<String, String> quanZiRevocation(List<String> ids) {
        VerifyCode code = VerifyThreadLocal.get();
        if (ids.size() == 0) {
            log.error("圈子审核参数为空");
            return null;
        }
        List<ObjectId> idList = new ArrayList<>();
        for (String id : ids) {
            idList.add(new ObjectId(id));
        }
        Map<String, String> map = quanZiListVOImplMXY.quanZiRevocation(idList);
        map.put("message", "操作成功");
        for (String id : ids) {
            String o="内容审核撤销为待审核";
            String a= code.getUsername()+"对圈子"+id+"进行了内容审核---"+o;
            this.sendRocketMQ(o,a);
        }
        return map;
    }

    /**
     * 发送rocketmq信息操作内容
     *
     * @param o
     * @param a
     */
    private void sendRocketMQ(String o, String a) {
        VerifyCode code = VerifyThreadLocal.get();
        LogRetained_yt logR = new LogRetained_yt();
        logR.setTime(System.currentTimeMillis());
        logR.setUsername(code.getUsername());
        logR.setIp("192.168.200.10");
        logR.setO(o);
        logR.setA(a);
        this.rocketMQTemplate.convertAndSend("tanhua", logR);
    }


}
