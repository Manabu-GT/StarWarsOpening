package com.ms_square.starwars.opening;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class StarWarsTextView extends TextView {

    private AnimatorSet animatorSet;

    private long duration;
    private long startDelay;
    private float rotation;

    public StarWarsTextView(Context context) {
        this(context, null);
    }

    public StarWarsTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarWarsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StarWarsTextView);
        duration = typedArray.getInteger(R.styleable.StarWarsTextView_duration, -1);
        startDelay = typedArray.getInteger(R.styleable.StarWarsTextView_startDelay, 0);
        rotation = typedArray.getFloat(R.styleable.StarWarsTextView_rotation, 25);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onPreDraw() {
        boolean result = super.onPreDraw();
        setPivotX(getMeasuredWidth() / 2);
        setPivotY(getMeasuredHeight() / 2);
        setRotationX(rotation);
        scrollTo(0, -getMeasuredHeight());
        return result;
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }

    public Animator startAnimation() {
        if (animatorSet == null) {
            animatorSet = new AnimatorSet();

            ValueAnimator scrollAnimator = ValueAnimator.ofInt(-getMeasuredHeight(),
                    Math.round(getLineHeight() * getLineCount() * 0.8f));

            scrollAnimator.setInterpolator(new DecelerateInterpolator());
            scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int val = (int) animation.getAnimatedValue();
                    scrollTo(0, val);
                }
            });

            Keyframe kf0 = Keyframe.ofFloat(0f, 1);
            Keyframe kf1 = Keyframe.ofFloat(.95f, 1);
            Keyframe kf2 = Keyframe.ofFloat(1f, 0);
            PropertyValuesHolder pvhTop = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1, kf2);
            ObjectAnimator alphaAnimator = ObjectAnimator.ofPropertyValuesHolder(this, pvhTop);
            alphaAnimator.setInterpolator(new DecelerateInterpolator());

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setAlpha(1f);
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                    scrollTo(0, -getMeasuredHeight());
                    animatorSet.removeAllListeners();
                    animatorSet = null;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    scrollTo(0, -getMeasuredHeight());
                    animatorSet.removeAllListeners();
                    animatorSet = null;
                }
            });

            if (duration == -1) {
                duration = ((getMeasuredHeight() / getLineHeight()) + getLineCount()) * 1000;
            }
            animatorSet.setDuration(duration).setStartDelay(startDelay);
            animatorSet.playTogether(scrollAnimator, alphaAnimator);
            animatorSet.start();
        }

        return animatorSet;
    }

    public void stopAnimation() {
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }
}