package com.ruanmeng.common.api;

import com.ruanmeng.common.Constant;

public final class Api {

    public static final String  BASE_URL;
    //头像地址前缀
//    public static final String PORTRAIT_URL = "http://os1k2lqfd.bkt.clouddn.com/";
    public static final String PORTRAIT_URL = "https://img.bibaovip.com/";
    static {
        if (Constant.DEBUG_MODE){
            BASE_URL = "http://wthrcdn.etouch.cn/";
        } else {
            BASE_URL = "http://wthrcdn.etouch.cn/";
        }
    }
}
