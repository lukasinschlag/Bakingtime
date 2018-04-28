package com.inschlag.lukas.bakingtime;

import android.app.Activity;
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
import android.widget.Toast;

import com.google.android.exoplayer2.ui.PlayerView;
import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.model.Step;
import com.inschlag.lukas.bakingtime.utils.ExoPlayerUtil;

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
    public RecipeStepDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(Constants.ARG_ITEM_ID)
                && getArguments().containsKey(Constants.ARG_STEP)) {
            mRecipeId = getArguments().getInt(Constants.ARG_ITEM_ID);
            mStepId = getArguments().getInt(Constants.ARG_STEP);

            // Load the recipe step
            mItem = loadStep();

            if (mItem == null) {
                showErr(); //err: couldn't find step
                return;
            }

            Activity activity = this.getActivity();
            if(activity != null) {
                CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mItem.getShortDescription());
                }
            }
        } else {
            showErr();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step_detail, container, false);

        if(mItem == null && savedInstanceState != null){
            mRecipeId = savedInstanceState.getInt(Constants.ARG_ITEM_ID);
            mStepId = savedInstanceState.getInt(Constants.ARG_STEP);
            mItem = loadStep();
        }

        ((TextView) rootView.findViewById(R.id.stepDesc)).setText(mItem.getDescription());

        Context context = getActivity();
        if (context != null && !TextUtils.isEmpty(mItem.getVideoURL())) {
            ExoPlayerUtil.getInstance()
                    .preparePlayer(context, Uri.parse(mItem.getVideoURL()), (PlayerView)rootView.findViewById(R.id.videoPlayer));
            ExoPlayerUtil.getInstance().goToForeground();
        }

        return rootView;
    }

    @Override
    public void onPause(){
        super.onPause();
        ExoPlayerUtil.getInstance().goToBackground();
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

    private void showErr(){
        Log.d(RecipeStepDetailFragment.class.getCanonicalName(), "Step not found");
        Toast.makeText(getActivity(), R.string.stepErr, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ExoPlayerUtil.getInstance().releaseVideoPlayer();
    }
}
