package com.inschlag.lukas.bakingtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.LoadRecipes;
import com.inschlag.lukas.bakingtime.data.model.Ingredient;
import com.inschlag.lukas.bakingtime.data.model.Recipe;
import com.inschlag.lukas.bakingtime.data.model.Step;
import com.inschlag.lukas.bakingtime.utils.NetworkUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * This activity shows the ingredients and steps of one recipe
 * In tablet-mode it also shows the details for a selected step using {@link RecipeStepDetailFragment}
 *
 */
public class RecipeStepListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recipe_list)
    RecyclerView mRecyclerView;

    // is in tablet mode?
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_list);

        ButterKnife.bind(this);

        Realm realm = Realm.getDefaultInstance();

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        if (findViewById(R.id.recipe_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        int recipeId = getIntent().getIntExtra(RecipeStepDetailActivity.ARG_ITEM_ID, 0);
        Recipe recipe = realm.where(Recipe.class).equalTo("id", recipeId).findFirst();

        if(recipe != null){
            setupRecyclerView(recipe.getSteps(), recipeId);
        }
    }

    private void setupRecyclerView(List<Step> steps, int recipeId) {
        mRecyclerView.setAdapter(new RecipeStepListAdapter(this, steps, recipeId, mTwoPane));
    }
}
