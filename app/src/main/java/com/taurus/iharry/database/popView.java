package com.taurus.iharry.database;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by taurus on 16/10/11.
 */
public class popView extends View {
    public popView(Context context) {
        super(context);
    }

    public popView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public popView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        Path path2 = new Path();
        path2.moveTo(26, 360);
        path2.lineTo(54, 360);
        path2.lineTo(70, 392);
        path2.lineTo(40, 420);
        path2.lineTo(10, 392);
        path2.close();
        canvas.drawPath(path2, paint);

    }
}
