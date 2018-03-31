package com.inschlag.lukas.bakingtime;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inschlag.lukas.bakingtime.data.model.Recipe;
import com.inschlag.lukas.bakingtime.data.model.Step;

import io.realm.Realm;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment {

    private Step mItem;

    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm realm = Realm.getDefaultInstance();

        if (getArguments() != null && getArguments().containsKey(RecipeDetailActivity.ARG_ITEM_ID)) {
            // Load the recipe
            mItem = realm.where(Step.class)
                    .equalTo("id", getArguments().getInt(RecipeDetailActivity.ARG_ITEM_ID))
                    .findFirst();

            if(mItem == null){
                //err: couldn't find step
                return;
            }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getShortDescription());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_step_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.desc)).setText(mItem.getDescription());
        }

        return rootView;
    }
}
