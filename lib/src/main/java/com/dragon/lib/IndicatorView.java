/*
 *  Copyright (C) 2012 Dragon
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.dragon.lib;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * TODO: document your custom view class.
 */
public class IndicatorView extends View implements Indicator, View.OnTouchListener{

    public final static int CIRCLE = 1;
    public final static int LINE = 2;

    public final static int LEFT = 1;
    public final static int CENTER = 2;
    public final static int RIGHT = 3;

    private int mIndicatorSelectedColor;
    private int mIndicatorUnselectedColor;
    private int mIndicatorType;
    private int mIndicatorBackground;
    private float mIndicatorWidth;
    private int mIndicatorPosition;
    private float mIndicatorHeight;
    private Paint mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ViewPager mViewPager;
    private int mCurrentPage;
    private boolean mAutoScroll = true;
    private int mInterval = 5000; //ms

    private float downX;
    private int mTouchSlop;

    private ViewPager.OnPageChangeListener mListener;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            return;
        }

        autoStart();
        init(attrs, defStyle);
        final ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(viewConfiguration);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final Resources res = getResources();

        final int defaultIndicatorSelectedColor = res.getColor(R.color.indicator_selected);
        final int defaultIndicatorUnseletedColor = res.getColor(R.color.indicator_unselected);
        final float defaultWidth = res.getDimension(R.dimen.indicator_width);
        final float defaultIndicatorHeight = res.getDimension(R.dimen.indicator_line_height);
        final int defaultIndicatorBackground = res.getColor(R.color.indicator_backgroup);
        final int defaultPosition = CENTER;
        mIndicatorHeight = defaultIndicatorHeight;
        mIndicatorPaint.setStyle(Paint.Style.FILL);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollImageView);

        mIndicatorSelectedColor = a.getColor(R.styleable.ScrollImageView_indicatorSelectedColor, defaultIndicatorSelectedColor);
        mIndicatorUnselectedColor = a.getColor(R.styleable.ScrollImageView_indicatorUnselectedColor, defaultIndicatorUnseletedColor);
        mIndicatorType = a.getInt(R.styleable.ScrollImageView_indicatorType, CIRCLE);
        mIndicatorBackground = a.getResourceId(R.styleable.ScrollImageView_indicatorBackground, defaultIndicatorBackground);
        mIndicatorPosition = a.getInt(R.styleable.ScrollImageView_indicatorPosition, defaultPosition);
        mIndicatorWidth = a.getDimension(R.styleable.ScrollImageView_indicatorWidth, defaultWidth);
        mIndicatorHeight = a.getDimension(R.styleable.ScrollImageView_indicatorHeight, defaultIndicatorHeight);

        a.recycle();

        mIndicatorPaint.setStrokeWidth(mIndicatorHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewPager == null) {
            return;
        }

        int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }

        if (mCurrentPage >= count) {
            setCurrentPage(count - 1);
            return;
        }

        drawIndicator(canvas, count);
    }

    private float calDistance(int paddingLeft, int paddingRight, int count){
        int width = getWidth();
        int drawIndicatorSpacing = width - paddingLeft - paddingRight;
        float perfectSpacing = mIndicatorWidth * ( 2 * count - 1);

        if (perfectSpacing >= drawIndicatorSpacing){
            mIndicatorWidth = drawIndicatorSpacing / (2 * count - 1);
        }

        return mIndicatorWidth;
    }

    private float calPadding(int paddingLeft, int paddingRight, int count){
        int width = getWidth();
        int drawIndicatorSpacing = width - paddingLeft - paddingRight;
        float perfectSpacing = mIndicatorWidth * ( 2 * count - 1);

        return (drawIndicatorSpacing - perfectSpacing) / 2;
    }

    private void drawLine(Canvas canvas, float startX, float startY, float radius){
        float width = radius * 2;
        canvas.drawLine(startX, startY, startX + width, startY, mIndicatorPaint);
    }

    private void drawCircle(Canvas canvas, float startX, float startY, float radius){
        canvas.drawCircle(startX + radius, startY, radius, mIndicatorPaint);
    }

    private void drawIndicatorGraphic(Canvas canvas, float dX, float dY, float radius){
        if (mIndicatorType == LINE) {
            drawLine(canvas, dX, dY, radius);
        } else if (mIndicatorType == CIRCLE) {
            drawCircle(canvas, dX, dY, radius);
        }
    }

    private void drawIndicator(Canvas canvas, int count) {
        int height, paddingLeft, paddingRight;
        float paddingBetween;
        float dX, dY;
        float start = 0;
        float radius, padding;

        height = getHeight();
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingBetween = calDistance(paddingLeft, paddingRight, count);
        radius = mIndicatorWidth / 2.0f;
        padding = calPadding(paddingLeft, paddingRight, count);

        if (mIndicatorPosition == LEFT) {
            start = paddingLeft;
        } else if (mIndicatorPosition == RIGHT){
            start = paddingLeft + padding * 2;
        } else if (mIndicatorPosition == CENTER){
            start = paddingLeft + padding;
        }

        mIndicatorPaint.setColor(Color.BLUE);
        dY = height / 2.0f;
        for (int i=0; i<count; i++) {
            dX = start + (mIndicatorWidth + paddingBetween) * i;
            drawIndicatorGraphic(canvas, dX, dY, radius);
        }

        mIndicatorPaint.setColor(Color.RED);
        dX = start + (mIndicatorWidth + paddingBetween) * mCurrentPage;
        drawIndicatorGraphic(canvas, dX, dY, radius);
    }

    public int getScreenHeight(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    public int getScreenWidth(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    private float calPerfectHeight(int length){
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int width = length;
        float result = mIndicatorWidth;
        int count = mViewPager.getAdapter().getCount();
        int drawIndicatorSpacing = width - paddingLeft - paddingRight;
        float perfectSpacing = mIndicatorWidth * ( 2 * count - 1);

        if (perfectSpacing >= drawIndicatorSpacing){
            result = drawIndicatorSpacing / (2 * count - 1);
        }

        return result;
    }

    private int measureHeight(int measureSpec){
        int result;
        int screenWidth = getScreenWidth();
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        } else {
            result = (int)mIndicatorWidth + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(specSize, result);
                if (mIndicatorType == LINE){
                    result = (int) (mIndicatorHeight * 6);
                } else {
                    result = Math.max(result, screenWidth);
                    int temp = (int) calPerfectHeight(result);

                    if (screenWidth != result){
                        result = temp + 20;
                    } else {
                        result = temp;
                    }

                }
            }
        }

        return result;
    }

    /**
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec){
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)){
            result = specSize;
        } else {
            int count = mViewPager.getAdapter().getCount();
            result = getPaddingLeft() + getPaddingRight() + (int)(mIndicatorWidth * (2f * count - 1));
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    @Override
    public void setCurrentPage(int index) {
        mCurrentPage = index;
        if (mViewPager != null){
            mViewPager.setCurrentItem(mCurrentPage);
        }
        invalidate();
    }


    @Override
    public void setViewPage(ViewPager viewPage) {
        setViewPage(viewPage, 0);
    }

    @Override
    public void setViewPage(ViewPager viewPage, int initIndex) {
        mViewPager = viewPage;
        mCurrentPage = initIndex;

        if (mViewPager != null){
            mViewPager.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int count = mViewPager.getAdapter().getCount();
        float distance = 0;

        switch (action){
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(NEXT_PAGE);
                mAutoScroll = false;
                downX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                distance = downX - event.getX();
                if (distance > 0){
                    if (Math.abs(distance) > mTouchSlop) {
                        mCurrentPage = (mCurrentPage + 1) % count;
                    }
                } else {
                    if (Math.abs(distance) > mTouchSlop) {
                        mCurrentPage = (mCurrentPage - 1) % count;
                        if (mCurrentPage < 0){
                            mCurrentPage += count;
                        }
                    }
                }
                //setCurrentPage(mCurrentPage);
                Message msg = mHandler.obtainMessage();
                msg.what = PREV_PAGE;
                mHandler.dispatchMessage(msg);
                downX = 0;
                break;
        }

        return true;
    }

    public static final int PREV_PAGE = 1;
    public static final int NEXT_PAGE = 2;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int count = mViewPager.getAdapter().getCount();
            switch (msg.what){
                case PREV_PAGE:
                case NEXT_PAGE:
                    if (getAutoScroll()) {
                        mCurrentPage = (mCurrentPage + 1) % count;
                    }
                    setCurrentPage(mCurrentPage);
                    sendDelayedMessage();
                    mAutoScroll = true;
                    break;
            }
            //super.handleMessage(msg);
        }
    };

    private void sendDelayedMessage(){
        Message msg = mHandler.obtainMessage();
        msg.what = NEXT_PAGE;
        mHandler.sendMessageDelayed(msg, mInterval);
    }

    public void autoStart(){
        mAutoScroll = true;
        sendDelayedMessage();
    }
    public void autoStop(){
        mAutoScroll = false;
    }
    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void notifyDataSetChanged() {
        invalidate();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /*if (mCurrentPage != position) {
            mCurrentPage = position;
        }*/
        invalidate();

        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        /*mCurrentPage = position;*/
        invalidate();

        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        IndicatorViewStatus status = (IndicatorViewStatus)state;
        try{
            super.onRestoreInstanceState(state);
        } catch (Exception e) {

        }
        mCurrentPage = status.mCurrentPage;
        requestLayout();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();
        IndicatorViewStatus status = new IndicatorViewStatus(state);
        status.mCurrentPage = mCurrentPage;
        return status;
    }













    public static class IndicatorViewStatus extends BaseSavedState{
        int mCurrentPage;

        public IndicatorViewStatus(Parcelable source) {
            super(source);
        }
        public IndicatorViewStatus(Parcel source) {
            super(source);
            mCurrentPage = source.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mCurrentPage);
        }


        public static final Creator<IndicatorViewStatus> CREATOR = new Creator<IndicatorViewStatus>(){

            @Override
            public IndicatorViewStatus createFromParcel(Parcel source) {
                return new IndicatorViewStatus(source);
            }

            @Override
            public IndicatorViewStatus[] newArray(int size) {
                return new IndicatorViewStatus[size];
            }
        };

    }




    /**
     * setters and getters
     */

    public int getmIndicatorSelectedColor() {
        return mIndicatorSelectedColor;
    }

    public void setmIndicatorSelectedColor(int mIndicatorSelectedColor) {
        this.mIndicatorSelectedColor = mIndicatorSelectedColor;
    }

    public int getmIndicatorUnselectedColor() {
        return mIndicatorUnselectedColor;
    }

    public void setmIndicatorUnselectedColor(int mIndicatorUnselectedColor) {
        this.mIndicatorUnselectedColor = mIndicatorUnselectedColor;
    }

    public int getIndicatorType() {
        return mIndicatorType;
    }

    public void setIndicatorType(int mIndicatorType) {
        this.mIndicatorType = mIndicatorType;
    }

    public int getIndicatorBackground() {
        return mIndicatorBackground;
    }

    public void setIndicatorBackground(int mIndicatorBackground) {
        this.mIndicatorBackground = mIndicatorBackground;
    }

    public float getIndicatorWidth() {
        return mIndicatorWidth;
    }

    public void setIndicatorWidth(float mWidth) {
        this.mIndicatorWidth = mWidth;
    }

    public int getIndicatorPosition() {
        return mIndicatorPosition;
    }

    public void setIndicatorPosition(int mIndicatorPosition) {
        this.mIndicatorPosition = mIndicatorPosition;
    }

    public void setAutoScroll(boolean autoScroll){
        mAutoScroll = autoScroll;
    }

    public boolean getAutoScroll(){
        return mAutoScroll;
    }
}
