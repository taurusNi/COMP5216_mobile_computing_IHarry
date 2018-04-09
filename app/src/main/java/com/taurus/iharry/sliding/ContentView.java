package com.taurus.iharry.sliding;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.taurus.iharry.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by taurus on 16/9/9.
 */
public class ContentView extends View {
//    private static final String Dir = "/mnt/sdcard/";
//    private ArrayList<String> wholeTxt= null;
//    private String filename=null;
    private  StaticLayout staticLayout;
    private boolean nightCondition=false;
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    private String content=null;
    private TextPaint textPaint;
    private float textSize = 50f;
    private int width1=displayMetrics.widthPixels;
    private int height1=displayMetrics.heightPixels;

    public ContentView(Context context) {
        super(context);
        this.textPaint = new TextPaint();

    }
    public int getScreenWidth() {
        int screenWidth = this.width1;
       return screenWidth;
    }
    public int getScreenHeight() {
        int screenHeight = this.height1;
        return getScreenHeight();
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.textPaint = new TextPaint();
    }

    public ContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.textPaint = new TextPaint();

    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void append(String content){
        this.content = this.content+content;
    }

    public void setNightCondition(boolean nightCondition) {
        this.nightCondition = nightCondition;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textPaint.setTextSize(this.textSize);
        if(!nightCondition){
        textPaint.setColor(Color.BLACK);}
        else{
            textPaint.setColor(getResources().getColor(R.color.nightColor));
        }
        this.staticLayout = new StaticLayout(this.content,this.textPaint,width1, Layout.Alignment.ALIGN_NORMAL,1f,0f,true);
        this.staticLayout.draw(canvas);
//        System.out.print(staticLayout.getLineCount());
        canvas.restore();
//        canvas.drawText(this.content,0,height1/2,textPaint);
    }
}
