package com.mydeerlet.deerletijkplayer.wkvd;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mydeerlet.deerletijkplayer.R;
import com.mydeerlet.deerletijkplayer.widget.media.IjkVideoView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public abstract class Wkvd extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    public static final  String TAG = "IjkPlayer_Log";
    public static Wkvd CURRENT_JZVD;


    IjkVideoView mVideoView;
    ViewGroup topContainer;
    ViewGroup bottomContainer;
    ImageView startButton,startImgView;
    ImageView fullscreenButton;
    SeekBar progressBar;
    TextView currentTimeTextView;
    TextView totalTimeTextView;
    ViewGroup textureViewContainer;
    RelativeLayout app_video_loading;
    public static LinkedList<ViewGroup> CONTAINER_LIST = new LinkedList<>();
    public TextView replayTextView;

    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    public static boolean TOOL_BAR_EXIST = true;

    public JZDataSource jzDataSource;
    public int screen = -1;

    protected Timer UPDATE_PROGRESS_TIMER; //更新进度定时器
    protected ProgressTimerTask mProgressTimerTask; //进展情况
    public int seekToManulPosition = -1;
    protected boolean mTouchingProgressBar;

    public static final int SCREEN_NORMAL = 0; //正常
    public static final int SCREEN_FULLSCREEN = 1; //全屏
    public static final int SCREEN_TINY = 2; //小窗口
    public static int NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static int FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
    protected long gobakFullscreenTime = 0;//这个应该重写一下，刷新列表，新增列表的刷新，不打断播放，应该是个flag




    /**
     * 是否是直播 默认为非直播，true为直播false为点播，根据isLive()方法前缀rtmp或者后缀.m3u8判断得出的为直播，比较片面，有好的建议欢迎交流
     */
    private boolean isLive = true;

    public Wkvd(@NonNull Context context) {
        super(context);
        init(context);
    }

    public Wkvd(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        try {
            // init player
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            Log.d(TAG, "loadLibraries error$e");
        }
        mVideoView = findViewById(R.id.video_view);
        TableLayout hud_view = findViewById(R.id.hud_view);
        mVideoView.setHudView(hud_view);


        topContainer = findViewById(R.id.layout_top);
        startButton = findViewById(R.id.start);
        startImgView = findViewById(R.id.app_video_play);

        fullscreenButton = findViewById(R.id.fullscreen);
        progressBar = findViewById(R.id.bottom_seek_progress);
        currentTimeTextView = findViewById(R.id.current);
        totalTimeTextView = findViewById(R.id.total);
        textureViewContainer = findViewById(R.id.surface_container);
        bottomContainer = findViewById(R.id.layout_bottom);
        replayTextView = findViewById(R.id.replay_text);
        app_video_loading = findViewById(R.id.app_video_loading);


        startButton.setOnClickListener(this);
        startImgView.setOnClickListener(this);
        fullscreenButton.setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(this);
        bottomContainer.setOnClickListener(this);
        textureViewContainer.setOnClickListener(this);
        textureViewContainer.setOnTouchListener(this);

        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
    }


    public abstract int getLayoutId();

    public void setUp(String url, String title, int screen) {
        setUp(new JZDataSource(url, title), screen);
    }

    public void setUp(JZDataSource jzDataSource, int screen) {
        this.jzDataSource = jzDataSource;
        this.screen = screen;
        mVideoView.setVideoPath(jzDataSource.getValueFromLinkedMap(0).toString());
        cancelProgressTimer();

    }


    public void startVideo() {
        mVideoView.start();
        CURRENT_JZVD = this;
        updatePausePlay(true);
        startProgressTimer();
    }

    public void stopPlayback() {
        mVideoView.stopPlayback();
    }

    /**
     * 更新播放、暂停和停止按钮
     */
    private void updatePausePlay(Boolean isplay) {
        if (isplay) {
            if (isLive) {
                startImgView.setImageResource(R.drawable.simple_player_stop_white_24dp);
            } else {
                startImgView.setImageResource(R.drawable.simple_player_icon_media_pause);
                startButton.setImageResource(R.drawable.simple_player_center_pause);
            }
        } else {
            startImgView.setImageResource(R.drawable.simple_player_arrow_white_24dp);
            startButton.setImageResource(R.drawable.simple_player_center_play);
        }
    }


    public void startProgressTimer() {
        Log.i(TAG, "startProgressTimer: " + " [" + this.hashCode() + "] ");
        cancelProgressTimer();
        UPDATE_PROGRESS_TIMER = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 300);
    }

    public void cancelProgressTimer() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }
    }


    public class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mVideoView.mTargetState == IjkVideoView.STATE_PLAYING || mVideoView.mTargetState == IjkVideoView.STATE_PLAYING) {
//                Log.v(TAG, "onProgressUpdate " + "[" + this.hashCode() + "] ");
                post(() -> {
                    long position = getCurrentPositionWhenPlaying();
                    long duration = mVideoView.getDuration();
                    int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                    onProgress(progress, position, duration);
                });
            }
        }
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        if (mVideoView.mTargetState == IjkVideoView.STATE_PLAYING || mVideoView.mTargetState == IjkVideoView.STATE_PLAYING) {
            try {
                position = mVideoView.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }


    public void onProgress(int progress, long position, long duration) {
//        Log.d(TAG, "onProgress: progress=" + progress + " position=" + position + " duration=" + duration);
        if (!mTouchingProgressBar) {
            if (seekToManulPosition != -1) {
                if (seekToManulPosition > progress) {
                    return;
                } else {
                    seekToManulPosition = -1;//这个关键帧有没有必要做
                }
            } else {
                if (progress != 0) progressBar.setProgress(progress);
            }
        }
        if (position != 0) currentTimeTextView.setText(JZUtils.stringForTime(position));
        totalTimeTextView.setText(JZUtils.stringForTime(duration));
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.start) {
            if (mVideoView.mCurrentState == IjkVideoView.STATE_PREPARING || mVideoView.mCurrentState == IjkVideoView.STATE_PREPARED
                    || mVideoView.mCurrentState == IjkVideoView.STATE_PAUSED) {
                mVideoView.start();
                updatePausePlay(true);
            }else {
                mVideoView.pause();
                updatePausePlay(false);
            }
        }
        if (i == R.id.app_video_play) {
            if (mVideoView.mCurrentState == IjkVideoView.STATE_PREPARING || mVideoView.mCurrentState == IjkVideoView.STATE_PREPARED
                    || mVideoView.mCurrentState == IjkVideoView.STATE_PAUSED) {
                startVideo();
                updatePausePlay(true);
            }else {
                mVideoView.pause();
                updatePausePlay(false);
            }
        }

        if (i == R.id.fullscreen) {
            if ( mVideoView.mCurrentState == mVideoView.STATE_PLAYBACK_COMPLETED) return;
            if (screen == SCREEN_FULLSCREEN) {
                //quit fullscreen
                backPress(); //返回正常
            } else {
                gotoScreenFullscreen(); //全屏
            }
        }
    }

    public void gotoScreenFullscreen() {
        ViewGroup vg = (ViewGroup) getParent();
        vg.removeView(this);
        cloneAJzvd(vg);
        CONTAINER_LIST.add(vg);
        vg = (ViewGroup) (JZUtils.scanForActivity(getContext())).getWindow().getDecorView();//和他也没有关系
        vg.addView(this, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setScreenFullscreen();
        JZUtils.hideStatusBar(getContext());
        JZUtils.setRequestedOrientation(getContext(), FULLSCREEN_ORIENTATION);
        JZUtils.hideSystemUI(getContext());//华为手机和有虚拟键的手机全屏时可隐藏虚拟键 issue:1326

    }

    public static boolean backPress() {
        Log.i(TAG, "backPress");
        if (CONTAINER_LIST.size() != 0 && CURRENT_JZVD != null) {//判断条件，因为当前所有goBack都是回到普通窗口
            CURRENT_JZVD.gotoScreenNormal();
            return true;
        } else if (CONTAINER_LIST.size() == 0 && CURRENT_JZVD != null && CURRENT_JZVD.screen != SCREEN_NORMAL) {//退出直接进入的全屏
            CURRENT_JZVD.clearFloatScreen();
            return true;
        }
        return false;
    }

    public void gotoScreenNormal() {//goback本质上是goto
        gobakFullscreenTime = System.currentTimeMillis();//退出全屏
        ViewGroup vg = (ViewGroup) (JZUtils.scanForActivity(getContext())).getWindow().getDecorView();
        vg.removeView(this);
        CONTAINER_LIST.getLast().removeAllViews();
        CONTAINER_LIST.getLast().addView(this, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        CONTAINER_LIST.pop();

        setScreenNormal();//这块可以放到jzvd中
        JZUtils.showStatusBar(getContext());
        JZUtils.setRequestedOrientation(getContext(), NORMAL_ORIENTATION);
        JZUtils.showSystemUI(getContext());
    }

    public void clearFloatScreen() {
        JZUtils.showStatusBar(getContext());
        JZUtils.setRequestedOrientation(getContext(), NORMAL_ORIENTATION);
        JZUtils.showSystemUI(getContext());

        ViewGroup vg = (ViewGroup) (JZUtils.scanForActivity(getContext())).getWindow().getDecorView();
        vg.removeView(this);
        mVideoView.release(true);
        CURRENT_JZVD = null;
    }

    //全屏
    public void setScreenFullscreen() {
        screen = SCREEN_FULLSCREEN;
    }
    //正常
    public void setScreenNormal() {//TODO 这块不对呀，还需要改进，设置flag之后要设置ui，不设置ui这么写没意义呀
        screen = SCREEN_NORMAL;
    }

    public void cloneAJzvd(ViewGroup vg) {
        try {
            Constructor<Wkvd> constructor = (Constructor<Wkvd>) Wkvd.this.getClass().getConstructor(Context.class);
            Wkvd jzvd = constructor.newInstance(getContext());
            jzvd.setId(getId());
            vg.addView(jzvd);
            jzvd.setUp(jzDataSource.cloneMe(), SCREEN_NORMAL);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
