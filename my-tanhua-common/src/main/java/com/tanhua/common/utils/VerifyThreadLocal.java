package com.tanhua.common.utils;

import com.tanhua.common.pojo.VerifyCode;

public class VerifyThreadLocal {

    private static final ThreadLocal<VerifyCode> LOCAL = new ThreadLocal<>();

    private VerifyThreadLocal(){

    }

    /**
     * 将对象放入到ThreadLocal
     *
     * @param
     */
    public static void set(VerifyCode verifyCode){
        LOCAL.set(verifyCode);
    }

    /**
     * 返回当前线程中的User对象
     *
     * @return
     */
    public static VerifyCode get(){
        return LOCAL.get();
    }

    /**
     * 删除当前线程中的User对象
     */
    public static void remove(){
        LOCAL.remove();
    }

}
