package com.tanhua.manager.user.management.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.common.enums.SexEnum;
import com.tanhua.common.mapper.UserInfoMapper;
import com.tanhua.common.mapper.UserLogInfoMapper_zxk;
import com.tanhua.common.mapper.UserMapper;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.common.mapper.AccountStatusMapperMXY;
import com.tanhua.common.pojo.AccountStatusMXY;
import com.tanhua.common.pojo.UserLogInfo;
import com.tanhua.manager.user.management.vo.PageResultMXY;
import com.tanhua.manager.user.management.vo.UsersListVOMXY;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserListInfoImplMXY {
    @Autowired
    private AccountStatusMapperMXY accountStatusMapperMXY;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserLogInfoMapper_zxk userLogInfoMapper_zxk;
    @Autowired
    private UserMapper userMapper;

    public PageResultMXY queryUserListInfo(Map<String, String> param) {
        PageResultMXY pageResultMXY = new PageResultMXY();
        pageResultMXY.setPage(Convert.toInt(param.get("page")));
        pageResultMXY.setPagesize(Convert.toInt(param.get("pagesize")));
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        Page<UserInfo> page = new Page<>();
        if(ObjectUtil.isNotEmpty(param.get("page"))&&ObjectUtil.isNotEmpty(param.get("pagesize"))){
            //分页查询
            page.setCurrent(Convert.toLong(param.get("page")));
            page.setSize(Convert.toLong(param.get("pagesize")));
        }

        if(ObjectUtil.isNotEmpty(param.get("nickname"))){
            queryWrapper.like("nick_name", param.get("nickname"));
        }
        if(ObjectUtil.isNotEmpty(param.get("id"))){
            queryWrapper.like("user_id", param.get("id"));
        }
        if(ObjectUtil.isNotEmpty(param.get("city"))){
            queryWrapper.like("city", param.get("city"));
        }
        //查询userInfor表获取列表

        IPage<UserInfo> userInfoIPage = userInfoMapper.selectPage(page, queryWrapper);
        List<UserInfo> userInfoList = userInfoIPage.getRecords();

        //List<UserInfo> userInfoList = userInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(userInfoList)) {
            log.error("查询用户信息列表出错");
            return pageResultMXY;
        }
        pageResultMXY.setCounts(userInfoList.size());
        pageResultMXY.setPages((int) Math.ceil(userInfoList.size() / Convert.toInt(param.get("pagesize"))));

        List<Object> userIds = CollUtil.getFieldValues(userInfoList, "userId");
        List<Long> userIdList = new ArrayList<>();
        for (Object userId : userIds) {
            userIdList.add(Convert.toLong(userId));
        }
        //查询user表获取信息
        QueryWrapper<User> wapper = new QueryWrapper<>();
        queryWrapper.in("id",userIdList);
        List<User> users = userMapper.selectList(wapper);
        //查询用户状态表获取用户状态信息
        QueryWrapper<AccountStatusMXY> query = new QueryWrapper<>();
        query.in("userId",userIdList);
        List<AccountStatusMXY> accountStatusList = accountStatusMapperMXY.selectList(query);
        //查询用户登录信息表获取用户的登录信息
        List<UserLogInfo> userLogInfos = new ArrayList<>();
        for (Long userId : userIdList) {
            QueryWrapper<UserLogInfo> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("user_id", userId);

            queryWrapper1.orderByDesc("login_time");

            List<UserLogInfo> userLogInfo = userLogInfoMapper_zxk.selectList(queryWrapper1);
            if(CollUtil.isNotEmpty(userLogInfo)){
                userLogInfos.add(userLogInfo.get(0));
            }
        }
        //用户信息对象填充
        List<UsersListVOMXY> usersListVOList = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            UsersListVOMXY usersListVOMXY = new UsersListVOMXY();
            usersListVOMXY.setAge(userInfo.getAge());
            usersListVOMXY.setCity(userInfo.getCity().split("-")[0]);
            usersListVOMXY.setId(Convert.toInt(userInfo.getUserId()));
            usersListVOMXY.setLogo(userInfo.getLogo());
            usersListVOMXY.setLogoStatus(1 + "");
            usersListVOMXY.setNickname(userInfo.getNickName());
            usersListVOMXY.setOccupation(null);
            usersListVOMXY.setSex(SexEnum.MAN == userInfo.getSex() ? "男" : "女");
            for (User user : users) {
                if(userInfo.getUserId() == user.getId()){
                    usersListVOMXY.setMobile(user.getMobile());
                    break;
                }
            }
            for (AccountStatusMXY accountStatusMXY : accountStatusList) {
                if (userInfo.getUserId() == accountStatusMXY.getUserid()) {
                    usersListVOMXY.setUserStatus(2 + "");
                    break;
                }
            }
            if(null==usersListVOMXY.getUserStatus()){
                usersListVOMXY.setUserStatus(1 + "");
            }
            for (UserLogInfo userLogInfo : userLogInfos) {
                if (userInfo.getUserId() == userLogInfo.getUserId()) {
                    usersListVOMXY.setLastActiveTime(Convert.toLong(userLogInfo.getLoginTime()));
                    break;
                }
            }
            usersListVOList.add(usersListVOMXY);
        }
        pageResultMXY.setItems(usersListVOList);
        return pageResultMXY;
    }
}
