package com.tanhua.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.mapper.UserInfoMapper;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.pojo.Video;
import com.tanhua.vo.PageResultMXY;
import com.tanhua.vo.VideosVOListMXY;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VideosListImplMXY {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserInfoMapper userInfoMapper;

    public PageResultMXY queryVideosVOList(Map<String, String> param) {
        Query query = Query.query(Criteria.where("userId").is(Convert.toLong(param.get("uid"))));
        List<Video> videoList = mongoTemplate.find(query, Video.class);
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", param.get("uid"));
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
        List<VideosVOListMXY> voListMXIES = new ArrayList<>();
        for (Video video : videoList) {
            long likeCount = mongoTemplate.count(Query.query(Criteria.where("publishId").is(video.getId()).and("commentType").is(1)), Comment.class);
            long commentCount = mongoTemplate.count(Query.query(Criteria.where("publishId").is(video.getId()).and("commentType").is(2)), Comment.class);
            long forwardingCount = mongoTemplate.count(Query.query(Criteria.where("publishId").is(video.getId()).and("commentType").is(3)), Comment.class);
            VideosVOListMXY voListMXY = new VideosVOListMXY();
            voListMXY.setId(StrUtil.toString(video.getId()));
            voListMXY.setNickname(userInfo.getNickName());
            voListMXY.setUserId(userInfo.getUserId());
            voListMXY.setCreateDate(video.getCreated());
            voListMXY.setReportCount(100);//举报数
            voListMXY.setLikeCount(Convert.toInt(likeCount));
            voListMXY.setCommentCount(Convert.toInt(commentCount));
            voListMXY.setForwardingCount(Convert.toInt(forwardingCount));
            voListMXY.setVideoUrl(video.getVideoUrl());
            voListMXY.setPicUrl(video.getPicUrl());
            voListMXIES.add(voListMXY);
        }
        if (CollUtil.isEmpty(voListMXIES)) {
            return null;
        }
        PageResultMXY pageResultMXY = new PageResultMXY();
        pageResultMXY.setCounts(voListMXIES.size());
        pageResultMXY.setPage(Convert.toInt(param.get("page")));
        pageResultMXY.setPagesize(Convert.toInt(param.get("pagesize")));
        pageResultMXY.setPages(Convert.toInt(Math.ceil(voListMXIES.size() / Convert.toInt(param.get("pagesize")))) + 1);
        switch (param.get("sortProp")) {
            case "createDate": {
                if ("ascending".equals(param.get("sortOrder"))) {
                    //升序
                    voListMXIES.sort(Comparator.comparing(VideosVOListMXY::getCreateDate));
                } else {
                    //降序
                    voListMXIES.sort(Comparator.comparing(VideosVOListMXY::getCreateDate).reversed());
                }
            }
            break;
            case "reportCount": {
                if ("ascending".equals(param.get("sortOrder"))) {
                    //升序
                    voListMXIES.sort(Comparator.comparing(VideosVOListMXY::getReportCount));
                } else {
                    //降序
                    voListMXIES.sort(Comparator.comparing(VideosVOListMXY::getReportCount).reversed());
                }
            }
            break;
            case "likeCount": {
                if ("ascending".equals(param.get("sortOrder"))) {
                    //升序
                    voListMXIES.sort(Comparator.comparing(VideosVOListMXY::getLikeCount));
                } else {
                    //降序
                    voListMXIES.sort(Comparator.comparing(VideosVOListMXY::getLikeCount).reversed());
                }
            }
            break;
            case "commentCount": {
                if ("ascending".equals(param.get("sortOrder"))) {
                    //升序
                    voListMXIES.sort(Comparator.comparing(VideosVOListMXY::getCommentCount));
                } else {
                    //降序
                    voListMXIES.sort(Comparator.comparing(VideosVOListMXY::getCommentCount).reversed());
                }
            }
            break;
            case "forwardingCount": {
                if ("ascending".equals(param.get("sortOrder"))) {
                    //升序
                    voListMXIES.sort(Comparator.comparing(VideosVOListMXY::getForwardingCount));
                } else {
                    //降序
                    voListMXIES.sort(Comparator.comparing(VideosVOListMXY::getForwardingCount).reversed());
                }
            }
            break;
            default:
                return null;
        }
        List<VideosVOListMXY> page = CollUtil.page(Convert.toInt(param.get("page")) - 1, Convert.toInt(param.get("pagesize")), voListMXIES);
        pageResultMXY.setItems(page);

        return pageResultMXY;
    }

}
