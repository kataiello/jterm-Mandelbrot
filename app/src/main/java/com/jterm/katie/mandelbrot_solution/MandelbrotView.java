/*
 *  Copyright 2016 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.jterm.katie.mandelbrot_solution;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MandelbrotView extends View {

    private static final double X_MAX = 1;
    private static final double X_MIN = -2.5;
    private static final double Y_MAX = 1.75;
    private static final double Y_MIN = -1.75;

    private double mXMin = X_MIN;
    private double mXMax = X_MAX;
    private double mXRange = X_MAX - X_MIN;
    private double mYMin = Y_MIN;
    private double mYMax = Y_MAX;
    private double mYRange = Y_MAX - Y_MIN;

    private Paint mBackgroundPaint;
    private Paint mPaint;
    private int mMaxDepth;
    private int mNumPoints = 100;

    private int[] mColors;

    public MandelbrotView(Context context) {
        super(context);
        init();
    }

    public MandelbrotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MandelbrotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MandelbrotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(getResources().getColor(R.color.background_color));

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (widthMeasureSpec == 0 || heightMeasureSpec == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        int size = Math.max(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(size, size);
    }

    public void reset(int depth) {
        mMaxDepth = depth;
        mXMin = X_MIN;
        mXMax = X_MAX;
        mXRange = mXMax - mXMin;
        mYMin = Y_MIN;
        mYMax = Y_MAX;
        mYRange = mYMax - mYMin;
        initializeColors();
        postInvalidate();
    }

    /**
     * Define the colors to use, based on the depth.
     * This will mean less calculation during onDraw!
     */
    private void initializeColors() {
        mColors = new int[mMaxDepth + 1];
        for (int i = 0; i < mMaxDepth; i++) {
            int r = (int) (255 * (i * 1.0 / mMaxDepth));
            int g = (int) (255 * (1 - i * 1.0 / mMaxDepth));
            mColors[i] = Color.rgb(r, g, 255);
        }
        mColors[mMaxDepth] = Color.BLACK;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBackgroundPaint);

        int dotSize = getWidth() / mNumPoints;

        for(int i = 0; i < mNumPoints; i++)
        {
            double mx = ((i * 1.0) / mNumPoints) * mXRange + mXMin;
            float px = ((i * 1.0f) / mNumPoints) * getWidth();

            for(int j = 0; j < mNumPoints; j++)
            {
                double my = ((j * 1.0) / mNumPoints) * mYRange + mYMin;
                float py = ((j * 1.0f) / mNumPoints) * getHeight();

                int color = getMandelbrotColor(mx, my);
                mPaint.setColor(color);
                canvas.drawCircle(px, py, dotSize, mPaint);
            }
        }
    }

    /**
     * Z_0 = (mX, mY)
     * Z_n+1 = Z_n * Z_n + Z_0
     * @param mX
     * @param mY
     * @return
     */
    private int getMandelbrotColor(double mX, double mY)
    {
        double zx = mX;
        double zy = mY;
        int iter = 0;

        while(  iter < mMaxDepth &&
                //check divergence
                (zx*zx) + (zy * zy) < 4
        )
        {
            double tx = (zx * zx) - (zy * zy) + mX;
            zy = 2 * zx * zy + mY;
            zx = tx;
            iter++;
        }

        return mColors[iter];
    }
}
