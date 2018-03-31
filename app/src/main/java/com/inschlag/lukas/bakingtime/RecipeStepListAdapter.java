package com.inschlag.lukas.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.model.Recipe;
import com.inschlag.lukas.bakingtime.data.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepListAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final RecipeStepListActivity mActivity;
    private final List<Step> mValues;
    private final int mRecipeId;
    private final boolean mTwoPane;

    // Ingredients detail fragment
    private final View.OnClickListener mOnIngredientClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(RecipeDetailActivity.ARG_ITEM_ID, mRecipeId);
                RecipeIngredientDetailFragment fragment = new RecipeIngredientDetailFragment();
                fragment.setArguments(arguments);
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.ARG_ITEM_ID, mRecipeId);

                context.startActivity(intent);
            }
        }
    };

    // Step detail fragment
    private final View.OnClickListener mOnStepClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Step item = (Step) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(RecipeDetailActivity.ARG_ITEM_ID, item.getId());
                RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                fragment.setArguments(arguments);
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.ARG_ITEM_ID, item.getId());

                context.startActivity(intent);
            }
        }
    };

    RecipeStepListAdapter(RecipeStepListActivity parent,
                          List<Step> items,
                          int recipeId,
                          boolean twoPane) {
        mValues = items;
        mRecipeId = recipeId;
        mActivity = parent;
        mTwoPane = twoPane;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_step_content, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case Constants.VIEWTYPE_INGREDIENT:
                ((ViewHolder)holder).text.setText(mActivity.getResources().getString(R.string.ingredients));
                holder.itemView.setOnClickListener(mOnIngredientClickListener);
                break;
            case Constants.VIEWTYPE_STEP:
                ((ViewHolder)holder).text.setText(String.format(mActivity.getResources().getString(R.string.stepX),
                        position, mValues.get(position-1).getShortDescription()));
                holder.itemView.setOnClickListener(mOnStepClickListener);
                holder.itemView.setTag(mValues.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return Constants.VIEWTYPE_INGREDIENT; //ingredients
        else return Constants.VIEWTYPE_STEP; // steps
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text) TextView text;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}