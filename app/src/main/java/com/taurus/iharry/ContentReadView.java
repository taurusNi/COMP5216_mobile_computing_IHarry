package com.taurus.iharry;

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
import android.view.View;
import android.widget.TextView;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/9/3.
 */
public class ContentReadView extends View {
    private DisplayMetrics displayMetrics;
    private int screenHeight;
    private int screenWidth;
    private String content="Hello World";
    private int wordCount;
    private int pageLine;
    private int color;
    private float textSize;
    private Paint paint;
    public ContentReadView(Context context) {
        super(context);
        this.displayMetrics = getResources().getDisplayMetrics();
        this.screenHeight = this.displayMetrics.heightPixels;
        this.screenWidth = this.displayMetrics.widthPixels;
        this.paint = new Paint();
    }
    public void append(String content){
        this.content = this.content+content;
    }
    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public void setPageLine(int pageLine) {
        this.pageLine = pageLine;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.paint.setTextSize(20);
        this.paint.setColor(Color.BLACK);
        canvas.drawText(content,0,this.screenHeight/2,this.paint);
    }
}













//    private TextPaint textPaint = null;
//    private StaticLayout staticLayout = null;
//    private int lineCount = 0;
//    private int width = 0;
//    private int height = 0;
//    private String content = null;
//    private float textSize ;
//
//    public void setTextSize(float textSize) {
//        this.textSize = textSize;
//        this.textPaint.setTextSize(textSize);
//    }
//
//    public void setWidth(int width) {
//        this.width = width;
//    }
//
//    public ContentReadView(Context context, float textSize) {
//        super(context);
//        this.textPaint = new TextPaint();
//        this.textPaint.setAntiAlias(true);
//        this.textPaint.setTextSize(textSize);
//        this.staticLayout = new StaticLayout(content, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
//        this.height = staticLayout.getHeight();
//    }
//
//
//    public int getLayoutHeight(){
//        return height;
//    }
//
//    public int getLineCount(){
//        return staticLayout.getLineCount();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        staticLayout.draw(canvas);
//        super.onDraw(canvas);
//    }
//    public static int[] measureTextViewHeight(TextView textView, String content, int width, int maxLine){
//        Log.i("Alex","宽度是"+width);
//        TextPaint textPaint  = textView.getPaint();
//        StaticLayout staticLayout = new StaticLayout(content, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
//        int[] result = new int[2];
//        if(staticLayout.getLineCount()>maxLine) {//如果行数超出限制
//            int lastIndex = staticLayout.getLineStart(maxLine) - 1;
//            result[0] = lastIndex;
//            result[1] = new StaticLayout(content.substring(0, lastIndex), textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false).getHeight();
//            return result;
//        }else {//如果行数没有超出限制
//            result[0] = -1;
//            result[1] = staticLayout.getHeight();
//            return result;
//        }
//    }






