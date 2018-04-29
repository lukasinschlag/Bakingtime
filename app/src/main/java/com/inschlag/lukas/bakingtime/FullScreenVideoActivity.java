package com.inschlag.lukas.bakingtime;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.utils.ExoPlayerUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenVideoActivity extends AppCompatActivity {

    @BindView(R.id.videoPlayer)
    PlayerView playerView;
    private long mCurrentVidPos = 0;
    private boolean mPlayVidWhenReady = false;
    private String mVidUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mVidUrl = savedInstanceState.getString(Constants.ARG_VIDEO_URL);
            mCurrentVidPos = savedInstanceState.getLong(Constants.ARG_VIDEO_POS);
            mPlayVidWhenReady = savedInstanceState.getBoolean(Constants.ARG_VIDEO_PLAY);
        } else if (getIntent().hasExtra(Constants.ARG_VIDEO_URL)) {
            mVidUrl = getIntent().getStringExtra(Constants.ARG_VIDEO_URL);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || ExoPlayerUtil.getFullScreenInstance().getPlayer() == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(Constants.ARG_VIDEO_URL, mVidUrl);
        outState.putLong(Constants.ARG_VIDEO_POS, mCurrentVidPos);
        outState.putBoolean(Constants.ARG_VIDEO_PLAY, mPlayVidWhenReady);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();

        SimpleExoPlayer player = ExoPlayerUtil.getFullScreenInstance().getPlayer();
        if(player != null) {
            mCurrentVidPos = player.getCurrentPosition();
            mPlayVidWhenReady = player.getPlayWhenReady();
        }

        if (Util.SDK_INT <= 23) {
            ExoPlayerUtil.getFullScreenInstance().releaseVideoPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            ExoPlayerUtil.getFullScreenInstance().releaseVideoPlayer();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializePlayer() {
        if (!TextUtils.isEmpty(mVidUrl)) {
            ExoPlayerUtil.getFullScreenInstance()
                    .preparePlayer(this, Uri.parse(mVidUrl), playerView);
            ExoPlayerUtil.getFullScreenInstance().goToForeground();
            SimpleExoPlayer player = ExoPlayerUtil.getFullScreenInstance().getPlayer();
            player.seekTo(mCurrentVidPos);
            player.setPlayWhenReady(mPlayVidWhenReady);
        }
    }
}
