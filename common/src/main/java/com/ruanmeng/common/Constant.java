
package com.ruanmeng.common;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

public final class Constant {

    private static Constant constant;

    public static boolean DEBUG_MODE = true;
    public static boolean DEBUG_MODE_LOG = true;
    private Context context;
    private Constant(){

    }

    public static Constant getInstance(){
        if (constant == null){
            synchronized (Constant.class){
                if (constant == null){
                    constant = new Constant();
                }
            }
        }
        return constant;
    }

    public void init(Context context){
        this.context = context;
        Fresco.initialize(context);//注册Fresco
    }

    public Context getContext(){
        return context;
    }
}
