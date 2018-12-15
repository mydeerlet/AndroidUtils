package com.ruanmeng.myapplication;

import android.os.Bundle;
import android.view.View;

import com.ruanmeng.common.api.RetrofitManager;
import com.ruanmeng.common.api.RxException;
import com.ruanmeng.common.base.BaseActivity;
import com.ruanmeng.common.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().unregister(this);


        initData();
    }

    private void initData() {

        RetrofitManager.getInstance(this)
                .create(Service.class)
                .getMessage("北京")
                .compose(this.<UpdateModel>scheduleSingle())
                .subscribe(new Consumer<UpdateModel>() {
                    @Override
                    public void accept(UpdateModel updateModel) throws Exception {
                        LogUtils.i("aaa",updateModel.getData().getGanmao());
                    }
                },new RxException<Throwable>());


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View needOffSet() {
        return null;
    }


    interface Service {

        @GET("weather_mini")
            //  此处回调返回的可为任意类型Call<T>，再也不用自己去解析json数据啦！！！
        Single<UpdateModel> getMessage(@Query("city") String city);
    }
}
