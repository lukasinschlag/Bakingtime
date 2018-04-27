package com.inschlag.lukas.bakingtime.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.inschlag.lukas.bakingtime.widget.WidgetRemoteViewsFactory;

public class WidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}