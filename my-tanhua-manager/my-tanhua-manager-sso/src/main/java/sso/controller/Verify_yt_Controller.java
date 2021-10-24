package sso.controller;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.StrUtil;
import com.tanhua.common.pojo.VerifyCode;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sso.service.Verification_yt_Service;
import sso.vo.ErrorResult;
import sso.vo.VerifyVo;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("system/users")
@Slf4j
public class Verify_yt_Controller {
    @Autowired
    Verification_yt_Service verification_yt_Service;
    @Autowired
    RedisTemplate redisTemplate;

    private static final String redisKey = "token_InFO";


    /**
     * 生成图片验证码。
     * 使用hutool工具包。
     * 对验证码进行输入验证
     *
     * @return
     */
    @GetMapping("verification")
    public ResponseEntity<Object> getCode(@RequestParam String uuid, HttpServletResponse response) {
        //  String uuid = params.get("uuid");
        // 随机生成 4 位验证码
        RandomGenerator randomGenerator = new RandomGenerator("0123456789", 4);
        // 定义图片的显示大小
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(100, 30);

        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        try {
            //将值保存到redis数据库
            if (!verification_yt_Service.pictureCode(uuid, lineCaptcha)) {
                throw new Exception();
            }

            // 调用父类的 setGenerator() 方法，设置验证码的类型
            lineCaptcha.setGenerator(randomGenerator);
            // 输出到页面
            lineCaptcha.write(response.getOutputStream());
            // 打印日志
            log.info("生成的验证码:{}", lineCaptcha.getCode());
            // 关闭流
            response.getOutputStream().close();

            //记录异常状态信息：
            return ResponseEntity.ok("验证码发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
            //将异常信息响应给前端。
            ErrorResult errorResult = ErrorResult.builder().errCode("000003").errMessage("验证码生成失败！").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }

    }


    @PostMapping("login")
    public Map<String, String> login(@RequestBody Map<String, String> params) {

     /*   try {
            String vCode = params.get("uuid");

            String o = (String) redisTemplate.opsForValue().get(vCode);

            if (o.equals(params.get("verificationCode"))) {*/
                String token = verification_yt_Service.login(params.get("username"), params.get("password"));
                if (StringUtils.isNotEmpty(token)) {
                    Map<String, String> map = new HashMap<>();
                    map.put("token", token);
                    return map;
                }
       /*     }
        } catch (Exception e) {
            log.error("验证码验证失败" + e);
        }*/
        return null;
    }

    /**
     * 接收token
     * @param token
     * @return
     */
    @PostMapping("profile")
    public VerifyVo profile(@RequestHeader("Authorization") String token) {
        //token = Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFkbWluIiwiZXhwIjoxNjM1MDM2NzQ5fQ.nVtEgW2bmGAoJT6U6EaPQYQhW3--JS6jqJMgaOWl3RA

        System.out.println("token = " + token);
        String[] s = StrUtil.split(token," ");

        return verification_yt_Service.profile(s[1]);
    }


    @PostMapping("logout")
    public Boolean logout(@RequestHeader("Authorization") String token) {
        System.out.println(token);
        System.out.println("token = " + token);
        String[] s = StrUtil.split(token," ");
        VerifyCode verifyCode = verification_yt_Service.queryUserByToken(s[1]);
       return redisTemplate.delete(redisKey+verifyCode.getUsername());
    }
}
