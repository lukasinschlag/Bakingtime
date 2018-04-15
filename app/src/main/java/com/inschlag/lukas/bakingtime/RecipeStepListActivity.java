package com.inschlag.lukas.bakingtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.model.Recipe;
import com.inschlag.lukas.bakingtime.data.model.Step;

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
    private int mRecipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_list);

        ButterKnife.bind(this);

        Realm realm = Realm.getDefaultInstance();

        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.recipe_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        mRecipeId = getIntent().getIntExtra(Constants.ARG_ITEM_ID, 0);
        Recipe recipe = realm.where(Recipe.class).equalTo("id", mRecipeId).findFirst();

        if(recipe != null){
            setupRecyclerView(recipe.getSteps());
            getSupportActionBar().setTitle(recipe.getName());
        }
    }

    private void setupRecyclerView(List<Step> steps) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(new RecipeStepListAdapter(this, steps, mRecipeId, mTwoPane));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe, menu);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(mRecipeId == sp.getInt(Constants.WIDGET_RECIPE, -1)){
            menu.getItem(0).setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button
            navigateUpTo(new Intent(this, RecipeStepListActivity.class));
            return true;
        } else if(id == R.id.menu_recipe){
            int rId = -1;
            if(item.isChecked()){
                item.setChecked(false);
            } else {
                rId = mRecipeId;
                item.setChecked(true);
            }
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().putInt(Constants.WIDGET_RECIPE, rId).apply();
        }
        return super.onOptionsItemSelected(item);
    }
}
