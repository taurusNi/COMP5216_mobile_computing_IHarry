package com.taurus.iharry.sliding;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by taurus on 16/9/8.
 */
public abstract class PageMoveAdapter<T> extends Activity{
    private View[] views;
    private int currentViewIndex;
    private PageMoveLayout pageMoveLayout;

    public PageMoveAdapter() {
        this.views = new View[3];
        this.currentViewIndex = 0;
    }
    public PageMoveAdapter(int currentViewIndex) {
        this.views = new View[3];
        this.currentViewIndex = currentViewIndex;
    }
    public void setPageMoveLayout(PageMoveLayout pageMoveLayout) {
        this.pageMoveLayout = pageMoveLayout;
    }
    public abstract View getView(View contentView, T t);

    public abstract T getCurrent();

    public abstract T getNext();

    public abstract T getPrevious();

    public abstract boolean hasNext();

    public abstract boolean hasPrevious();

    protected abstract void computeNext();

    protected abstract void computePrevious();

    public View getUpdatedCurrentView() {
        View curView = this.views[this.currentViewIndex];
        if (curView == null) {
            curView = getView(null, getCurrent());
            this.views[this.currentViewIndex] = curView;
        } else {
            View updateView = getView(curView, getCurrent());
            if (curView != updateView) {
                curView = updateView;
                this.views[this.currentViewIndex] = updateView;
            }
        }
        return curView;
    }
    public View getCurrentView() {
        View curView = this.views[this.currentViewIndex];
        if (curView == null) {
            curView = getView(null, getCurrent());
            this.views[this.currentViewIndex] = curView;
        }
        return curView;
    }
    private View getView(int index) {
        return this.views[(index + 3) % 3];
    }
    private void setView(int index, View view) {
        this.views[(index + 3) % 3] = view;
    }
    public View getUpdatedNextView() {
        View nextView = getView(this.currentViewIndex + 1);
        boolean hasNext = hasNext();
        if (nextView == null && hasNext) {
            nextView = getView(null, getNext());
            setView(this.currentViewIndex + 1, nextView);
        } else if (hasNext) {
            View updatedView = getView(nextView, getNext());
            if (updatedView != nextView) {
                nextView = updatedView;
                setView(this.currentViewIndex + 1, nextView);
            }
        }
        return nextView;
    }
    public View getNextView() {
        View nextView = getView(this.currentViewIndex + 1);
        if (nextView == null && hasNext()) {
            nextView = getView(null, getNext());
            setView(this.currentViewIndex + 1, nextView);
        }
        return nextView;
    }
    public View getUpdatedPreviousView() {
        View prevView = getView(this.currentViewIndex - 1);
        boolean hasprev = hasPrevious();
        if (prevView == null && hasprev) {
            prevView = getView(null, getPrevious());
            setView(this.currentViewIndex - 1, prevView);
        } else if (hasprev) {
            View updatedView = getView(prevView, getPrevious());
            if (updatedView != prevView) {
                prevView = updatedView;
                setView(this.currentViewIndex - 1, prevView);
            }
        }
        return prevView;
    }
    public View getPreviousView() {
        View prevView = getView(this.currentViewIndex - 1);
        if (prevView == null && hasPrevious()) {
            prevView = getView(null, getPrevious());
            setView(this.currentViewIndex - 1, prevView);
        }
        return prevView;
    }
    public void setPreviousView(View view) {
        setView(this.currentViewIndex - 1, view);
    }

    public void setNextView(View view) {
        setView(this.currentViewIndex + 1, view);
    }

    public void setCurrentView(View view) {
        setView(this.currentViewIndex, view);
    }
    public void moveToNext() {
        // Move to next element
        computeNext();

        // Increase view index
        this.currentViewIndex = (this.currentViewIndex + 1) % 3;
    }

    public void moveToPrevious() {
        // Move to next element
        computePrevious();

        // Increase view index
        this.currentViewIndex = (this.currentViewIndex + 2) % 3;
    }
    public Bundle saveState() {
        return null;
    }

    public void restoreState(Parcelable parcelable, ClassLoader loader) {
        this.currentViewIndex = 0;
        if (this.views != null) {
            this.views[0] = null;
            this.views[1] = null;
            this. views[2] = null;
        }
    }

    public void notifyDataSetChanged() {
        if (this.pageMoveLayout != null) {
            this.pageMoveLayout.notifyViewChanged();
            this.pageMoveLayout.postInvalidate();
        }
    }
    public int getCurrentViewIndex(){
        return this.currentViewIndex;
    }






}










