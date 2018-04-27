package com.inschlag.lukas.bakingtime.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inschlag.lukas.bakingtime.R;
import com.inschlag.lukas.bakingtime.RecipeListActivity;
import com.inschlag.lukas.bakingtime.RecipeStepListActivity;
import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter
        extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    private final RecipeListActivity mActivity;
    private final List<Recipe> mRecipes;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Recipe item = (Recipe) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, RecipeStepListActivity.class);
            intent.putExtra(Constants.ARG_ITEM_ID, item.getId());
            //open
            context.startActivity(intent);
        }
    };

    public RecipeListAdapter(RecipeListActivity parent,
                             List<Recipe> items) {
        mRecipes = items;
        mActivity = parent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.name.setText(mRecipes.get(position).getName());
        holder.servings.setText(String.format(mActivity.getResources().getString(R.string.servings), mRecipes.get(position).getServings()));

        holder.itemView.setTag(mRecipes.get(position));
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
