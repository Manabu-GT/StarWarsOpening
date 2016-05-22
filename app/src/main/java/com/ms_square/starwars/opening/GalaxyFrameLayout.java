package com.ms_square.starwars.opening;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.support.percent.PercentFrameLayout;
import android.util.AttributeSet;

import java.util.Random;

public class GalaxyFrameLayout extends PercentFrameLayout {

    /* The default number of lines */
    private static final int DEF_NUM_STARS = 64;

    /* The default minimum star gradient radius in dp */
    private static final int DEF_MIN_STAR_RADIUS = 2;

    /* The default maximum star gradient radius in dp */
    private static final int DEF_MAX_STAR_RADIUS = 4;

    private static final Random RANDOM_GEN = new Random();

    private int numStars;

    private int maxRadius;

    private int minRadius;

    private boolean starBoundsInitialized;

    private GradientDrawable[] stars;

    public GalaxyFrameLayout(Context context) {
        this(context, null);
    }

    public GalaxyFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalaxyFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GalaxyFrameLayout);
        numStars = typedArray.getInt(R.styleable.GalaxyFrameLayout_numStars, DEF_NUM_STARS);
        minRadius = typedArray.getDimensionPixelSize(R.styleable.GalaxyFrameLayout_minStarRadius,
                Math.round(DimenUtil.convertToPixelFromDip(getContext(), DEF_MIN_STAR_RADIUS)));
        maxRadius = typedArray.getDimensionPixelSize(R.styleable.GalaxyFrameLayout_maxStarRadius,
                Math.round(DimenUtil.convertToPixelFromDip(getContext(), DEF_MAX_STAR_RADIUS)));
        typedArray.recycle();

        stars = new GradientDrawable[numStars];
        for (int i = 0, length = numStars; i < length; i++) {
            GradientDrawable drawable = new GradientDrawable();
            int radius = randomInt(minRadius, maxRadius);
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            drawable.setColors(new int[] {0xffffffff, 0x00ffffff});
            drawable.setSize(radius, radius);
            drawable.setGradientRadius(radius);
            stars[i] = drawable;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        for (int i = 0, length = stars.length; i < length; i++) {
            int left = randomInt(0, w);
            int top = randomInt(0, h);
            stars[i].setBounds(left, top, left + stars[i].getIntrinsicWidth(), top + stars[i].getIntrinsicHeight());
        }

        starBoundsInitialized = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!starBoundsInitialized) return;
        for (int i = 0, length = stars.length; i < length; i++) {
            stars[i].draw(canvas);
        }
    }

    private static int randomInt(int min, int max) {
        return RANDOM_GEN.nextInt((max - min) + 1) + min;
    }
}
