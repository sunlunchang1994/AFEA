/*
 * Copyright  2017  zengp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slc.code.ui.weight.wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.slc.code.R;
import com.slc.code.ui.weight.wheel.bean.WheelBaseData;

import java.util.List;


/**
 * Created by on the way on 2018/8/10.
 */

public class RecyclerWheelPicker extends RecyclerView {

    private boolean mScrollEnabled = true;
    private int mTextColor, mUnitColor, mDecorationColor;
    private float mTextSize, mUnitSize, mDecorationSize;
    private String mUnitText = "";//单位

    private Paint mDecorationPaint;//分割线画笔
    private TextPaint mUnitTextPaint;//单位画笔
    private Rect mDecorationRect;//分割线区域

    private SoundPool mSoundPool;//声音播放工具
    private int mSoundId = 0;//声音id
    private int mSoundTrigger = -1;//声音触发器
    private boolean mPickerSoundEnabled = false;//选择器声音是否允许播放

    private boolean mIsScrolling = true;//是否滑动
    private boolean mIsInitFinish = false;  // whether RecyclerView's children count is over zero

    private IDecoration mDecoration;//分割器接口
    //private WheelAdapter1 mAdapter;//适配器
    private WheelPickerLayoutManager mLayoutManager;//布局管理器
    private OnWheelScrollListener mListener;//选择监听
    private int mSelectPosition;//选择的位置

    private Camera mCamera;
    private Matrix mMatrix;//变换的矩阵
    private boolean isMatrix;

    public interface OnWheelScrollListener {
        void onWheelScrollChanged(RecyclerWheelPicker wheelPicker, boolean isScrolling, int position, WheelBaseData wheelBaseData);
    }

    public RecyclerWheelPicker(Context context) {
        this(context, null);
    }

    public RecyclerWheelPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerWheelPicker(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        float density = context.getResources().getDisplayMetrics().density;
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RecyclerWheelPicker);
        mDecorationSize = mTypedArray.getDimension(R.styleable.RecyclerWheelPicker_rwp_decorationSize, 35 * density);
        mDecorationColor = mTypedArray.getColor(R.styleable.RecyclerWheelPicker_rwp_decorationColor, 0x09191919);
        mTextColor = mTypedArray.getColor(R.styleable.RecyclerWheelPicker_rwp_textColor, Color.BLACK);
        mTextSize = mTypedArray.getDimension(R.styleable.RecyclerWheelPicker_rwp_textSize, 22 * scaledDensity);
        mUnitColor = mTypedArray.getColor(R.styleable.RecyclerWheelPicker_rwp_unitColor, mTextColor);
        mUnitSize = mTypedArray.getDimension(R.styleable.RecyclerWheelPicker_rwp_unitSize, mTextSize);
        mTypedArray.recycle();

        init(context);
    }

    private void init(Context context) {
        initSound();

        setOverScrollMode(OVER_SCROLL_NEVER);
        setHasFixedSize(true);

        mDecorationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnitTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mDecorationRect = new Rect();
        mDecoration = new DefaultDecoration();

        /*mAdapter = new WheelAdapter(context);
        super.setAdapter(mAdapter);*/
        mLayoutManager = new WheelPickerLayoutManager(this);
        super.setLayoutManager(mLayoutManager);
        new LinearSnapHelper().attachToRecyclerView(this);

        mCamera = new Camera();
        mMatrix = new Matrix();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // if on child has been attached , do not dispatch touch event
        return !mIsInitFinish || super.dispatchTouchEvent(ev);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        mIsInitFinish = getAdapter().getItemCount() == 0 || getChildCount() > 0;
        if (state == SCROLL_STATE_IDLE) {
            if (!mIsInitFinish)
                dispatchOnScrollEvent(true, NO_POSITION, null);
            else {
                int centerPosition = mLayoutManager.findCenterItemPosition();
                if (centerPosition == NO_POSITION) {
                    dispatchOnScrollEvent(true, NO_POSITION, null);
                } else
                    dispatchOnScrollEvent(false, centerPosition, ((WheelAdapter) getAdapter()).getData(centerPosition));
            }
        } else
            dispatchOnScrollEvent(true, NO_POSITION, null);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        mIsInitFinish = getAdapter().getItemCount() == 0 || getChildCount() > 0;
        if (dx == 0 && dy == 0) {
            if (!mIsInitFinish)
                dispatchOnScrollEvent(true, NO_POSITION, null);
            else {
                int centerPosition = mLayoutManager.findCenterItemPosition();
                if (centerPosition == NO_POSITION) {
                    dispatchOnScrollEvent(true, NO_POSITION, null);
                } else
                    dispatchOnScrollEvent(false, centerPosition, ((WheelAdapter) getAdapter()).getData(centerPosition));
            }
        } else
            dispatchOnScrollEvent(true, NO_POSITION, null);

        if (mPickerSoundEnabled && Math.abs(dy) > 1 && mLayoutManager.mItemHeight > 0) {
            int currentTrigger = mLayoutManager.mVerticalOffset / mLayoutManager.mItemHeight;
            if (!mLayoutManager.mIsOverScroll && currentTrigger != mSoundTrigger) {
                playSound();
                mSoundTrigger = currentTrigger;
            }
        }
    }

    private void dispatchOnScrollEvent(boolean isScrolling, int position, WheelBaseData wheelBaseData) {
        mIsScrolling = isScrolling;
        this.mSelectPosition = position;
        if (null != mListener)
            mListener.onWheelScrollChanged(RecyclerWheelPicker.this, isScrolling, position, wheelBaseData);
    }

    public void scrollTargetPositionToCenter(int position) {
        mLayoutManager.scrollTargetPositionToCenter(position, ((WheelAdapter) getAdapter()).getItemHeight());
    }

    public int getSelectPosition() {
        if (mSelectPosition == NO_POSITION) {
            int firstVisibleItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
            int lastVisibleItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
            int position = NO_POSITION;
            float alpha = 0;
            for (int i = firstVisibleItemPosition; i < lastVisibleItemPosition; i++) {
                View itemView = mLayoutManager.findViewByPosition(i);
                if (itemView != null && itemView.getAlpha() > alpha) {
                    alpha = itemView.getAlpha();
                    position = i;
                }
            }
            return position;
        }
        return mSelectPosition;
    }

    @Override
    public void smoothScrollToPosition(int position) {
        if (getAdapter().getItemCount() == 0) return;
        super.smoothScrollToPosition(position);
    }

    @Override
    @Deprecated
    public void setLayoutManager(LayoutManager layout) {
        //TODO 弃用  不赞成使用改方法 使用无效
    }

    @Override
    @Deprecated
    public void setAdapter(Adapter adapter) {
        //TODO 弃用  不赞成使用改方法 使用无效
    }

    /**
     * 设置WheelAdapter适配器
     *
     * @param adapter
     */
    public void setAdapter(WheelAdapter adapter) {
        //TODO 此处添加适配器
        super.setAdapter(adapter);
    }

    public void setOnWheelScrollListener(OnWheelScrollListener listener) {
        this.mListener = listener;
    }

    public void setUnit(String unitText) {
        if (mUnitText.equals(unitText)) return;
        this.mUnitText = unitText;
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public void setUnitColor(int unitColor) {
        this.mUnitColor = unitColor;
    }

    public void setDecorationColor(int decorationColor) {
        this.mDecorationColor = decorationColor;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    public void setUnitSize(float unitSize) {
        this.mUnitSize = unitSize;
    }

    public void setDecorationSize(float decorationSize) {
        this.mDecorationSize = decorationSize;
    }

    public boolean isInitFinish() {
        return mIsInitFinish;
    }

    /**
     * 设置是否播放声音
     *
     * @param enabled
     */
    public void setPickerSoundEnabled(boolean enabled) {
        this.mPickerSoundEnabled = enabled;
    }

    /*@Deprecated
    public void setData(List<WheelBaseData> data) {
        *//*mAdapter.setData(data, mTextColor, mTextSize);
        super.setAdapter(mAdapter);*//*
        // if there is no data, RecyclerView will disable scrolling,
        // we need manually notify the listener of the scroll status;
        if (null == data || data.size() == 0)
            onScrolled(0, 0);
        // check the scroll border
        mLayoutManager.checkVerticalOffsetBound();
    }*/

    public void setScrollEnabled(final boolean scrollEnabled) {
        if (mScrollEnabled != scrollEnabled) {
            if (scrollEnabled) {
                mScrollEnabled = scrollEnabled;
                smoothScrollBy(0, 1);
            }
            if (mLayoutManager.findCenterItemPosition() == -1) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScrollEnabled = scrollEnabled;
                    }
                }, 200);
            } else {
                mScrollEnabled = scrollEnabled;
            }
        }
    }

    public boolean isScrollEnabled() {
        return mScrollEnabled;
    }

    public boolean isScrolling() {
        return mIsScrolling;
    }

    public void setDecoration(IDecoration mDecoration) {
        this.mDecoration = mDecoration;
        invalidate();
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return mScrollEnabled;
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        drawDecoration(c);
        drawUnitText(c);
    }

    /**
     * 绘制分割线
     *
     * @param c
     */
    private void drawDecoration(Canvas c) {
        if (null != mDecoration) {
            int decorationTop = (int) (getVerticalSpace() / 2 - mDecorationSize / 2);
            int decorationBottom = (int) (getVerticalSpace() / 2 + mDecorationSize / 2);
            mDecorationRect.set(-1, decorationTop, getWidth() + 1, decorationBottom);
            mDecorationPaint.setColor(mDecorationColor);
            mDecorationPaint.setStyle(Paint.Style.STROKE);
            mDecorationPaint.setStrokeWidth(0.25f);
            mDecoration.drawDecoration(this, c, mDecorationRect, mDecorationPaint);
        }
    }

    /**
     * 绘制单位
     *
     * @param c
     */
    private void drawUnitText(Canvas c) {
        if (null != mUnitText && !TextUtils.isEmpty(mUnitText)) {
            mUnitTextPaint.setColor(mUnitColor);
            mUnitTextPaint.setTextSize(mUnitSize);
            float startX = getWidth() - getPaddingRight() - StaticLayout.getDesiredWidth(mUnitText, 0, mUnitText.length(), mUnitTextPaint);
            Paint.FontMetrics fontMetrics = mUnitTextPaint.getFontMetrics();
            float startY = getVerticalSpace() / 2 + mUnitSize / 2 - fontMetrics.descent / 2;
            c.drawText(mUnitText, startX, startY, mUnitTextPaint);
        }
    }

    @Override
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        // resize the text size if text can not be shown completely
        /*if (child instanceof TextView) {
            String data = ((TextView) child).getText().toString();
            if (((TextView) child).getTextSize() == mTextSize) {
                float finalTextSize = mTextSize;
                float dataStringW = StaticLayout.getDesiredWidth(data, 0, data.length(), ((TextView) child).getPaint());
                if (getHorizontalSpace() > 0 && dataStringW * 1.1f > getHorizontalSpace()) {
                    finalTextSize = getHorizontalSpace() / dataStringW / 1.1f * mTextSize;
                }
                ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_PX, finalTextSize);
            }
        }*/

        // scale child X Y
        int centerY = getVerticalSpace() / 2;
        int childCenterY = child.getTop() + child.getHeight() / 2;
        float factor = (centerY - childCenterY) * 1f / centerY;
        float alphaFactor = 1 - 0.6f * Math.abs(factor);
        child.setAlpha(alphaFactor * alphaFactor * alphaFactor);
        float scaleFactor = 1 - 0.4f * Math.abs(factor);
        child.setScaleX(scaleFactor);
        child.setScaleY(scaleFactor);

        if (isMatrix) {
            // rotateX calculate rotate radius
            float rotateRadius = 2.0f * centerY / (float) Math.PI;
            float rad = (centerY - childCenterY) * 1f / rotateRadius;
            float offsetZ = rotateRadius * (1 - (float) Math.cos(rad));
            float rotateDeg = rad * 180 / (float) Math.PI;
            // offset Y for item rotate
            float offsetY = centerY - childCenterY - rotateRadius * (float) Math.sin(rad) * 1.3f;
            child.setTranslationY(offsetY);

            canvas.save();
            mCamera.save();
            mCamera.translate(0, 0, offsetZ);
            mCamera.rotateX(rotateDeg);
            mCamera.getMatrix(mMatrix);
            mCamera.restore();
            mMatrix.preTranslate(-child.getWidth() / 2, -childCenterY);
            mMatrix.postTranslate(child.getWidth() / 2, childCenterY);
            canvas.concat(mMatrix);
            boolean result = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return result;
            // for most of all devices, the childview would be rotated around X-axis perfectly by calling
            // view's setRotationX(). But, this would not work on huawei devices. so for the compatibility
            // here we resolve this compatibility-bug through another way by using Camera;
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    /**
     * 获取垂直间隔
     *
     * @return
     */
    public int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    /**
     * 获取水平间隔
     *
     * @return
     */
    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 初始化声音播放器
     */
    private void initSound() {
        mSoundPool = new SoundPool(50, AudioManager.STREAM_SYSTEM, 5);
        try {
            mSoundPool.load(getContext(), R.raw.wheelpickerkeypress, 1);
        } catch (Exception e) {
        }
    }

    /**
     * 释放声音播放器
     */
    public void release() {
        mSoundPool.release();
    }

    /**
     * 播放声音
     */
    private void playSound() {
        try {
            mSoundPool.stop(mSoundId);
            mSoundId = mSoundPool.play(1, 1, 1, 0, 0, 1);
        } catch (Exception e) {
        }
    }

    public static abstract class WheelAdapter<T extends WheelBaseData> extends Adapter<ViewHolder> {
        Context mContext;
        List<T> mWheelBaseData;
        int mItemHeight = 0;
        int mLayout;

        public WheelAdapter(Context context, @LayoutRes int layout, List<T> wheelBaseData, int itemHeight) {
            this.mContext = context;
            this.mLayout = layout;
            this.mWheelBaseData = wheelBaseData;
            this.mItemHeight = itemHeight;
        }

        /**
         * 获取item高度
         *
         * @return
         */
        int getItemHeight() {
            return mItemHeight;
        }

        @Override
        public WheelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WheelHolder(LayoutInflater.from(mContext).inflate(mLayout, null));
        }

        @Override
        public final void onBindViewHolder(ViewHolder holder, int position) {
            if (null != mWheelBaseData) {
                convert(holder, mWheelBaseData.get(position), position);
            }
        }

        public abstract void convert(ViewHolder holder, T t, int position);

        /**
         * 获取数据
         *
         * @param position
         * @return
         */
        WheelBaseData getData(int position) {
            return null == mWheelBaseData || position > mWheelBaseData.size() - 1 ? null : mWheelBaseData.get(position);
        }

        @Override
        public int getItemCount() {
            return null == mWheelBaseData ? 0 : mWheelBaseData.size();
        }

        class WheelHolder extends ViewHolder {
            WheelHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
