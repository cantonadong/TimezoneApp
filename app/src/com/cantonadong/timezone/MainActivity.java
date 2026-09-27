package com.cantonadong.timezone;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.time.ZonedDateTime;
import java.util.*;

public final class MainActivity extends Activity {
    private Store store;
    private boolean zh,adding,anchorOnOpen;
    private ArrayList<String> ids;
    private String selected="shanghai",query="";
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final ArrayList<Ui.Card> clocks=new ArrayList<>();
    private TextView compactSelection;
    private FrameLayout root;
    private LinearLayout body,content,footer,selection;
    private ScrollView scroll;
    private ListView cityList;
    private CityAdapter adapter;
    private Button save;
    private String removed;
    private int removedIndex;
    private long undoDeadline;
    private View undoBar;
    private final Runnable expireUndo=()->{removed=null;if(undoBar!=null){root.removeView(undoBar);undoBar=null;}};
    private final Runnable ticker=new Runnable(){public void run(){refreshClocks();handler.postDelayed(this,60000-System.currentTimeMillis()%60000);}};

    private String t(String cn,String en){return zh?cn:en;}
    @Override public void onCreate(Bundle state){super.onCreate(state);Ui.window(this);store=new Store(this);zh=store.chinese();ids=new ArrayList<>(store.cities());if(state!=null){adding=state.getBoolean("adding");selected=state.getString("selected","shanghai");query=state.getString("query","");removed=state.getString("removed");removedIndex=state.getInt("removedIndex");undoDeadline=state.getLong("undoDeadline");}render();if(state!=null){int position=state.getInt("scroll",0);if(scroll!=null)scroll.post(()->scroll.scrollTo(0,position));if(cityList!=null)cityList.setSelection(state.getInt("pickerPosition",0));}}
    @Override protected void onSaveInstanceState(Bundle out){super.onSaveInstanceState(out);out.putBoolean("adding",adding);out.putString("selected",selected);out.putString("query",query);out.putString("removed",removed);out.putInt("removedIndex",removedIndex);out.putLong("undoDeadline",undoDeadline);if(scroll!=null)out.putInt("scroll",scroll.getScrollY());if(cityList!=null)out.putInt("pickerPosition",cityList.getFirstVisiblePosition());}
    @Override protected void onResume(){super.onResume();boolean current=store.chinese();if(current!=zh){zh=current;render();}handler.removeCallbacks(ticker);ticker.run();ClockWidget.refreshAll(this);}
    @Override protected void onPause(){super.onPause();handler.removeCallbacks(ticker);}
    @Override protected void onDestroy(){handler.removeCallbacksAndMessages(null);super.onDestroy();}
    private void refreshClocks(){for(Ui.Card c:clocks)c.refresh();if(adapter!=null)adapter.notifyDataSetChanged();if(compactSelection!=null){City c=City.find(selected);compactSelection.setText(c.name(zh)+" · "+c.country(zh)+"\n"+c.time(c.now())+" · "+c.offset(c.now()));}}

    private void render(){
        clocks.clear();compactSelection=null;adapter=null;cityList=null;scroll=null;
        root=new FrameLayout(this);root.setBackgroundColor(Ui.PAPER);body=Ui.column(this);root.addView(body,new FrameLayout.LayoutParams(-1,-1));setContentView(root);
        body.addView(Ui.header(this,adding?t("添加城市","Add City"):t("世界时钟","Timezone"),adding?()->back():null,adding?null:"⚙",()->settings()));
        if(adding){renderPicker();root.addOnLayoutChangeListener((v,l,top,r,b,ol,ot,or,ob)->{boolean shortWindow=(b-top)<Ui.dp(this,440);if(selection!=null){selection.setVisibility(shortWindow?View.GONE:View.VISIBLE);compactSelection.setVisibility(shortWindow?View.VISIBLE:View.GONE);}});}else renderCities();
        if(removed!=null&&SystemClock.elapsedRealtime()<undoDeadline)showUndo();
    }
    private void renderCities(){
        scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setClipToPadding(false);scroll.setPadding(Ui.dp(this,16),0,Ui.dp(this,16),0);body.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        content=Ui.column(this);content.setBackground(Ui.bg(Color.WHITE,20,this));content.setClipToOutline(true);scroll.addView(content,new ScrollView.LayoutParams(-1,-2));
        if(ids.isEmpty()){
            LinearLayout empty=Ui.column(this);empty.setGravity(Gravity.CENTER);empty.setPadding(Ui.dp(this,24),Ui.dp(this,72),Ui.dp(this,24),Ui.dp(this,60));TextView icon=Ui.text(this,"◷",50,0xffa8bad5,false);empty.addView(icon);TextView title=Ui.text(this,t("世界时间，从一座城市开始","Your world starts with a city"),20,Ui.INK,true);title.setGravity(Gravity.CENTER);title.setPadding(0,Ui.dp(this,20),0,Ui.dp(this,12));empty.addView(title);TextView note=Ui.note(this,t("添加你关心的城市，轻松查看当地时间。","Add cities to keep their local times close."));note.setGravity(Gravity.CENTER);empty.addView(note);content.addView(empty);
        } else for(String id:ids){City c=City.find(id);if(c==null)continue;CityRow row=new CityRow(this,c,zh,()->delete(id),()->widget(id));content.addView(row,new LinearLayout.LayoutParams(-1,-2));clocks.add(row.card);content.addView(Ui.divider(this));}
        footer=Ui.column(this);footer.setPadding(Ui.dp(this,22),Ui.dp(this,16),Ui.dp(this,22),Ui.dp(this,16));Button add=Ui.button(this,t("添加城市","Add City"),true);footer.addView(add,new LinearLayout.LayoutParams(-1,-2));add.setOnClickListener(v->{adding=true;anchorOnOpen=true;query="";for(City c:City.all())if(!ids.contains(c.id)){selected=c.id;break;}render();});TextView hint=Ui.text(this,t("左滑删除 · 右滑添加桌面卡片","Swipe left to delete · right for a widget"),11,Ui.MUTED,false);hint.setGravity(Gravity.CENTER);hint.setPadding(0,Ui.dp(this,10),0,0);footer.addView(hint);body.addView(footer);
    }
    private void delete(String id){
        int index=ids.indexOf(id);if(index<0)return;handler.removeCallbacks(expireUndo);removed=id;removedIndex=index;undoDeadline=SystemClock.elapsedRealtime()+2000;ids.remove(index);store.cities(ids);int y=scroll==null?0:scroll.getScrollY();render();if(scroll!=null)scroll.post(()->scroll.scrollTo(0,y));
    }
    private void showUndo(){
        if(undoBar!=null)root.removeView(undoBar);LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(Ui.dp(this,16),Ui.dp(this,8),Ui.dp(this,8),Ui.dp(this,8));bar.setBackground(Ui.bg(0xff242832,14,this));bar.setElevation(Ui.dp(this,8));TextView message=Ui.text(this,t("已删除 ","Removed ")+City.find(removed).name(zh),14,Color.WHITE,false);bar.addView(message,new LinearLayout.LayoutParams(0,-2,1));Button undo=Ui.button(this,t("撤销","Undo"),false);undo.setTextColor(0xff8fbeff);undo.setBackgroundColor(Color.TRANSPARENT);bar.addView(undo,new LinearLayout.LayoutParams(-2,Ui.dp(this,44)));undo.setOnClickListener(v->{if(removed!=null&&SystemClock.elapsedRealtime()<=undoDeadline){if(!ids.contains(removed))ids.add(Math.min(removedIndex,ids.size()),removed);store.cities(ids);}removed=null;handler.removeCallbacks(expireUndo);render();});FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM);p.setMargins(Ui.dp(this,20),0,Ui.dp(this,20),Ui.dp(this,108));root.addView(bar,p);undoBar=bar;bar.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);handler.removeCallbacks(expireUndo);handler.postDelayed(expireUndo,Math.max(0,undoDeadline-SystemClock.elapsedRealtime()));
    }
    private void renderPicker(){
        LinearLayout searchWrap=Ui.column(this);searchWrap.setPadding(Ui.dp(this,22),0,Ui.dp(this,22),Ui.dp(this,10));EditText search=new EditText(this);search.setTextSize(14);search.setSingleLine(true);search.setHint(t("搜索城市（中文或英文）","Search cities in Chinese or English"));search.setContentDescription(search.getHint());search.setPadding(Ui.dp(this,16),Ui.dp(this,8),Ui.dp(this,16),Ui.dp(this,8));search.setBackground(Ui.bg(0xffeff1f5,26,this));search.setText(query);searchWrap.addView(search,new LinearLayout.LayoutParams(-1,Ui.dp(this,44)));body.addView(searchWrap);
        FrameLayout listFrame=new FrameLayout(this);body.addView(listFrame,new LinearLayout.LayoutParams(-1,0,1));cityList=new ListView(this);cityList.setPadding(Ui.dp(this,16),0,Ui.dp(this,16),0);cityList.setClipToPadding(false);cityList.setDividerHeight(0);cityList.setSelector(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));cityList.setBackgroundColor(Ui.PAPER);listFrame.addView(cityList,new FrameLayout.LayoutParams(-1,-1));TextView empty=Ui.text(this,t("没有找到城市\n试试中文或英文城市名称","No cities found\nTry a Chinese or English name"),16,Ui.MUTED,false);empty.setGravity(Gravity.CENTER);listFrame.addView(empty,new FrameLayout.LayoutParams(-1,-1));cityList.setEmptyView(empty);adapter=new CityAdapter();cityList.setAdapter(adapter);adapter.filter(query);if(anchorOnOpen&&query.isEmpty()){int beijing=adapter.indexOf("beijing");if(beijing>=0)cityList.post(()->cityList.setSelectionFromTop(beijing,0));anchorOnOpen=false;}cityList.setOnItemClickListener((p,v,pos,id)->{selected=adapter.matches.get(pos).id;adapter.notifyDataSetChanged();refreshSelection();hideKeyboard();});
        search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int f){}public void onTextChanged(CharSequence s,int a,int before,int count){query=s.toString();adapter.filter(query);}public void afterTextChanged(Editable e){}});
        footer=Ui.column(this);footer.setPadding(Ui.dp(this,22),Ui.dp(this,8),Ui.dp(this,22),Ui.dp(this,16));selection=Ui.column(this);footer.addView(selection);compactSelection=Ui.text(this,"",13,Ui.INK,true);compactSelection.setMaxLines(2);compactSelection.setEllipsize(TextUtils.TruncateAt.END);compactSelection.setGravity(Gravity.CENTER_VERTICAL);compactSelection.setVisibility(View.GONE);footer.addView(compactSelection,new LinearLayout.LayoutParams(-1,Ui.dp(this,48)));LinearLayout buttons=new LinearLayout(this);Button cancel=Ui.button(this,t("取消","Cancel"),false);save=Ui.button(this,t("保存","Save"),true);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);p.rightMargin=Ui.dp(this,12);buttons.addView(cancel,p);buttons.addView(save,new LinearLayout.LayoutParams(0,-2,1));footer.addView(buttons);body.addView(footer);cancel.setOnClickListener(v->back());save.setOnClickListener(v->{if(!ids.contains(selected)){ids.add(selected);store.cities(ids);}back();});refreshSelection();search.clearFocus();body.setFocusableInTouchMode(true);body.requestFocus();
    }
    private void refreshSelection(){clocks.clear();selection.removeAllViews();City city=City.find(selected);if(city==null){selected=City.all().get(0).id;city=City.find(selected);}Ui.Card card=new Ui.Card(this,city,zh,true);card.setBackgroundColor(Color.TRANSPARENT);card.setPadding(0,Ui.dp(this,6),0,Ui.dp(this,14));selection.addView(card);clocks.add(card);refreshClocks();boolean exists=ids.contains(selected);save.setEnabled(!exists);save.setAlpha(exists?.45f:1f);save.setText(exists?t("已添加","Already added"):t("保存","Save"));}
    private final class CityAdapter extends BaseAdapter {
        final ArrayList<City> matches=new ArrayList<>();
        void filter(String q){matches.clear();for(City c:City.all())if(c.matches(q))matches.add(c);matches.sort(Comparator.comparingInt((City c)->c.now().getOffset().getTotalSeconds()).thenComparing(c->c.en,String.CASE_INSENSITIVE_ORDER));notifyDataSetChanged();}
        int indexOf(String id){for(int i=0;i<matches.size();i++)if(matches.get(i).id.equals(id))return i;return -1;}
        public int getCount(){return matches.size();}public Object getItem(int p){return matches.get(p);}public long getItemId(int p){return p;}
        public View getView(int p,View recycled,android.view.ViewGroup parent){City c=matches.get(p);LinearLayout row=new LinearLayout(MainActivity.this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(Ui.dp(MainActivity.this,10),Ui.dp(MainActivity.this,10),Ui.dp(MainActivity.this,10),Ui.dp(MainActivity.this,10));row.setBackground(Ui.bg(c.id.equals(selected)?0xffe6f1ff:Color.WHITE,12,MainActivity.this));row.addView(Ui.globeIcon(MainActivity.this),new LinearLayout.LayoutParams(Ui.dp(MainActivity.this,44),Ui.dp(MainActivity.this,44)));LinearLayout info=Ui.column(MainActivity.this);info.setPadding(Ui.dp(MainActivity.this,12),0,Ui.dp(MainActivity.this,8),0);info.addView(Ui.text(MainActivity.this,c.name(zh),15,Ui.INK,true));info.addView(Ui.text(MainActivity.this,c.country(zh)+(ids.contains(c.id)?t(" · 已添加"," · Added"):""),12,Ui.MUTED,false));row.addView(info,new LinearLayout.LayoutParams(0,-2,1));TextView offset=Ui.text(MainActivity.this,c.offset(c.now()),12,c.id.equals(selected)?Ui.BLUE:Ui.MUTED,false);row.addView(offset,new LinearLayout.LayoutParams(-2,-2));return row;}
    }
    private void settings(){String[] labels={"跟随系统 / System","中文","English"};String value=store.language();int checked="zh".equals(value)?1:"en".equals(value)?2:0;new AlertDialog.Builder(this).setTitle(t("语言","Language")).setSingleChoiceItems(labels,checked,(dialog,which)->{store.language(which==0?"system":which==1?"zh":"en");zh=store.chinese();dialog.dismiss();render();ClockWidget.refreshAll(this);}).setNegativeButton(t("取消","Cancel"),null).show();}
    private void widget(String id){hideKeyboard();startActivity(new Intent(this,WidgetConfigActivity.class).putExtra("city",id));}
    private void hideKeyboard(){((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(root.getWindowToken(),0);}
    private void back(){hideKeyboard();adding=false;query="";render();}
    @Override public void onBackPressed(){if(adding)back();else super.onBackPressed();}
}
