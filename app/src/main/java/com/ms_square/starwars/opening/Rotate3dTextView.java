package com.ms_square.starwars.opening;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.TextView;

public class Rotate3dTextView extends TextView {

    private final Camera camera;

    private final Matrix cameraMatrix;

    private final Point displaySize;

    private int centerX;

    private int centerY;

    public Rotate3dTextView(Context context) {
        this(context, null);
    }

    public Rotate3dTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Rotate3dTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        camera = new Camera();
        cameraMatrix = new Matrix();
        displaySize = new Point();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getDisplay().getSize(displaySize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        camera.save();
        camera.translate(0f, 0f, 300f);
        camera.rotateX(15);
        camera.getMatrix(cameraMatrix);
        cameraMatrix.preTranslate(-centerX, -centerY);
        cameraMatrix.postTranslate(centerX, centerY);
        camera.restore();

        canvas.concat(cameraMatrix);
        super.onDraw(canvas);
    }
}
