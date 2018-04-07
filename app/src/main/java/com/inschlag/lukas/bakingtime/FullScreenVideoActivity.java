package com.inschlag.lukas.bakingtime;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.utils.ExoPlayerUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenVideoActivity extends AppCompatActivity {

    @BindView(R.id.videoPlayer)
    PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(Constants.ARG_VIDEO_URL)) {
            ExoPlayerUtil.getInstance()
                    .preparePlayer(this, Uri.parse(getIntent()
                            .getStringExtra(Constants.ARG_VIDEO_URL)), playerView);
            ExoPlayerUtil.getInstance().goToForeground();
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
    public void onPause(){
        super.onPause();
        ExoPlayerUtil.getInstance().goToBackground();
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
}
