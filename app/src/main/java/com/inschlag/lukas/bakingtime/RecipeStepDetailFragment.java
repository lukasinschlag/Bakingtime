package com.inschlag.lukas.bakingtime;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.model.Step;

import io.realm.Realm;

/**
 * A fragment representing a single Step detail screen, showing e.g. a video and step instructions
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment {

    private int mRecipeId, mStepId;
    private Step mItem;
    private SimpleExoPlayer player;

    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(Constants.ARG_ITEM_ID)) {
            mRecipeId = getArguments().getInt(Constants.ARG_ITEM_ID);
            mStepId = getArguments().getInt(Constants.ARG_STEP);

            // Load the recipe step
            mItem = loadStep();

            if (mItem == null) {
                //err: couldn't find step
                return;
            }

            Log.d("StepDetail", mItem.toString());

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getShortDescription());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step_detail, container, false);

        if(mItem == null && savedInstanceState != null){
            mRecipeId = savedInstanceState.getInt(Constants.ARG_ITEM_ID);
            mStepId = savedInstanceState.getInt(Constants.ARG_STEP);
        } else {
            getActivity().finish();
            return rootView;
        }

        ((TextView) rootView.findViewById(R.id.stepDesc)).setText(mItem.getDescription());

        Context context = getActivity();
        if (context != null && !TextUtils.isEmpty(mItem.getVideoURL())) {
            // See: https://google.github.io/ExoPlayer/guide.html
            // 1. Create a default TrackSelector
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

            PlayerView mPlayerView = rootView.findViewById(R.id.videoPlayer);
            mPlayerView.requestFocus();
            mPlayerView.setPlayer(player);

            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "com.inschlag.lukas.bakingtime"), bandwidthMeter);
            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mItem.getVideoURL()));
            // Prepare the player with the source.
            player.prepare(videoSource);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(Constants.ARG_ITEM_ID, mRecipeId);
        outState.putInt(Constants.ARG_STEP, mStepId);
        super.onSaveInstanceState(outState);
    }

    private Step loadStep(){
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Step.class)
                .equalTo("recipeId", mRecipeId)
                .equalTo("id", mStepId)
                .findFirst();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}
