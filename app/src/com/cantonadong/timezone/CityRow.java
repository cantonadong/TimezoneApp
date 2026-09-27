package com.cantonadong.timezone;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Bundle;
import android.widget.*;

/** Only intercepts a gesture after horizontal intent is clear; vertical scroll remains native. */
final class CityRow extends FrameLayout {
    final Ui.Card card;
    private final int distance,slop;
    private float downX,downY,origin;
    private boolean dragging,vertical;
    private final Runnable remove,pin;
    CityRow(Context context,City city,boolean zh,Runnable remove,Runnable pin){
        super(context);this.remove=remove;this.pin=pin;distance=Ui.dp(context,84);slop=ViewConfiguration.get(context).getScaledTouchSlop();
        Button add=Ui.button(context,zh?"▦\n添加到桌面":"▦\nAdd widget",true);add.setTextSize(12);add.setBackgroundColor(Ui.BLUE);add.setPadding(0,0,0,0);add.setContentDescription(zh?"添加到桌面":"Add widget");FrameLayout.LayoutParams left=new FrameLayout.LayoutParams(distance,-1,Gravity.LEFT);addView(add,left);add.setOnClickListener(v->{close();pin.run();});
        Button delete=Ui.button(context,zh?"⌫\n删除":"⌫\nDelete",true);delete.setBackgroundColor(0xffff393f);delete.setTextSize(13);delete.setPadding(0,0,0,0);FrameLayout.LayoutParams right=new FrameLayout.LayoutParams(distance,-1,Gravity.RIGHT);addView(delete,right);delete.setOnClickListener(v->remove.run());
        card=new Ui.Card(context,city,zh,true);addView(card,new FrameLayout.LayoutParams(-1,-2));
        card.setOnClickListener(v->{if(Math.abs(card.getTranslationX())>1)close();else options(zh);});
        card.setOnLongClickListener(v->{options(zh);return true;});
        card.setFocusable(true);card.setContentDescription(city.name(zh)+". "+(zh?"点击查看城市操作":"Tap for city actions"));
        card.setOnKeyListener((v,key,e)->{if(e.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_LEFT){slide(-distance);return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT){slide(distance);return true;}return false;});
    }
    private void options(boolean zh){new android.app.AlertDialog.Builder(getContext()).setItems(zh?new String[]{"添加到桌面","删除城市"}:new String[]{"Add widget","Delete city"},(d,which)->{if(which==0)pin.run();else remove.run();}).show();}
    private void slide(float value){card.animate().translationX(value).setDuration(180).start();}
    void close(){slide(0);}
    @Override public boolean onInterceptTouchEvent(android.view.MotionEvent e){
        switch(e.getActionMasked()){
            case MotionEvent.ACTION_DOWN:downX=e.getX();downY=e.getY();origin=card.getTranslationX();dragging=false;vertical=false;card.animate().cancel();getParent().requestDisallowInterceptTouchEvent(true);break;
            case MotionEvent.ACTION_MOVE:
                float dx=e.getX()-downX,dy=e.getY()-downY;
                if(!dragging&&!vertical&&Math.abs(dy)>slop&&Math.abs(dy)>=Math.abs(dx)){vertical=true;getParent().requestDisallowInterceptTouchEvent(false);}
                if(!vertical&&Math.abs(dx)>slop&&Math.abs(dx)>Math.abs(dy)*1.3f){dragging=true;getParent().requestDisallowInterceptTouchEvent(true);return true;}break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:getParent().requestDisallowInterceptTouchEvent(false);break;
        }return dragging;
    }
    @Override public boolean onTouchEvent(MotionEvent e){
        if(e.getActionMasked()==MotionEvent.ACTION_MOVE){card.setTranslationX(Math.max(-distance,Math.min(distance,origin+e.getX()-downX)));return true;}
        if(e.getActionMasked()==MotionEvent.ACTION_UP||e.getActionMasked()==MotionEvent.ACTION_CANCEL){float position=card.getTranslationX();slide(e.getActionMasked()==MotionEvent.ACTION_CANCEL?0:Math.abs(position)>distance*.35f?(position>0?distance:-distance):0);dragging=false;getParent().requestDisallowInterceptTouchEvent(false);return true;}return true;
    }
}
