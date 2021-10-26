package com.tanhua.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mongodb.client.result.DeleteResult;
import com.tanhua.common.mapper.FourUserMapper;
import com.tanhua.common.mapper.UserInfoMapper;
import com.tanhua.common.pojo.LogRetained_yt;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.common.pojo.VerifyCode;
import com.tanhua.common.utils.VerifyThreadLocal;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.pojo.QuanZiStatusMXY;
import com.tanhua.pojo.QuanZiTopMXY;
import com.tanhua.vo.CommentVOListMXY;
import com.tanhua.vo.PageResultMXY;
import com.tanhua.vo.QuanZiListVOMXY;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
public class QuanZiListVOImplMXY {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private FourUserMapper fourUserMapper;
    private static final String QUANZITOP = "圈子置顶";
    private static final String QUANZIUNTOP = "取消圈子置顶";

    /**
     * 获取用户动态列表
     *
     * @param param
     * @return
     */
    public PageResultMXY queryQuanZiVOList(Map<String, String> param) {
        //当获取第一页圈子列表时判断是否有置顶圈子
        List<QuanZiListVOMXY> quanZiListVOMXYList1 = getTopQuanZiList(param.get("uid"));
        List<QuanZiListVOMXY> quanZiListVOMXYList2 = getQuanZiListVOMXIES(param.get("uid"));
        List<QuanZiListVOMXY> quanZiListVOMXYList = CollUtil.subtractToList(quanZiListVOMXYList2, quanZiListVOMXYList1);
        if (CollUtil.isEmpty(quanZiListVOMXYList)) {
            return null;
        }
        PageResultMXY pageResultMXY = new PageResultMXY();
        pageResultMXY.setPage(Convert.toInt(param.get("page")));
        pageResultMXY.setPagesize(Convert.toInt(param.get("pagesize")));

        switch (param.get("sortProp")) {
            case "createDate": {
                if ("ascending".equals(param.get("sortOrder"))) {
                    //升序
                    quanZiListVOMXYList.sort(Comparator.comparing(QuanZiListVOMXY::getCreateDate));
                } else {
                    //降序
                    quanZiListVOMXYList.sort(Comparator.comparing(QuanZiListVOMXY::getCreateDate).reversed());
                }
            }
            break;
            case "reportCount": {
                if ("ascending".equals(param.get("sortOrder"))) {
                    //升序
                    quanZiListVOMXYList.sort(Comparator.comparing(QuanZiListVOMXY::getReportCount));
                } else {
                    //降序
                    quanZiListVOMXYList.sort(Comparator.comparing(QuanZiListVOMXY::getReportCount).reversed());
                }
            }
            break;
            case "likeCount": {
                if ("ascending".equals(param.get("sortOrder"))) {
                    //升序
                    quanZiListVOMXYList.sort(Comparator.comparing(QuanZiListVOMXY::getLikeCount));
                } else {
                    //降序
                    quanZiListVOMXYList.sort(Comparator.comparing(QuanZiListVOMXY::getLikeCount).reversed());
                }
            }
            break;
            case "commentCount": {
                if ("ascending".equals(param.get("sortOrder"))) {
                    //升序
                    quanZiListVOMXYList.sort(Comparator.comparing(QuanZiListVOMXY::getCommentCount));
                } else {
                    //降序
                    quanZiListVOMXYList.sort(Comparator.comparing(QuanZiListVOMXY::getCommentCount).reversed());
                }
            }
            break;
            case "forwardingCount": {
                if ("ascending".equals(param.get("sortOrder"))) {
                    //升序
                    quanZiListVOMXYList.sort(Comparator.comparing(QuanZiListVOMXY::getForwardingCount));
                } else {
                    //降序
                    quanZiListVOMXYList.sort(Comparator.comparing(QuanZiListVOMXY::getForwardingCount).reversed());
                }
            }
            break;
            default:
                return null;
        }
        if (Convert.toInt(param.get("page")) == 1) {
            for (int i = 0; i < quanZiListVOMXYList1.size(); i++) {
                quanZiListVOMXYList.add(i, quanZiListVOMXYList1.get(i));
            }
        }
        pageResultMXY.setCounts(quanZiListVOMXYList.size());
        pageResultMXY.setPages(Convert.toInt(Math.ceil(quanZiListVOMXYList.size() / Convert.toInt(param.get("pagesize")))));
        List<QuanZiListVOMXY> page = CollUtil.page(Convert.toInt(param.get("page")) - 1, Convert.toInt(param.get("pagesize")), quanZiListVOMXYList);
        pageResultMXY.setItems(page);

        return pageResultMXY;
    }

    private List<QuanZiListVOMXY> getTopQuanZiList(String uid) {
        List<QuanZiTopMXY> topMXIES = mongoTemplate.find(Query.query(Criteria.where("userId").is(Convert.toLong(uid))), QuanZiTopMXY.class);
        List<ObjectId> publishId = CollUtil.getFieldValues(topMXIES, "publishId", ObjectId.class);
        //List<ObjectId> publishIds = getObjectIds(publishId);
        List<Publish> publishList = mongoTemplate.find(Query.query(Criteria.where("id").in(publishId)), Publish.class);
        return getQuanZiListVOMXList(uid, publishList);
    }

    private List<ObjectId> getObjectIds(List<Object> publishId) {
        List<ObjectId> publishIds = new ArrayList<>();
        for (Object o : publishId) {
            publishIds.add(new ObjectId((String) o));
        }
        return publishIds;
    }

    private List<QuanZiListVOMXY> getQuanZiListVOMXList(String uid, List<Publish> publishList) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", uid);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
        List<QuanZiListVOMXY> quanZiListVOMXYList = new ArrayList<>();
        for (Publish publish : publishList) {
            long likeCount = mongoTemplate.count(Query.query(Criteria.where("publishId").is(publish.getId()).and("commentType").is(1)), Comment.class);
            long commentCount = mongoTemplate.count(Query.query(Criteria.where("publishId").is(publish.getId()).and("commentType").is(2)), Comment.class);
            long forwardingCount = mongoTemplate.count(Query.query(Criteria.where("publishId").is(publish.getId()).and("commentType").is(3)), Comment.class);
            QuanZiListVOMXY quanZiListVOMXY = new QuanZiListVOMXY();
            quanZiListVOMXY.setId(StrUtil.toString(publish.getId()));
            quanZiListVOMXY.setNickname(userInfo.getNickName());
            quanZiListVOMXY.setUserId(userInfo.getUserId());
            quanZiListVOMXY.setUserLogo(userInfo.getLogo());
            quanZiListVOMXY.setCreateDate(publish.getCreated());
            quanZiListVOMXY.setText(publish.getText());
            QuanZiStatusMXY quanZiStatusMXY = mongoTemplate.findOne(Query.query(Criteria.where("publishId").is(publish.getId())), QuanZiStatusMXY.class);
            quanZiListVOMXY.setState(ObjectUtil.isEmpty(quanZiStatusMXY) ? "all" : quanZiStatusMXY.getStatus());
            quanZiListVOMXY.setReportCount(100);//举报数
            quanZiListVOMXY.setLikeCount(Convert.toInt(likeCount));
            quanZiListVOMXY.setCommentCount(Convert.toInt(commentCount));
            quanZiListVOMXY.setForwardingCount(Convert.toInt(forwardingCount));
            quanZiListVOMXY.setMedias(publish.getMedias().toArray(new String[publish.getMedias().size()]));
            quanZiListVOMXYList.add(quanZiListVOMXY);
        }
        return quanZiListVOMXYList;
    }

    private boolean isTop(String uid) {
        boolean exists = mongoTemplate.exists(Query.query(Criteria.where("userId").is(uid)), QuanZiTopMXY.class);
        return exists;
    }

    private List<QuanZiListVOMXY> getQuanZiListVOMXIES(String uid) {
        Query query = Query.query(Criteria.where("userId").is(Convert.toLong(uid)));
        List<Publish> publishList = mongoTemplate.find(query, Publish.class);
        return getQuanZiListVOMXList(uid, publishList);
    }

    /**
     * 查询动态详情
     *
     * @param id
     * @return
     */
    public QuanZiListVOMXY queryQuanZiInfo(String id) {
        Publish publish = mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), Publish.class);
        List<QuanZiListVOMXY> quanZiListVOMXIES = getQuanZiListVOMXIES(publish.getUserId() + "");
        for (QuanZiListVOMXY quanZiListVOMXY : quanZiListVOMXIES) {
            if (quanZiListVOMXY.getId().equals(id)) {
                //TODO
                //动态的置顶状态
                quanZiListVOMXY.setTopState(1);
                return quanZiListVOMXY;
            }
        }
        return null;
    }

    public PageResultMXY queryCommentVOList(Map<String, String> param) {
        PageResultMXY pageResultMXY = new PageResultMXY();
        long count = mongoTemplate.count(Query.query(Criteria.where("publishId").is(new ObjectId(param.get("messageID")))), Comment.class);
        pageResultMXY.setPages(Convert.toInt(count / Convert.toInt(param.get("pagesize"))) + 1);
        pageResultMXY.setCounts(Convert.toInt(count));
        PageRequest pageRequest = PageRequest.of(Convert.toInt(param.get("page")) - 1, Convert.toInt(param.get("pagesize")));
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(param.get("messageID"))));
        query.with(pageRequest).with(Sort.by(Sort.Order.desc(param.get("sortProp"))));
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        if (CollUtil.isEmpty(comments)) {
            return new PageResultMXY(0, 0, 0, 0, null, null);
        }
        List<Object> list = CollUtil.getFieldValues(comments, "userId");
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", list);
        List<UserInfo> userInfos = userInfoMapper.selectList(queryWrapper);
        List<CommentVOListMXY> commentVOListMXYList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentVOListMXY commentVOListMXY = new CommentVOListMXY();
            commentVOListMXY.setId(comment.getId().toHexString());
            commentVOListMXY.setUserId(comment.getUserId());
            commentVOListMXY.setContent(comment.getContent());
            commentVOListMXY.setCreateDate(comment.getCreated());
            for (UserInfo userInfo : userInfos) {
                if (comment.getUserId().equals(userInfo.getUserId())) {
                    commentVOListMXY.setNickname(userInfo.getNickName());
                    break;
                }
            }
            commentVOListMXYList.add(commentVOListMXY);
        }

        pageResultMXY.setPage(Convert.toInt(Convert.toInt(param.get("page"))));
        pageResultMXY.setPagesize(Convert.toInt(param.get("pagesize")));

        pageResultMXY.setItems(commentVOListMXYList);

        return pageResultMXY;
    }

    public Map<String, String> setQuanZiTop(ObjectId publishId) {
        VerifyCode code = VerifyThreadLocal.get();
        Map<String, String> result = new HashMap<>();
        Query query = Query.query(Criteria.where("id").is(publishId));
        Publish publish = mongoTemplate.findOne(query, Publish.class);
        QuanZiTopMXY quanZiTopMXY = new QuanZiTopMXY();
        quanZiTopMXY.setId(ObjectId.get());
        quanZiTopMXY.setPublishId(publishId);
        quanZiTopMXY.setUserId(publish.getUserId());
        //设置管理员账户名称
        quanZiTopMXY.setManagername(code.getUsername());
        quanZiTopMXY.setCreated(System.currentTimeMillis());

        QuanZiTopMXY topMXY = mongoTemplate.save(quanZiTopMXY);
        if (ObjectUtil.isNotEmpty(topMXY)) {
            result.put("message", "操作成功");

            String o = QUANZITOP;
            String a = code.getUsername() + "将用户为" + publish.getUserId() + "的圈子" + publishId + "进行了置顶操作";
            sendRocketMQ(o, a);

            return result;
        }
        result.put("message", "操作失败");
        return result;
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

    public Map<String, String> setQuanZiUntop(ObjectId objectId) {
        VerifyCode code = VerifyThreadLocal.get();
        Map<String, String> result = new HashMap<>();
        boolean flag = mongoTemplate.exists(Query.query(Criteria.where("publishId").is(objectId)), QuanZiTopMXY.class);
        if (!flag) {
            result.put("message", "操作成功");
            String o = QUANZIUNTOP;
            String a = code.getUsername() + "将圈子为" + objectId + "进行了取消置顶操作";
            sendRocketMQ(o, a);
            return result;
        }
        DeleteResult deleteResult = mongoTemplate.remove(Query.query(Criteria.where("publishId").is(objectId)), QuanZiTopMXY.class);
        if (deleteResult.getDeletedCount() != 1) {
            result.put("message", "失败");
            return result;
        }
        result.put("message", "操作成功");

        String o = QUANZIUNTOP;
        String a = code.getUsername() + "将圈子为" + objectId + "进行了取消置顶操作";
        //String a = "admin将圈子为" + objectId + "进行了取消置顶操作";
        sendRocketMQ(o, a);
        return result;
    }

    public PageResultMXY quanZiListCheck(Map<String, String> param) {
        PageResultMXY pageResultMXY = new PageResultMXY();
        pageResultMXY.setPage(Convert.toInt(param.get("page")));
        pageResultMXY.setPagesize(Convert.toInt(param.get("pagesize")));
        List<QuanZiListVOMXY> quanZiList = getQuanZiList(param);
        pageResultMXY.setCounts(Convert.toInt(this.getCount(param)));
        pageResultMXY.setPages(Convert.toInt(Math.ceil(pageResultMXY.getCounts() / pageResultMXY.getPagesize())));
        //List<QuanZiListVOMXY> page = CollUtil.page(Convert.toInt(param.get("page")) - 1, Convert.toInt(param.get("pagesize")), quanZiList);
        pageResultMXY.setItems(quanZiList);
        return pageResultMXY;

    }


    public long getCount(Map<String, String> param) {
        List<Publish> publishList = null;
        Query query = new Query();
        if (Convert.toLong(param.get("sd")) > 0 && Convert.toLong(param.get("ed")) > 0 && !param.get("sd").equals(param.get("ed"))) {
            query.addCriteria(
                    new Criteria("").andOperator(Criteria.where("created").lte(Convert.toLong(param.get("ed"))),
                            Criteria.where("created").gte(Convert.toLong(param.get("sd")))));
        }else if (Convert.toLong(param.get("sd")).equals(Convert.toLong(param.get("ed")))){
            DateTime dateTime2 = new DateTime(Convert.toLong(param.get("sd")), DateTimeZone.forID("+08:00"));
            long now = dateTime2.withMillisOfDay(0).plusDays(0).getMillis();
            long after = dateTime2.withMillisOfDay(0).plusDays(1).getMillis();
            query.addCriteria(
                    new Criteria("").andOperator(Criteria.where("created").lt(after),
                            Criteria.where("created").gte(now)));
        }
        if ("descending".equals(param.get("sortOrder"))) {
            query.with(Sort.by(Sort.Order.desc("created")));
        } else {
            query.with(Sort.by(Sort.Order.asc("created")));
        }
        List<UserInfo> userInfoList = null;
        if (StrUtil.isNotEmpty(param.get("id"))) {
            userInfoList = getUserInfoListByIds(Collections.singletonList(Convert.toLong(param.get("id"))));
            List<Long> userId = CollUtil.getFieldValues(userInfoList, "userId", Long.class);
            query.addCriteria(Criteria.where("userId").in(userId));
        } else {
            userInfoList = getUserInfoListByIds(null);
        }
        //全部  all
        if ("all".equals(param.get("state"))) {
            publishList = mongoTemplate.find(query, Publish.class);
        } else {
            List<QuanZiStatusMXY> quanZiStatusMXYList = getquanZiStatusMXYListByState(param);
            List<ObjectId> ids = CollUtil.getFieldValues(quanZiStatusMXYList, "publishId", ObjectId.class);
            query.addCriteria(Criteria.where("id").in(ids));
            publishList = mongoTemplate.find(query, Publish.class);

        }

        return publishList.size();


    }

    private List<QuanZiListVOMXY> getQuanZiList(Map<String, String> param) {
        PageRequest pageRequest = PageRequest.of(Convert.toInt(param.get("page")) - 1, Convert.toInt(param.get("pagesize")));
        List<QuanZiListVOMXY> quanZiListVOMXYList = null;
        Query query = new Query();
        if (Convert.toLong(param.get("sd")) > 0 && Convert.toLong(param.get("ed")) > 0&& !param.get("sd").equals(param.get("ed"))) {
            query.addCriteria(
                    new Criteria("").andOperator(Criteria.where("created").lte(Convert.toLong(param.get("ed"))),
                            Criteria.where("created").gte(Convert.toLong(param.get("sd")))));
        }else if (Convert.toLong(param.get("sd")).equals(Convert.toLong(param.get("ed"))) ){
            DateTime dateTime2 = new DateTime(Convert.toLong(param.get("sd")), DateTimeZone.forID("+08:00"));
            long now = dateTime2.withMillisOfDay(0).plusDays(0).getMillis();
            long after = dateTime2.withMillisOfDay(0).plusDays(1).getMillis();
            query.addCriteria(
                    new Criteria("").andOperator(Criteria.where("created").lt(after),
                            Criteria.where("created").gte(now)));
        }
        if ("descending".equals(param.get("sortOrder"))) {
            query.with(Sort.by(Sort.Order.desc("created")));
        } else {
            query.with(Sort.by(Sort.Order.asc("created")));
        }
        query.with(pageRequest);
        List<UserInfo> userInfoList = null;
        if (StrUtil.isNotEmpty(param.get("id"))) {
            userInfoList = getUserInfoListByIds(Collections.singletonList(Convert.toLong(param.get("id"))));
            List<Long> userId = CollUtil.getFieldValues(userInfoList, "userId", Long.class);
            query.addCriteria(Criteria.where("userId").in(userId));
        } else {
            userInfoList = getUserInfoListByIds(null);
        }
        //全部  all
        if ("all".equals(param.get("state"))) {
            List<Publish> publishList = mongoTemplate.find(query, Publish.class);
            List<ObjectId> ids = CollUtil.getFieldValues(publishList, "id", ObjectId.class);
            List<QuanZiStatusMXY> quanZiStatusMXYList = getquanZiStatusMXYListByIds(ids);
            quanZiListVOMXYList = getFullquanZiListVOMXY(publishList, userInfoList, quanZiStatusMXYList);
        } else {
            List<QuanZiStatusMXY> quanZiStatusMXYList = getquanZiStatusMXYListByState(param);
            List<ObjectId> ids = CollUtil.getFieldValues(quanZiStatusMXYList, "publishId", ObjectId.class);
            query.addCriteria(Criteria.where("id").in(ids));
            List<Publish> publishList = mongoTemplate.find(query, Publish.class);
            quanZiListVOMXYList = getFullquanZiListVOMXY(publishList, userInfoList, quanZiStatusMXYList);
        }

        return quanZiListVOMXYList;
    }

    /**
     * 通过state获取QuanZiStatusMXY
     *
     * @param param
     * @return
     */
    private List<QuanZiStatusMXY> getquanZiStatusMXYListByState(Map<String, String> param) {
        Query query = Query.query(new Criteria());
        if (Convert.toLong(param.get("sd")) > 0 && Convert.toLong(param.get("ed")) > 0) {
            query.addCriteria(
                    new Criteria("").andOperator(Criteria.where("created").lte(Convert.toLong(param.get("ed"))),
                            Criteria.where("created").gte(Convert.toLong(param.get("sd")))));
        }
        if (!"all".equals(param.get("state"))) {
            query.addCriteria(Criteria.where("status").is(param.get("state")));
        }
        List<QuanZiStatusMXY> quanZiStatusMXYList = mongoTemplate.find(query, QuanZiStatusMXY.class);
        return quanZiStatusMXYList;
    }

    /**
     * 填充QuanZiListVOMXY对象
     *
     * @param publishList
     * @param userInfoList
     * @param quanZiStatusMXYList
     * @return
     */
    private List<QuanZiListVOMXY> getFullquanZiListVOMXY(List<Publish> publishList, List<UserInfo> userInfoList, List<QuanZiStatusMXY> quanZiStatusMXYList) {
        List<QuanZiListVOMXY> quanZiListVOMXYList = new ArrayList<>();
        int i = 0;
        for (Publish publish : publishList) {
            long likeCount = mongoTemplate.count(Query.query(Criteria.where("publishId").is(publish.getId()).and("commentType").is(1)), Comment.class);
            long commentCount = mongoTemplate.count(Query.query(Criteria.where("publishId").is(publish.getId()).and("commentType").is(2)), Comment.class);
            long forwardingCount = mongoTemplate.count(Query.query(Criteria.where("publishId").is(publish.getId()).and("commentType").is(3)), Comment.class);
            QuanZiListVOMXY quanZiListVOMXY = new QuanZiListVOMXY();
            quanZiListVOMXY.setId(StrUtil.toString(publish.getId()));
            for (UserInfo userInfo : userInfoList) {
                if (ObjectUtil.equal(userInfo.getUserId(), publish.getUserId())) {
                    quanZiListVOMXY.setNickname(userInfo.getNickName());
                    quanZiListVOMXY.setUserId(userInfo.getUserId());
                    quanZiListVOMXY.setUserLogo(userInfo.getLogo());
                    break;
                }
            }
            quanZiListVOMXY.setCreateDate(publish.getCreated());
            quanZiListVOMXY.setText(publish.getText());
            for (QuanZiStatusMXY quanZiStatusMXY : quanZiStatusMXYList) {
                if (quanZiStatusMXY.getPublishId().equals(publish.getId())) {
                    quanZiListVOMXY.setState(quanZiStatusMXY.getStatus());
                    break;
                }
            }
            quanZiListVOMXY.setReportCount(100);//举报数
            quanZiListVOMXY.setLikeCount(Convert.toInt(likeCount));
            quanZiListVOMXY.setCommentCount(Convert.toInt(commentCount));
            quanZiListVOMXY.setForwardingCount(Convert.toInt(forwardingCount));
            quanZiListVOMXY.setMedias(publish.getMedias().toArray(new String[publish.getMedias().size()]));
            quanZiListVOMXYList.add(quanZiListVOMXY);
            i++;
            if (i == 10) {
                break;
            }
        }
        return quanZiListVOMXYList;
    }

    /**
     * 通过id获取QuanZiStatusMXY
     *
     * @param ids
     * @return
     */
    private List<QuanZiStatusMXY> getquanZiStatusMXYListByIds(List<ObjectId> ids) {
        List<QuanZiStatusMXY> quanZiStatusMXYList = mongoTemplate.find(Query.query(Criteria.where("publishId").in(ids)), QuanZiStatusMXY.class);
        return quanZiStatusMXYList;
    }

    /**
     * 通过userid获取userinfo信息
     *
     * @param userId
     * @return
     */
    private List<UserInfo> getUserInfoListByIds(List<Long> userId) {
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(userId)) {
            query.like("user_id", userId.get(0));
        }
        List<UserInfo> userInfoList = userInfoMapper.selectList(query);
        return userInfoList;
    }

    /**
     * 圈子审核通过
     *
     * @param ids
     * @return
     */
    public Map<String, String> quanZiPass(List<ObjectId> ids) {
        //先查询圈子状态
        List<QuanZiStatusMXY> QuanZiStatusMXYs = mongoTemplate.find(Query.query(Criteria.where("publishId").in(ids)), QuanZiStatusMXY.class);
        for (QuanZiStatusMXY quanZiStatusMXY : QuanZiStatusMXYs) {
            if (!"3".equals(quanZiStatusMXY.getStatus())) {
                quanZiStatusMXY.setStatus("3");
                mongoTemplate.save(quanZiStatusMXY);
            }
        }
        return new HashMap<>();
    }

    /**
     * 圈子审核驳回
     *
     * @param ids
     * @return
     */
    public Map<String, String> quanZiReject(List<ObjectId> ids) {
        //先查询圈子状态
        List<QuanZiStatusMXY> QuanZiStatusMXYs = mongoTemplate.find(Query.query(Criteria.where("publishId").in(ids)), QuanZiStatusMXY.class);
        for (QuanZiStatusMXY quanZiStatusMXY : QuanZiStatusMXYs) {
            if (!"4".equals(quanZiStatusMXY.getStatus())) {
                quanZiStatusMXY.setStatus("4");
                mongoTemplate.save(quanZiStatusMXY);
            }
        }
        return new HashMap<>();
    }

    /**
     * 圈子状态撤销
     *
     * @param ids
     * @return
     */
    public Map<String, String> quanZiRevocation(List<ObjectId> ids) {
        //先查询圈子状态
        List<QuanZiStatusMXY> QuanZiStatusMXYs = mongoTemplate.find(Query.query(Criteria.where("publishId").in(ids)), QuanZiStatusMXY.class);
        for (QuanZiStatusMXY quanZiStatusMXY : QuanZiStatusMXYs) {
            if (!"2".equals(quanZiStatusMXY.getStatus())) {
                quanZiStatusMXY.setStatus("3");
                mongoTemplate.save(quanZiStatusMXY);
            }
        }
        return new HashMap<>();
    }

}
