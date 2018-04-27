package com.inschlag.lukas.bakingtime.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inschlag.lukas.bakingtime.FullScreenVideoActivity;
import com.inschlag.lukas.bakingtime.R;
import com.inschlag.lukas.bakingtime.RecipeDetailFragment;
import com.inschlag.lukas.bakingtime.RecipeStepDetailActivity;
import com.inschlag.lukas.bakingtime.RecipeStepDetailFragment;
import com.inschlag.lukas.bakingtime.RecipeStepListActivity;
import com.inschlag.lukas.bakingtime.data.Constants;
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
            Bundle arguments = new Bundle();
            arguments.putInt(Constants.ARG_ITEM_ID, mRecipeId);
            arguments.putBoolean(Constants.ARG_INGREDIENT, true);
            if (mTwoPane) {
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(arguments);
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeStepDetailActivity.class);
                intent.putExtras(arguments);
                context.startActivity(intent);
            }
        }
    };

    // Step detail fragment
    private final View.OnClickListener mOnStepClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Step item = (Step) view.getTag();
            Bundle arguments = new Bundle();
            arguments.putInt(Constants.ARG_ITEM_ID, mRecipeId);
            arguments.putInt(Constants.ARG_STEP, item.getId());
            if (mTwoPane) {
                RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                fragment.setArguments(arguments);
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent;
                // is phone in landscape - if, show Fullscreen video
                if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                        && !TextUtils.isEmpty(item.getVideoURL())){
                    intent = new Intent(context, FullScreenVideoActivity.class);
                    intent.putExtra(Constants.ARG_VIDEO_URL, item.getVideoURL());
                } else {
                    intent = new Intent(context, RecipeStepDetailActivity.class);
                    intent.putExtras(arguments);
                }
                context.startActivity(intent);
            }
        }
    };

    public RecipeStepListAdapter(RecipeStepListActivity parent,
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
                holder.itemView.setTag(mValues.get(position-1));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size()+1;
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
