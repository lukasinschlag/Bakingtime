package com.inschlag.lukas.bakingtime;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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

import com.inschlag.lukas.bakingtime.data.model.Recipe;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class RecipeStepDetailActivity extends AppCompatActivity {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_STEP = "step";
    public static final String ARG_INGREDIENT = "ingredient";

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

    private int mRecipeId = 0;
    private int mNumSteps = 0;
    private int mCurrentItem = -1;

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
            mRecipeId = getIntent().getIntExtra(ARG_ITEM_ID, 0);

            Recipe recipe = realm.where(Recipe.class)
                    .equalTo("id", mRecipeId)
                    .findFirst();

            if(recipe == null){
                //err: couldn't find recipe
                return;
            }
            mNumSteps = recipe.getSteps().size();
            Log.d("StepDetail", "numOfSteps: " + mNumSteps);

            CollapsingToolbarLayout appBarLayout = findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(recipe.getName());
                if(!TextUtils.isEmpty(recipe.getImage())){
                    Picasso.with(this).load(recipe.getImage()).into(mToolbarImg);
                }
            }

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            // it gets either the recipe id (if showing the ingredients) or the step id (showing a step)
            if(getIntent().hasExtra(ARG_INGREDIENT)){
                showIngredients();
            } else if(getIntent().hasExtra(ARG_STEP)) {
                showStep(getIntent().getIntExtra(ARG_STEP, 0));
            }
            setNavButtons(mCurrentItem);
        }
    }

    private final View.OnClickListener mOnPrevClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setNavButtons(mCurrentItem-1);
            if(mCurrentItem == 0){ //show Ingredients
                showIngredients();
            } else {
                showStep(mCurrentItem-1);
            }
        }
    };

    private final View.OnClickListener mOnNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setNavButtons(mCurrentItem+1);
            showStep(mCurrentItem+1);
        }
    };

    private void setNavButtons(int id){
        mPrevBtn.setText(getResources().getString(R.string.previous));
        mNextBtn.setText(getResources().getString(R.string.next));
        mPrevBtn.setVisibility(View.VISIBLE);
        if(id > 0){
            if(id == (mNumSteps -1)){ //last step
                Log.d("StepDetail", "lastStep");
                mNextBtn.setEnabled(false);
            } else { //a step in the middle
                mNextBtn.setEnabled(true);
            }
        } else if (id == 0){ //first step
            mPrevBtn.setText(getResources().getString(R.string.showIngredients));
        } else {
            // ingredients screen
            mPrevBtn.setVisibility(View.GONE);
            mNextBtn.setText(getResources().getString(R.string.showSteps));
        }
    }

    private void showIngredients(){
        Log.d(getClass().getCanonicalName(), "Show ingredients fragment");
        mDetailContainer.removeAllViews();
        mCurrentItem = -1;
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_ITEM_ID, mRecipeId);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.recipe_detail_container, fragment)
                .commit();
    }

    private void showStep(int id){
        Log.d(getClass().getCanonicalName(), "Show step fragment");
        mDetailContainer.removeAllViews();
        mCurrentItem = id;
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_ITEM_ID, mRecipeId);
        arguments.putInt(ARG_STEP, mCurrentItem);
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.recipe_detail_container, fragment)
                .commit();
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
