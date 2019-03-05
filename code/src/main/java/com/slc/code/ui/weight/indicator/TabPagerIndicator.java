package com.slc.code.ui.weight.indicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slc.code.R;

import java.util.Locale;

public class TabPagerIndicator extends HorizontalScrollView {

    public interface IconTabProvider {
        int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor
    };
    // @formatter:on

    private LinearLayout.LayoutParams wrapTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public ViewPager.OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;
    private static final String TAG = "xujun";
    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint piderPaint;

    private int indicatorColor = 0xFF666666;
    private int underlineColor = 0x1A000000;
    private int piderColor = 0x1A000000;

    //表示是否扩展
    private boolean isExpand = false;
    //表示下滑线的长度是否与标题字体的长度一样
    private boolean isSame = false;
    private boolean textAllCaps = true;

    private int scrollOffset = 52;
    private int indicatorHeight = 8;
    private int underlineHeight = 2;
    private int piderPadding = 12;
    //表示自己之间的间隔

    private int horizontalPadding = 24;
    private int verticalPadding = 10;
    private int piderWidth = 1;

    private int tabTextSize = 12;
    private int tabTextColor = 0xFF666666;
    private int tabTextSelectColor = 0xFF666666;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.BOLD;

    private int lastScrollX = -1;

    private int tabBackgroundResId = R.drawable.background_indicator_tab;
    //Indicator的样式
    private IndicatorMode curMode = IndicatorMode.MODE_WRAP_EXPAND_SAME;

    private Locale locale;

    public TabPagerIndicator(Context context) {
        this(context, null);
    }

    public TabPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        addView(tabsContainer);
        //根据IndicatorMode初始化各个变量
        setIndicatorMode(curMode);
        //初始化自定义属性
        obtainAttrs(context, attrs);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);

        piderPaint = new Paint();
        piderPaint.setAntiAlias(true);
        piderPaint.setStrokeWidth(piderWidth);

        wrapTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    public void setIndicatorMode(IndicatorMode indicatorMode) {
        this.setIndicatorMode(indicatorMode, false);
    }

    public void setIndicatorMode(IndicatorMode indicatorMode, boolean isNotify) {
        switch (indicatorMode) {
            case MODE_WRAP_EXPAND_SAME:
                isExpand = false;
                isSame = true;
                break;
            case MODE_WRAP_EXPAND_NOSAME:
                isExpand = false;
                isSame = false;
                break;
            case MODE_WEIGHT_EXPAND_NOSAME:
                isExpand = true;
                isSame = false;
                break;
            case MODE_WEIGHT_EXPAND_SAME:
                isExpand = true;
                isSame = true;
                break;
        }
        this.curMode = indicatorMode;
        if (isNotify) {
            notifyDataSetChanged();
        }
    }

    private void obtainAttrs(Context context, AttributeSet attrs) {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset,
                dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                underlineHeight, dm);
        piderPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                piderPadding, dm);
        horizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                horizontalPadding, dm);
        piderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, piderWidth,
                dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        tabTextColor = a.getColor(1, tabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.TabPagerIndicator);

        indicatorColor = a.getColor(R.styleable.TabPagerIndicator_pstsIndicatorColor,
                indicatorColor);
        underlineColor = a.getColor(R.styleable.TabPagerIndicator_pstsUnderlineColor,
                underlineColor);
        piderColor = a.getColor(R.styleable.TabPagerIndicator_pstsDividerColor, piderColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable
                .TabPagerIndicator_pstsIndicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable
                .TabPagerIndicator_pstsUnderlineHeight, underlineHeight);
        piderPadding = a.getDimensionPixelSize(R.styleable
                .TabPagerIndicator_pstsDividerPadding, piderPadding);
        horizontalPadding = a.getDimensionPixelSize(R.styleable
                .TabPagerIndicator_pstsTabPaddingLeftRight, horizontalPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.TabPagerIndicator_pstsTabBackground,
                tabBackgroundResId);
        isExpand = a.getBoolean(R.styleable.TabPagerIndicator_pstsShouldExpand,
                isExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.TabPagerIndicator_pstsScrollOffset,
                scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.TabPagerIndicator_pstsTextAllCaps, textAllCaps);
        tabTextSelectColor = a.getColor(R.styleable.TabPagerIndicator_pstsTextSelectColor, tabTextSelectColor);
        a.recycle();
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.addOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        //先移除掉所有的View ，防止重复添加
        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {
            //区分是文字还是Icon的导航
            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }

        }

        updateTabStyles();
        //监听视图树，在绘制完毕后调用相关的方法完成初始化工作
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);

            }
        });

    }

    private void addTextTab(final int position, String title) {

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);

        addTab(position, tab);

    }

    //添加孩子
    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        // 设置监听
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
            }
        });
        // 这里我们下划线的 高度是否与文字的长度保持一致，是通过给孩子设置padding或者margin实现的
        //  注意与onDraw里面的逻辑结合起来
        if (!isSame) {
            tab.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
            wrapTabLayoutParams.setMargins(0, 0, 0, 0);
            expandedTabLayoutParams.setMargins(0, 0, 0, 0);
        } else {
            wrapTabLayoutParams.setMargins(horizontalPadding, verticalPadding,
                    horizontalPadding, verticalPadding);
            expandedTabLayoutParams.setMargins(horizontalPadding, verticalPadding,
                    horizontalPadding, verticalPadding);
        }

        //根据是否可以扩展来设置不同的layoutParams
        tabsContainer.addView(tab, position, isExpand ? expandedTabLayoutParams :
                wrapTabLayoutParams);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setBackgroundResource(tabBackgroundResId);

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);

                if (i == currentPosition) {
                    tab.setTextColor(tabTextSelectColor);
                } else {
                    tab.setTextColor(tabTextColor);
                }

                // setAllCaps() is only available from API 14, so the upper case is made manually
                // if we are on a pre-ICS-build
                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }
            }
        }

    }

    // 调用这个方法是HorizontalScrollView滑动到相应的位置
    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }
        int newScrollX;
        View child = tabsContainer.getChildAt(position);
        int left = child.getLeft();
        if (isSame) {
            newScrollX = left + offset - horizontalPadding;
        } else {
            newScrollX = left + offset;
        }

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }
        Log.i(TAG, "scrollToChild:newScrollX=" + newScrollX);
        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line

        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current
        // and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) *
                    lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) *
                    lineRight);
        }

        canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);

        // draw underline

        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // draw pider

        piderPaint.setColor(piderColor);
        for (int i = 0; i < tabCount - 1; i++) {
            View tab = tabsContainer.getChildAt(i);
            if (!isSame) {
                canvas.drawLine(tab.getRight(), piderPadding, tab.getRight(),
                        height - piderPadding, piderPaint);
            } else {
                canvas.drawLine(tab.getRight() + horizontalPadding, piderPadding,
                        tab.getRight() + horizontalPadding, height - piderPadding, piderPaint);
            }
        }
    }

    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            currentPosition = position;
            currentPositionOffset = positionOffset;

            View child = tabsContainer.getChildAt(position);
            int width = child.getWidth();

            if (isSame) {
                width += horizontalPadding * 2;
            }

            Log.i(TAG, "onPageScrolled:width=" + width);
            //  调用这个方法是HorizontalScrollView滑动到相应的位置
            scrollToChild(position, (int) (positionOffset * width));
            //调用这个方法重新绘制
            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
            for (int i = 0; i < tabsContainer.getChildCount(); i++) {
                View child = tabsContainer.getChildAt(i);
                if (child instanceof TextView) {
                    if (i == position) {
                        ((TextView) child).setTextColor(tabTextSelectColor);
                    } else {
                        ((TextView) child).setTextColor(tabTextColor);
                    }
                }
            }
            //tabTextSelectColor
        }

    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int piderColor) {
        this.piderColor = piderColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.piderColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return piderColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int piderPaddingPx) {
        this.piderPadding = piderPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return piderPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setExpand(boolean expand) {
        this.isExpand = expand;
        requestLayout();
    }

    public boolean getExpand() {
        return isExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.horizontalPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return horizontalPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    //用来保存状态
    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * 定义4种模式
     */
    public enum IndicatorMode {
        // 给枚举传入自定义的int值
        MODE_WRAP_EXPAND_SAME(1),// 可扩展，导航线跟标题相等
        MODE_WRAP_EXPAND_NOSAME(2),// 可扩展，导标不相等
        MODE_WEIGHT_EXPAND_SAME(3),// 可扩展，导航线跟标题相等
        MODE_WEIGHT_EXPAND_NOSAME(4);// 可扩展，导标不相等
        private int value;

        IndicatorMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


}