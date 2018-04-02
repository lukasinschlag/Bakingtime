package com.inschlag.lukas.bakingtime.data;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.inschlag.lukas.bakingtime.RecipeListActivity;
import com.inschlag.lukas.bakingtime.data.model.Recipe;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.realm.Realm;

public class LoadRecipes extends AsyncTask<String, Void, Boolean> {

    private WeakReference<RecipeListActivity> activityReference;

    public LoadRecipes(RecipeListActivity activity){
        this.activityReference = new WeakReference<RecipeListActivity>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activityReference.get().onRecipesLoadingStarted();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String mUrl = strings[0];
        Realm realm = Realm.getDefaultInstance();

        try {
            URL url = new URL(mUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            final InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    try {
                        realm.createAllFromJson(Recipe.class, in);
                    } catch (IOException e) {
                        Log.d(LoadRecipes.class.getCanonicalName(), "Error while parsing json: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } catch (MalformedURLException e){
            Log.d(LoadRecipes.class.getCanonicalName(), "Error while parsing url: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(LoadRecipes.class.getCanonicalName(), "Error while opening connection: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        activityReference.get().onRecipesLoaded();
    }
}
