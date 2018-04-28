package com.inschlag.lukas.bakingtime.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.inschlag.lukas.bakingtime.R;
import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.model.Ingredient;
import com.inschlag.lukas.bakingtime.data.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<String> mStringList = new ArrayList<>();
    private int mRecipeId = 0;

    WidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        mRecipeId = intent.getIntExtra(Constants.WIDGET_RECIPE, 0);
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        Realm realm = Realm.getDefaultInstance();
        Recipe recipe = realm.where(Recipe.class).equalTo("id", mRecipeId).findFirst();

        if(recipe == null) {
            return;
        }

        String line = mContext.getString(R.string.appwidget_text_line);
        for (Ingredient i : recipe.getIngredients()) {
            mStringList.add(String.format(line, i.getIngredient(), i.getQuantity(), i.getMeasure()));
        }

        realm.close();
    }

    @Override
    public void onDestroy() {}

    @Override
    public int getCount() {
        return mStringList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || mStringList.isEmpty()) {
            return null;
        }
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.ingredients_widget_item);
        rv.setTextViewText(R.id.appwidget_text, mStringList.get(position));

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}