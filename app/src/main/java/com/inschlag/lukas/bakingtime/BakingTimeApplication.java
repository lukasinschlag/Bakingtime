package com.inschlag.lukas.bakingtime;

import android.app.Application;

import io.realm.Realm;

public class BakingTimeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
