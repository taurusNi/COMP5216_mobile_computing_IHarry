package com.taurus.iharry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.taurus.iharry.database.LogAdapter;
import com.taurus.iharry.database.Page_Number_2;
import com.taurus.iharry.database.type_theme;
import com.taurus.iharry.database.Speed1;
import com.taurus.iharry.database.bookStatus;
import com.taurus.iharry.sliding.Page;
import com.taurus.iharry.sliding.PageMoveLayout;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by taurus on 16/10/11.
 */
public class ReadActivity2 extends Activity {
//    public ArrayList<String> page = new ArrayList<String>();
//    TextView textView;
    public ArrayList<String> image=new ArrayList<String>();
    Boolean bookend = false;
    int location=1;
    Scanner s;
//    private int num = 0;
    String name;

    String name2="Q";
    String lastName = null;
    boolean complete = true;
    String cur = "";//当前句子
    String rest = "";//剩余句子

//    private boolean bookstatue = false;
    private boolean typestate=true;
    private ListView lscontent;
    private static final int forType = 10;
    private float fontSize = 25f;
    private TextView popView;
    private ArrayList<Integer> st;
    private ArrayList<Integer> indexs;
    private ArrayList<Integer> speeds;
    private ArrayList<Boolean> conditions;//每本书的模式
    private ArrayList<String> logs;
    private ProgressDialog proDia;
    private ImageView imageView;
    private ImageView imageView1;
    private ListView listView = null;
    private contentAdapter2 adapter = null;
    private Animation alphaAnimation;
    private PageMoveLayout mSlidingLayout;
    private int bookNum;
    private int speed;
    private ArrayList<ChapterAndContent> chapters;
    private ArrayList<String> pages;//用来存储每句话
//    private ArrayList<ChapterAndContent> chapter = null;//一本书的目录
    private TextView text;
    private int num1;
    private boolean typeCondition;//要从数据库读
    private int num2;//用于统计一句话的长度
    private int num3;//用来记录这个书的index
    private TextView aniText;
    private ListView lv;
    private TextView showView ;
    private char[] charArrays;
    private String len = "";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case forType:
                    text.append(len);
                    if(num1==num2){
                        aniText.startAnimation( alphaAnimation );
                    }
                    break;
                case 0:
                    Intent intent = new Intent(ReadActivity2.this,ReadActivity.class);
                    intent.putExtra("bookNum",String.valueOf(ReadActivity2.this.bookNum));
                    ReadActivity2.this.startActivity(intent);
                    ReadActivity2.this.finish();
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read2);

        this.conditions = readThemeFromDatabase();
        this.speeds = readSpeedFromDatabase();
        this.indexs = readBookPageIndexFromDatabase();
        String bookNum=ReadActivity2.this.getIntent().getStringExtra("bookNum");
        if(bookNum.equals("1")){
            this.bookNum = 1;
        }

        this.typeCondition = conditions.get(this.bookNum-1);
        this.speed = speeds.get(this.bookNum-1);
        this.num3 = indexs.get(this.bookNum-1);
        st = readBookStatusFromDatabase();
//        for(int i=0;i<st.size();i++){
//            Log.d("status",String.valueOf(st.get(i)));
//        }
//        Log.d(">>>>>>>>>>>>>>>>>>",">>>>>>>>>>>>>>>>>>>");
        st.set(this.bookNum-1,1);
//        ArrayList<Boolean> st1 = readBookStatusFromDatabase();
//        for(int i=0;i<st.size();i++){
//            Log.d("status",String.valueOf(st.get(i)));
//        }
//        Log.d(">>>>>>>>>>>>>>>>>>",">>>>>>>>>>>>>>>>>>>");
        saveBookStatusToDatabase(st);
        ArrayList<Integer> st2 = readBookStatusFromDatabase();
        for(int i=0;i<st2.size();i++){
            Log.d("status",String.valueOf(st2.get(i)));
        }
        showView=(TextView)findViewById(R.id.showView1);
        text = (TextView) findViewById(R.id.newTextView1);
        text.setText("      ");
        aniText = (TextView)findViewById(R.id.aniTextView);
        text.setTextColor(Color.BLACK);
        aniText.setTextColor(Color.BLACK);
        aniText.setVisibility(View.INVISIBLE);
        popView = (TextView)findViewById(R.id.popView);
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("opoopop",">>>>>>>>>>>>>>>>>>>>>");
                showPopWindow();
            }
        });
        alphaAnimation = new AlphaAnimation( 1, 0 );
        alphaAnimation.setDuration( 300 );
        alphaAnimation.setInterpolator( new LinearInterpolator( ) );
        alphaAnimation.setRepeatCount( Animation.INFINITE );
        alphaAnimation.setRepeatMode( Animation.REVERSE );
        pages = new ArrayList<String>();
        loadtext();
//        int a = 0;
        addimage();
        while (!bookend) {
            readOnclick(rest);
        }
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(num1!=num2){
                    text.setText("      "+pages.get(num3));
                    len = pages.get(num3);
                    aniText.startAnimation( alphaAnimation );
                    num1=num2;
                }else{
                    if(hasNext()) {
                        len = "";
                        text.setText("      ");
                        num3++;
//                text.setText(pages.get(num3));//改换句子了

                        indexs.set(ReadActivity2.this.bookNum-1,num3);
                        saveBookPageIndexToDatabase(indexs);
//                        if (location==1) imageView1.setImageDrawable(null);
//                        else imageView.setImageDrawable(null);
                        if(typeCondition){//开了
                            if(pages.get(num3).contains("\"")){
                                name = getName(num3);
//                                Log.d("名字",name);
//                                Log.d("进来了",">>>>>>>>>>>>>>>>>>>>>>>>>>>");
////                                imageoutput(name);
//                                if(name.equals("Mr.Dursley")){
//                                    Log.d("true",">>>>>>>>>>>>>>>>>>>");
//                                    imageView.setImageResource(R.drawable.mr_dursley);
//                                }else{
//                                    Log.d(">>>>>>>>>>>>>>","false");
//                                }
                                imageoutput(name);
//                                imageoutput("1");
                                name2=name;
                            }
                            typeSpeed(speed, pages.get(num3));

                        }else{
                            if(pages.get(num3).contains("\"")){
                                name = getName(num3);
//                                if(name.equals("Mr.Dursley")){
//                                    Log.d("true",">>>>>>>>>>>>>>>>>>>");
//                                    imageView.setImageResource(R.drawable.mr_dursley);
//                                }else{
//                                    Log.d(">>>>>>>>>>>>>>","false");
//                                }
                                imageoutput(name);
                                name2=name;
                            }
                            text.setText("      "+pages.get(num3));
                        }
                        aniText.clearAnimation();
                        aniText.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
//        pages = new ArrayList<String>();
//        pages.add("I love");
//        pages.add("I love 2 I love I love 2 I love I love 2 I love I love 2 I love I love 2 I love I love 2 I love I love 2 I love I love 2 I love ");
//        pages.add("I love 3");
//        pages.add("I love 4");
//        pages.add("I love 5");
//        pages.add("I love 6");
//        logs.add(pages.get(num3));

        if(typeCondition){//开了
            imageView= (ImageView) findViewById(R.id.imageView111);
            imageView1=(ImageView) findViewById(R.id.imageView222);
//            if (location==1) imageView1.setImageDrawable(null);
//            else imageView.setImageDrawable(null);
            if(pages.get(num3).contains("\"")){
                name = getName(num3);
                imageoutput(name);
                name2=name;
            }
            typeSpeed(speed,pages.get(num3));
        }else{//关了
            imageView= (ImageView) findViewById(R.id.imageView111);
            imageView1=(ImageView) findViewById(R.id.imageView222);
            text.setText("      "+pages.get(num3));
        }
    }
    private void typeSpeed(final int num,final String s){
        new Thread(){
            public void run() {
                try {
                    charArrays = s.toCharArray();
                    num2 = s.length();
//                    Log.d("StringLength",String.valueOf(num2));
//                    Log.d("ArraysLength",String.valueOf(charArrays.length));
                    num1=0;//用于计数
                    for (int i = 0; i < charArrays.length; i++) {
                        sleep(speed);
                        if(len.length()==pages.get(num3).length()){
                            break;
                        }
                        Log.d("22222",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        len = charArrays[i]+"";
                        handler.sendEmptyMessage(forType);
                        num1++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private ArrayList<Integer> readBookPageIndexFromDatabase() {
        //read book page index from database
        List<Page_Number_2> bookPageIndexList = Page_Number_2.listAll(Page_Number_2.class);
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        if (bookPageIndexList != null && bookPageIndexList.size() > 0) {
            for (Page_Number_2 index : bookPageIndexList) {
                indexs.add(index.getIndex());
            }
        } else {
            for (int i = 0; i < 8; i++) {
                indexs.add(0);
            }
        }

        return indexs;

    }

    private void saveBookPageIndexToDatabase(ArrayList<Integer> index) {
        Page_Number_2.deleteAll(Page_Number_2.class);
        for (int content : index) {
            Page_Number_2 item = new Page_Number_2(content);
            item.save();
        }
    }
    public boolean hasNext() {
        return num3 < pages.size()-1;
    }
    private int getStartHight(Context context) {
        Class<?> class1= null;
        Object object = null;
        Field field = null;
        int width = 0, high = 0;
        try {
            class1 = Class.forName("com.android.internal.R$dimen");
            object = class1.newInstance();
            field = class1.getField("status_bar_height");
            width = Integer.parseInt(field.get(object).toString());
            high = context.getResources().getDimensionPixelSize(width);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return high;
    }
    private void showPopWindow(){
        View contentView = LayoutInflater.from(ReadActivity2.this).inflate(R.layout.pop_content_for_special, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_style2);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAtLocation(showView, Gravity.TOP, 500, getStartHight(this));
        TextView  content = (TextView)contentView.findViewById(R.id.textView11);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setText("      "+pages.get(num3));
                len = pages.get(num3);
                num1=num2;//结束线程
                popupWindow.dismiss();
                showContentPopWindow();
            }
        });
        TextView  type = (TextView)contentView.findViewById(R.id.textView22);
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                showTypePopWindow();
            }
        });
        TextView log = (TextView)contentView.findViewById(R.id.textView44);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadActivity2.this.logs  = new ArrayList<String>();
                if(ReadActivity2.this.num3>=10){
                    for(int i=ReadActivity2.this.num3-10;i<ReadActivity2.this.num3;i++){
                        logs.add(ReadActivity2.this.pages.get(i));
                    }
                }else{
                    for(int i=0;i<ReadActivity2.this.num3;i++){
                        logs.add(ReadActivity2.this.pages.get(i));
                    }
                }

//                for(int index1 = ReadActivity2.this.num3;index1>ReadActivity2.this.num3-10;index1--){
//                    if(ReadActivity2.this.pages.size()>=10){
//                        if(index1>=0){
//                            logs.add()}
//                    }else{
//
//                    }
//
//                }
                popupWindow.dismiss();
                showLogPopWindow();
            }
        });
        TextView switch1 = (TextView)contentView.findViewById(R.id.textView55);
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                proDia = new ProgressDialog(ReadActivity2.this);
                proDia.setTitle("loading book...");
                proDia.setMessage("We are trying best...");
                proDia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                proDia.show();
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(300);
                            Message msg = new Message();
                            msg.what=0;
                            handler.sendMessage(msg);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            proDia.dismiss();
                        }
                    }
                }.start();
//                Toast.makeText(ReadActivity2.this, "Not avaiable", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(ReadActivity2.this,ReadActivity.class);
//                intent.putExtra("bookNum",String.valueOf(ReadActivity2.this.bookNum));
//                ReadActivity2.this.startActivity(intent);
            }
        });
    }
    private void showContentPopWindow(){
        View contentView = LayoutInflater.from(ReadActivity2.this).inflate(R.layout.content_pop_window2, null);
        getChapters();
        lscontent = (ListView)contentView.findViewById(R.id.contentOfBook2);
        adapter = new contentAdapter2(this,chapters);
        lscontent.setAdapter(adapter);
        lscontent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int index = 0;
//                text.setText("      "+pages.get(num3));
//                len = pages.get(num3);
//                num1=num2;//结束线程
//                len = pages.get(num3);
//                len = pages.get(num3);
//                len = pages.get(num3);
                if (location==1) imageView1.setImageDrawable(null);
                        else imageView.setImageDrawable(null);
                String chap = adapter.getItem(position).getChapter();
                for (int i = 0; i < pages.size(); i++) {
                    if (pages.get(i).contains(chap)) {
                        index = i;
                        break;
                    }
                }
//                ArrayList<Integer> indexs = readBookPageIndexFromDatabase();
                indexs.set(ReadActivity2.this.bookNum - 1, index);
                saveBookPageIndexToDatabase(indexs);
//                ReadActivity2.this.onRestart();
                num3=index;
                len = "";
//                charArrays=null;
                text.setText("      ");
                if(typeCondition){//开了
                    Log.d("1111",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    typeSpeed(speed, pages.get(num3));
                }else{
                    text.setText("      "+pages.get(num3));
                }
                aniText.clearAnimation();
                aniText.setVisibility(View.INVISIBLE);

            }
        });

//        for(ChapterAndContent e:chapters){
//            Log.d("content",e.getContent());
//            Log.d("chapter",e.getChapter());
//        }
//        Log.d("size",String.valueOf(chapters.size()))
        final PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_style2);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAtLocation(showView, Gravity.TOP, 0, getStartHight(this));
    }
    private void showTypePopWindow(){
        View contentView = LayoutInflater.from(ReadActivity2.this).inflate(R.layout.type_pop_window, null);
        SeekBar speedBar = (SeekBar) contentView.findViewById(R.id.TypeseekBar);
        Switch typeSwitch = (Switch) contentView.findViewById(R.id.switchForTpye);
        speedBar.setMax(1000);
        speedBar.setProgress(speed);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(progress<100){
                    typestate=false;
//                    Toast.makeText(ReadActivity2.this,"no function",Toast.LENGTH_SHORT).show();
                }else{
                    typestate = true;
                    speed = progress;
                    speeds.set(ReadActivity2.this.bookNum-1,speed);
                    saveSpeedToDatabase(speeds);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(typestate){
                Toast.makeText(ReadActivity2.this,"succeed",Toast.LENGTH_SHORT).show();}
                else{
                    Toast.makeText(ReadActivity2.this,"no function",Toast.LENGTH_SHORT).show();
                }
            }
        });
//        lightBar.setProgress(this.progress);
//        lightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                ReadActivity.this.light = (float)seekBar.getProgress()/100;
//                ReadActivity.this.lights.set(ReadActivity.this.bookNum-1,ReadActivity.this.light);
//                saveLightsToDataBase(lights);
//                ReadActivity.this.setScreenBrightness((float) seekBar.getProgress() / 100);
//                ReadActivity.this.progress = progress;
//                progresses.set(ReadActivity.this.bookNum - 1, progress);
//                saveProgressToDatabase(progresses);

        typeSwitch.setChecked(typeCondition);
        typeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){//type模式开启
                    typeCondition=b;
                    ReadActivity2.this.conditions.set(ReadActivity2.this.bookNum - 1, b);
                    Toast.makeText(ReadActivity2.this,"open succeed",Toast.LENGTH_SHORT).show();
                    saveThemeToDatabase(conditions);
                }else{
                    typeCondition=b;
                    text.setText("      "+pages.get(num3));
                    len = pages.get(num3);
//                    aniText.startAnimation( alphaAnimation );
                    num1=num2;
                    aniText.clearAnimation();
                    aniText.setVisibility(View.INVISIBLE);
                    ReadActivity2.this.conditions.set(ReadActivity2.this.bookNum - 1, b);
                    Toast.makeText(ReadActivity2.this,"close succeed",Toast.LENGTH_SHORT).show();
                    saveThemeToDatabase(conditions);

                }
            }
        });
        final PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_style2);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAtLocation(showView, Gravity.TOP, 0, getStartHight(this));
    }
    private void showLogPopWindow(){
        View contentView = LayoutInflater.from(ReadActivity2.this).inflate(R.layout.log_pop_window, null);
        listView = (ListView) contentView.findViewById(R.id.logOFBook);
        LogAdapter la = new LogAdapter(this,logs);
//        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,logs);
        listView.setAdapter(la);
        final PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_style2);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAtLocation(showView, Gravity.TOP, 0, getStartHight(this));
    }

    private ArrayList<Boolean> readThemeFromDatabase() {
        List<type_theme> themes = type_theme.listAll(type_theme.class);
        ArrayList<Boolean> conditions = new ArrayList<Boolean>();
        if (themes != null && themes.size() > 0) {
            for (type_theme index : themes) {
                conditions.add(index.isCondition());
            }
        } else {
            for (int i = 0; i < 8; i++) {
                conditions.add(true);
            }
        }

        return conditions;
    }

    private void saveThemeToDatabase(ArrayList<Boolean> conditions) {
        type_theme.deleteAll(type_theme.class);
        for (boolean content : conditions) {
            type_theme item = new type_theme(content);
            item.save();
        }
    }

    private ArrayList<Integer> readSpeedFromDatabase() {
        List<Speed1> progresses = Speed1.listAll(Speed1.class);
        ArrayList<Integer> progress = new ArrayList<Integer>();
        if (progresses != null && progresses.size() > 0) {
            for (Speed1 index : progresses) {
                progress.add(index.getProgress());
            }
        } else {
            for (int i = 0; i < 8; i++) {
                progress.add(200);
            }
        }
        return progress;

    }

    private void saveSpeedToDatabase(ArrayList<Integer> progress) {
        Speed1.deleteAll(Speed1.class);
        for (int content : progress) {
            Speed1 item = new Speed1(content);
            item.save();
        }
    }

    private ArrayList<Integer> readBookStatusFromDatabase(){
        List<bookStatus> statuses = bookStatus.listAll(bookStatus.class);
        ArrayList<Integer> statuse = new ArrayList<Integer>();
        if (statuses != null && statuses.size() > 0) {
            for (bookStatus index : statuses) {
                statuse.add(index.getStatus());
            }
        } else {
            for (int i = 0; i < 8; i++) {
                statuse.add(0);
            }
        }
        return statuse;
    }
    private void saveBookStatusToDatabase(ArrayList<Integer> sta){
        bookStatus.deleteAll(bookStatus.class);
        for(int e:sta){
            bookStatus item = new bookStatus(e);
            item.save();
        }
    }

    public String getName(int num) {

        String lastsentence = pages.get(num - 1);
        String nextsentence = pages.get(num + 1);
        String last=null;
        int lastnumber=10000000;
        int nextnumber=10000000;
        String next=null;
        int nextloc=0;
        int lastloc=0;
        if (lastsentence.contains(" ")) {
            String[] sentence = lastsentence.split(" ");
            for (int i = sentence.length - 1; i >= 0; i--) {
                for (int j=0;j<image.size();j++)
                {
                    if (sentence[i].contains(image.get(j))){
                        lastnumber=i;
                        lastloc=j;
                        break;
                    }
                }
            }
        }
        if (nextsentence.contains(" "))
        {
            String []sentence=nextsentence.split(" ");
            for (int i=sentence.length-1;i>=0;i--)
            {
                for (int j=0;j<image.size();j++)
                {
                    if (sentence[i].contains(image.get(j))){
                        nextnumber=i;
                        nextloc=j;
                        break;
                    }
                }
            }
        }
        if (lastnumber==10000000 && nextnumber==10000000) return name2;
        else
        if (lastnumber>nextnumber) return image.get(nextloc);
        else return image.get(lastloc);

    }

    public void addimage(){
        image.add("Abbott");image.add("Bones");image.add("Brocklehurst");image.add("Crabbe");
        image.add("Doris");image.add("Draco");image.add("Dudley");image.add("Dumbledore");
        image.add("Figg");image.add("Filch");image.add("Firenze");image.add("Flitwick");
        image.add("George");image.add("Ginny");image.add("Griphook");image.add("Gulpin");
        image.add("Hagrid");image.add("Harry");image.add("Hermione");image.add("Hogwart");
        image.add("Lee");image.add("Malfoy");image.add("Minerva");image.add("Mr.Dursley");
        image.add("Mrs.Dursley");image.add("Neville");image.add("Nicolas");image.add("Norris");
        image.add("Ollivander");image.add("Peeves");image.add("Percy");image.add("Petunia");
        image.add("Potter");image.add("Quirrell");image.add("Ron");image.add("Scabber");
        image.add("Seamus");image.add("Snape");image.add("Thomas");image.add("Turpin");
        image.add("Vernon");image.add("Voldemort");image.add("Weasley");image.add("Wood");
        image.add("Zabini");
    }

    public void imageoutput(String name){
//        Log.d(">>>>>>>>>>>>>>>>>>>>>",">>>>>>>>>>>>>>>>>>>>>>");
//        Log.d(">>>>>>>>>>>>>>>>",name);
//        Log.d("location",String.valueOf(location));
//        if(name.equals("Mr.Dursley")){
//                                    Log.d("true",">>>>>>>>>>>>>>>>>>>");
////                                    imageView.setImageResource(R.drawable.mr_dursley);
//                                }else{
//                                    Log.d(">>>>>>>>>>>>>>","false");
//                                }
        if (location==1){
            imageView1.setImageDrawable(null);
//
            switch(name) {
                case "Abbott":
                    imageView.setImageResource(R.drawable.abbott);
                    break;
                case "Bones":
                    imageView.setImageResource(R.drawable.bones);
                    break;
                case "Brocklehurst":
                    imageView.setImageResource(R.drawable.brocklehurst);
                    break;
                case "Crabbe":
                    imageView.setImageResource(R.drawable.crabbe);
                    break;
                case "Doris":
                    imageView.setImageResource(R.drawable.doris);
                    break;
                case "Draco":
                    imageView.setImageResource(R.drawable.draco);
                    break;
                case "Dudley":
                    imageView.setImageResource(R.drawable.dudley);
                    break;
                case "Dumbledore":
                    imageView.setImageResource(R.drawable.dumbledore);
                    break;
                case "Figg":
                    imageView.setImageResource(R.drawable.figg);
                    break;
                case "Filch":
                    imageView.setImageResource(R.drawable.filch);
                    break;
                case "Firenze":
                    imageView.setImageResource(R.drawable.firenze);
                    break;
                case "Flitwick":
                    imageView.setImageResource(R.drawable.firenze);
                    break;
                case "George":
                    imageView.setImageResource(R.drawable.george);
                    break;
                case "Ginny":
                    imageView.setImageResource(R.drawable.ginny);
                    break;
                case "Griphook":
                    imageView.setImageResource(R.drawable.griphook);
                    break;
                case "Gulpin":
                    imageView.setImageResource(R.drawable.gulpin);
                    break;
                case "Hagrid":
                    imageView.setImageResource(R.drawable.hagrid);
                    break;
                case "Harry":
                    imageView.setImageResource(R.drawable.harry);
                    break;
                case "Hermione":
                    imageView.setImageResource(R.drawable.hermione);
                    break;
                case "Hogwart":
                    imageView.setImageResource(R.drawable.hogwart);
                    break;
                case "Lee":
                    imageView.setImageResource(R.drawable.lee);
                    break;
                case "Malfoy":
                    imageView.setImageResource(R.drawable.malfoy);
                    break;
                case "Minerva":
                    imageView.setImageResource(R.drawable.minerva);
                    break;
                case "Mr.Dursley":
                    imageView.setImageResource(R.drawable.mr_dursley);
                    break;
                case "Mrs.Dursley":
                    imageView.setImageResource(R.drawable.mrs_dursley);
                    break;
                case "Neville":
                    imageView.setImageResource(R.drawable.neville);
                    break;
                case "Nicolas":
                    imageView.setImageResource(R.drawable.nicolas);
                    break;
                case "Norris":
                    imageView.setImageResource(R.drawable.norris);
                    break;
                case "Ollivander":
                    imageView.setImageResource(R.drawable.ollivander);
                    break;
                case "Peeves":
                    imageView.setImageResource(R.drawable.peeves);
                    break;
                case "Percy":
                    imageView.setImageResource(R.drawable.percy);
                    break;
                case "Petunia":
                    imageView.setImageResource(R.drawable.petunia);
                    break;
                case "Potter":
                    imageView.setImageResource(R.drawable.potter);
                    break;
                case "Quirrell":
                    imageView.setImageResource(R.drawable.quirrell);
                    break;
                case "Ron":
                    imageView.setImageResource(R.drawable.ron);
                    break;
                case "Scabber":
                    imageView.setImageResource(R.drawable.scabber);
                    break;
                case "Seamus":
                    imageView.setImageResource(R.drawable.seamus);
                    break;
                case "Snape":
                    imageView.setImageResource(R.drawable.snape);
                    break;
                case "Thomas":
                    imageView.setImageResource(R.drawable.thomas);
                    break;
                case "Turpin":
                    imageView.setImageResource(R.drawable.turpin);
                    break;
                case "Vernon":
                    imageView.setImageResource(R.drawable.vernon);
                    break;
                case "Voldemort":
                    imageView.setImageResource(R.drawable.voldemort);
                    break;
                case "Weasley":
                    imageView.setImageResource(R.drawable.weasley);
                    break;
                case "Wood":
                    imageView.setImageResource(R.drawable.wood);
                    break;
                case "Zabini":
                    imageView.setImageResource(R.drawable.zabini);
                    break;
                default:
                    imageView.setImageResource(R.drawable.question);
                    break;
            }
            location=2;
        }else if (location==2){
            imageView.setImageDrawable(null);
            switch(name) {
                case "Abbott":
                    imageView1.setImageResource(R.drawable.abbott);
                    break;
                case "Bones":
                    imageView1.setImageResource(R.drawable.bones);
                    break;
                case "Brocklehurst":
                    imageView1.setImageResource(R.drawable.brocklehurst);
                    break;
                case "Crabbe":
                    imageView1.setImageResource(R.drawable.crabbe);
                    break;
                case "Doris":
                    imageView1.setImageResource(R.drawable.doris);
                    break;
                case "Draco":
                    imageView1.setImageResource(R.drawable.draco);
                    break;
                case "Dudley":
                    imageView1.setImageResource(R.drawable.dudley);
                    break;
                case "Dumbledore":
                    imageView1.setImageResource(R.drawable.dumbledore);
                    break;
                case "Figg":
                    imageView1.setImageResource(R.drawable.figg);
                    break;
                case "Filch":
                    imageView1.setImageResource(R.drawable.filch);
                    break;
                case "Firenze":
                    imageView1.setImageResource(R.drawable.firenze);
                    break;
                case "Flitwick":
                    imageView1.setImageResource(R.drawable.firenze);
                    break;
                case "George":
                    imageView1.setImageResource(R.drawable.george);
                    break;
                case "Ginny":
                    imageView1.setImageResource(R.drawable.ginny);
                    break;
                case "Griphook":
                    imageView1.setImageResource(R.drawable.griphook);
                    break;
                case "Gulpin":
                    imageView1.setImageResource(R.drawable.gulpin);
                    break;
                case "Hagrid":
                    imageView1.setImageResource(R.drawable.hagrid);
                    break;
                case "Harry":
                    imageView1.setImageResource(R.drawable.harry);
                    break;
                case "Hermione":
                    imageView1.setImageResource(R.drawable.hermione);
                    break;
                case "Hogwart":
                    imageView1.setImageResource(R.drawable.hogwart);
                    break;
                case "Lee":
                    imageView1.setImageResource(R.drawable.lee);
                    break;
                case "Malfoy":
                    imageView1.setImageResource(R.drawable.malfoy);
                    break;
                case "Minerva":
                    imageView1.setImageResource(R.drawable.minerva);
                    break;
                case "Mr.Dursley":
                    imageView1.setImageResource(R.drawable.mr_dursley);
                    break;
                case "Mrs.Dursley":
                    imageView1.setImageResource(R.drawable.mrs_dursley);
                    break;
                case "Neville":
                    imageView1.setImageResource(R.drawable.neville);
                    break;
                case "Nicolas":
                    imageView1.setImageResource(R.drawable.nicolas);
                    break;
                case "Norris":
                    imageView1.setImageResource(R.drawable.norris);
                    break;
                case "Ollivander":
                    imageView1.setImageResource(R.drawable.ollivander);
                    break;
                case "Peeves":
                    imageView1.setImageResource(R.drawable.peeves);
                    break;
                case "Percy":
                    imageView1.setImageResource(R.drawable.percy);
                    break;
                case "Petunia":
                    imageView1.setImageResource(R.drawable.petunia);
                    break;
                case "Potter":
                    imageView1.setImageResource(R.drawable.potter);
                    break;
                case "Quirrell":
                    imageView1.setImageResource(R.drawable.quirrell);
                    break;
                case "Ron":
                    imageView1.setImageResource(R.drawable.ron);
                    break;
                case "Scabber":
                    imageView1.setImageResource(R.drawable.scabber);
                    break;
                case "Seamus":
                    imageView1.setImageResource(R.drawable.seamus);
                    break;
                case "Snape":
                    imageView1.setImageResource(R.drawable.snape);
                    break;
                case "Thomas":
                    imageView1.setImageResource(R.drawable.thomas);
                    break;
                case "Turpin":
                    imageView1.setImageResource(R.drawable.turpin);
                    break;
                case "Vernon":
                    imageView1.setImageResource(R.drawable.vernon);
                    break;
                case "Voldemort":
                    imageView1.setImageResource(R.drawable.voldemort);
                    break;
                case "Weasley":
                    imageView1.setImageResource(R.drawable.weasley);
                    break;
                case "Wood":
                    imageView1.setImageResource(R.drawable.wood);
                    break;
                case "Zabini":
                    imageView1.setImageResource(R.drawable.zabini);
                    break;
                default:
                    imageView1.setImageResource(R.drawable.question);
                    break;
            }
            location=1;
        }
    }

    public void readOnclick(String rest2) {
        rest = rest2;
        rest = rest.trim();
        String line = null;
//        textView = (TextView) findViewById(R.id.textView6);
        complete = true;
        if (is_ex_sentence(rest))//如何剩余句子里还有句子 处理
        {
            if (!rest.substring(0, 1).equals("\""))
                deal_sentence(rest);
        } else {
            while (complete) {
                if (!s.hasNextLine()) {
                    bookend = true;
                    break;
                }

                if (!rest.equals(""))
                    if (rest.substring(rest.length() - 1, rest.length()).equals("\""))
                        rest = rest + " ";
                String[] speak = rest.split("\"");
                if (speak.length == 1) {//没有引号
                    line = s.nextLine();
                    line = line.trim();
                    if (!rest.equals("") && line.equals("")) {
                        pages.add(speak[0]);
                        rest = "";
                        break;
                    }
                    // if (!rest.equals("") && line.equals(""))  {page.add(rest);rest="";break;}
                    if (line.length() == 0 && rest.equals("")) {
                        continue;
                    }
                    if (!line.equals(""))
                        if (line.substring(0, 1).equals("\"")) {
                            if (rest.equals("")) {
                                rest = rest + line;
                                continue;
                            } else {
                                rest = rest + " " + line;
                                deal_sentence(rest);
                                break;
                            }
                        } else {
                            if (rest.equals("")) rest = rest + line;
                            else rest = rest + " " + line;
                        }
                    if (is_ex_sentence(rest)) {
                        deal_sentence(rest);
                        break;
                    }
                } else if (speak.length == 2) {//一句只有一个引号
                    line = s.nextLine();
                    line = line.trim();
                    rest = rest + line;
                    continue;
                } else if (speak.length > 2)//两个引号的处理
                {
                    if (!speak[0].equals("")) {
                        //textView.setText(speak[0]);
                        pages.add(speak[0]);
                        rest = "";
                        for (int i = 1; i < speak.length; i++) rest = rest + "\"" + speak[i];
                        break;
                    }
                    // textView.setText("\""+speak[1].toString()+"\"");
                    cur = "\"" + speak[1].toString() + "\"";
                    pages.add(cur);
                    cur = "";
                    rest = "";
                    rest = rest + speak[2];
                    for (int i = 3; i < speak.length; i++) rest = rest + "\"" + speak[i];
                    rest = rest.trim();
                    break;
                }
            }
        }
    }
    public void loadtext() {
        Resources res = super.getResources();
        InputStream input = res.openRawResource(R.raw.harry1);
        s = new Scanner(input);
    }

    public Boolean is_ex_sentence(String line) {
        if (line.equals("")) return false;
        if (((line.contains(". ")) || (line.contains("!")) || (line.contains("?"))
                || (line.contains("..."))
                || (line.substring(line.length() - 1).equals("."))
        ) && (!line.contains("\""))) {
            return true;
        } else return false;
    }

    public void deal_sentence(String line) {
//        Timer timer = new Timer();
        char[] sentence = line.toCharArray();
        TextView textView;
        textView = (TextView) findViewById(R.id.textView6);
        for (int i = 0; i < sentence.length - 2; i++) {
            if (sentence[i] == '\"' && i != 0) {//引号的处理
                for (int j = 0; j < i; j++) cur = cur + sentence[j];
                rest = "";
                for (int k = i; k < sentence.length; k++) rest = rest + sentence[k];
                //textView.setText(cur);
                pages.add(cur);
                cur = "";
                return;
                //break;
            }
            if ((sentence[i] == '.' && sentence[i + 1] == '.' && sentence[i + 2] == '.')) {//省略号的处理
                for (int j = 0; j <= i + 2; j++) cur = cur + sentence[j];
                rest = "";
                for (int k = i + 3; k < sentence.length; k++) rest = rest + sentence[k];
                // textView.setText("");
                //for (int j=0;j<=i+2;j++)  timer.schedule(new appendtext(String.valueOf(sentence[j]),100));
                //textView.setText(cur);
                pages.add(cur);
                cur = "";
                return;
                //break;
            }
            if ((sentence[i] == '.' && sentence[i + 1] == ' ') || (sentence[i] == '!') || (sentence[i] == '?')
                    || sentence[i] == ';') {//. ! ? ;的处理
                for (int j = 0; j <= i + 1; j++) cur = cur + sentence[j];
                rest = "";
                for (int k = i + 2; k < sentence.length; k++) rest = rest + sentence[k];
                //textView.setText(cur);
                pages.add(cur);
                cur = "";
                return;
                //break;

            }
        }
        if(sentence.length>=2)
        if ((sentence[sentence.length - 2] == '.' && sentence[sentence.length - 1] == ' ') || (sentence[sentence.length - 2] == '!') || (sentence[sentence.length - 2] == '?')
                || sentence[sentence.length - 2] == ';') {//最后是.+空格
            for (int j = 0; j < sentence.length; j++) cur = cur + sentence[j];
            rest = "";
            // textView.setText(cur);
            pages.add(cur);
            cur = "";
        }
        if (sentence.length>=1)
        if (sentence[sentence.length - 1] == '.' || (sentence[sentence.length - 1] == '!') || (sentence[sentence.length - 1] == '?')
                || sentence[sentence.length - 1] == ';') {//最后是. 另接一段
            for (int j = 0; j < sentence.length; j++) cur = cur + sentence[j];
            rest = "";
            //textView.setText(cur);
            pages.add(cur);
            cur = "";
        }

    }

    private void getChapters(){
        if(this.bookNum==1){
            chapters= new ArrayList<ChapterAndContent>();
//            Page page =  new Page(ReadActivity2.this, "Harry1", this.fontSize);
//            page.seperateTextForPage();
//            chapters = page.getChapters();
            for(int i=0;i<pages.size();i++){
                if(pages.get(i).contains("CHAPTER")){
                    ChapterAndContent a  = new ChapterAndContent(pages.get(i),pages.get(i+1));
                    chapters.add(a);
                }
            }
        }
    }

}
