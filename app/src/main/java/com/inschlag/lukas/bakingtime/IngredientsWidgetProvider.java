package com.inschlag.lukas.bakingtime;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.inschlag.lukas.bakingtime.data.Constants;
import com.inschlag.lukas.bakingtime.data.model.Ingredient;
import com.inschlag.lukas.bakingtime.data.model.Recipe;

import io.realm.Realm;

/**
 * Implementation of App Widget functionality.
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

            String line = context.getString(R.string.appwidget_text_line);
            StringBuilder widgetText = new StringBuilder(context.getString(R.string.appwidget_text));
            for (Ingredient i : recipe.getIngredients()) {
                widgetText.append(String.format(line, i.getIngredient(), i.getQuantity(), i.getMeasure()));
            }
            views.setTextViewText(R.id.appwidget_title, recipe.getName());
            views.setTextViewText(R.id.appwidget_text, widgetText.toString());

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
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

