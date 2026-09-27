package com.cantonadong.timezone;

import android.appwidget.AppWidgetManager;
import android.content.*;

/** Explicit, one-shot PendingIntent target. Not externally exported. */
public final class PinReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context,Intent intent){
        int id=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        String city=intent.getStringExtra("city");
        if(id==AppWidgetManager.INVALID_APPWIDGET_ID||City.find(city)==null)return;
        android.appwidget.AppWidgetProviderInfo info=AppWidgetManager.getInstance(context).getAppWidgetInfo(id);
        if(info==null||!new ComponentName(context,ClockWidget.class).equals(info.provider))return;
        Store store=new Store(context);store.widget(id,city,intent.getBooleanExtra("dark",false));ClockWidget.update(context,id);
    }
}
