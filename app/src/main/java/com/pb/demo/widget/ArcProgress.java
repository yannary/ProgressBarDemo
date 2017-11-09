package com.pb.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.pb.demo.R;

import java.lang.ref.WeakReference;

/**
 * Created by zhangyy on 16/9/6.
 */
public class ArcProgress extends ProgressBar {
    private final int DEFAULT_LINEHEIGHT = dp2px(15);
    private final int DEFAULT_mRadius = dp2px(90);
    private final int DEFAULT_mUnProgressColor = 0xffeaeaea;  // 未使用进度条边框颜色
    private final int DEFAULT_mProgressColor = Color.YELLOW;  // 进度条边框颜色
    private final int DEFAULT_OFFSETDEGREE = 60;  // 底部预留空缺弧度 默认60度
    private final int DEFAULT_BACKGROUND_INSIDE = Color.TRANSPARENT;  // 弧内部背景色 默认透明
    private final int DEFAULT_TEXT_SIZE_INSIDE = 18;  // 弧内文字大小 默认18dp
    private final int DEFAULT_TEXT_COLOR_INSIDE = Color.BLACK;  // 弧内文字颜色 默认黑色
    private float mRadius;  // 半径
    private int mBoardWidth; // 边框宽度
    private int mDegree = DEFAULT_OFFSETDEGREE;  // 底部预留空缺弧度
    private int mBackgroundInside = DEFAULT_BACKGROUND_INSIDE;  // 弧内部背景色
    private int mTextSizeInside = DEFAULT_TEXT_SIZE_INSIDE;   // 弧内文字大小
    private int mTextColorInside = DEFAULT_TEXT_COLOR_INSIDE; // 弧内文字颜色
    private RectF mArcRectF;
    private Paint mArcPaint;
    private int mUnProgressColor;
    private int mProgressColor;
    private Bitmap mCenterBitmap;
    private Canvas mCenterCanvas;

    protected ArcProgressHandler handler = new ArcProgressHandler(this);
    protected Thread thread = null;

    public ArcProgress(Context context) {
        this(context, null);
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.ArcProgress);
        // 边框宽度
        mBoardWidth = attributes.getDimensionPixelOffset(R.styleable.ArcProgress_borderWidth, DEFAULT_LINEHEIGHT);
        // 未使用进度条边框颜色
        mUnProgressColor = attributes.getColor(R.styleable.ArcProgress_unprogresColor, DEFAULT_mUnProgressColor);
        // 进度条边框颜色
        mProgressColor = attributes.getColor(R.styleable.ArcProgress_progressColor, DEFAULT_mProgressColor);
        // 半径
        mRadius = attributes.getDimensionPixelOffset(R.styleable.ArcProgress_radius, DEFAULT_mRadius);
        // 底部预留空缺弧度
        mDegree = attributes.getInt(R.styleable.ArcProgress_degree, DEFAULT_OFFSETDEGREE);
        // 弧内部背景色
        mBackgroundInside = attributes.getInt(R.styleable.ArcProgress_backgroundInside, mBackgroundInside);
        // 弧内文字大小
        mTextSizeInside = attributes.getInt(R.styleable.ArcProgress_TextSizeInside, DEFAULT_TEXT_SIZE_INSIDE);
        // 弧内文字颜色
        mTextColorInside = attributes.getInt(R.styleable.ArcProgress_TextColorInside, DEFAULT_TEXT_COLOR_INSIDE);
        boolean capRound = attributes.getBoolean(R.styleable.ArcProgress_arcCapRound, false);

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (capRound)
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStrokeWidth(mBoardWidth);  // 设置画笔的宽度
        mArcPaint.setStyle(Paint.Style.STROKE);  // 设置绘制轮廓
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            int widthSize = (int) (mRadius * 2 + mBoardWidth * 2);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            int heightSize = (int) (mRadius * 2 + mBoardWidth * 2);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mArcRectF = new RectF(mBoardWidth, mBoardWidth,
                mRadius * 2 - mBoardWidth,
                mRadius * 2 - mBoardWidth);
        Log.e("ArcProgress", "right == " + mArcRectF.right + "   mRadius == " + mRadius * 2);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();

        // 文字底部背景图片
//        if (mCenterCanvas == null) {
//            mCenterBitmap = Bitmap.createBitmap((int) mRadius * 2, (int) mRadius * 2, Bitmap.Config.ARGB_8888);
//            mCenterCanvas = new Canvas(mCenterBitmap);
//        }
//        mCenterCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        Bitmap mTarget = Bitmap.createScaledBitmap(
//                BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.aa),
//                (int) (mArcRectF.right - mBoardWidth * 2),
//                (int) (mArcRectF.right - mBoardWidth * 2), false);
//        Bitmap target = Bitmap.createBitmap(mTarget, 0, 0, mTarget.getWidth(), mTarget.getHeight());
//        float sx = mArcRectF.right / 2 + mBoardWidth / 2 - target.getWidth() / 2;
//        float sy = mArcRectF.right / 2 + mBoardWidth / 2 - target.getHeight() / 2;
//        Paint imgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mCenterCanvas.drawArc(mArcRectF, 90 + mDegree / 2, (360 - mDegree),
//                false, imgPaint);
//        imgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        mCenterCanvas.drawBitmap(target, sx, sy, imgPaint);
//        canvas.drawBitmap(mCenterBitmap, 0, 0, null);

        // 文字底部背景色
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(mBackgroundInside);
        canvas.drawArc(mArcRectF, 90 + mDegree / 2, (360 - mDegree), false, bgPaint);

        float x = mArcRectF.right / 2 + mBoardWidth / 2;
        float y = mArcRectF.right / 2 + mBoardWidth / 2;

        // 绘制文字
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStrokeWidth(dp2px(18));
        textPaint.setTextSize(dp2px(mTextSizeInside));
        textPaint.setColor(mTextColorInside);
        String progressStr = String.valueOf(getProgress());
        float textX = x - (textPaint.measureText(progressStr) / 2);
        if (mDegree < 180) {
            y = y - ((textPaint.descent() + textPaint.ascent()) / 2);
        }
        canvas.drawText(progressStr + "%", textX, y, textPaint);

        // 绘制弧
        float rotate = getProgress() * 1.0f / getMax();
        int angle = mDegree / 2;
        float targetDegree = (360 - mDegree) * rotate;
        //绘制完成部分
        mArcPaint.setColor(mProgressColor);
        canvas.drawArc(mArcRectF, 90 + angle, targetDegree, false, mArcPaint);
        //绘制未完成部分
        mArcPaint.setColor(mUnProgressColor);
        canvas.drawArc(mArcRectF, 90 + angle + targetDegree, 360 - mDegree - targetDegree, false, mArcPaint);
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCenterBitmap != null) {
            mCenterBitmap.recycle();
            mCenterBitmap = null;
        }
    }


    public void setArcProgress(int startProgress, int endProgress) {
        if (startProgress < 0 || startProgress >= endProgress)
            return;
        if (thread != null && thread.isAlive()) {
            return;
        }
        thread = new Thread(new ArcProgressRunnable(startProgress, endProgress));
        thread.start();
    }

    protected class ArcProgressRunnable implements Runnable {
        private int startProgress, endProgress;

        public ArcProgressRunnable(int startProgress, int endProgress) {
            this.startProgress = startProgress;
            this.endProgress = endProgress;
        }

        @Override
        public void run() {
            for (int i = startProgress; i <= endProgress; i++) {
                SystemClock.sleep(100);
                handler.sendEmptyMessage(i);
            }
        }
    }

    protected void handleMessage(Message msg) {
        setProgress(msg.what);
    }

    protected class ArcProgressHandler extends Handler {
        private final WeakReference<ArcProgress> arcProgress;

        public ArcProgressHandler(ArcProgress arcProgress) {
            this.arcProgress = new WeakReference<ArcProgress>(arcProgress);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArcProgress aPb = arcProgress.get();
            if (aPb != null) {
                aPb.handleMessage(msg);
            }
        }
    }


    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
}
