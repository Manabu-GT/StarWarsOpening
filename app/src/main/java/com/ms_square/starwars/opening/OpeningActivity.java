package com.ms_square.starwars.opening;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;

public class OpeningActivity extends AppCompatActivity {

    private static final String TAG = OpeningActivity.class.getSimpleName();

    private static final long ANIM_DURATION_OPENING = 81000;
    private static final long ANIM_DELAY_OPENING = 13000;

    private MediaPlayer mediaPlayer;

    private ScrollView scrollView;
    private ImageView logoView;
    private TextView introTextView;
    private Rotate3dTextView openingTextView;

    private boolean audioPrepared;

    private AnimatorSet animatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        logoView = (ImageView) findViewById(R.id.logo_view);
        introTextView = (TextView) findViewById(R.id.intro_text_view);
        openingTextView = (Rotate3dTextView) findViewById(R.id.opening_text_view);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // disables scrolling by touch
                return true;
            }
        });

        FullScreenSupport.hideSystemUI(this);

        initMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            FullScreenSupport.hideSystemUI(this);
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                audioPrepared = true;
                mp.start();
                startAnimation();
            }
        });

        try {
            AssetFileDescriptor afd = getAssets().openFd("opening_crawl_1977.ogg");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    private void startAnimation() {
        Animator introAnimator = AnimatorInflater.loadAnimator(this, R.animator.intro);
        introAnimator.setTarget(introTextView);
        introAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                introTextView.setVisibility(View.VISIBLE);
            }
        });
        introAnimator.start();

        Animator logoAnimator = AnimatorInflater.loadAnimator(this, R.animator.logo);
        logoAnimator.setTarget(logoView);
        logoAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                logoView.setVisibility(View.VISIBLE);
            }
        });
        logoAnimator.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            animateOpeningText();
        } else {
            animateOpeningTextPreKitkat();
        }
    }

    private void animateOpeningTextHelper() {
        Animator animator1 = AnimatorInflater.loadAnimator(OpeningActivity.this, R.animator.opening);
        animator1.setTarget(openingTextView);

        Keyframe kf0 = Keyframe.ofFloat(0f, Math.round(scrollView.getHeight() * 0.4));
        Keyframe kf1 = Keyframe.ofFloat(1f, -openingTextView.getHeight() * 0.8f);
        PropertyValuesHolder pvhTop = PropertyValuesHolder.ofKeyframe("translationY", kf0, kf1);
        ValueAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(openingTextView, pvhTop);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d(TAG, "translationY:" + openingTextView.getTranslationY());
                openingTextView.invalidate();
            }
        });

        animator2.setDuration(ANIM_DURATION_OPENING);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1, animator2);
        animatorSet.start();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void animateOpeningText() {
        Log.d(TAG, "text Height:" + openingTextView.getHeight());
        if (openingTextView.getHeight() == 0) {
            openingTextView.post(new Runnable() {
                @Override
                public void run() {
                    animateOpeningTextHelper();
                }
            });
        } else {
            animateOpeningTextHelper();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void animateOpeningTextPreKitkat() {
        ObjectAnimator oa = ObjectAnimator.ofFloat(openingTextView, View.TRANSLATION_Y, -2000)
                .setDuration(ANIM_DURATION_OPENING);
        oa.setInterpolator(new LinearInterpolator());
        oa.setStartDelay(ANIM_DELAY_OPENING);
        oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                openingTextView.invalidate();
            }
        });
        oa.start();
    }
}
