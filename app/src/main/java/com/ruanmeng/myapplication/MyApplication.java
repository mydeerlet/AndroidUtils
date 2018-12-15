package com.ruanmeng.myapplication;

import android.app.Application;

import com.ruanmeng.common.Constant;

/**
 * Created by Andriod0729 on 2018/12/15.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 注册Context,在Application中
        Constant.getInstance().init(this);
    }
}
