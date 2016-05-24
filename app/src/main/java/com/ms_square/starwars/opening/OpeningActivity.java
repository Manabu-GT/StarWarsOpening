package com.ms_square.starwars.opening;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class OpeningActivity extends AppCompatActivity {

    private static final String TAG = OpeningActivity.class.getSimpleName();

    private MediaPlayer mediaPlayer;

    private ImageView logoView;
    private TextView introTextView;
    private StarWarsTextView openingTextView;

    private Button btnStart;

    private boolean audioPrepared;

    private boolean startRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        logoView = (ImageView) findViewById(R.id.logo_view);
        introTextView = (TextView) findViewById(R.id.intro_text_view);
        openingTextView = (StarWarsTextView) findViewById(R.id.opening_text_view);
        btnStart = (Button) findViewById(R.id.btn_start);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioPrepared) {
                    startOpeningScene();
                } else {
                    startRequested = true;
                }
            }
        });

        FullScreenSupport.hideSystemUI(this);

        initMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();
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
                if (startRequested) {
                    startOpeningScene();
                    startRequested = false;
                }
            }
        });

        try {
            AssetFileDescriptor afd = getAssets().openFd("opening_crawl_1977.ogg");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            Log.w(TAG, "Error loading music:" + e.toString());
        }
        mediaPlayer.prepareAsync();
    }

    private void startOpeningScene() {
        btnStart.setVisibility(View.INVISIBLE);
        try {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } catch (IllegalStateException ie) {
            Log.w(TAG, "could not start playing audio:" + ie.toString());
        }
        startAnimation();
    }

    private void startAnimation() {
        startIntroTextAnimation();
        startLogoAnimation();

        openingTextView.startAnimation().addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                btnStart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                btnStart.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startIntroTextAnimation() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Animator introAnimator = AnimatorInflater.loadAnimator(this, R.animator.intro);
            introAnimator.setTarget(introTextView);
            introAnimator.start();
        } else {
            Keyframe kf0 = Keyframe.ofFloat(0f, 0);
            Keyframe kf1 = Keyframe.ofFloat(.2f, 1);
            Keyframe kf2 = Keyframe.ofFloat(.9f, 1);
            Keyframe kf3 = Keyframe.ofFloat(1f, 0);
            PropertyValuesHolder pvh = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1, kf2, kf3);
            ObjectAnimator alphaAnimator = ObjectAnimator.ofPropertyValuesHolder(introTextView, pvh);
            alphaAnimator.setInterpolator(new DecelerateInterpolator());
            alphaAnimator.setDuration(6000).setStartDelay(1000);
            alphaAnimator.start();
        }
    }

    private void startLogoAnimation() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Animator logoAnimator = AnimatorInflater.loadAnimator(this, R.animator.logo);
            logoAnimator.setTarget(logoView);
            logoAnimator.start();
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            PropertyValuesHolder pvh = PropertyValuesHolder.ofKeyframe("alpha",
                    Keyframe.ofFloat(0f, 1),
                    Keyframe.ofFloat(.5f, 1),
                    Keyframe.ofFloat(1f, 0));
            ObjectAnimator alphaAnimator = ObjectAnimator.ofPropertyValuesHolder(logoView, pvh);

            pvh = PropertyValuesHolder.ofKeyframe("scaleX", Keyframe.ofFloat(0f, 2.75f), Keyframe.ofFloat(1f, .1f));
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofPropertyValuesHolder(logoView, pvh);

            pvh = PropertyValuesHolder.ofKeyframe("scaleY", Keyframe.ofFloat(0f, 2.75f), Keyframe.ofFloat(1f, .1f));
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofPropertyValuesHolder(logoView, pvh);

            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(9000).setStartDelay(9000);
            animatorSet.playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator);
            animatorSet.start();
        }
    }
}
