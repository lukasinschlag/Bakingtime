package com.inschlag.lukas.bakingtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.LoadRecipes;
import com.inschlag.lukas.bakingtime.data.model.Recipe;
import com.inschlag.lukas.bakingtime.utils.NetworkUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class RecipeListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recipe_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        // if this is a table show a GridLayout instead of a List
        if(getResources().getBoolean(R.bool.isTablet)){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
            mRecyclerView.addItemDecoration(dividerItemDecoration);
        }
        // initialize recipe data
        if(realm.isEmpty() && NetworkUtil.isOnline(this)){
            LoadRecipes loadRecipes = new LoadRecipes(this);
            loadRecipes.execute(Constants.JSON_URL);
        } else {
            setupRecyclerView(realm.where(Recipe.class).findAll());
        }
    }

    private void setupRecyclerView(List<Recipe> recipeList) {
        mRecyclerView.setAdapter(new RecipeListAdapter(this, recipeList));
    }

    //show loading indicator
    public void onRecipesLoadingStarted(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    // recipes loaded: hide loading indicator and update list
    public void onRecipesLoaded(){
        mProgressBar.setVisibility(View.GONE);
        setupRecyclerView(realm.where(Recipe.class).findAll());
    }
}
