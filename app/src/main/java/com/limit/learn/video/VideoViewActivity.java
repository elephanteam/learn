package com.limit.learn.video;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.limit.learn.R;
import com.limit.learn.base.BaseActivity;
import com.limit.learn.base.BasePresenter;
import com.limit.learn.loading.ShapeLoadingDialog;
import com.limit.learn.util.DensityUtil;
import com.limit.learn.util.VideoTimeUtil;
import com.limit.learn.video.constant.VideoUrlConstant;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 原生VideoView播放器
 * */
public class VideoViewActivity extends BaseActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {

    //VideoView播放器
    @BindView(R.id.video_view)
    VideoView videoViewPlayer;
    //SeekBar进度控制器
    @BindView(R.id.video_view_seek_bar)
    AppCompatSeekBar videoViewSeekBar;
    //视频总时长
    @BindView(R.id.video_view_duration)
    TextView videoViewDuration;
    //视频当前播放时间
    @BindView(R.id.video_view_current_time)
    TextView videoViewCurrentTime;

    //进度广播
    private static final int VIDEO_PROGRESS = 10000;

    private ShapeLoadingDialog shapeLoadingDialog;

    //是否正在拖动seek bar
    private boolean isStartTracking = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_view;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        shapeLoadingDialog = new ShapeLoadingDialog.Builder(this)
                .loadText(getString(R.string.loading)).cancelable(false)
                .build();
        videoViewPlayer.setOnCompletionListener(this);//播放完成监听
        videoViewPlayer.setOnErrorListener(this);//播放失败监听
        videoViewPlayer.setOnInfoListener(this);//播放信息监听
        videoViewPlayer.setOnPreparedListener(this);//资源准备完成监听
        Uri mUri = Uri.parse(VideoUrlConstant.VIDEO_M3U8);
        videoViewPlayer.setVideoURI(mUri);
        videoViewPlayer.start();
        videoViewPlayer.requestFocus();
        videoViewSeekBar.setOnSeekBarChangeListener(this);//进度条监听
    }

    //播放视频
    @OnClick(R.id.video_view_play)
    public void onClickPlayVideo(){
        if (videoViewPlayer != null && !videoViewPlayer.isPlaying()){
            videoViewPlayer.start();
        }
    }

    //暂停视频
    @OnClick(R.id.video_view_pause)
    public void onClickPauseVideo(){
        if (videoViewPlayer != null && videoViewPlayer.isPlaying()){
            videoViewPlayer.pause();
        }
    }

    //横竖屏切换
    @OnClick(R.id.video_view_orientation)
    public void onClickOrientation(){
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("***********","onCompletion");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("***********","onError" + "----what = " + what + "----extra = " + extra);
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.e("***********","onInfo" + "----what = " + what + "----extra = " + extra);
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                //显示 Loading 图
                shapeLoadingDialog.show();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                //隐藏 Loading 图
                shapeLoadingDialog.cancel();
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("***********","onPrepared");
        videoViewSeekBar.setMax(mp.getDuration());
        //发消息
        handler.sendEmptyMessage(VIDEO_PROGRESS);
        if (videoViewDuration != null){
            videoViewDuration.setText(VideoTimeUtil.stringForTime(mp.getDuration()));
        }
        if (videoViewCurrentTime != null){
            videoViewCurrentTime.setText(VideoTimeUtil.stringForTime(mp.getCurrentPosition()));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //更新显示当前拖动的时间
        if (videoViewCurrentTime != null){
            videoViewCurrentTime.setText(VideoTimeUtil.stringForTime(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //(开始拖动时)停止倒计时
        isStartTracking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //(停止拖动时)将 VideoView seekTo 当前位置.
        int position = seekBar.getProgress();
        int duration = videoViewPlayer.getDuration();
        if (videoViewPlayer != null) {
            if (duration > 0) {
                videoViewPlayer.seekTo(position);
            }
        }
        if (videoViewDuration != null){
            videoViewDuration.setText(VideoTimeUtil.stringForTime(duration));
        }
        if (videoViewCurrentTime != null){
            videoViewCurrentTime.setText(VideoTimeUtil.stringForTime(position));
        }
        isStartTracking = false;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == VIDEO_PROGRESS) {//1.得到当前的视频播放进程
                if (!isStartTracking){
                    if (videoViewPlayer != null){
                        int position = videoViewPlayer.getCurrentPosition();
                        //SeekBar.setProgress(当前进度);
                        if (videoViewSeekBar != null) {
                            videoViewSeekBar.setProgress( position);
                        }
                    }
                }
                //每秒更新一次
                if (handler != null){
                    handler.removeMessages(VIDEO_PROGRESS);
                    handler.sendEmptyMessageDelayed(VIDEO_PROGRESS, 1000);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        videoViewPlayer.suspend();
        videoViewPlayer.setOnErrorListener(null);
        videoViewPlayer.setOnPreparedListener(null);
        videoViewPlayer.setOnCompletionListener(null);
        videoViewPlayer.setOnInfoListener(null);
        videoViewPlayer = null;
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoViewPlayer == null) {
            return;
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){//横屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().invalidate();
            float height = DensityUtil.getWidthInPx(this);
            float width = DensityUtil.getHeightInPx(this);
            videoViewPlayer.getLayoutParams().height = (int) width;
            videoViewPlayer.getLayoutParams().width = (int) height;
        } else {
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            float width = DensityUtil.getWidthInPx(this);
            float height = DensityUtil.dip2px(this, 200.f);
            videoViewPlayer.getLayoutParams().height = (int) height;
            videoViewPlayer.getLayoutParams().width = (int) width;
        }
    }
}
