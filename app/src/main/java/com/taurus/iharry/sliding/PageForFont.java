package com.taurus.iharry.sliding;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Created by taurus on 16/9/18.
 */
public class PageForFont extends ContentView {
    private ArrayList<String> pges;
    private float textSize;
    private final float leading = 1.0f;
    public PageForFont(Context context, ArrayList<String> pages,float textSize) {
        super(context);
        this.pges = pages;
        this.textSize = textSize;
    }

    public PageForFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PageForFont(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private int getEstimatedLines(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        TextPaint textPaint  = new TextPaint();
        textPaint.setTextSize(this.textSize);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float wordTop = fontMetrics.ascent;
        float wordBottom = fontMetrics.descent;
        float wordHeight = wordBottom- wordTop;
        int linenum  = (int) (displayMetrics.heightPixels/(wordHeight+this.leading));
        return linenum;
    }


}
