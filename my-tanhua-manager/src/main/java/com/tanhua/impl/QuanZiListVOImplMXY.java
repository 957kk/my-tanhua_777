package com.tanhua.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mongodb.client.result.DeleteResult;
import com.tanhua.common.mapper.UserInfoMapper;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.pojo.QuanZiTopMXY;
import com.tanhua.vo.CommentVOListMXY;
import com.tanhua.vo.PageResultMXY;
import com.tanhua.vo.QuanZiListVOMXY;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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
        if(Convert.toInt(param.get("page"))== 1){
            for (int i = 0; i < quanZiListVOMXYList1.size(); i++) {
                quanZiListVOMXYList.add(i,quanZiListVOMXYList1.get(i));
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
        List<ObjectId> publishId = CollUtil.getFieldValues(topMXIES, "publishId",ObjectId.class);
        //List<ObjectId> publishIds = getObjectIds(publishId);
        List<Publish> publishList = mongoTemplate.find(Query.query(Criteria.where("id").in(publishId)), Publish.class);
        return getQuanZiListVOMXList(uid,publishList);
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
            quanZiListVOMXY.setState(1);//TODO//圈子状态待查寻
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
            return new PageResultMXY(0, 0, 0, 0, null);
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
        Map<String, String> result = new HashMap<>();
        Query query = Query.query(Criteria.where("id").is(publishId));
        Publish publish = mongoTemplate.findOne(query, Publish.class);
        QuanZiTopMXY quanZiTopMXY = new QuanZiTopMXY();
        quanZiTopMXY.setId(ObjectId.get());
        quanZiTopMXY.setPublishId(publishId);
        quanZiTopMXY.setUserId(publish.getUserId());
        //设置管理员账户名称
        quanZiTopMXY.setManagername("");//TODO
        quanZiTopMXY.setCreated(System.currentTimeMillis());

        QuanZiTopMXY topMXY = mongoTemplate.save(quanZiTopMXY);
        if (ObjectUtil.isNotEmpty(topMXY)) {
            result.put("message", "操作成功");
            return result;
        }
        result.put("message", "操作失败");
        return result;
    }

    public Map<String, String> setQuanZiUntop(ObjectId objectId) {
        Map<String, String> result = new HashMap<>();
        boolean flag = mongoTemplate.exists(Query.query(Criteria.where("publishId").is(objectId)), QuanZiTopMXY.class);
        if (!flag) {
            result.put("message", "操作成功");
            return result;
        }
        DeleteResult deleteResult = mongoTemplate.remove(Query.query(Criteria.where("publishId").is(objectId)), QuanZiTopMXY.class);
        if (deleteResult.getDeletedCount() != 1) {
            result.put("message", "失败");
            return result;
        }
        result.put("message", "操作成功");
        return result;
    }
}
