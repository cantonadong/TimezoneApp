package com.cantonadong.timezone;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Store {
    private final SharedPreferences prefs;
    public Store(Context context) { prefs=context.getSharedPreferences("world_clock",Context.MODE_PRIVATE); }
    public String language() { return prefs.getString("language","system"); }
    public boolean chinese() { return "zh".equals(language()) || ("system".equals(language()) && Locale.getDefault().getLanguage().equals("zh")); }
    public void language(String language) { prefs.edit().putString("language",language).apply(); }
    public List<String> cities() {
        if(!prefs.contains("cities"))return new ArrayList<>(Arrays.asList("beijing","newyork","london","tokyo","sydney"));
        List<String> result=new ArrayList<>();
        try { JSONArray array=new JSONArray(prefs.getString("cities","[]"));
            for(int i=0;i<array.length();i++){String id=array.optString(i);if(City.find(id)!=null&&!result.contains(id))result.add(id);}
        } catch(JSONException ignored) { /* Keep a recoverable empty list if a local record is damaged. */ }
        return result;
    }
    public void cities(List<String> ids) { prefs.edit().putString("cities",new JSONArray(ids).toString()).apply(); }
    public String widgetCity(int id) { String city=prefs.getString("widget_city_"+id,"beijing");return City.find(city)==null?"beijing":city; }
    public boolean widgetDark(int id) { return prefs.getBoolean("widget_dark_"+id,false); }
    public void widget(int id,String city,boolean dark) { prefs.edit().putString("widget_city_"+id,city).putBoolean("widget_dark_"+id,dark).apply(); }
    public void deleteWidget(int id) { prefs.edit().remove("widget_city_"+id).remove("widget_dark_"+id).apply(); }
}
