package com.limit.learn.video;

import android.content.Intent;

import com.limit.learn.R;
import com.limit.learn.base.BaseActivity;
import com.limit.learn.base.BasePresenter;

import butterknife.OnClick;

public class VideoActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.video_view_player)
    public void onClickVideoViewPlayer(){
        startActivity(new Intent(this,VideoViewActivity.class));
    }

    @OnClick(R.id.video_exo_player)
    public void onClickExoPlayer(){
        startActivity(new Intent(this,ExoPlayerActivity.class));
    }

    @OnClick(R.id.video_media_player)
    public void onClickMediaPlayer(){
        startActivity(new Intent(this,MediaPlayerActivity.class));
    }
}
