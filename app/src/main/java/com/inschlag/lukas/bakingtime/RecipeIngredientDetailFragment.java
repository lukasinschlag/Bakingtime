package com.inschlag.lukas.bakingtime;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inschlag.lukas.bakingtime.data.model.Ingredient;
import com.inschlag.lukas.bakingtime.data.model.Recipe;

import io.realm.Realm;

/**
 * A fragment representing a single Ingredient detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 *
 * It is called with the recipe id
 */
public class RecipeIngredientDetailFragment extends Fragment {

    private Recipe mItem;

    public RecipeIngredientDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm realm = Realm.getDefaultInstance();

        if (getArguments() != null && getArguments().containsKey(RecipeDetailActivity.ARG_ITEM_ID)) {
            // Load the recipe
            mItem = realm.where(Recipe.class)
                    .equalTo("id", getArguments().getInt(RecipeDetailActivity.ARG_ITEM_ID))
                    .findFirst();

            if(mItem == null){
                //err: couldn't find recipe
                return;
            }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_incredient_detail, container, false);

        if (mItem != null) {
            StringBuilder ingredients = new StringBuilder();
            for(Ingredient i : mItem.getIngredients()){
                ingredients.append(i.toString()).append("\n\n");
            }

            ((TextView) rootView.findViewById(R.id.ingredients)).setText(ingredients.toString());
        }

        return rootView;
    }
}
