package com.tanhua.service;


import cn.hutool.captcha.LineCaptcha;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.common.mapper.FourUserMapper;
import com.tanhua.common.mapper.VerifyLogMapper;
import com.tanhua.common.pojo.VerifyCode;
import com.tanhua.common.pojo.VerifyLog;
import com.tanhua.vo.VerifyVo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class Verification_yt_Service {

    @Autowired
    FourUserMapper fourUserMapper;

    @Autowired
    VerifyLogMapper verifyLogMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Value("${jwt.secret}")
    private String secret;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String redisKey = "token_InFO";


    /**
     * 将传入的key 保存到redis中，
     *
     * @param
     * @return
     */
    public Boolean pictureCode(String uuid, LineCaptcha lineCaptcha) {

        String code = lineCaptcha.getCode();

        try {
            // 将数据存储到redis数据库。
            redisTemplate.opsForValue().set(uuid, code, 5, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            log.error("验证码保存到数据库异常。" + e);
        }
        return false;
    }

    public String login(String username, String password) {

        try {
            QueryWrapper<VerifyCode> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username)
                    .eq("password", password);
            VerifyCode verifyCode = fourUserMapper.selectOne(queryWrapper);
         /*   System.out.println(verifyCode.getUsername());
            System.out.println(secret);*/


            if (verifyCode != null) {
                //生成token用得payload（不放置密码等重要信息）
                Map<String, Object> claims = new HashMap<String, Object>();
                claims.put("id", verifyCode.getUsername());

                // 生成token
                String token = Jwts.builder()
                        .setClaims(claims) //payload，存放数据的位置，不能放置敏感数据，如：密码等
                        .signWith(SignatureAlgorithm.HS256, secret) //设置加密方法和加密盐
                        .setExpiration(new DateTime().plusHours(12).toDate()) //设置过期时间，12小时后过期
                        .compact();

                System.out.println(token);

                //将token存入redis
                redisTemplate.opsForValue().set(redisKey + verifyCode.getUsername(), token, 720, TimeUnit.MINUTES);

                VerifyLog verifyLog = new VerifyLog();
                verifyLog.setUsername(verifyCode.getUsername());
                verifyLog.setLog("登录成功！");
                verifyLog.setTime(System.currentTimeMillis());
                try {
                    verifyLogMapper.insert(verifyLog);
                } catch (Exception e) {
                    log.error("写入日志表失败" + e);
                }
                return token;
            }
        } catch (Exception e) {
            QueryWrapper<VerifyCode> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username)
                    .eq("password", password);
            VerifyCode verifyCode = fourUserMapper.selectOne(queryWrapper);

            VerifyLog verifyLog = new VerifyLog();
            verifyLog.setUsername(verifyCode.getUsername());
            verifyLog.setLog("登录失败！");
            verifyLog.setTime(System.currentTimeMillis());
            verifyLogMapper.insert(verifyLog);

            log.error("获取token失败");


        }


        return null;
    }

    /**
     * 验证tiken
     *
     * @param token
     * @return
     */

    public VerifyVo profile(String token) {
        Map<String, Object> body = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();


        QueryWrapper<VerifyCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", body.get("id"));
        VerifyCode verifyCode = fourUserMapper.selectOne(queryWrapper);


        VerifyVo verifyVo = new VerifyVo();
        verifyVo.setUid(body.get("id").toString());
        verifyVo.setUsername(verifyCode.getUsername());
        verifyVo.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return verifyVo;
    }

    /**
     * 解析验证token
     *
     * @param token
     * @return
     */
    public VerifyCode queryUserByToken(String token) {
        Map<String, Object> body = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

       // verifyCode.setUsername(body.get("id").toString());
        String o = (String) redisTemplate.opsForValue().get(redisKey + body.get("id").toString());
        if (StringUtils.isNotEmpty(o)) {
            QueryWrapper<VerifyCode> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", body.get("id"));
            return fourUserMapper.selectOne(queryWrapper);
        }
        return null;
    }
}
