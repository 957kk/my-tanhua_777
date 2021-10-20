package com.tanhua.server.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.common.service.PicUploadService;
import com.tanhua.common.utils.RelativeDateFormat;
import com.tanhua.common.utils.UserThreadLocal;
import com.tanhua.common.vo.PicUploadResult;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.api.VisitorsApi;
import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.pojo.Visitors;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.vo.CommentVo;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.QuanZiVo;
import com.tanhua.server.vo.VisitorsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class QuanZiService {

    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Reference(version = "1.0.0")
    private VisitorsApi visitorsApi;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PicUploadService picUploadService;

    public PageResult queryPublishList(Integer page, Integer pageSize) {
        //分析：通过dubbo中的服务查询用户的好友动态
        //通过mysql查询用户的信息，回写到结果对象中（QuanZiVo）

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //直接从ThreadLocal中获取对象
        User user = UserThreadLocal.get();

        //通过dubbo查询数据
        PageInfo<Publish> pageInfo = this.quanZiApi.queryPublishList(user.getId(), page, pageSize);
        List<Publish> records = pageInfo.getRecords();
        if (CollUtil.isEmpty(records)) {
            return pageResult;
        }

        pageResult.setItems(this.fillQuanZiVo(records));
        return pageResult;
    }

    /**
     * 填充用户信息
     *
     * @param userInfo
     * @param quanZiVo
     */
    private void fillUserInfoToQuanZiVo(UserInfo userInfo, QuanZiVo quanZiVo) {
        BeanUtil.copyProperties(userInfo, quanZiVo, "id");
        quanZiVo.setGender(userInfo.getSex().name().toLowerCase());
        quanZiVo.setTags(StringUtils.split(userInfo.getTags(), ','));

        //当前用户
        User user = UserThreadLocal.get();

        quanZiVo.setCommentCount(0); //TODO 评论数
        quanZiVo.setDistance("1.2公里"); //TODO 距离
        quanZiVo.setHasLiked(this.quanZiApi.queryUserIsLike(user.getId(), quanZiVo.getId()) ? 1 : 0); //是否点赞（1是，0否）
        quanZiVo.setLikeCount(Convert.toInt(this.quanZiApi.queryLikeCount(quanZiVo.getId()))); //点赞数
        quanZiVo.setHasLoved(this.quanZiApi.queryUserIsLove(user.getId(), quanZiVo.getId()) ? 1 : 0); //是否喜欢（1是，0否）
        quanZiVo.setLoveCount(Convert.toInt(this.quanZiApi.queryLoveCount(quanZiVo.getId()))); //喜欢数
    }

    /**
     * 根据查询到的publish集合填充QuanZiVo对象
     *
     * @param records
     * @return
     */
    private List<QuanZiVo> fillQuanZiVo(List<Publish> records) {
        List<QuanZiVo> quanZiVoList = new ArrayList<>();
        records.forEach(publish -> {
            QuanZiVo quanZiVo = new QuanZiVo();
            quanZiVo.setId(publish.getId().toHexString());
            quanZiVo.setTextContent(publish.getText());
            quanZiVo.setImageContent(publish.getMedias().toArray(new String[]{}));
            quanZiVo.setUserId(publish.getUserId());
            quanZiVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));

            quanZiVoList.add(quanZiVo);
        });

        //查询用户信息
        List<Object> userIds = CollUtil.getFieldValues(records, "userId");
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoByUserIdList(userIds);
        for (QuanZiVo quanZiVo : quanZiVoList) {
            //找到对应的用户信息
            for (UserInfo userInfo : userInfoList) {
                if (quanZiVo.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    this.fillUserInfoToQuanZiVo(userInfo, quanZiVo);
                    break;
                }
            }
        }

        return quanZiVoList;
    }

    /**
     * 发布动态
     *
     * @param textContent
     * @param location
     * @param latitude
     * @param longitude
     * @param multipartFile
     * @return
     */
    public String savePublish(String textContent,
                              String location,
                              String latitude,
                              String longitude,
                              MultipartFile[] multipartFile) {
        //查询当前的登录信息
        User user = UserThreadLocal.get();

        Publish publish = new Publish();
        publish.setUserId(user.getId());
        publish.setText(textContent);
        publish.setLocationName(location);
        publish.setLatitude(latitude);
        publish.setLongitude(longitude);
        publish.setSeeType(1);

        List<String> picUrls = new ArrayList<>();
        //图片上传
        for (MultipartFile file : multipartFile) {
            PicUploadResult picUploadResult = this.picUploadService.upload(file);
            picUrls.add(picUploadResult.getName());
        }

        publish.setMedias(picUrls);
        return this.quanZiApi.savePublish(publish);
    }

    public PageResult queryRecommendPublishList(Integer page, Integer pageSize) {
        //分析：通过dubbo中的服务查询系统推荐动态
        //通过mysql查询用户的信息，回写到结果对象中（QuanZiVo）

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //直接从ThreadLocal中获取对象
        User user = UserThreadLocal.get();

        //通过dubbo查询数据
        PageInfo<Publish> pageInfo = this.quanZiApi.queryRecommendPublishList(user.getId(), page, pageSize);
        List<Publish> records = pageInfo.getRecords();
        if (CollUtil.isEmpty(records)) {
            return pageResult;
        }

        pageResult.setItems(this.fillQuanZiVo(records));
        return pageResult;
    }

    /**
     * 动态点赞
     * @param publishId
     * @return
     */
    public Long likeComment(String publishId) {
        User user = UserThreadLocal.get();

        Boolean result = this.quanZiApi.likeComment(user.getId(), publishId);
        if (result) {
            //查询点赞数
            return this.quanZiApi.queryLikeCount(publishId);
        }
        return null;
    }

    /**
     * 动态的取消点赞
     *
     * @param publishId
     * @return
     */
    public Long disLikeComment(String publishId) {
        User user = UserThreadLocal.get();

        Boolean result = this.quanZiApi.disLikeComment(user.getId(), publishId);
        if (result) {
            //查询点赞数
            return this.quanZiApi.queryLikeCount(publishId);
        }
        return null;
    }

    public Long loveComment(String publishId) {
        User user = UserThreadLocal.get();
        //喜欢
        Boolean result = this.quanZiApi.loveComment(user.getId(), publishId);
        if(result){
            //查询喜欢数
            return this.quanZiApi.queryLoveCount(publishId);
        }
        return null;
    }

    public Long disLoveComment(String publishId) {
        User user = UserThreadLocal.get();
        //取消喜欢
        Boolean result = this.quanZiApi.disLoveComment(user.getId(), publishId);
        if(result){
            //查询喜欢数
            return this.quanZiApi.queryLoveCount(publishId);
        }
        return null;
    }

    public QuanZiVo queryById(String publishId) {
        Publish publish = this.quanZiApi.queryPublishById(publishId);
        if (publish == null) {
            return null;
        }
        return this.fillQuanZiVo(Arrays.asList(publish)).get(0);
    }

    /**
     * 查询评论列表
     *
     * @param publishId
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult queryCommentList(String publishId, Integer page, Integer pageSize) {

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        User user = UserThreadLocal.get();

        //查询评论列表数据
        PageInfo<Comment> pageInfo = this.quanZiApi.queryCommentList(publishId, page, pageSize);
        List<Comment> records = pageInfo.getRecords();
        if(CollUtil.isEmpty(records)){
            return pageResult;
        }

        //查询用户信息
        List<Object> userIdList = CollUtil.getFieldValues(records, "userId");
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoByUserIdList(userIdList);

        List<CommentVo> result = new ArrayList<>();
        for (Comment record : records) {
            CommentVo commentVo = new CommentVo();
            commentVo.setContent(record.getContent());
            commentVo.setId(record.getId().toHexString());
            commentVo.setCreateDate(DateUtil.format(new Date(record.getCreated()), "HH:mm"));
            //是否点赞
            commentVo.setHasLiked(this.quanZiApi.queryUserIsLike(user.getId(), commentVo.getId()) ? 1 : 0);
            //点赞数
            commentVo.setLikeCount(Convert.toInt(this.quanZiApi.queryLikeCount(commentVo.getId())));


            for (UserInfo userInfo : userInfoList) {
                if(ObjectUtil.equals(record.getUserId(), userInfo.getUserId())){

                    commentVo.setAvatar(userInfo.getLogo());
                    commentVo.setNickname(userInfo.getNickName());

                    break;
                }
            }

            result.add(commentVo);
        }

        pageResult.setItems(result);

        return pageResult;
    }

    /**
     * 发表评论
     * @param publishId
     * @param content
     * @return
     */
    public Boolean saveComments(String publishId, String content) {
        User user = UserThreadLocal.get();
        return this.quanZiApi.saveComment(user.getId(), publishId, content);
    }

    public PageResult queryAlbumList(Long userId, Integer page, Integer pageSize) {
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //查询数据
        PageInfo<Publish> pageInfo = this.quanZiApi.queryAlbumList(userId, page, pageSize);
        if(CollUtil.isEmpty(pageInfo.getRecords())){
            return pageResult;
        }

        //填充数据
        pageResult.setItems(this.fillQuanZiVo(pageInfo.getRecords()));

        return pageResult;
    }

    public List<VisitorsVo> queryVisitorsList() {
        User user = UserThreadLocal.get();
        List<Visitors> visitorsList = this.visitorsApi.queryMyVisitor(user.getId());
        if (CollUtil.isEmpty(visitorsList)) {
            return Collections.emptyList();
        }

        List<Object> userIds = CollUtil.getFieldValues(visitorsList, "visitorUserId");
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoByUserIdList(userIds);

        List<VisitorsVo> visitorsVoList = new ArrayList<>();

        for (Visitors visitor : visitorsList) {
            for (UserInfo userInfo : userInfoList) {
                if (ObjectUtil.equals(visitor.getVisitorUserId(), userInfo.getUserId())) {

                    VisitorsVo visitorsVo = new VisitorsVo();
                    visitorsVo.setAge(userInfo.getAge());
                    visitorsVo.setAvatar(userInfo.getLogo());
                    visitorsVo.setGender(userInfo.getSex().name().toLowerCase());
                    visitorsVo.setId(userInfo.getUserId());
                    visitorsVo.setNickname(userInfo.getNickName());
                    visitorsVo.setTags(StringUtils.split(userInfo.getTags(), ','));
                    visitorsVo.setFateValue(visitor.getScore().intValue());

                    visitorsVoList.add(visitorsVo);
                    break;
                }
            }
        }

        return visitorsVoList;
    }
}
