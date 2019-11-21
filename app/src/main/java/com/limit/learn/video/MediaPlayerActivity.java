package com.limit.learn.video;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.limit.learn.R;
import com.limit.learn.base.BaseActivity;
import com.limit.learn.base.BasePresenter;
import com.limit.learn.loading.ShapeLoadingDialog;
import com.limit.learn.util.DensityUtil;
import com.limit.learn.util.VideoTimeUtil;
import com.limit.learn.video.constant.VideoUrlConstant;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * MediaPlayer 能播放的格式很少
 *
 * MediaPlayer是使用Surface进行视频的展示的。
 * MediaPlayer只支持mp4、avi、3gp格式的视频，支持格式相对单一。
 * MediaPlayer可以播放网络视频，支持的网络视频的协议为：Http协议和RTSP协议两种。
 *
 * 在使用start()播放流媒体之前，需要装载流媒体资源。
 * 这里最好使用prepareAsync()用异步的方式装载流媒体资源。
 * 因为流媒体资源的装载是会消耗系统资源的，在一些硬件不理想的设备上，如果使用prepare()同步的方式装载资源，可能会造成UI界面的卡顿，这是非常影响用于体验的。
 * 因为推荐使用异步装载的方式，为了避免还没有装载完成就调用start()而报错的问题，需要绑定MediaPlayer.setOnPreparedListener()事件，它将在异步装载完成之后回调。
 * 异步装载还有一个好处就是避免装载超时引发ANR（(Application Not Responding）错误。
 *
 * 使用完MediaPlayer需要回收资源。MediaPlayer是很消耗系统资源的，所以在使用完MediaPlayer，不要等待系统自动回收，最好是主动回收资源
 * */
public class MediaPlayerActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.media_player_surface)
    SurfaceView mSurfaceView;
    @BindView(R.id.media_player_current_time)
    TextView mediaPlayerCurrentTime;
    @BindView(R.id.media_player_seek_bar)
    AppCompatSeekBar mediaPlayerSeekBar;
    @BindView(R.id.media_player_duration)
    TextView mediaPlayerDuration;

    private MediaPlayer mMediaPlayer;

    //进度广播
    private static final int VIDEO_PROGRESS = 10000;

    private ShapeLoadingDialog shapeLoadingDialog;

    //是否正在拖动seek bar
    private boolean isStartTracking = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_media_player;
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

        SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnErrorListener(this);
        try {
            mMediaPlayer.setDataSource(this, Uri.parse(VideoUrlConstant.VIDEO_MP4));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayerSeekBar.setOnSeekBarChangeListener(this);
    }

    @OnClick(R.id.media_player_play)
    public void onClickPlayerPlay(){
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()){
            mMediaPlayer.start();
        }
    }

    @OnClick(R.id.media_player_pause)
    public void onClickPlayerPause(){
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Surface surface = holder.getSurface();
        mMediaPlayer.setSurface(surface);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("***********", "onPrepared");
        mMediaPlayer.start();
        mediaPlayerSeekBar.setMax(mp.getDuration());
        //发消息
        handler.sendEmptyMessage(VIDEO_PROGRESS);
        if (mediaPlayerDuration != null) {
            mediaPlayerDuration.setText(VideoTimeUtil.stringForTime(mp.getDuration()));
        }
        if (mediaPlayerCurrentTime != null) {
            mediaPlayerCurrentTime.setText(VideoTimeUtil.stringForTime(mp.getCurrentPosition()));
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == VIDEO_PROGRESS) {//1.得到当前的视频播放进程
                if (!isStartTracking){
                    if (mMediaPlayer != null){
                        int position = mMediaPlayer.getCurrentPosition();
                        //SeekBar.setProgress(当前进度);
                        if (mediaPlayerSeekBar != null) {
                            mediaPlayerSeekBar.setProgress( position);
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
    public void onCompletion(MediaPlayer mp) {
        Log.e("***********", "onCompletion");
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.e("***********", "onInfo" + "----what = " + what + "----extra = " + extra);
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
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("***********", "onError" + "----what = " + what + "----extra = " + extra);
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //更新显示当前拖动的时间
        if (mediaPlayerCurrentTime != null){
            mediaPlayerCurrentTime.setText(VideoTimeUtil.stringForTime(progress));
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
        int duration = mMediaPlayer.getDuration();
        if (mMediaPlayer != null) {
            if (duration > 0) {
                mMediaPlayer.seekTo(position);
            }
        }
        if (mediaPlayerDuration != null){
            mediaPlayerDuration.setText(VideoTimeUtil.stringForTime(duration));
        }
        if (mediaPlayerCurrentTime != null){
            mediaPlayerCurrentTime.setText(VideoTimeUtil.stringForTime(position));
        }
        isStartTracking = false;
    }

    //横竖屏切换
    @OnClick(R.id.media_player_orientation)
    public void onClickOrientation(){
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnInfoListener(null);
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mSurfaceView == null) {
            return;
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){//横屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().invalidate();
            float height = DensityUtil.getWidthInPx(this);
            float width = DensityUtil.getHeightInPx(this);
            mSurfaceView.getLayoutParams().height = (int) width;
            mSurfaceView.getLayoutParams().width = (int) height;
        } else {
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            float width = DensityUtil.getWidthInPx(this);
            float height = DensityUtil.dip2px(this, 200.f);
            mSurfaceView.getLayoutParams().height = (int) height;
            mSurfaceView.getLayoutParams().width = (int) width;
        }
    }
}
