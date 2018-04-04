package com.inschlag.lukas.bakingtime;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.model.Recipe;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class RecipeStepDetailActivity extends AppCompatActivity {

    private static final String ARG_STEPS = "numberOfSteps";
    private static final String ARG_CURRENT = "currentItem";
    private static final String ARG_FRAGMENT_TAG = "stepDetailFragment";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_img)
    ImageView mToolbarImg;
    @BindView(R.id.btnPrevious)
    Button mPrevBtn;
    @BindView(R.id.btnNext)
    Button mNextBtn;
    @BindView(R.id.recipe_detail_container)
    NestedScrollView mDetailContainer;

    private Fragment mFragment;
    private int mRecipeId = 0;
    private int mNumSteps = 0;
    private int mCurrentItem = -1;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPrevBtn.setOnClickListener(mOnPrevClickListener);
        mNextBtn.setOnClickListener(mOnNextClickListener);

        if (savedInstanceState == null) {
            Realm realm = Realm.getDefaultInstance();
            mRecipeId = getIntent().getIntExtra(Constants.ARG_ITEM_ID, 0);

            mRecipe = realm.where(Recipe.class)
                    .equalTo("id", mRecipeId)
                    .findFirst();

            if (mRecipe == null) { //err: couldn't find recipe
                finish();
                return;
            }
            mNumSteps = mRecipe.getSteps().size();

            CollapsingToolbarLayout appBarLayout = findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mRecipe.getName());
                if (!TextUtils.isEmpty(mRecipe.getImage())) {
                    Picasso.with(this).load(mRecipe.getImage()).into(mToolbarImg);
                }
            }

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            // it gets either the recipe id (if showing the ingredients) or the step id (showing a step)
            if (getIntent().hasExtra(Constants.ARG_INGREDIENT)) {
                showIngredients();
            } else if (getIntent().hasExtra(Constants.ARG_STEP)) {
                showStep(getIntent().getIntExtra(Constants.ARG_STEP, 0));
            }
        } else { //restore the state on orientation change
            Log.d("StepDetail", "restoreState");
            mRecipeId = savedInstanceState.getInt(Constants.ARG_ITEM_ID);
            mNumSteps = savedInstanceState.getInt(ARG_STEPS);
            mCurrentItem = savedInstanceState.getInt(ARG_CURRENT);
            mFragment = getSupportFragmentManager().findFragmentByTag(ARG_FRAGMENT_TAG);
            if (mCurrentItem == -1) { //ingredients
                showIngredients();
            } else {
                showStep(mCurrentItem);
            }
        }

        // set the navigation buttons depending on the current step
        setNavButtons(mCurrentItem);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent fullScreenVideo = new Intent(this, FullScreenVideoActivity.class);
            fullScreenVideo.putExtra(Constants.ARG_VIDEO_URL, mRecipe.getSteps().get(mCurrentItem).getVideoURL());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.ARG_ITEM_ID, mRecipeId);
        outState.putInt(ARG_STEPS, mNumSteps);
        outState.putInt(ARG_CURRENT, mCurrentItem);
        super.onSaveInstanceState(outState);
    }

    /*
     * ClickListener for when the left ('previous') button has been pressed
     */
    private final View.OnClickListener mOnPrevClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setNavButtons(mCurrentItem - 1);
            if (mCurrentItem == 0) { //show Ingredients
                showIngredients();
            } else {
                showStep(mCurrentItem - 1);
            }
        }
    };

    /*
     * ClickListener for when the right ('next') button has been pressed
     */
    private final View.OnClickListener mOnNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setNavButtons(mCurrentItem + 1);
            showStep(mCurrentItem + 1);
        }
    };

    private void setNavButtons(int id) {
        mPrevBtn.setText(getResources().getString(R.string.previous));
        mNextBtn.setText(getResources().getString(R.string.next));
        mPrevBtn.setVisibility(View.VISIBLE);
        if (id > 0) {
            if (id == (mNumSteps - 1)) { //last step
                Log.d("StepDetail", "lastStep");
                mNextBtn.setEnabled(false);
            } else { //a step in the middle
                mNextBtn.setEnabled(true);
            }
        } else if (id == 0) { //first step
            mPrevBtn.setText(getResources().getString(R.string.showIngredients));
        } else { // ingredients screen
            mPrevBtn.setVisibility(View.GONE);
            mNextBtn.setText(getResources().getString(R.string.showSteps));
        }
    }

    private void showIngredients() {
        Log.d(getClass().getCanonicalName(), "Show ingredients fragment");

        mCurrentItem = -1;
        Bundle arguments = new Bundle();
        arguments.putInt(Constants.ARG_ITEM_ID, mRecipeId);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);
        setFragment(fragment);
    }

    private void showStep(int id) {
        Log.d(getClass().getCanonicalName(), "Show step fragment");

        mCurrentItem = id;
        Bundle arguments = new Bundle();
        arguments.putInt(Constants.ARG_ITEM_ID, mRecipeId);
        arguments.putInt(Constants.ARG_STEP, mCurrentItem);
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.setArguments(arguments);
        setFragment(fragment);
    }

    private void setFragment(Fragment fragment) {
        if (mFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment, ARG_FRAGMENT_TAG)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, fragment, ARG_FRAGMENT_TAG)
                    .commit();
        }
        mFragment = fragment;
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
