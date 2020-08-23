package com.mydeerlet.deerletijkplayer.widget.media;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mydeerlet.deerletijkplayer.R;
import com.mydeerlet.deerletijkplayer.utlis.JZUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 *  这个是自定义的播放器控件类
 */
public class MyVideoPlayer extends IjkVideoView implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {


    private String TAG = "My__IjkVideoView";
    private ImageView poster,startButton;
    private LinearLayout bottomContainer;
    TextView replayTextView;
    public ProgressBar bottomProgressBar, loadingProgressBar;
    public SeekBar progressBar;


    protected boolean mTouchingProgressBar;

    protected Timer UPDATE_PROGRESS_TIMER;
    protected ProgressTimerTask mProgressTimerTask;
    public TextView currentTimeTextView, totalTimeTextView;
    public int seekToManulPosition = -1;

    public ImageView fullscreenButton;

    public MyVideoPlayer(Context context) {
        super(context);
    }

    public MyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public static int FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
    public static int NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static LinkedList<ViewGroup> CONTAINER_LIST = new LinkedList<>();
    public static MyVideoPlayer CURRENT_JZVD;
    public static final int SCREEN_FULLSCREEN = 1;
    public static final int SCREEN_NORMAL = 0;
    public int screen = 0;


    @Override
    protected void initVideoView(Context context) {
        super.initVideoView(context);
        View view = LayoutInflater.from(context).inflate(R.layout.jz_layout_std, null);
        poster = view.findViewById(R.id.poster);
        startButton = view.findViewById(R.id.start);
        bottomContainer = view.findViewById(R.id.layout_bottom);
        replayTextView = view.findViewById(R.id.replay_text);
        loadingProgressBar = view.findViewById(R.id.loading);
        bottomProgressBar = view.findViewById(R.id.bottom_progress);
        progressBar = view.findViewById(R.id.bottom_seek_progress);
        currentTimeTextView = view.findViewById(R.id.current);
        totalTimeTextView = view.findViewById(R.id.total);
        fullscreenButton = view.findViewById(R.id.fullscreen);
        init();
        addView(view);
        CURRENT_JZVD = this;
    }

    private void init() {
        poster.setVisibility(GONE);
        startButton.setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(this);
        fullscreenButton.setOnClickListener(this);



        setMediaController(new IMediaController() {
            @Override
            public void hide() {
                startButton.setVisibility(GONE);
            }

            @Override
            public boolean isShowing() {
                return startButton.isShown();
            }

            @Override
            public void setAnchorView(View view) {

            }

            @Override
            public void setEnabled(boolean enabled) {

            }

            @Override
            public void setMediaPlayer(MediaController.MediaPlayerControl player) {

            }

            @Override
            public void show(int timeout) {
                startButton.setVisibility(VISIBLE);
            }

            @Override
            public void show() {
                startButton.setVisibility(VISIBLE);
            }

            @Override
            public void showOnce(View view) {

            }
        });

        //视频播放状态
        setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer,int arg1, int arg2) {
                switch (arg1) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        //媒体信息视频跟踪标记
                        Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        //媒体信息视频渲染开始
                        Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //媒体信息缓冲开始
                        Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                        setAllControlsVisiblity(0,0,GONE,VISIBLE,0,0,0);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //媒体信息缓冲结束
                        setAllControlsVisiblity(0,0,GONE,GONE,0,0,0);
                        Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        //媒体信息网络带宽
                        Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        //MEDIA_INFO_BAD_INTERLEAVING
                        Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        //媒体信息不正确
                        Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        //媒体信息元数据更新
                        Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                        //MEDIA_INFO_UNSUPPORTED_SUBTITLE
                        Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                        //媒体信息字幕超时
                        Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                        //媒体信息视频旋转已更改
                        Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                        break;
                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        //媒体信息音频渲染开始
                        Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                        break;
                }
                return true;
            }
        });

        //关于完成侦听器
        setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                updateStartImage();
            }
        });



    }




    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.start) {
            if (mCurrentState == STATE_IDLE || mCurrentState == STATE_ERROR) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mCurrentState == STATE_PREPARING) {
                start();
            } else if (mCurrentState == STATE_PLAYING) {
                pause();
            } else if (mCurrentState == STATE_PAUSED) {
                start();
            } else if (mCurrentState == STATE_PLAYBACK_COMPLETED) {
                //start();
            }
        }else if (i == R.id.fullscreen) {
            Log.i(TAG, "onClick fullscreen [" + this.hashCode() + "] ");
            if (mCurrentState == STATE_PLAYBACK_COMPLETED) return;
            if (screen == SCREEN_NORMAL) {
                gotoScreenFullscreen();
            } else {
                Log.d(TAG, "toFullscreenActivity [" + this.hashCode() + "] ");
                backPress();
            }
        }
    }



    public  boolean backPress() {
        Log.i(TAG, "backPress");
        if (CONTAINER_LIST.size() != 0 && CURRENT_JZVD != null) {//判断条件，因为当前所有goBack都是回到普通窗口
            CURRENT_JZVD.gotoScreenNormal();
            return true;
            // && CURRENT_JZVD.screen != SCREEN_NORMAL
        } else if (CONTAINER_LIST.size() == 0 && CURRENT_JZVD != null) {//退出直接进入的全屏
            CURRENT_JZVD.clearFloatScreen();
            return true;
        }
        return false;
    }

    public void clearFloatScreen() {
        JZUtils.getWindow(getContext()).clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        JZUtils.setRequestedOrientation(getContext(), NORMAL_ORIENTATION);
        JZUtils.showSystemUI(getContext());

        ViewGroup vg = (ViewGroup) (JZUtils.scanForActivity(getContext())).getWindow().getDecorView();
        vg.removeView(this);
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        CURRENT_JZVD = null;
    }


    public void gotoScreenFullscreen() {
        ViewGroup vg = (ViewGroup) getParent();
        vg.removeView(this);
        CONTAINER_LIST.add(vg);
        vg = (ViewGroup) (JZUtils.scanForActivity(getContext())).getWindow().getDecorView();
        vg.addView(this, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        screen = SCREEN_FULLSCREEN;



        JZUtils.getWindow(getContext()).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        JZUtils.setRequestedOrientation(getContext(), FULLSCREEN_ORIENTATION);
        JZUtils.hideSystemUI(getContext());//华为手机和有虚拟键的手机全屏时可隐藏虚拟键 issue:1326
    }

    public void gotoScreenNormal() {//goback本质上是goto
        ViewGroup vg = (ViewGroup) (JZUtils.scanForActivity(getContext())).getWindow().getDecorView();
        vg.removeView(this);
        CONTAINER_LIST.getLast().removeAllViews();

        ViewGroup.LayoutParams ll  =  new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.height= mVideoHeight*2;
        CONTAINER_LIST.getLast().addView(this,ll);
        CONTAINER_LIST.pop();

        JZUtils.getWindow(getContext()).clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        JZUtils.setRequestedOrientation(getContext(), NORMAL_ORIENTATION);
        JZUtils.showSystemUI(getContext());
        screen = SCREEN_NORMAL;
    }


    public void updateStartImage() {
        if (mTargetState == STATE_PLAYING) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.drawable.jz_click_pause_selector);
            replayTextView.setVisibility(GONE);
        } else if (mTargetState == STATE_ERROR) {
            startButton.setVisibility(INVISIBLE);
            replayTextView.setVisibility(GONE);
        } else if (mTargetState == STATE_PLAYBACK_COMPLETED) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.drawable.jz_click_replay_selector);
            replayTextView.setVisibility(VISIBLE);
        } else {
            startButton.setImageResource(R.drawable.jz_click_play_selector);
            replayTextView.setVisibility(GONE);
        }

        if (startButton.isShown()){
            bottomContainer.setVisibility(VISIBLE);
        }else {
            bottomContainer.setVisibility(GONE);
        }
    }

    public void changeStartButtonSize(int size) {
        ViewGroup.LayoutParams lp = startButton.getLayoutParams();
        lp.height = size;
        lp.width = size;
        lp = loadingProgressBar.getLayoutParams();
        lp.height = size;
        lp.width = size;
    }

    public void setAllControlsVisiblity(int topCon, int bottomCon, int startBtn, int loadingPro,
                                        int posterImg, int bottomPro, int retryLayout) {
//        topContainer.setVisibility(topCon);
//        bottomContainer.setVisibility(bottomCon);
        startButton.setVisibility(startBtn);
        loadingProgressBar.setVisibility(loadingPro);
//        posterImageView.setVisibility(posterImg);
//        bottomProgressBar.setVisibility(bottomPro);
//        mRetryLayout.setVisibility(retryLayout);
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





    //进度
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            //设置这个progres对应的时间，给textview
            long duration = getDuration();
            currentTimeTextView.setText(JZUtils.stringForTime(progress * duration / 100));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + this.hashCode() + "] ");
        cancelProgressTimer();
        ViewParent vpdown = getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStopTrackingTouch [" + this.hashCode() + "] ");
        startProgressTimer();
        ViewParent vpup = getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (mCurrentState != STATE_PLAYING && mCurrentState != STATE_PAUSED) return;
        long time = seekBar.getProgress() * getDuration() / 100;
        seekToManulPosition = seekBar.getProgress();
        mMediaPlayer.seekTo(time);
        Log.i(TAG, "seekTo " + time + " [" + this.hashCode() + "] ");
    }







    public class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mCurrentState == STATE_PLAYING) {
//                Log.v(TAG, "onProgressUpdate " + "[" + this.hashCode() + "] ");
                post(() -> {
                    long position = getCurrentPositionWhenPlaying();
                    long duration = getDuration();
                    int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                    onProgress(progress, position, duration);
                });
            }
        }
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        if (mCurrentState == STATE_PLAYING) {
            try {
                position = mMediaPlayer.getCurrentPosition();
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
        if (position != 0)
            currentTimeTextView.setText(JZUtils.stringForTime(position));
        totalTimeTextView.setText(JZUtils.stringForTime(duration));
    }


    @Override
    public void start() {
        super.start();
        updateStartImage();
        startProgressTimer();
    }

    @Override
    public void pause() {
        super.pause();
        updateStartImage();
        cancelProgressTimer();
    }
}
