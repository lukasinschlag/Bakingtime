package com.inschlag.lukas.bakingtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.inschlag.lukas.bakingtime.R;
import com.inschlag.lukas.bakingtime.RecipeStepListActivity;
import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.model.Recipe;

import java.util.List;

import io.realm.Realm;

/**
 * Implementation of Bakingtime App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_provider);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int id = sp.getInt(Constants.WIDGET_RECIPE, -1);

        if(id > -1) {
            Realm realm = Realm.getDefaultInstance();
            Recipe recipe = realm.where(Recipe.class).equalTo("id", id).findFirst();

            if (recipe == null) {
                return;
            }

            views.setTextViewText(R.id.appwidget_title, recipe.getName());

            Intent listIntent = new Intent(context, WidgetRemoteViewsService.class);
            listIntent.putExtra(Constants.WIDGET_RECIPE, id);
            views.setRemoteAdapter(R.id.appwidget_list, listIntent);

            Intent intent = new Intent(context, RecipeStepListActivity.class);
            intent.putExtra(Constants.ARG_ITEM_ID, id);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.appwidget, pendingIntent);
        } else {
            views.setTextViewText(R.id.appwidget_title, context.getString(R.string.no_widget));
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}
}

