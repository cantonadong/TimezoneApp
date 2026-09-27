package com.cantonadong.timezone;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.*;
import android.widget.*;
import java.time.ZonedDateTime;

final class Ui {
    static final int BLUE=0xff086bff,INK=0xff111318,MUTED=0xff767c87,PAPER=0xfffafbfe;
    static int dp(Context c,float value){return Math.round(c.getResources().getDisplayMetrics().density*value);}
    static GradientDrawable bg(int color,float radius,Context c){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(c,radius));return d;}
    static TextView text(Context c,String value,float size,int color,boolean bold){TextView t=new TextView(c);t.setText(value);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,1);return t;}
    static LinearLayout column(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.VERTICAL);return l;}
    static Button button(Context c,String value,boolean primary){Button b=new Button(c);b.setText(value);b.setAllCaps(false);b.setTextSize(16);b.setTextColor(primary?Color.WHITE:INK);b.setMinHeight(dp(c,52));b.setMinimumHeight(dp(c,52));b.setPadding(dp(c,16),dp(c,10),dp(c,16),dp(c,10));b.setStateListAnimator(null);b.setBackground(new android.graphics.drawable.RippleDrawable(ColorStateList.valueOf(0x22000000),bg(primary?BLUE:0xffeceef3,28,c),null));return b;}
    static void window(Activity a){a.getWindow().setStatusBarColor(PAPER);a.getWindow().setNavigationBarColor(PAPER);a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);}
    static LinearLayout header(Activity a,String title,Runnable back,String action,Runnable run){
        LinearLayout row=new LinearLayout(a);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(a,18),dp(a,12),dp(a,18),dp(a,10));
        if(back!=null){Button b=icon(a,"‹");b.setContentDescription(new Store(a).chinese()?"返回":"Back");b.setOnClickListener(v->back.run());row.addView(b,new LinearLayout.LayoutParams(dp(a,44),dp(a,48)));}
        TextView text=text(a,title,back==null?25:19,INK,true);if(back!=null)text.setGravity(Gravity.CENTER);row.addView(text,new LinearLayout.LayoutParams(0,dp(a,48),1));
        if(action!=null){
            ImageButton b=new ImageButton(a);b.setImageResource(R.drawable.settings);b.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            b.setPadding(dp(a,10),dp(a,12),dp(a,10),dp(a,12));b.setBackground(bg(Color.TRANSPARENT,24,a));
            b.setContentDescription(new Store(a).chinese()?"设置":"Settings");b.setOnClickListener(v->run.run());
            row.addView(b,new LinearLayout.LayoutParams(dp(a,44),dp(a,48)));
        }else if(back!=null)row.addView(new View(a),new LinearLayout.LayoutParams(dp(a,44),1));
        return row;
    }
    static Button icon(Context c,String glyph){Button b=new Button(c);b.setText(glyph);b.setTextColor(INK);b.setTextSize(26);b.setPadding(0,0,0,0);b.setMinWidth(0);b.setMinimumWidth(0);b.setBackground(bg(Color.TRANSPARENT,24,c));return b;}
    static TextView note(Context c,String s){TextView t=text(c,s,13,MUTED,false);t.setLineSpacing(dp(c,3),1);return t;}
    static View divider(Context c){View v=new View(c);v.setBackgroundColor(0xfff0f1f4);v.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(c,1)));return v;}
    static ImageView globeIcon(Context c){ImageView icon=new ImageView(c);icon.setImageResource(R.drawable.globe);icon.setScaleType(ImageView.ScaleType.FIT_CENTER);return icon;}

    static final class Card extends LinearLayout {
        private final City city;
        private final boolean zh,listStyle;
        private final TextView time,date,zone;
        Card(Context c,City city,boolean zh){this(c,city,zh,false);}
        Card(Context c,City city,boolean zh,boolean listStyle){
            super(c);this.city=city;this.zh=zh;this.listStyle=listStyle;
            setGravity(Gravity.CENTER_VERTICAL);setPadding(dp(c,10),dp(c,17),dp(c,10),dp(c,17));setBackgroundColor(Color.WHITE);
            int size=c.getResources().getConfiguration().screenWidthDp<360?44:56;
            addView(globeIcon(c),new LinearLayout.LayoutParams(dp(c,size),dp(c,size)));
            LinearLayout info=column(c);info.setPadding(dp(c,12),0,dp(c,8),0);
            TextView name=text(c,city.name(zh),17,INK,true);name.setGravity(Gravity.START);info.addView(name);
            zone=text(c,"",12,MUTED,false);zone.setGravity(Gravity.START);zone.setPadding(0,dp(c,4),0,0);zone.setLineSpacing(dp(c,2),1);info.addView(zone);
            addView(info,new LinearLayout.LayoutParams(0,-2,1));
            LinearLayout clock=column(c);clock.setGravity(Gravity.CENTER_HORIZONTAL);
            time=text(c,"",c.getResources().getConfiguration().screenWidthDp<360?25:29,INK,true);
            time.setFontFeatureSettings("tnum");time.setSingleLine(true);time.setGravity(Gravity.CENTER);
            date=text(c,"",12,MUTED,false);date.setPadding(0,dp(c,3),0,0);date.setSingleLine(true);date.setGravity(Gravity.CENTER);
            clock.addView(time);clock.addView(date);addView(clock,new LinearLayout.LayoutParams(-2,-2));refresh();
        }
        void boundSummary(){LinearLayout info=(LinearLayout)getChildAt(1);TextView name=(TextView)info.getChildAt(0);name.setSingleLine(true);name.setEllipsize(android.text.TextUtils.TruncateAt.END);zone.setMaxLines(2);zone.setEllipsize(android.text.TextUtils.TruncateAt.END);}
        void refresh(){
            ZonedDateTime n=city.now();time.setText(city.time(n));
            if(listStyle){zone.setText(city.country(zh));date.setText(city.offset(n));}
            else{zone.setText(city.zoneName(n,zh)+"\n"+city.offset(n));date.setText(city.date(n,zh));}
        }
    }
}
