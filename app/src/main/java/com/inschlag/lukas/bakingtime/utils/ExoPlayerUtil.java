package com.inschlag.lukas.bakingtime.utils;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Reference:
 * https://medium.com/tall-programmer/fullscreen-functionality-with-android-exoplayer-5fddad45509f
 * <p>
 * This class handles the use of the ExoPlayer
 */
public class ExoPlayerUtil {

    private static ExoPlayerUtil instance;
    private static ExoPlayerUtil fullScreenInstance;

    public static ExoPlayerUtil getInstance() {
        if (instance == null) {
            instance = new ExoPlayerUtil();
        }
        return instance;
    }

    public static ExoPlayerUtil getFullScreenInstance() {
        if (fullScreenInstance == null) {
            fullScreenInstance = new ExoPlayerUtil();
        }
        return fullScreenInstance;
    }

    private ExoPlayerUtil() {
    }

    private SimpleExoPlayer player;
    private Uri videoUri;
    private boolean isPlaying = false;

    public void preparePlayer(Context context, Uri uri, PlayerView playerView) {
        if (context != null && uri != null && playerView != null) {
            if (!uri.equals(videoUri) || player == null) {
                videoUri = uri;
                // See: https://google.github.io/ExoPlayer/guide.html
                // 1. Create a default TrackSelector
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                // 2. Create the player
                player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                        Util.getUserAgent(context, "com.inschlag.lukas.bakingtime"), bandwidthMeter);
                // This is the MediaSource representing the media to be played.
                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(videoUri);
                // Prepare the player with the source.
                player.prepare(videoSource);
            }
            player.clearVideoSurface();
            player.setVideoSurfaceView((SurfaceView) playerView.getVideoSurfaceView());
            player.seekTo(player.getCurrentPosition() + 1);
            playerView.setPlayer(player);
        }
    }

    public SimpleExoPlayer getPlayer(){
        return player;
    }

    public void releaseVideoPlayer() {
        if (player != null) {
            player.release();
        }
        player = null;
    }

    public void goToBackground() {
        if (player != null) {
            isPlaying = player.getPlayWhenReady();
            player.setPlayWhenReady(false);
        }
    }

    public void goToForeground() {
        if (player != null) {
            player.setPlayWhenReady(isPlaying);
        }
    }
}

