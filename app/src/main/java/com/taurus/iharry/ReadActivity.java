package com.taurus.iharry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.taurus.iharry.sliding.BasicPageMove;
import com.taurus.iharry.sliding.ContentView;
import com.taurus.iharry.sliding.IOnTapListener;
import com.taurus.iharry.sliding.Page;
import com.taurus.iharry.sliding.PageMoveAdapter;
import com.taurus.iharry.sliding.PageMoveLayout;
import com.taurus.iharry.database.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taurus on 16/9/2.
 */
public class ReadActivity extends Activity {

    private PageMoveLayout mSlidingLayout;
    private float fontSize;
    private int color;
    private ArrayList<Integer> st;
//    private boolean bookstatue = true;
    private float light;
    private int progress;
    private ProgressDialog proDia;
    private Handler changeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case 1:
                   ReadActivity.this.fontSize -= 2f;
                   changeFontSize(ReadActivity.this.bookNum);
                   showHint();
                   Toast.makeText(ReadActivity.this,"lower succeed",Toast.LENGTH_SHORT).show();
                   break;
               case 0:
                   ReadActivity.this.fontSize += 2f;
                   changeFontSize(ReadActivity.this.bookNum);
//                   popupWindow.dismiss();
                   showHint();
                   Toast.makeText(ReadActivity.this,"upper succeed",Toast.LENGTH_SHORT).show();
                   break;
               case 10000:
                   Intent itent =  new Intent(ReadActivity.this,ReadActivity2.class);
                   itent.putExtra("bookNum",String.valueOf(ReadActivity.this.bookNum));
                   ReadActivity.this.startActivity(itent);
                   ReadActivity.this.finish();
           }
        }
    };
    private boolean nightCondition;
    private int bookNum;
    private ArrayList<String> pages;
    private ArrayList<Float> lights;
    private ArrayList<Float> sizes;//每本书的字体大小
    private ArrayList<Integer> colors;//每本书的背景
    private ArrayList<Boolean> conditions;//每本书的模式
    private ArrayList<Integer> progresses;//每本书的亮度
    private String chehckText = null;
    private View showView = null;
    private boolean hasChangedSize = false;
    private contentAdapter adapter = null;
    private ArrayList<ChapterAndContent> chapter = null;//一本书的目录
    private ListView listView = null;
    private boolean stillChange = false;
//    private ProgressDialog proDia;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if(nightCondition){
//            this.setTheme(R.style.NightTheme);
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
//        View decorView = getWindow().getDecorView();
//        int uiNavigation = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        decorView.setSystemUiVisibility();


        mSlidingLayout = (PageMoveLayout) findViewById(R.id.page_container);
        mSlidingLayout.setOnTapListener(new IOnTapListener() {
            @Override
            public void onSingleTap(MotionEvent event) {

                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                int x = (int) event.getX();
                int y = (int) event.getY();
                int leftBoundry = screenWidth / 2 - 150;
                int rightBoundry = screenWidth / 2 + 150;
                int topBoundry = screenHeight / 2 - 150;
                int bottomBoundry = screenHeight / 2 + 150;
//                stillChange = false;
                hasChangedSize = false;
                if ((x <= rightBoundry) && (x >= leftBoundry) && (y >= topBoundry) && (y <= bottomBoundry)) {
//                    LayoutInflater layoutInflater  = LayoutInflater.from(ReadActivity.this);
//                    popView = layoutInflater.inflate(R.layout.pop_content,null);
//                    popWin  = new PopupWindow(popView,200,200);
//                    popWin.showAsDropDown(popView);
////                    showPopWindow();
                    LayoutInflater layoutInflater = LayoutInflater.from(ReadActivity.this);
                    View contentView = layoutInflater.inflate(R.layout.content_layout, null);
                    showView = contentView;
//                    showView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//                        @Override
//                        public void onSystemUiVisibilityChange(int i) {
//                            Log.d("test","visibaleChanged");
//                        }
//                    });
                    showPopWindow(showView);
//                    showPopWindow2(contentView);
//                    mSlidingLayout.PopContent();
                } else {
                    if ((x > rightBoundry) || (((x > screenWidth / 2) && (x < rightBoundry)) && ((y > bottomBoundry) || (y < topBoundry)))) {
                        mSlidingLayout.pageNext();
                    } else if ((x < leftBoundry) || ((x <= screenWidth / 2) && (x > leftBoundry)) && ((y > bottomBoundry) || (y < topBoundry))) {
                        mSlidingLayout.pagePrevious();
                    }
                }
            }
        });
        this.sizes = readBookFontSizeFromDatabase();
        this.colors = readBookBackgroundFromDatabase();
        this.conditions = readThemeFromDatabase();
        this.progresses = readLightProgressFromDatabase();
        this.lights=readLightsFromDatabase();
        String bNum = ReadActivity.this.getIntent().getStringExtra("bookNum");
        startReading(bNum);
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
    }


    class NormalSlidingAdapter extends PageMoveAdapter<String> {
        private int index;
        private int bookNum;
        private int colorNum;
        private int background;
        //        private float size;
        private String color;
        ArrayList<String> colors;
        ArrayList<Integer> indexs;
        //        ArrayList<Float> sizes;
        private Page page;
        private ArrayList<String> pages;

        public NormalSlidingAdapter(int bookNum, ArrayList<String> pages, Page page, int colorNum) {
            if (colorNum != 0) {
                this.colorNum = colorNum;
            }
            this.bookNum = bookNum;
            this.pages = pages;
//            this.sizes = readBookFontSizeFromDatabase();
//            this.size = this.sizes.get(this.bookNum-1);
            this.indexs = readBookPageIndexFromDatabase();
            this.index = this.indexs.get(this.bookNum - 1);
        }

//        Page page = new Page(ReadActivity.this,"Harry Potter and the Sorcerer's Stone.txt",ReadActivity.this.fontSize);
//        ArrayList<String> pages = checkDabaseForContent(page);
//        ArrayList<String> pages = page.seperateTextforParagraph();
//        ArrayList<String> pages = page.seperateParagraphForLine2();
//        private int index = 0;

//        private final int originalIndex = this.index;

        @Override
        public View getView(View contentView, String s) {
            if (contentView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(ReadActivity.this);
                contentView = layoutInflater.inflate(R.layout.content_layout, null);
            }
            ContentView cv = (ContentView) contentView.findViewById(R.id.contentView);
            cv.setTextSize(ReadActivity.this.fontSize);
            cv.setContent(s);
            if (this.colorNum == 1) {
                cv.setBackgroundResource(R.color.ivory);
            } else if (this.colorNum == 2) {
                cv.setBackgroundResource(R.color.cornsilk);
            } else if (this.colorNum == 3) {
                cv.setBackgroundResource(R.color.moccasin);
            } else if (this.colorNum == 4) {
                cv.setBackgroundResource(R.color.nightBackgroundColor);
                cv.setNightCondition(true);
            }
//            cv.setBackgroundColor(getResources().getColor(R.color.Green));
//            cv.setBackgroundColor();
//            TextView tv_content = (TextView) contentView.findViewById(R.id.contentView);
//            tv_content.setText(s);
            return contentView;
        }


        @Override
        public boolean hasNext() {
            return index < pages.size()-1;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        protected void computeNext() {
            ++index;
        }

        @Override
        protected void computePrevious() {
            --index;
        }

        @Override
        public String getNext() {
            this.indexs.set(this.bookNum - 1, index);
            saveBookPageIndexToDatabase(indexs);
            return pages.get(index + 1);

        }

        @Override
        public String getPrevious() {
            indexs.set(this.bookNum - 1, index);
            saveBookPageIndexToDatabase(indexs);
            return pages.get(index - 1);
        }

        @Override
        public String getCurrent() {
            this.indexs.set(this.bookNum - 1, index);
            saveBookPageIndexToDatabase(indexs);
            return pages.get(index);
        }
    }

    private ArrayList<Integer> readBookPageIndexFromDatabase() {
        //read book page index from database
        List<Page_Number> bookPageIndexList = Page_Number.listAll(Page_Number.class);
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        if (bookPageIndexList != null && bookPageIndexList.size() > 0) {
            for (Page_Number index : bookPageIndexList) {
                indexs.add(index.getIndex());
            }
        } else {
            //set the original index of books
            for (int i = 0; i < 8; i++) {
                indexs.add(0);
            }
        }

        return indexs;

    }

    private void saveBookPageIndexToDatabase(ArrayList<Integer> index) {
        Page_Number.deleteAll(Page_Number.class);
        for (int content : index) {
            Page_Number item = new Page_Number(content);
            item.save();
        }
    }

    private ArrayList<Float> readBookFontSizeFromDatabase() {
        List<Font_size> bookFontSize = Font_size.listAll(Font_size.class);
        ArrayList<Float> sizes = new ArrayList<Float>();
        if (bookFontSize != null && bookFontSize.size() > 0) {
            for (Font_size index : bookFontSize) {
                sizes.add(index.getSize());
            }
        } else {
            //set the original index of books
            for (int i = 0; i < 8; i++) {
                sizes.add(50f);
            }
        }
        return sizes;
    }

    private void saveBookFontSizeToDatabase(ArrayList<Float> sizes) {
        Font_size.deleteAll(Font_size.class);
        for (float content : sizes) {
            Font_size item = new Font_size(content);
            item.save();
        }
    }
    private ArrayList<Float> readLightsFromDatabase(){
        List<light_content> light2 = light_content.listAll(light_content.class);
        ArrayList<Float> lights = new ArrayList<Float>();
        if (light2 != null && light2.size() > 0) {
            for (light_content index : light2) {
                lights.add(index.getCondition());
            }
        } else {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            for (int i = 0; i < 8; i++) {
                lights.add(layoutParams.screenBrightness);
            }
        }
        return lights;
    }

    private void saveLightsToDataBase(ArrayList<Float> lights){
        light_content.deleteAll(light_content.class);
            for(float content:lights){
                light_content item = new light_content(content);
                item.save();
            }

    }

    private ArrayList<Integer> readBookBackgroundFromDatabase() {
        List<Background_color> background_colors = Background_color.listAll(Background_color.class);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        if (background_colors != null && background_colors.size() > 0) {
            for (Background_color index : background_colors) {
                colors.add(index.getColor());
            }
        } else {
            //set the original index of books
            for (int i = 0; i < 8; i++) {
                colors.add(1);
            }

        }
        return colors;
    }

    private void saveBookBackgroundToDatabase(ArrayList<Integer> colors) {
        Background_color.deleteAll(Background_color.class);
        for (int content : colors) {
            Background_color item = new Background_color(content);
            item.save();
        }
    }

    private ArrayList<Boolean> readThemeFromDatabase() {
        List<Theme> themes = Theme.listAll(Theme.class);
        ArrayList<Boolean> conditions = new ArrayList<Boolean>();
        if (themes != null && themes.size() > 0) {
            for (Theme index : themes) {
                conditions.add(index.isCondition());
            }
        } else {
            for (int i = 0; i < 8; i++) {
                conditions.add(false);
            }
        }

        return conditions;
    }

    private void saveThemeToDatabase(ArrayList<Boolean> conditions) {
        Theme.deleteAll(Theme.class);
        for (boolean content : conditions) {
            Theme item = new Theme(content);
            item.save();
        }
    }

    private ArrayList<Integer> readLightProgressFromDatabase() {
        List<light_progress> progresses = light_progress.listAll(light_progress.class);
        ArrayList<Integer> progress = new ArrayList<Integer>();
        if (progresses != null && progresses.size() > 0) {
            for (light_progress index : progresses) {
                progress.add(index.getProgress());
            }
        } else {
            for (int i = 0; i < 8; i++) {
                progress.add(0);
            }
        }
        return progress;

    }

    private void saveProgressToDatabase(ArrayList<Integer> progress) {
        light_progress.deleteAll(light_progress.class);
        for (int content : progress) {
            light_progress item = new light_progress(content);
            item.save();
        }
    }

    private void showPopWindow(final View view1) {
        View contentView = LayoutInflater.from(ReadActivity.this).inflate(R.layout.pop_content, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_style);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        checkNavigationBar(popupWindow,view1);

        ImageButton font = (ImageButton) contentView.findViewById(R.id.font);
        ImageButton content = (ImageButton) contentView.findViewById(R.id.contentOF);
//        ImageButton night = (ImageButton) contentView.findViewById(R.id.night);
        ImageButton light = (ImageButton) contentView.findViewById(R.id.light_button);
        ImageButton switch1 = (ImageButton)contentView.findViewById(R.id.Switch);
        font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d("调整","是");
                popupWindow.dismiss();
                showFontPopWindow(view1);
            }
        });
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
//                View temp = LayoutInflater.from(ReadActivity.this).inflate(R.layout.content_layout,null);
                showContentPopWindow(view1);
            }
        });
//        night.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(nightCondition==false){
//                    nightCondition= true;
//                    ArrayList<String> page2 =readBookPagesFromDatabase();
//                    Page p = new Page(ReadActivity.this,page2,ReadActivity.this.fontSize);
//                    mSlidingLayout.setPageMoveAdapter(new TestSlidingAdapter(bookNum, page2, p,4));
//                    ReadActivity.this.conditions.set(ReadActivity.this.bookNum-1,nightCondition);
//                    saveThemeToDatabase(ReadActivity.this.conditions);
////                    ReadActivity.this.recreate();
//                }else if(nightCondition==true){
//                    nightCondition=false;
//                  ArrayList<String> page2 =readBookPagesFromDatabase();
//                    Page p = new Page(ReadActivity.this,page2,ReadActivity.this.fontSize);
//                    mSlidingLayout.setPageMoveAdapter(new TestSlidingAdapter(bookNum, page2, p,color));
//                    ReadActivity.this.conditions.set(ReadActivity.this.bookNum-1,nightCondition);
//                    saveThemeToDatabase(ReadActivity.this.conditions);
//                }
////                if(nightCondition==0){//白天模式
////                    nightCondition=1;
////
////                }
//            }
//        });
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                showLightPopWindow(view1);
            }
        });
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ReadActivity.this.bookNum==1){
                    popupWindow.dismiss();
//                    popupWindow.dismiss();
                    proDia = new ProgressDialog(ReadActivity.this);
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
                                msg.what=10000;
                                changeHandler.sendMessage(msg);
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }finally {
                                proDia.dismiss();
                            }
                        }
                    }.start();
//                    Intent itent =  new Intent(ReadActivity.this,ReadActivity2.class);
//                    itent.putExtra("bookNum",String.valueOf(ReadActivity.this.bookNum));
//                    ReadActivity.this.startActivity(itent);
////                    ReadActivity.this.finish();
                }
                else{
                    Toast.makeText(ReadActivity.this,"The schema is not available right now",Toast.LENGTH_SHORT).show();
                }

            }
        });


//        popupWindow1.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Log.d("哈哈","正");
//
//                return false;
//            }
//        });

//        popupWindow.setBackgroundDrawable();
    }

    private void showFontPopWindow(View view) {
        View contentView = LayoutInflater.from(ReadActivity.this).inflate(R.layout.font_pop_window, null);
        ImageButton add = (ImageButton) contentView.findViewById(R.id.imageButton_bigger);
        ImageButton minus = (ImageButton) contentView.findViewById(R.id.imageButton_smaller);
        ImageButton origialColor = (ImageButton) contentView.findViewById(R.id.originalColor);
        ImageButton change1 = (ImageButton) contentView.findViewById(R.id.change1);
        ImageButton change2 = (ImageButton) contentView.findViewById(R.id.change2);

//        ImageButton font = (ImageButton) contentView.findViewById(R.id.font);
//        font.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("调整","是");
//            }
//        });
        final PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_style);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        checkNavigationBar(popupWindow,view);
//        if(status){
//            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//        }else{
//        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, getNavigationHight(this));}


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proDia = new ProgressDialog(ReadActivity.this);
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
                            changeHandler.sendMessage(msg);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            proDia.dismiss();
                        }
//                Message msg = new Message();
//                msg.what=1;
//                transferHandler.sendMessage(msg);
                    }
                }.start();
//                ReadActivity.this.fontSize += 2f;
//                changeFontSize(ReadActivity.this.bookNum);
                popupWindow.dismiss();
//                showHint();


            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proDia = new ProgressDialog(ReadActivity.this);
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
                            msg.what=1;
                            changeHandler.sendMessage(msg);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            proDia.dismiss();
                        }
//                Message msg = new Message();
//                msg.what=1;
//                transferHandler.sendMessage(msg);
                    }
                }.start();
//                ReadActivity.this.fontSize -= 2f;
//                changeFontSize(ReadActivity.this.bookNum);
                popupWindow.dismiss();
//                showHint();
            }
        });

        origialColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBackground(ReadActivity.this.bookNum, 1);
            }
        });

        change1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBackground(ReadActivity.this.bookNum, 2);
            }
        });
        change2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBackground(ReadActivity.this.bookNum, 3);
            }
        });


    }

    private void showContentPopWindow(View view) {
        View contentView = LayoutInflater.from(ReadActivity.this).inflate(R.layout.content_pop_window, null);
        listView = (ListView) contentView.findViewById(R.id.contentOfBook);
        adapter = new contentAdapter(this, chapter);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int index = 0;
                String chap = adapter.getItem(position).getChapter();
//                    ArrayList<String> pages = readBookPagesFromDatabase();
                for (int i = 0; i < pages.size(); i++) {
                    if (pages.get(i).contains(chap)) {
                        index = i;
                        break;
                    }
                }
                Log.d("index", String.valueOf(index));
                ArrayList<Integer> indexs = readBookPageIndexFromDatabase();
                indexs.set(ReadActivity.this.bookNum - 1, index);
                saveBookPageIndexToDatabase(indexs);
                Page p = new Page(ReadActivity.this, pages, ReadActivity.this.fontSize);
                if (nightCondition) {//夜间模式
                    ReadActivity.this.mSlidingLayout.setPageMoveAdapter(new NormalSlidingAdapter(ReadActivity.this.bookNum, pages, p, 4));
                    Toast.makeText(ReadActivity.this,"succeed",Toast.LENGTH_SHORT).show();
                } else {
                    ReadActivity.this.mSlidingLayout.setPageMoveAdapter(new NormalSlidingAdapter(ReadActivity.this.bookNum, pages, p, color));
                    Toast.makeText(ReadActivity.this,"succeed",Toast.LENGTH_SHORT).show();
                }

            }
        });
        final PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_style);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        checkNavigationBar(popupWindow,view);
//
//            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//
//            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, getNavigationHight(this));




    }

    private void showLightPopWindow(View view) {
        View contentView = LayoutInflater.from(ReadActivity.this).inflate(R.layout.pop_light_window, null);
        SeekBar lightBar = (SeekBar) contentView.findViewById(R.id.LightseekBar);
        Switch nightSwitch = (Switch) contentView.findViewById(R.id.switchForNight);
//        ToggleButton nightSwitch = (ToggleButton)contentView.findViewById(R.id.switchForNight);
        lightBar.setMax(100);
        lightBar.setProgress(this.progress);
        lightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ReadActivity.this.light = (float)seekBar.getProgress()/100;
                ReadActivity.this.lights.set(ReadActivity.this.bookNum-1,ReadActivity.this.light);
                saveLightsToDataBase(lights);
                ReadActivity.this.setScreenBrightness((float) seekBar.getProgress() / 100);
                ReadActivity.this.progress = progress;
                progresses.set(ReadActivity.this.bookNum - 1, progress);
                saveProgressToDatabase(progresses);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(ReadActivity.this,"succeed",Toast.LENGTH_SHORT).show();
            }
        });
        nightSwitch.setChecked(nightCondition);
        nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {//夜间模式开启
//                    ArrayList<String> page2 =readBookPagesFromDatabase();
                    Page p = new Page(ReadActivity.this, pages, ReadActivity.this.fontSize);
                    mSlidingLayout.setPageMoveAdapter(new NormalSlidingAdapter(bookNum, pages, p, 4));
                    ReadActivity.this.conditions.set(ReadActivity.this.bookNum - 1, b);
                    saveThemeToDatabase(ReadActivity.this.conditions);
                    nightCondition = b;
                    Toast.makeText(ReadActivity.this,"succeed",Toast.LENGTH_SHORT).show();

                } else {//夜间模式关闭
//                    ArrayList<String> page2 =readBookPagesFromDatabase();
                    Page p = new Page(ReadActivity.this, pages, ReadActivity.this.fontSize);
                    mSlidingLayout.setPageMoveAdapter(new NormalSlidingAdapter(bookNum, pages, p, color));
                    ReadActivity.this.conditions.set(ReadActivity.this.bookNum - 1, b);
                    saveThemeToDatabase(ReadActivity.this.conditions);
                    nightCondition = b;
                    Toast.makeText(ReadActivity.this,"succeed",Toast.LENGTH_SHORT).show();


                }
            }
        });

//        MySwitchButton msb = (MySwitchButton)contentView.findViewById(R.id.mySwitchButton);
//
//        MySwitchButton.OnListener on = new MySwitchButton.OnListener() {
//            @Override
//            public void on() {
//                Toast.makeText(ReadActivity.this,"开",Toast.LENGTH_SHORT).show();
//
//            }
//        };
//        MySwitchButton.OffListener off = new MySwitchButton.OffListener() {
//            @Override
//            public void off() {
//                Toast.makeText(ReadActivity.this,"关",Toast.LENGTH_SHORT).show();
//
//            }
//        };
//        msb.setOnListener(on);
//        msb.setOffListener(off);


        final PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_style);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        checkNavigationBar(popupWindow,view);
//        if(status){
//            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//        }else{
//            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, getNavigationHight(this));
//        }


    }

    private void showHint() {
        if (hasChangedSize) {
            View contentView2 = LayoutInflater.from(ReadActivity.this).inflate(R.layout.size_change, null);
            float high = getStringPostion()[0];
            float width = getStringPostion()[1];//1 width
            final PopupWindow popupWindow = new PopupWindow(contentView2, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setAnimationStyle(R.style.popWindow_style);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popupWindow.showAsDropDown(showView, (int) width, (int) high);
        }
    }

    private void setScreenBrightness(float num) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = num;
        super.getWindow().setAttributes(layoutParams);
    }

    private void changeFontSize(int bookNum) {

        hasChangedSize = true;
        ArrayList<String> outPages1 = null;
        ArrayList<String> tempPages = new ArrayList<String>();
        ArrayList<Integer> indexs = readBookPageIndexFromDatabase();
//        ArrayList<String> page2 =readBookPagesFromDatabase();
        int index = indexs.get(bookNum - 1);//拿到这本书的这页的index
        int index2 = 0;
//        String page = page2.get(index);//拿到这本书这页的内容
        String page = pages.get(index);//拿到这本书这页的内容
        if (!stillChange) {
            chehckText = page.substring(0, 50);
            stillChange = true;
        }//截取头50个字符用于匹配
//        for(int i=0;i<page2.size();i++){
//            tempPages.add(page2.get(i));
//        }
        for (int i = 0; i < pages.size(); i++) {
            tempPages.add(pages.get(i));
        }
        Page p = new Page(ReadActivity.this, tempPages, this.fontSize);
        outPages1 = p.changeFontForPage();
        this.pages = outPages1;
//        saveBookPagesToDatabase(outPages1); //把现在的这些页存进数据库，之前的都删了
        for (int i = 0; i < outPages1.size(); i++) { //找到这些页里包含那句话的
            if (outPages1.get(i).indexOf(chehckText.trim()) > 0) {  //必须在当前页的最开始的段是完整段的时候才找的到
                index2 = i;
                Log.d("really index==========", String.valueOf(index2));
                break;
            }
        }
        indexs.set(bookNum - 1, index2);
        saveBookPageIndexToDatabase(indexs);

        Log.d("checkString", chehckText);
        Log.d("outPageSize", String.valueOf(outPages1.size()));
        if (nightCondition) {//夜间模式
            mSlidingLayout.setPageMoveAdapter(new NormalSlidingAdapter(bookNum, outPages1, p, 4));

            this.sizes.set(bookNum - 1, this.fontSize);
            saveBookFontSizeToDatabase(this.sizes);
        } else {
            mSlidingLayout.setPageMoveAdapter(new NormalSlidingAdapter(bookNum, outPages1, p, this.color));
            this.sizes.set(bookNum - 1, this.fontSize);
            saveBookFontSizeToDatabase(this.sizes);
        }


    }

    private void changeBackground(int bookNum, int color) {

//        ArrayList<String> page2 =readBookPagesFromDatabase();
//        Page p = new Page(ReadActivity.this,page2,this.fontSize);
        int temp = this.color;
        this.color = color;
        Page p = new Page(ReadActivity.this, pages, this.fontSize);
//            Log.d("hahahahaha","++++++++++++++++++++++++++++++++++");
        if (nightCondition) {//夜间模式
            Toast.makeText(this, "night schema", Toast.LENGTH_SHORT).show();
        } else {
//            mSlidingLayout.setPageMoveAdapter(new TestSlidingAdapter(bookNum, page2, p,color));
            if(temp==this.color){
                Toast.makeText(this, "It is the same", Toast.LENGTH_SHORT).show();
            }else{
            mSlidingLayout.setPageMoveAdapter(new NormalSlidingAdapter(bookNum, pages, p, color));
            this.colors.set(bookNum - 1, color);
            saveBookBackgroundToDatabase(this.colors);
            Toast.makeText(ReadActivity.this,"succeed",Toast.LENGTH_SHORT).show();}
        }
    }

    private float[] getStringPostion() {
//        ArrayList<String> pages = readBookPagesFromDatabase();
        ArrayList<Integer> indexs = readBookPageIndexFromDatabase();
        boolean find = false;
        String page = pages.get(indexs.get(this.bookNum - 1));//找到含这句话的那页
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(this.fontSize);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float wordTop = fontMetrics.ascent;
        float wordBottom = fontMetrics.descent;
        float wordHeight = wordBottom - wordTop;
        float leading = 1.0f;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width1 = displayMetrics.widthPixels;
        String line = "";
        int indexOfWidth = 0;
        int num = 0;
        StaticLayout staticLayout = new StaticLayout(page, textPaint, width1, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true);
        int lineCount = staticLayout.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            num++;
            int lineStartChar = staticLayout.getLineStart(i);
            int lineEndChar = staticLayout.getLineEnd(i);
            line = page.substring(lineStartChar, lineEndChar);
            Log.d("eachLine" + num, line);
            if (line.contains(chehckText.substring(0, 10).trim())) {
                indexOfWidth = line.indexOf(chehckText.substring(0, 10).trim());
                find = true;
                break;
            }
        }
        if (!find) {
            for (int i = 0; i < lineCount; i++) {
                num++;
                int lineStartChar = staticLayout.getLineStart(i);
                int lineEndChar = staticLayout.getLineEnd(i);
                line = page.substring(lineStartChar, lineEndChar);
                if (line.contains(chehckText.substring(0, 9).trim())) {
                    indexOfWidth = line.indexOf(chehckText.substring(0, 9).trim());
                    find = true;
                    break;
                }
            }
        }
        if (!find) {
            for (int i = 0; i < lineCount; i++) {
                num++;
                int lineStartChar = staticLayout.getLineStart(i);
                int lineEndChar = staticLayout.getLineEnd(i);
                line = page.substring(lineStartChar, lineEndChar);
                if (line.contains(chehckText.substring(0, 8).trim())) {
                    indexOfWidth = line.indexOf(chehckText.substring(0, 8).trim());
                    find = true;
                    break;
                }
            }
        }
        if (!find) {
            for (int i = 0; i < lineCount; i++) {
                num++;
                int lineStartChar = staticLayout.getLineStart(i);
                int lineEndChar = staticLayout.getLineEnd(i);
                line = page.substring(lineStartChar, lineEndChar);
                if (line.contains(chehckText.substring(0, 7).trim())) {
                    indexOfWidth = line.indexOf(chehckText.substring(0, 7).trim());
                    find = true;
                    break;
                }
            }
        }
        if (!find) {
            for (int i = 0; i < lineCount; i++) {
                num++;
                int lineStartChar = staticLayout.getLineStart(i);
                int lineEndChar = staticLayout.getLineEnd(i);
                line = page.substring(lineStartChar, lineEndChar);
//                Log.d("eachLine"+num,line);
                if (line.contains(chehckText.substring(0, 6).trim())) {
                    indexOfWidth = line.indexOf(chehckText.substring(0, 6).trim());
                    find = true;
                    break;
                }
            }
        }
        if (!find) {
            for (int i = 0; i < lineCount; i++) {
                num++;
                int lineStartChar = staticLayout.getLineStart(i);
                int lineEndChar = staticLayout.getLineEnd(i);
                line = page.substring(lineStartChar, lineEndChar);
                if (line.contains(chehckText.substring(0, 5).trim())) {
                    indexOfWidth = line.indexOf(chehckText.substring(0, 5).trim());
                    find = true;
                    break;
                }
            }
        }
        if (!find) {
            for (int i = 0; i < lineCount; i++) {
                num++;
                int lineStartChar = staticLayout.getLineStart(i);
                int lineEndChar = staticLayout.getLineEnd(i);
                line = page.substring(lineStartChar, lineEndChar);
                if (line.contains(chehckText.substring(0, 4).trim())) {
                    indexOfWidth = line.indexOf(chehckText.substring(0, 4).trim());
                    find = true;
                    break;
                }
            }
        }
        float priorWidth = textPaint.measureText(line.substring(0, indexOfWidth));
        float high = (num + 1) * wordHeight + (num - 1) * leading;
        Log.d("lineNum", String.valueOf(num));
        Log.d("width", String.valueOf(priorWidth));
        Log.d("checkString", chehckText.substring(0, 10));
        Log.d("wordHight", String.valueOf(wordHeight));
        Log.d("high", String.valueOf(high));
        Log.d("Current String", chehckText);
        float[] position = new float[2];
        position[0] = high;//高度
        position[1] = priorWidth;//宽度
        return position;

    }

    private void startReading(String bNum) {
        Page page = null;
        if (bNum.equals("1")) {
            this.bookNum = 1;
            this.fontSize = this.sizes.get(this.bookNum - 1);
            page = new Page(ReadActivity.this, "Harry1", this.fontSize);
        } else if (bNum.equals("2")) {
            this.bookNum = 2;
            this.fontSize = this.sizes.get(this.bookNum - 1);
            page = new Page(ReadActivity.this, "Harry2", this.fontSize);
        } else if (bNum.equals("3")) {
            this.bookNum = 3;
            this.fontSize = this.sizes.get(this.bookNum - 1);
            page = new Page(ReadActivity.this, "Harry3", this.fontSize);
        } else if (bNum.equals("4")) {
            this.bookNum = 4;
            this.fontSize = this.sizes.get(this.bookNum - 1);
            page = new Page(ReadActivity.this, "Harry4", this.fontSize);
        } else if (bNum.equals("5")) {
            this.bookNum = 5;
            this.fontSize = this.sizes.get(this.bookNum - 1);
            page = new Page(ReadActivity.this, "Harry5", this.fontSize);
        } else if (bNum.equals("6")) {
            this.bookNum = 6;
            this.fontSize = this.sizes.get(this.bookNum - 1);
            page = new Page(ReadActivity.this, "Harry6", this.fontSize);
        } else if (bNum.equals("7")) {
            this.bookNum = 7;
            this.fontSize = this.sizes.get(this.bookNum - 1);
            page = new Page(ReadActivity.this, "Harry7", this.fontSize);
        } else if (bNum.equals("8")) {
            this.bookNum = 8;
            this.fontSize = this.sizes.get(this.bookNum - 1);
            page = new Page(ReadActivity.this, "Harry8", this.fontSize);
        }
        st = readBookStatusFromDatabase();
        for(int i=0;i<st.size();i++){
            Log.d("status",String.valueOf(st.get(i)));
        }
        Log.d(">>>>>>>>>>>>>>>>>>",">>>>>>>>>>>>>>>>>>>");
        st.set(this.bookNum-1,0);
        for(int i=0;i<st.size();i++){
            Log.d("status",String.valueOf(st.get(i)));
        }
        Log.d(">>>>>>>>>>>>>>>>>>",">>>>>>>>>>>>>>>>>>>");
        saveBookStatusToDatabase(st);
        st = readBookStatusFromDatabase();
        for(int i=0;i<st.size();i++){
            Log.d("status",String.valueOf(st.get(i)));
        }
        Log.d(">>>>>>>>>>>>>>>>>>",">>>>>>>>>>>>>>>>>>>");
        this.progress = this.progresses.get(this.bookNum - 1);
        this.color = this.colors.get(this.bookNum - 1);
//        this.pages = checkDabaseForContent(page);
        this.light=this.lights.get(this.bookNum-1);
        this.pages = page.seperateTextForPage();

        chapter = page.getChapters();
        this.nightCondition = conditions.get(bookNum - 1);//当前的conditionTheme
        if (nightCondition) {
            setScreenBrightness(this.light);
            mSlidingLayout.setPageMoveAdapter(new NormalSlidingAdapter(this.bookNum, this.pages, page, 4));

        } else {
            setScreenBrightness(this.light);
            mSlidingLayout.setPageMoveAdapter(new NormalSlidingAdapter(this.bookNum, this.pages, page, this.color));
        }
        Log.d("状态栏高度",String.valueOf(getNavigationHight(this)));
        mSlidingLayout.setPageMove(new BasicPageMove());
    }

    private int getNavigationHight(Context context) {
        Class<?> class1= null;
        Object object = null;
        Field field = null;
        int width = 0, high = 0;
        try {
            class1 = Class.forName("com.android.internal.R$dimen");
            object = class1.newInstance();
            field = class1.getField("navigation_bar_height");
            width = Integer.parseInt(field.get(object).toString());
            high = context.getResources().getDimensionPixelSize(width);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return high;
    }

    private void checkNavigationBar(PopupWindow popupWindow, View view){
        //虚拟导航栏是否开启
        Display display = this.getWindowManager().getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(realDisplayMetrics);
        }
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        boolean hasNavigationBar = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
      if(hasNavigationBar){
                popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, getNavigationHight(this));
        }else{
          popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//          popupWindow.showAtLocation(view,Gravity.TOP,0,0);
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
}









