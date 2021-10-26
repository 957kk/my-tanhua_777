package com.tanhua.sso.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.mapper.UserFreezeMapperMXY;
import com.tanhua.common.mapper.UserLogInfoMapper_zxk;
import com.tanhua.common.mapper.UserMapper;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserFreezeMXY;
import com.tanhua.common.pojo.UserLogInfo;
import com.tanhua.common.pojo.UserlogIn_yt;
import com.tanhua.common.utils.UserFreezenState;
import com.tanhua.dubbo.server.api.HuanXinApi;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Reference(version = "1.0.0")
    private HuanXinApi huanXinApi;

    @Autowired
    private UserLogInfoMapper_zxk userLogInfoMapper_zxk;

    @Autowired
    private UserFreezeMapperMXY userFreezeMapperMXY;

    private static final String USER_FREEZE_PREFX = "USER_FREEZE_";

    /**
     * 用户登录
     *
     * @param phone 手机号
     * @param code  验证码
     * @return
     */
    public String login(String phone, String code) {
        String redisKey = "CHECK_CODE_" + phone;
        boolean isNew = false;

        //校验验证码
        String redisData = this.redisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.equals(code, redisData)) {
            return null; //验证码错误
        }

        //验证码在校验完成后，需要废弃
        this.redisTemplate.delete(redisKey);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", phone);

        User user = this.userMapper.selectOne(queryWrapper);

        if (null == user) {
            //需要注册该用户
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));

            //注册新用户
            this.userMapper.insert(user);
            isNew = true;


            //将该用户信息注册到环信平台
            Boolean register = this.huanXinApi.register(user.getId());
            if (!register) {
                log.error("注册到环信平台失败！ " + user.getId());
            }
        }
        //用户登录冻结状态
        int state = 1;
        //判断用户是否冻结登录状态

        boolean freezen = UserFreezenState.isFreezen(user, state,userFreezeMapperMXY);
        //如果是则登录失败
        if(freezen){
            return "用户当前处于冻结登录状态";
        }

        //生成token
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("id", user.getId());

        // 生成token
        String token = Jwts.builder()
                .setClaims(claims) //payload，存放数据的位置，不能放置敏感数据，如：密码等
                .signWith(SignatureAlgorithm.HS256, secret) //设置加密方法和加密盐
                .setExpiration(new DateTime().plusHours(12).toDate()) //设置过期时间，12小时后过期
                .compact();

        try {
            //发送用户登录成功的消息
            Map<String, Object> msg = new HashMap<>();
            msg.put("id", user.getId());
            msg.put("date", System.currentTimeMillis());

            ArrayList<String> strings = new ArrayList<>();
            strings.add("北京市");
            strings.add("上海市");
            strings.add("深圳市");
            strings.add("广州市");
            strings.add("浙江省杭州市");
            strings.add("浙江省宁波市");
            strings.add("河南省商丘市");
            strings.add("江苏省苏州市");
            strings.add("河北省邯郸市");
            strings.add("山东省东营市");
            strings.add("台湾省台北市");
            Random random = new Random();
            UserLogInfo info = new UserLogInfo();
            info.setUserId(user.getId());
            info.setLoginIp("192.168.0.01");
            info.setLoginAddress(strings.get(random.nextInt(11)));
            info.setLoginTime(System.currentTimeMillis());
            info.setLoginDevice("手机");
            this.userLogInfoMapper_zxk.insert(info);

          //  this.rocketMQTemplate.convertAndSend("tanhua-sso-login", msg);
        } catch (MessagingException e) {
            log.error("发送消息失败！", e);
        }

        return token + "|" + isNew;
    }

    public User queryUserByToken(String token) {
        try {
            // 通过token解析数据
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            User user = new User();
            user.setId(Long.valueOf(body.get("id").toString()));

            //需要返回user对象中的mobile，需要查询数据库获取到mobile数据
            //如果每次都查询数据库，必然会导致性能问题，需要对用户的手机号进行缓存操作
            //数据缓存时，需要设置过期时间，过期时间要与token的时间一致
            //如果用户修改了手机号，需要同步修改redis中的数据

            String redisKey = "TANHUA_USER_MOBILE_" + user.getId();
            if (this.redisTemplate.hasKey(redisKey)) {
                String mobile = this.redisTemplate.opsForValue().get(redisKey);
                user.setMobile(mobile);
            } else {
                //查询数据库
                User u = this.userMapper.selectById(user.getId());
                user.setMobile(u.getMobile());

                //将手机号写入到redis中
                //在jwt中的过期时间的单位为：秒
                long timeout = Long.valueOf(body.get("exp").toString()) * 1000 - System.currentTimeMillis();
                this.redisTemplate.opsForValue().set(redisKey, u.getMobile(), timeout, TimeUnit.MILLISECONDS);
            }

            return user;
        } catch (ExpiredJwtException e) {
            log.info("token已经过期！ token = " + token);
        } catch (Exception e) {
            log.error("token不合法！ token = " + token, e);
        }
        return null;
    }

    public Boolean updatePhone(Long userId, String newPhone) {
        //先查询新手机号是否已经注册，如果已经注册，就不能修改
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", newPhone);
        User user = this.userMapper.selectOne(queryWrapper);
        if(ObjectUtil.isNotEmpty(user)){
            //新手机号已经被注册
            return false;
        }

        user = new User();
        user.setId(userId);
        user.setMobile(newPhone);

        return this.userMapper.updateById(user) > 0;
    }
}
