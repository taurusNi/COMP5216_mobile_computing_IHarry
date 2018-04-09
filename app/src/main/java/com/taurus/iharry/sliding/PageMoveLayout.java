package com.taurus.iharry.sliding;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;



import java.util.zip.Inflater;

/**
 * Created by taurus on 16/9/8.
 */
public class PageMoveLayout extends ViewGroup {
    //It is used for record click event (or touch event)
    private int downMotionX;
    private int downMotionY;
    private long downMotionTime;
    private IOnTapListener iOnTapListener;
    private IOnPageChangeListener onPageChangeListener;
    private PageMoveAdapter pageMoveAdapter = null;
    private IPageMove iPageMove=null;
    private Parcelable restoredAdapterState = null; //打包接口 Parcelable需要在多个部件(Activity或Service)之间通过Intent传递一些数据
    private ClassLoader restoredClassLoader = null;

    public PageMoveLayout(Context context) {
        super(context);
    }

    public PageMoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageMoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void PopContent(){
        Log.d("aahahaha","sssss");
    }


    public void setPageMove(IPageMove iPageMove) {
        this.iPageMove = iPageMove;
        iPageMove.init(this);
        notifyViewChanged();
    }

    public PageMoveAdapter getAdapter() {
        return pageMoveAdapter;
    }

    public void setPageMoveAdapter(PageMoveAdapter adapter) {
        this.pageMoveAdapter = adapter;

        this.pageMoveAdapter.setPageMoveLayout(this);
        if (this.restoredAdapterState != null) {
            this.pageMoveAdapter.restoreState(this.restoredAdapterState, this.restoredClassLoader);
            this.restoredAdapterState = null;
            this.restoredAdapterState = null;
        }

        notifyViewChanged();

        postInvalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.downMotionX = (int) event.getX();
                this.downMotionY = (int) event.getY();
                this.downMotionTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_UP:
                computeTapMotion(event);
                break;
        }

        return this.iPageMove.onTouchEvent(event) || super.onTouchEvent(event);

    }
    public void setOnTapListener(IOnTapListener l) {
        this.iOnTapListener = l;
    }




    private void computeTapMotion(MotionEvent event) {
        if (this.iOnTapListener == null)
            return;

        int xDiff = Math.abs((int) event.getX() - this.downMotionX);
        int yDiff = Math.abs((int) event.getY() - this.downMotionY);
        long timeDiff = System.currentTimeMillis() - this.downMotionTime;

        if (xDiff < 5 && yDiff < 5 && timeDiff < 200) {
            iOnTapListener.onSingleTap(event);
        }
    }
    @Override
    public void computeScroll() {
        super.computeScroll();
        this.iPageMove.computeScroll();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void pageNext() {
        this.iPageMove.slideNext();
    }

    public void pagePrevious() {
        this.iPageMove.slidePrevious();
    }

    public static class SavedState extends View.BaseSavedState {
        Parcelable adapterState;
        ClassLoader loader;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(adapterState, flags);
        }

        @Override
        public String toString() {
            return "BaseSlidingLayout.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        });

        SavedState(Parcel in, ClassLoader loader) {
            super(in);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        if (pageMoveAdapter != null) {
            ss.adapterState = pageMoveAdapter.saveState();
        }
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (pageMoveAdapter != null) {
            pageMoveAdapter.restoreState(ss.adapterState, ss.loader);
            notifyViewChanged();
        } else {
            restoredAdapterState = ss.adapterState;
            restoredClassLoader = ss.loader;
        }
    }

    public void notifyViewChanged() {
        removeAllViews();
        if (iPageMove != null && pageMoveAdapter != null)
            iPageMove.notifyViewChanged(pageMoveAdapter);
    }


    public void setIOnPageChangeListener(IOnPageChangeListener l) {
        onPageChangeListener = l;
    }



    public void pageScrollStateChanged(int moveDirection) {
        if (onPageChangeListener != null)
            onPageChangeListener.onPageScrollStateChanged(moveDirection);
    }

    public void pageSelected(Object obj) {
        if (onPageChangeListener != null)
            onPageChangeListener.onPageSelected(obj);
    }
}


















































