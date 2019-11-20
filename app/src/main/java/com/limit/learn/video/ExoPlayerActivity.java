package com.limit.learn.video;

import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.limit.learn.R;
import com.limit.learn.base.BaseActivity;
import com.limit.learn.base.BasePresenter;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * google 的ExoPlayer
 * */
public class ExoPlayerActivity extends BaseActivity implements PlayerControlView.VisibilityListener, Player.EventListener {

    @BindView(R.id.player_view)
    PlayerView mPlayerView;

    private SimpleExoPlayer player;

    @Override
    public int getLayoutId() {
        return R.layout.activity_exo_player;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        Uri mUri = Uri.parse(VideoUrlConstant.VIDEO_MP4);
        Uri mUri2 = Uri.parse(VideoUrlConstant.VIDEO_M3U8);
        mPlayerView.setControllerVisibilityListener(this);
        player = ExoPlayerFactory.newSimpleInstance(this);
        // Produces DataSource instances through which media data is loaded.
        // 产生通过其加载媒体数据的DataSource实例。
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));
        DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSourceFactory( Util.getUserAgent(this, getString(R.string.app_name)));
        // 创建媒体播放器  可以多个加载一起播放
        MediaSource firstSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mUri);//播放mp4
        //.setAllowChunklessPreparation(true)  加速启动
        MediaSource secondSource = new HlsMediaSource.Factory(httpDataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(mUri2);//播放m3u8

        //给定一个视频文件和一个单独的字幕文件，MergingMediaSource可以将它们合并到一个源中进行播放。
        // Build the subtitle MediaSource.
        Format subtitleFormat = Format.createTextSampleFormat(
                getString(R.string.app_name), // An identifier for the track. May be null.
                MimeTypes.APPLICATION_SUBRIP, // The mime type. Must be set correctly.
                C.SELECTION_FLAG_DEFAULT, // Selection flags for the track.
                getString(R.string.app_name)); // The subtitle language. May be null.
        MediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(mUri2, subtitleFormat, C.TIME_UNSET);
        // Plays the video with the sideloaded subtitle. 播放带有副标题的视频。
        //给定一个视频文件和一个单独的字幕文件，MergingMediaSource可以将它们合并到一个源中进行播放。
        MergingMediaSource mergedSource = new MergingMediaSource(firstSource, subtitleSource);

        // Plays the first video , then the second video. 播放第一个视频后播放第二个视频
        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(mergedSource, secondSource);
//        //剪辑视频
//        ClippingMediaSource clippingSource = new ClippingMediaSource(
//                        concatenatedSource,
//                        /* startPositionUs= */ 5_000_000,
//                        /* endPositionUs= */ 10_000_000);
        // Prepare the player with the source. 准备播放器和播放源

        //循环播放2次
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.prepare(concatenatedSource);
        player.addListener(this);
        mPlayerView.setPlayer(player);
        mPlayerView.requestFocus();
    }

    //播放视频
    @OnClick(R.id.exo_view_play)
    public void onClickPlayVideo(){
        if (player != null && !player.isPlaying() && mPlayerView != null){
            player.setPlayWhenReady(true);
        }
    }

    //暂停视频
    @OnClick(R.id.exo_view_pause)
    public void onClickPauseVideo(){
        if (player != null && player.isPlaying()){
            player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    //Player.STATE_IDLE：这是初始状态，即播放器停止和播放失败时的状态。
    //Player.STATE_BUFFERING：播放器无法立即从当前位置播放。这主要是因为需要加载更多数据。
    //Player.STATE_READY：播放器可以立即从其当前位置播放。
    //Player.STATE_ENDED：播放器完成了所有媒体的播放。
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playWhenReady && playbackState == Player.STATE_READY) {
            // Active playback.
        } else if (playWhenReady) {
            // Not playing because playback ended, the player is buffering, stopped or
            // failed. Check playbackState and player.getPlaybackError for details.
        } else {
            // Paused by app.
        }
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {

    }

    //通过onPlayerError(ExoPlaybackException error)在已注册的中实施， 可以接收到导致回放失败的错误 Player.EventListener。
    // 发生故障时，将在播放状态转换为之前立即调用此方法Player.STATE_IDLE。
    // ExoPlaybackException有一个type字段，以及对应的获取方法，这些方法返回原因异常，以提供有关故障的更多信息。
    // 下面的示例显示了如何检测由于HTTP网络问题而导致播放失败的时间。
    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
            IOException cause = error.getSourceException();
            if (cause instanceof HttpDataSource.HttpDataSourceException) {
                // An HTTP error occurred.
                HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                // This is the request for which the error occurred.
                DataSpec requestDataSpec = httpError.dataSpec;
                // It's possible to find out more about the error both by casting and by
                // querying the cause.
                if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                    // Cast to InvalidResponseCodeException and retrieve the response code,
                    // message and headers.
                } else {
                    // Try calling httpError.getCause() to retrieve the underlying cause,
                    // although note that it may be null.
                }
            }
        }
    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null){
            player.release();
        }
    }
}
