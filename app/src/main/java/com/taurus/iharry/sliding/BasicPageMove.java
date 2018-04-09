package com.taurus.iharry.sliding;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by taurus on 16/9/9.
 */
public class BasicPageMove extends BasePageMove{
    private Scroller scroller;
    private VelocityTracker velocityTracker;
    private PageMoveLayout pageMoveLayout;

    private int limitDistance = 0;
    private int screenWidth = 0;
    private int touchResult = MOVE_NO_RESULT;
    private int direction = MOVE_NO_RESULT;

    private int mode = MODE_NONE;

    private boolean moveLastPage, moveFirstPage;

    private int startX = 0;
    private View leftScrollerView = null;
    private View rightScrollerView = null;



    private PageMoveAdapter getAdapter() {
        return this.pageMoveLayout.getAdapter();
    }


    @Override
    public void init(PageMoveLayout pageMoveLayout) {
        this.pageMoveLayout = pageMoveLayout;
        this.scroller = new Scroller(pageMoveLayout.getContext());
        screenWidth = pageMoveLayout.getContext().getResources().getDisplayMetrics().widthPixels;
        limitDistance = screenWidth / 3;
    }

    @Override
    public void notifyViewChanged(PageMoveAdapter adapter) {
        View curView = getAdapter().getUpdatedCurrentView();
        this.pageMoveLayout.addView(curView);
        curView.scrollTo(0, 0);

        if (getAdapter().hasPrevious()) {
            View prevView = getAdapter().getUpdatedPreviousView();
            this.pageMoveLayout.addView(prevView);
            prevView.scrollTo(screenWidth, 0);
        }


        if (getAdapter().hasNext()) {
            View nextView = getAdapter().getUpdatedNextView();
            this.pageMoveLayout.addView(nextView);
            nextView.scrollTo(-screenWidth, 0);
        }

        this.pageMoveLayout.pageSelected(getAdapter().getCurrent());
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        obtainVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!this.scroller.isFinished()) {
                    break;
                }
                startX = (int) event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                if (!this.scroller.isFinished()) {
                    return false;
                }
                if (startX == 0) {
                    startX = (int) event.getX();
                }
                final int distance = startX - (int) event.getX();
                if (this.direction == MOVE_NO_RESULT) {
                    if (distance > 0) {
                        this.direction = MOVE_TO_LEFT;
                        this.moveLastPage = !getAdapter().hasNext();
                        this.moveFirstPage = false;

                        this.pageMoveLayout.pageScrollStateChanged(MOVE_TO_LEFT);

                    } else if (distance < 0) {
                        this.direction = MOVE_TO_RIGHT;
                        this.moveFirstPage = !getAdapter().hasPrevious();
                        this.moveLastPage = false;

                        this.pageMoveLayout.pageScrollStateChanged(MOVE_TO_RIGHT);
                    }
                }
                if (this.mode == MODE_NONE
                        && ((this.direction == MOVE_TO_LEFT)
                        || (this.direction == MOVE_TO_RIGHT))) {
                    this.mode = MODE_MOVE;
                }

                if (this.mode == MODE_MOVE) {
                    if ((this.direction == MOVE_TO_LEFT && distance <= 0) || (this.direction == MOVE_TO_RIGHT && distance >= 0)) {
                        this.mode = MODE_NONE;
                    }
                }

                if (this.direction != MOVE_NO_RESULT) {
                    if (this.direction == MOVE_TO_LEFT) {
                        this.leftScrollerView = getCurrentShowView();
                        if (!this.moveLastPage)
                            this.rightScrollerView = getBottomView();
                        else this.rightScrollerView = null;
                    } else {
                        this.rightScrollerView = getCurrentShowView();
                        if (!this.moveFirstPage)
                            this.leftScrollerView = getTopView();
                        else this.leftScrollerView = null;
                    }
                    if (this.mode == MODE_MOVE) {
                        this.velocityTracker.computeCurrentVelocity(1000, 0.5f);
                        if (this.direction == MOVE_TO_LEFT) {
                            if (this.moveLastPage) {
                                this.leftScrollerView.scrollTo(distance/2, 0);
                            } else {
                                this.leftScrollerView.scrollTo(distance, 0);
                                this.rightScrollerView.scrollTo(-screenWidth + distance, 0);
                            }
                        } else {
                            if (this.moveFirstPage) {
                                this.rightScrollerView.scrollTo(distance/2, 0);
                            } else {
                                this.leftScrollerView.scrollTo(screenWidth + distance, 0);
                                this.rightScrollerView.scrollTo(distance, 0);
                            }
                        }
                    } else {
                        int scrollX = 0;
                        if (this.leftScrollerView != null) {
                            scrollX = this.leftScrollerView.getScrollX();
                        } else if (this.rightScrollerView != null) {
                            scrollX = this.rightScrollerView.getScrollX();
                        }
                        if (this.direction == MOVE_TO_LEFT && scrollX != 0 && getAdapter().hasNext()) {
                            this.leftScrollerView.scrollTo(0, 0);
                            if (this.rightScrollerView != null) this.rightScrollerView.scrollTo(screenWidth, 0);
                        } else if (this.direction == MOVE_TO_RIGHT && getAdapter().hasPrevious() && screenWidth != Math.abs(scrollX)) {
                            if (this.leftScrollerView != null)
                                this.leftScrollerView.scrollTo(-screenWidth, 0);
                            this.rightScrollerView.scrollTo(0, 0);
                        }

                    }
                }

                invalidate();

                break;

            case MotionEvent.ACTION_UP:

                if ((this.leftScrollerView == null && this.direction == MOVE_TO_LEFT) ||
                        (this.rightScrollerView == null && this.direction == MOVE_TO_RIGHT)) {
                    return false;
                }

                int time = 500;

                if (this.moveFirstPage && this.rightScrollerView != null) {
                    final int rscrollx = this.rightScrollerView.getScrollX();
                    this.scroller.startScroll(rscrollx, 0, -rscrollx, 0, time * Math.abs(rscrollx)/screenWidth);
                    this.touchResult = MOVE_NO_RESULT;
                }

                if (this.moveLastPage && this.leftScrollerView != null) {
                    final int lscrollx = this.leftScrollerView.getScrollX();
                    this.scroller.startScroll(lscrollx, 0, -lscrollx, 0, time * Math.abs(lscrollx)/screenWidth);
                    this.touchResult = MOVE_NO_RESULT;
                }

                if (!this.moveLastPage && !this.moveFirstPage && this.leftScrollerView != null) {
                    final int scrollX = this.leftScrollerView.getScrollX();
                    int velocityValue = (int) this.velocityTracker.getXVelocity();
                    if (this.mode == MODE_MOVE && this.direction == MOVE_TO_LEFT) {
                        if (scrollX > limitDistance || velocityValue < -time) {
                            this.touchResult = MOVE_TO_LEFT;
                            if (velocityValue < -time) {
                                int tmptime = 1000 * 1000 / Math.abs(velocityValue);
                                time = tmptime > 500 ? 500 : tmptime;
                            }
                            this.scroller.startScroll(scrollX, 0, screenWidth - scrollX, 0, time);
                        } else {
                            this.touchResult = MOVE_NO_RESULT;
                            this.scroller.startScroll(scrollX, 0, -scrollX, 0, time);
                        }
                    } else if (this.mode == MODE_MOVE && this.direction == MOVE_TO_RIGHT) {
                        if ((screenWidth - scrollX) > limitDistance || velocityValue > time) {
                            this.touchResult = MOVE_TO_RIGHT;
                            if (velocityValue > time) {
                                int tmptime = 1000 * 1000 / Math.abs(velocityValue);
                                time = tmptime > 500 ? 500 : tmptime;
                            }
                            this.scroller.startScroll(scrollX, 0, -scrollX, 0, time);
                        } else {
                            this.touchResult = MOVE_NO_RESULT;
                            this.scroller.startScroll(scrollX, 0, screenWidth - scrollX, 0, time);
                        }
                    }
                }
                resetVariables();
                invalidate();

                break;
        }

        return true;
    }

    private void invalidate() {
        this.pageMoveLayout.postInvalidate();
    }

    private void resetVariables() {
        this.direction = MOVE_NO_RESULT;
        this.mode = MODE_NONE;
        startX = 0;
        releaseVelocityTracker();
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (this.velocityTracker != null) {
            this.velocityTracker.recycle();
            this.velocityTracker = null;
        }
    }

    private boolean moveToNext() {
        if (!getAdapter().hasNext())
            return false;
        View prevView = getAdapter().getPreviousView();
        if (prevView != null)
            this.pageMoveLayout.removeView(prevView);
        View newNextView = prevView;

        getAdapter().moveToNext();

        this.pageMoveLayout.pageSelected(getAdapter().getCurrent());

        if (getAdapter().hasNext()) {
            if (newNextView != null) {
                View updateNextView = getAdapter().getView(newNextView, getAdapter().getNext());
                if (updateNextView != newNextView) {
                    getAdapter().setNextView(updateNextView);
                    newNextView = updateNextView;
                }
            } else {
                newNextView = getAdapter().getNextView();
            }
            newNextView.scrollTo(-screenWidth, 0);
            this.pageMoveLayout.addView(newNextView);
        }

        return true;
    }

    private boolean moveToPrevious() {
        if (!getAdapter().hasPrevious())
            return false;
        View nextView = getAdapter().getNextView();
        if (nextView != null)
            this.pageMoveLayout.removeView(nextView);
        View newPrevView = nextView;

        getAdapter().moveToPrevious();

        this.pageMoveLayout.pageSelected(getAdapter().getCurrent());

        if (getAdapter().hasPrevious()) {
            if (newPrevView != null) {
                View updatedPrevView = getAdapter().getView(newPrevView, getAdapter().getPrevious());
                if (newPrevView != updatedPrevView) {
                    getAdapter().setPreviousView(updatedPrevView);
                    newPrevView = updatedPrevView;
                }
            } else {
                newPrevView = getAdapter().getPreviousView();
            }

            newPrevView.scrollTo(screenWidth, 0);
            this.pageMoveLayout.addView(newPrevView);
        }

        return true;
    }

    @Override
    public void computeScroll() {
        if (this.scroller.computeScrollOffset()) {
            if (this.leftScrollerView != null) {
                this.leftScrollerView.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
            }
            if (this.rightScrollerView != null) {
                if (this.moveFirstPage)
                    this.rightScrollerView.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
                else
                    this.rightScrollerView.scrollTo(this.scroller.getCurrX() - screenWidth, this.scroller.getCurrY());
            }

            invalidate();

        } else if (this.scroller.isFinished()) {
            if (this.touchResult != MOVE_NO_RESULT) {
                if (this.touchResult == MOVE_TO_LEFT) {
                    moveToNext();
                } else {
                    moveToPrevious();
                }
                this.touchResult = MOVE_NO_RESULT;

                this.pageMoveLayout.pageScrollStateChanged(MOVE_NO_RESULT);

                invalidate();
            }

        }
    }

    @Override
    public void slideNext() {
        if (!getAdapter().hasNext() || !this.scroller.isFinished())
            return;

        this.leftScrollerView = getCurrentShowView();
        this.rightScrollerView = getBottomView();

        this.scroller.startScroll(0, 0, screenWidth, 0, 500);
        this.touchResult = MOVE_TO_LEFT;

        this.pageMoveLayout.pageScrollStateChanged(MOVE_TO_LEFT);

        invalidate();
    }

    @Override
    public void slidePrevious() {
        if (!getAdapter().hasPrevious() || !this.scroller.isFinished())
            return;

        this.leftScrollerView = getTopView();
        this.rightScrollerView = getCurrentShowView();

        this.scroller.startScroll(screenWidth, 0, -screenWidth, 0, 500);
        this.touchResult = MOVE_TO_RIGHT;

        this.pageMoveLayout.pageScrollStateChanged(MOVE_TO_RIGHT);

        invalidate();
    }

    public View getTopView() {
        return getAdapter().getPreviousView();
    }

    public View getCurrentShowView() {
        return getAdapter().getCurrentView();
    }

    public View getBottomView() {
        return getAdapter().getNextView();
    }
}
