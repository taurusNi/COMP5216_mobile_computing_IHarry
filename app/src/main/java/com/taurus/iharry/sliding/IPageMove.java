package com.taurus.iharry.sliding;

import android.view.MotionEvent;

/**
 * Created by taurus on 16/9/8.
 */
public interface IPageMove {
    public void init(PageMoveLayout pageMoveLayout);
    public void notifyViewChanged(PageMoveAdapter adapter);
    public boolean onTouchEvent(MotionEvent event);
    public void computeScroll();
    public void slideNext();
    public void slidePrevious();
}



