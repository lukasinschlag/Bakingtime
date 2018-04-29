package com.inschlag.lukas.bakingtime.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inschlag.lukas.bakingtime.R;
import com.inschlag.lukas.bakingtime.RecipeListActivity;
import com.inschlag.lukas.bakingtime.RecipeStepListActivity;
import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter
        extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    private final RecipeListActivity mActivity;
    private final List<Recipe> mRecipes;
    private int mLayoutResId = R.layout.recipe_list_content;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Recipe item = (Recipe) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, RecipeStepListActivity.class);
            intent.putExtra(Constants.ARG_ITEM_ID, item.getId());
            context.startActivity(intent);
        }
    };

    public RecipeListAdapter(RecipeListActivity parent, List<Recipe> items) {
        mRecipes = items;
        mActivity = parent;

        if(mActivity.getResources().getBoolean(R.bool.isTablet)){
            mLayoutResId = R.layout.recipe_list_content_card;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);
        holder.name.setText(recipe.getName());
        holder.servings.setText(String.format(mActivity.getResources().getString(R.string.servings), recipe.getServings()));

        if (!TextUtils.isEmpty(recipe.getImage())) {
            Picasso.with(mActivity).load(recipe.getImage()).into(holder.image);
        }

        holder.itemView.setTag(recipe);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.servings)
        TextView servings;
        @BindView(R.id.image)
        ImageView image;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
