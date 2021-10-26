package com.tanhua.handler;

import cn.hutool.core.util.StrUtil;
import com.tanhua.common.pojo.VerifyCode;
import com.tanhua.common.utils.NoAuthorization;
import com.tanhua.common.utils.VerifyThreadLocal;
import com.tanhua.service.Verification_yt_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 将对象放入线程中
 */
@Component
public class Verification_yt_TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private Verification_yt_Service verification_yt_Service;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //校验handler是否是HandlerMethod
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //判断是否包含@NoAuthorization注解，如果包含，直接放行
        if (((HandlerMethod) handler).hasMethodAnnotation(NoAuthorization.class)) {
            return true;
        }

        //从请求头中获取token
        String token = request.getHeader("Authorization");
        if (StrUtil.isNotEmpty(token)) {
            VerifyCode verifyCode = this.verification_yt_Service.queryUserByToken(token.substring(6));
            if (verifyCode != null) {
                //token有效
                //将User对象放入到ThreadLocal中
                VerifyThreadLocal.set(verifyCode);
                return true;
            }
        }
        //token无效，响应状态为401
        response.setStatus(401); //无权限

        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //从ThreadLocal中移除User对象
        VerifyThreadLocal.remove();
    }
}
