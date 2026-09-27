package com.cantonadong.timezone;

import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public final class WidgetConfigActivity extends Activity {
    private Store store;
    private int widgetId=AppWidgetManager.INVALID_APPWIDGET_ID;
    private String cityId;
    private boolean dark,zh;
    private Button submit;
    private String t(String cn,String en){return zh?cn:en;}
    @Override public void onCreate(Bundle state){super.onCreate(state);Ui.window(this);store=new Store(this);zh=store.chinese();setResult(RESULT_CANCELED);widgetId=getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);cityId=widgetId!=AppWidgetManager.INVALID_APPWIDGET_ID?store.widgetCity(widgetId):getIntent().getStringExtra("city");if(City.find(cityId)==null)cityId="beijing";dark=widgetId!=AppWidgetManager.INVALID_APPWIDGET_ID&&store.widgetDark(widgetId);if(state!=null){cityId=state.getString("city",cityId);dark=state.getBoolean("dark",dark);}if(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE.equals(getIntent().getAction())&&widgetId==AppWidgetManager.INVALID_APPWIDGET_ID){finish();return;}render();}
    @Override protected void onSaveInstanceState(Bundle out){super.onSaveInstanceState(out);out.putString("city",cityId);out.putBoolean("dark",dark);}
    private void render(){
        java.util.List<String> savedCities=store.cities();
        if(savedCities.isEmpty()){
            LinearLayout empty=Ui.column(this);empty.setBackgroundColor(Ui.PAPER);setContentView(empty);empty.addView(Ui.header(this,t("桌面卡片","Home Widget"),()->finish(),null,null));
            TextView message=Ui.note(this,t("请先在世界时钟添加城市，再创建桌面卡片。","Add a city in Timezone before creating a home widget."));message.setPadding(Ui.dp(this,24),Ui.dp(this,40),Ui.dp(this,24),Ui.dp(this,20));empty.addView(message);
            Button open=Ui.button(this,t("打开世界时钟","Open Timezone"),true);LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(-1,-2);params.setMargins(Ui.dp(this,22),0,Ui.dp(this,22),0);empty.addView(open,params);open.setOnClickListener(v->{startActivity(new Intent(this,MainActivity.class));finish();});return;
        }
        if(!savedCities.contains(cityId))cityId=savedCities.get(0);
        City city=City.find(cityId);LinearLayout root=Ui.column(this);root.setBackgroundColor(Ui.PAPER);setContentView(root);root.addView(Ui.header(this,t("桌面卡片","Home Widget"),()->finish(),null,null));
        ScrollView scroll=new ScrollView(this);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));LinearLayout content=Ui.column(this);content.setPadding(Ui.dp(this,22),Ui.dp(this,8),Ui.dp(this,22),Ui.dp(this,24));scroll.addView(content);
        LinearLayout choose=new LinearLayout(this);choose.setGravity(Gravity.CENTER_VERTICAL);choose.setPadding(Ui.dp(this,16),0,Ui.dp(this,16),0);choose.setBackground(Ui.bg(0xffeceef3,28,this));choose.setClickable(true);choose.setFocusable(true);choose.setContentDescription(city.name(zh)+" · "+city.offset(city.now()));
        TextView chooseName=Ui.text(this,city.name(zh),16,Ui.INK,false);chooseName.setSingleLine(true);chooseName.setEllipsize(TextUtils.TruncateAt.END);choose.addView(chooseName,new LinearLayout.LayoutParams(0,-2,1));
        TextView chooseUtc=Ui.text(this,city.offset(city.now()),14,Ui.INK,false);chooseUtc.setPadding(Ui.dp(this,8),0,Ui.dp(this,8),0);choose.addView(chooseUtc);choose.addView(Ui.text(this,"▾",16,Ui.INK,false));content.addView(choose,new LinearLayout.LayoutParams(-1,Ui.dp(this,52)));choose.setOnClickListener(v->chooseCity());
        FrameLayout wallpaper=new FrameLayout(this);GradientDrawable backdrop=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff8aafd4,0xffd7b9be,0xfff1c3a7,0xff31587c});backdrop.setCornerRadius(Ui.dp(this,24));wallpaper.setBackground(backdrop);LinearLayout.LayoutParams wallpaperParams=new LinearLayout.LayoutParams(-1,Ui.dp(this,230));wallpaperParams.topMargin=Ui.dp(this,16);content.addView(wallpaper,wallpaperParams);
        LinearLayout card=Ui.column(this);card.setGravity(Gravity.CENTER);card.setPadding(Ui.dp(this,8),Ui.dp(this,6),Ui.dp(this,8),Ui.dp(this,6));card.setBackground(getDrawable(dark?R.drawable.widget_dark:R.drawable.widget_light));FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(Ui.dp(this,210),Ui.dp(this,140),Gravity.CENTER);wallpaper.addView(card,cp);
        TextClock clock=new TextClock(this);clock.setTimeZone(city.zone);clock.setFormat12Hour("HH:mm");clock.setFormat24Hour("HH:mm");clock.setTextColor(dark?Color.WHITE:Ui.INK);clock.setTextSize(52);clock.setTextScaleX(1f);clock.setScaleX(1f);clock.setScaleY(1f);clock.setIncludeFontPadding(false);android.graphics.Typeface base=android.graphics.Typeface.create("sans-serif",0);clock.setTypeface(Build.VERSION.SDK_INT>=28?android.graphics.Typeface.create(base,600,false):android.graphics.Typeface.create("sans-serif-medium",0));clock.setSingleLine(true);clock.setGravity(Gravity.CENTER);card.addView(clock,new LinearLayout.LayoutParams(-2,-2));TextView name=Ui.text(this,city.name(zh),15,dark?Color.WHITE:Ui.INK,false);name.setGravity(Gravity.CENTER);name.setSingleLine(true);name.setAutoSizeTextTypeUniformWithConfiguration(12,15,1,android.util.TypedValue.COMPLEX_UNIT_SP);card.addView(name,new LinearLayout.LayoutParams(-1,Ui.dp(this,21)));
        LinearLayout options=new LinearLayout(this);options.setPadding(0,Ui.dp(this,20),0,Ui.dp(this,20));Button light=Ui.button(this,t("白色半透明","Light"),!dark),black=Ui.button(this,t("黑色半透明","Dark"),dark);LinearLayout.LayoutParams half=new LinearLayout.LayoutParams(0,-2,1);half.rightMargin=Ui.dp(this,12);options.addView(light,half);options.addView(black,new LinearLayout.LayoutParams(0,-2,1));content.addView(options);light.setOnClickListener(v->{dark=false;render();});black.setOnClickListener(v->{dark=true;render();});
        content.addView(Ui.note(this,t("2 × 1 桌面卡片，显示城市当地时间。添加后点击卡片可更换城市或样式。","A 2 × 1 home widget showing local city time. Tap it on your home screen to change its city or style.")));
        LinearLayout footer=Ui.column(this);footer.setPadding(Ui.dp(this,22),Ui.dp(this,12),Ui.dp(this,22),Ui.dp(this,16));submit=Ui.button(this,widgetId==AppWidgetManager.INVALID_APPWIDGET_ID?t("添加到桌面","Add to home screen"):t("保存","Save"),true);footer.addView(submit,new LinearLayout.LayoutParams(-1,-2));root.addView(footer);submit.setOnClickListener(v->save());
    }
    private void chooseCity(){
        LinearLayout content=Ui.column(this);content.setPadding(Ui.dp(this,16),0,Ui.dp(this,16),0);
        ArrayList<City> available=new ArrayList<>();for(String id:store.cities()){City city=City.find(id);if(city!=null)available.add(city);}
        ListView list=new ListView(this);list.setDividerHeight(0);int height=Math.min(420,Math.min(getResources().getConfiguration().screenHeightDp/2,Math.max(64,available.size()*64)));content.addView(list,new LinearLayout.LayoutParams(-1,Ui.dp(this,height)));
        BaseAdapter adapter=new BaseAdapter(){
            @Override public int getCount(){return available.size();}
            @Override public Object getItem(int position){return available.get(position);}
            @Override public long getItemId(int position){return position;}
            @Override public View getView(int position,View recycled,android.view.ViewGroup parent){
                City city=available.get(position);
                LinearLayout row=new LinearLayout(WidgetConfigActivity.this);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(Ui.dp(WidgetConfigActivity.this,10),Ui.dp(WidgetConfigActivity.this,7),Ui.dp(WidgetConfigActivity.this,10),Ui.dp(WidgetConfigActivity.this,7));
                row.addView(Ui.globeIcon(WidgetConfigActivity.this),new LinearLayout.LayoutParams(Ui.dp(WidgetConfigActivity.this,44),Ui.dp(WidgetConfigActivity.this,44)));
                TextView name=Ui.text(WidgetConfigActivity.this,city.name(zh),15,Ui.INK,true);name.setPadding(Ui.dp(WidgetConfigActivity.this,12),0,Ui.dp(WidgetConfigActivity.this,8),0);row.addView(name,new LinearLayout.LayoutParams(0,-2,1));
                TextView time=Ui.text(WidgetConfigActivity.this,city.time(city.now()),20,Ui.INK,false);time.setFontFeatureSettings("tnum");time.setGravity(Gravity.CENTER);row.addView(time,new LinearLayout.LayoutParams(-2,-2));
                return row;
            }
        };
        list.setAdapter(adapter);
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle(t("\u9009\u62e9\u57ce\u5e02","Choose city")).setView(content).setNegativeButton(t("\u53d6\u6d88","Cancel"),null).create();
        list.setOnItemClickListener((a,v,pos,id)->{cityId=available.get(pos).id;dialog.dismiss();render();});
        dialog.show();
    }
    private void save(){
        if(widgetId!=AppWidgetManager.INVALID_APPWIDGET_ID){store.widget(widgetId,cityId,dark);ClockWidget.update(this,widgetId);setResult(RESULT_OK,new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId));finish();return;}
        AppWidgetManager manager=AppWidgetManager.getInstance(this);
        if(!manager.isRequestPinAppWidgetSupported()){manualInstructions();return;}
        String token=UUID.randomUUID().toString();
        Intent result=new Intent(this,PinReceiver.class).setAction("com.cantonadong.timezone.PINNED").setData(Uri.parse("worldclock://pin/"+token)).putExtra("city",cityId).putExtra("dark",dark);
        int flags=PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT;
        if(Build.VERSION.SDK_INT>=31)flags|=PendingIntent.FLAG_MUTABLE;
        PendingIntent callback=PendingIntent.getBroadcast(this,0,result,flags);
        Bundle extras=new Bundle();extras.putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW,ClockWidget.views(this,cityId,dark,AppWidgetManager.INVALID_APPWIDGET_ID));
        try {
            boolean accepted=manager.requestPinAppWidget(new ComponentName(this,ClockWidget.class),extras,callback);
            if(accepted){Toast.makeText(this,t("请在系统弹窗中确认添加","Confirm the widget in the system dialog"),Toast.LENGTH_LONG).show();}
            else {callback.cancel();manualInstructions();}
        }catch(IllegalStateException|SecurityException ex){callback.cancel();manualInstructions();}
    }
    private void manualInstructions(){new AlertDialog.Builder(this).setTitle(t("从桌面添加小组件","Add from your home screen")).setMessage(t("当前桌面不支持直接添加。请返回桌面，长按空白处 → 小组件 → 世界时钟，拖入 2 × 1 卡片后选择城市和样式。","This launcher does not support direct pinning. Long-press an empty area on your home screen → Widgets → Timezone. Drag the 2 × 1 widget onto your home screen, then choose its city and style.")).setPositiveButton(t("知道了","Got it"),null).show();}
}
