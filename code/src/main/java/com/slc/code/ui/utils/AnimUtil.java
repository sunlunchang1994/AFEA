package com.slc.code.ui.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class AnimUtil {
    private ValueAnimator valueAnimator;
    private UpdateListener updateListener;
    private EndListener endListener;
    private long duration;
    private float start;
    private float end;
    private Interpolator interpolator = new LinearInterpolator();

    public AnimUtil() {
        duration = 500; //默认动画时常1s
        start = 0.0f;
        end = 1.0f;
        interpolator = new LinearInterpolator();// 匀速的插值器
    }


    public void setDuration(int timeLength) {
        duration = timeLength;
    }

    public void setValueAnimator(float start, float end, long duration) {
        this.start = start;
        this.end = end;
        this.duration = duration;
    }
    public void setValueAnimator(float start, float end) {
        this.start = start;
        this.end = end;
    }
    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    /**
     * 取消动画
     */
    public void cancelAnimator(){
        if (valueAnimator != null) {
            if(valueAnimator.isRunning()){
                valueAnimator.cancel();
            }
            valueAnimator = null;
        }
    }
    public void startAnimator() {
        cancelAnimator();
        valueAnimator = ValueAnimator.ofFloat(start, end);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                if (updateListener == null) {
                    return;
                }
                float cur = (Float) valueAnimator.getAnimatedValue();
                updateListener.progress(cur);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (endListener == null) {
                    return;
                }
                endListener.endUpdate(animator);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        valueAnimator.start();
    }

    public void addUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public void addEndListener(EndListener endListener) {
        this.endListener = endListener;
    }

    public interface EndListener {
        void endUpdate(Animator animator);
    }

    public interface UpdateListener {
        void progress(float progress);
    }

}