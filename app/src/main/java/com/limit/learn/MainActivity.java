package com.limit.learn;

import android.content.Intent;

import com.limit.learn.base.BaseActivity;
import com.limit.learn.base.BasePresenter;
import com.limit.learn.video.ExoPlayerActivity;
import com.limit.learn.video.VideoActivity;
import com.limit.learn.video.VideoViewActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.main_video)
    public void onClickVideoView(){
        startActivity(new Intent(this, VideoActivity.class));
    }
}
