package com.cantonadong.timezone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

/** The launcher's TextClock owns minute ticks; no process, alarm, or battery exemption is needed. */
public final class ClockWidget extends AppWidgetProvider {
    public static RemoteViews views(Context context,String cityId,boolean dark,int widgetId){
        City city=City.find(cityId);if(city==null)city=City.find("beijing");
        Store store=new Store(context);RemoteViews views=new RemoteViews(context.getPackageName(),R.layout.widget_clock);
        views.setInt(R.id.widget_root,"setBackgroundResource",dark?R.drawable.widget_dark:R.drawable.widget_light);
        views.setString(R.id.widget_time,"setTimeZone",city.zone);
        views.setCharSequence(R.id.widget_time,"setFormat12Hour","HH:mm");
        views.setCharSequence(R.id.widget_time,"setFormat24Hour","HH:mm");
        views.setTextColor(R.id.widget_time,dark?Color.WHITE:Ui.INK);
        views.setTextColor(R.id.widget_city,dark?Color.WHITE:Ui.INK);
        views.setTextViewText(R.id.widget_city,city.name(store.chinese()));
        views.setContentDescription(R.id.widget_root,city.name(store.chinese())+(store.chinese()?"，点击设置桌面卡片":", tap to configure widget"));
        if(widgetId!=AppWidgetManager.INVALID_APPWIDGET_ID){
            Intent configure=new Intent(context,WidgetConfigActivity.class).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId).setData(Uri.parse("worldclock://widget/"+widgetId));
            views.setOnClickPendingIntent(R.id.widget_root,PendingIntent.getActivity(context,widgetId,configure,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));
        }return views;
    }
    static void update(Context c,int id){Store store=new Store(c);AppWidgetManager.getInstance(c).updateAppWidget(id,views(c,store.widgetCity(id),store.widgetDark(id),id));}
    static void refreshAll(Context c){AppWidgetManager manager=AppWidgetManager.getInstance(c);for(int id:manager.getAppWidgetIds(new ComponentName(c,ClockWidget.class)))update(c,id);}
    @Override public void onUpdate(Context c,AppWidgetManager manager,int[] ids){for(int id:ids)update(c,id);}
    @Override public void onAppWidgetOptionsChanged(Context c,AppWidgetManager manager,int id,Bundle options){update(c,id);}
    @Override public void onDeleted(Context c,int[] ids){Store store=new Store(c);for(int id:ids)store.deleteWidget(id);}
    @Override public void onRestored(Context c,int[] oldIds,int[] newIds){Store store=new Store(c);for(int i=0;i<oldIds.length&&i<newIds.length;i++){store.widget(newIds[i],store.widgetCity(oldIds[i]),store.widgetDark(oldIds[i]));store.deleteWidget(oldIds[i]);update(c,newIds[i]);}}
    @Override public void onReceive(Context c,Intent intent){super.onReceive(c,intent);String a=intent.getAction();if(Intent.ACTION_LOCALE_CHANGED.equals(a)||Intent.ACTION_TIME_CHANGED.equals(a)||Intent.ACTION_TIMEZONE_CHANGED.equals(a)||Intent.ACTION_BOOT_COMPLETED.equals(a)||Intent.ACTION_MY_PACKAGE_REPLACED.equals(a))refreshAll(c);}
}
