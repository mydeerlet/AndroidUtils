package com.mydeerlet.deerletijkplayer.wkvd;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mydeerlet.deerletijkplayer.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class WkvdStd extends Wkvd{

    public TextView titleTextView;
    public ProgressBar bottomProgressBar, loadingProgressBar;

    public WkvdStd(@NonNull Context context) {
        super(context);
    }

    public WkvdStd(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.wk_layout_player;
    }


    @Override
    public void init(Context context) {
        super.init(context);
        titleTextView = findViewById(R.id.title);
        loadingProgressBar = findViewById(R.id.loading);
    }

    public void setUp(JZDataSource jzDataSource, int screen) {
        super.setUp(jzDataSource, screen);
        titleTextView.setText(jzDataSource.title);
        setScreen(screen);
    }


    @Override
    public void setScreenNormal() {
        super.setScreenNormal();
        fullscreenButton.setImageResource(R.drawable.jz_enlarge);
//        backButton.setVisibility(View.GONE);
//        tinyBackImageView.setVisibility(View.INVISIBLE);
        changeStartButtonSize(45);
//        batteryTimeLayout.setVisibility(View.GONE);
//        clarity.setVisibility(View.GONE);
    }
    @Override
    public void setScreenFullscreen() {
        super.setScreenFullscreen();
        //进入全屏之后要保证原来的播放状态和ui状态不变，改变个别的ui
        fullscreenButton.setImageResource(R.drawable.jz_shrink);
//        backButton.setVisibility(View.VISIBLE);
//        tinyBackImageView.setVisibility(View.INVISIBLE);
//        batteryTimeLayout.setVisibility(View.VISIBLE);
//        if (jzDataSource.urlsMap.size() == 1) {
//            clarity.setVisibility(GONE);
//        } else {
//            clarity.setText(jzDataSource.getCurrentKey().toString());
//            clarity.setVisibility(View.VISIBLE);
//        }
        changeStartButtonSize(62);
    }


    public void changeStartButtonSize(int size) {
        ViewGroup.LayoutParams lp = startButton.getLayoutParams();
        lp.height = size;
        lp.width = size;
        lp = loadingProgressBar.getLayoutParams();
        lp.height = size;
        lp.width = size;
    }

    public void setScreen(int screen) {//特殊的个别的进入全屏的按钮在这里设置  只有setup的时候能用上
//        switch (screen) {
//            case SCREEN_NORMAL:
//                setScreenNormal();
//                break;
//            case SCREEN_FULLSCREEN:
//                setScreenFullscreen();
//                break;
//            case SCREEN_TINY:
//                setScreenTiny();
//                break;
//        }
    }
}
