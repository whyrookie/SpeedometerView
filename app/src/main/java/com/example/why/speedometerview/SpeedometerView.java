package com.example.why.speedometerview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by why on 17-3-9.
 */

public class SpeedometerView extends View {

    private Paint mCenterCirclePaint;//内部白色圆形画笔
    private Paint mOutCirclePaint;//外部个红色圆形画笔
    private Paint mWhiteScalePaint;//白色刻度画笔
    private Paint mMultipleOfTenScalePaint;//红色加粗刻度画笔
    private Paint mNumberPaint;//数字画笔
    private Paint mIndicatorPathPaint;

    private int mCenterCircleLineWidth;//内部白色线条宽度
    private int mOutCircleLineWidth;//外部红色线条宽度
    private int mNormalScaleWidth;//白色刻度线条宽度
    private int mMultipleOfTenScaleWidth;//红色加粗刻度线条宽度
    private int mMultipleOfTenScaleLen ;//红色加粗线条长度
    private int mNormalScaleLen;//白色刻度线条长度
    private int mNumberWidth;
    private int mPointerWidth;
    private int mScalePathLineWidth;
    private float mNumberTextSize;

    private int mCenterCircleRadius;
    private int mOutCircleRadius;

    private  RectF mOutRectF,mCenterRectF;

    private int mViewDefaultWidth;
    private int mViewDefaultHeight;
    private int mMultipleOfTenCount;

    private int[] mWindowSize = new int[2];
    private float mDensity;
    private Path mIndcatorPath;
    private int mScaleIndicatorLen;
    private int mIndicatorX;
    private int mIndicatorY;

    private int mOutCircleColor = Color.RED;
    private int mNumberColor =Color.RED;
    private int mInnerCircleColor = Color.WHITE;
    private int mNormalScaleColor = Color.WHITE;
    private int mMultipleOfTenScaleColor = Color.RED;


    private void initSize(){

        mWindowSize = getWindowSize();
        mViewDefaultWidth = mWindowSize[0];
        mViewDefaultHeight = mWindowSize[1];
        mOutCircleRadius = mWindowSize[0]/2-20;
        mNumberTextSize = mOutCircleRadius / 13;
        mCenterCircleLineWidth = mOutCircleRadius/150;
        mCenterCircleRadius = mOutCircleRadius / 8;
        mOutCircleLineWidth = mOutCircleRadius /150;
        mNormalScaleWidth =  mOutCircleRadius /150;
        mMultipleOfTenScaleWidth = mOutCircleRadius / 150;
        mMultipleOfTenScaleLen = mOutCircleRadius /6;
        mNormalScaleLen = mOutCircleRadius / 7;
        mMultipleOfTenCount = 12;
        mScalePathLineWidth = mOutCircleRadius/100;
    }

    public SpeedometerView(Context context) {
        super(context);
        init(context,null,0);
    }

    public SpeedometerView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context,attrs,0);
    }

    public SpeedometerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context,AttributeSet attrs, int defStyleAttr){

        initSize();
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.SpeedometerView,defStyleAttr,0);
            mOutCircleColor = typedArray.getColor(R.styleable.SpeedometerView_outCircleColor,mOutCircleColor);
            mNumberColor = typedArray.getColor(R.styleable.SpeedometerView_numberColor, mNumberColor);
            mInnerCircleColor = typedArray.getColor(R.styleable.SpeedometerView_innerCircleColor, mInnerCircleColor);
            mNormalScaleColor = typedArray.getColor(R.styleable.SpeedometerView_normalScaleColor, mNormalScaleColor);
            mMultipleOfTenScaleColor = typedArray.getColor(R.styleable.SpeedometerView_multipleOfTenScaleColor, mMultipleOfTenScaleColor);
        }

        mCenterCirclePaint = new Paint();
        mCenterCirclePaint.setStyle(Paint.Style.STROKE);
        mCenterCirclePaint.setColor(mInnerCircleColor);
        mCenterCirclePaint.setAntiAlias(true);//抗锯齿
        mCenterCirclePaint.setStrokeWidth(mCenterCircleLineWidth);

        mOutCirclePaint = new Paint();
        mOutCirclePaint.setStyle(Paint.Style.STROKE);
        mOutCirclePaint.setColor(mOutCircleColor);
        mOutCirclePaint.setAntiAlias(true);
        mOutCirclePaint.setStrokeWidth(mOutCircleLineWidth);

        mWhiteScalePaint = new Paint();
        mWhiteScalePaint.setStyle(Paint.Style.FILL);
        mWhiteScalePaint.setColor(mNormalScaleColor);
        mWhiteScalePaint.setAntiAlias(true);
        mWhiteScalePaint.setStrokeWidth(mNormalScaleWidth);

        mMultipleOfTenScalePaint = new Paint();
        mMultipleOfTenScalePaint.setStyle(Paint.Style.FILL);
        mMultipleOfTenScalePaint.setColor(mMultipleOfTenScaleColor);
        mMultipleOfTenScalePaint.setAntiAlias(true);
        mMultipleOfTenScalePaint.setStrokeWidth(mMultipleOfTenScaleWidth);

        mNumberPaint = new Paint();
        mNumberPaint.setStyle(Paint.Style.FILL);
        mNumberPaint.setColor(mNumberColor);
        mNumberPaint.setAntiAlias(true);
        mNumberPaint.setStrokeWidth(mNumberWidth);
        mNumberPaint.setTextSize(mNumberTextSize);
        mNumberPaint.setTextAlign(Paint.Align.CENTER);

        mIndicatorPathPaint = new Paint();
        mIndicatorPathPaint.setAntiAlias(true);
        mIndicatorPathPaint.setStyle(Paint.Style.STROKE);
        mIndicatorPathPaint.setColor(Color.RED);
        mIndicatorPathPaint.setStrokeWidth(mScalePathLineWidth);

        mScaleIndicatorLen = mOutCircleRadius*8/13;

        mIndicatorX = -mScaleIndicatorLen;
        mIndicatorY = 0;

        mIndcatorPath = new Path();
        mOutRectF = new RectF();
        mCenterRectF = new RectF();


    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightMeasureSpec == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mViewDefaultWidth, mViewDefaultHeight);
        }else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode != MeasureSpec.AT_MOST) {
            setMeasuredDimension(mViewDefaultWidth, heightSpecSize);
        }else if (widthMeasureSpec != MeasureSpec.AT_MOST && heightMeasureSpec == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize, heightMeasureSpec);
        }else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
        
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mWindowSize[0]/2, mWindowSize[1]/2);//canvas坐标平移到屏幕中心,这样容易操作

        //画外层的弧形红色
        drawOutRedArc(mOutRectF, canvas);

        //画内层的弧形白色
        drawInnerWhiteArc(mCenterRectF, canvas);
        //注意,Math.cos(),Math,.sin()使用弧度计算
        drawMultipleOfTenScale(canvas);

        //接下来画普通的刻度,即未加粗的刻度
        drawWhiteScale(canvas);
        //接下来画指示图标
        drawIndicator(mIndcatorPath, canvas);

    }

    private static final String TAG = "SpeedometerView";
    private int[] getWindowSize() {
        int[] size = new int[2];
        Resources resources = getContext().getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        mDensity = displayMetrics.density;

        size[0] = displayMetrics.widthPixels;
        Log.d(TAG,"屏幕宽"+size[0]);
        size[1] = displayMetrics.heightPixels;
        Log.d(TAG,"屏幕高"+size[1]);
        mDensity = displayMetrics.density;
        Log.d(TAG,"密度"+mDensity);
        return size;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        float posX;
        float posY;
        double radian;
        double scale;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                posX  = event.getX();
                posY = event.getY();
                if (!mOutRectF.contains(posX-mWindowSize[0]/2,posY - mWindowSize[1]/2) || posY - mWindowSize[1]/2 > 0){
                    return true;
                }

                scale = Math.sqrt((posX-mWindowSize[0]/2)*(posX-mWindowSize[0]/2)+ (posY - mWindowSize[1]/2)*(posY - mWindowSize[1]/2))/mScaleIndicatorLen;
                mIndicatorX = rawXToIndicatorX(posX, scale);
                mIndicatorY = rawXToIndicatorY(posY, scale);
                break;
            case MotionEvent.ACTION_MOVE:

                posX = event.getX();
                posY = event.getY();
                if (!mOutRectF.contains(posX-mWindowSize[0]/2,posY - mWindowSize[1]/2)|| posY - mWindowSize[1]/2 > 0){
                    return true;
                }
                Log.d(TAG,"x的="+posX+"--y的值=" +
                        ""+posY);
                scale = Math.sqrt((posX-mWindowSize[0]/2)*(posX-mWindowSize[0]/2)+ (posY - mWindowSize[1]/2)*(posY - mWindowSize[1]/2))/mScaleIndicatorLen;
                mIndicatorX = rawXToIndicatorX(posX, scale);
                mIndicatorY = rawXToIndicatorY(posY, scale);
                break;
        }

        invalidate();
        return true;
    }

    private int rawXToIndicatorX(double posX, double scale) {
        return (int)((posX-mWindowSize[0]/2) / scale);
    }

    private int rawXToIndicatorY(double posY, double scale) {
        return (int)((posY - mWindowSize[1]/2) / scale);
    }
    private int getPxOfSize(int size) {
            return (int)(size*mDensity);
    }

    private void drawOutRedArc(RectF rectF, Canvas canvas) {
        rectF.set(-mOutCircleRadius,  - mOutCircleRadius, mOutCircleRadius, mOutCircleRadius);
        canvas.drawArc(mOutRectF,0,-180,false,mOutCirclePaint);
    }

    private void drawInnerWhiteArc(RectF rectF, Canvas canvas) {
        rectF.set(-mCenterCircleRadius, -mCenterCircleRadius,mCenterCircleRadius, mCenterCircleRadius);
        canvas.drawArc(mCenterRectF, 0, -180, false, mCenterCirclePaint);
    }
    private void drawMultipleOfTenScale(Canvas canvas) {
        int distance = mOutCircleRadius - mMultipleOfTenScaleLen;
        //接下来画10的倍数的刻度
        for (int i = -180; i<=0; i += 15) {
            double radian = ((double)i/180)*Math.PI;
            double cosValue = Math.cos(radian);
            double sinValue = Math.sin(radian);
            float startX = (float)(distance*cosValue);
            float startY = (float)(distance*sinValue);
            float endX = (float)(mOutCircleRadius*Math.cos(radian));
            float endY = (float)(mOutCircleRadius*Math.sin(radian));
            canvas.drawLine(startX,startY,endX,endY,mMultipleOfTenScalePaint);

            //刻度数字标识
            float numberX =(float)((distance-50)*cosValue);
            float numberY =(float)((distance-50)*sinValue);
            canvas.drawText(""+Math.abs(i+180)*2/3,numberX, numberY, mNumberPaint);
        }
    }

    private void drawWhiteScale(Canvas canvas) {
        float normalIntervalRadian = (float)Math.PI / 60;
        int  normalIntervalRadianCount = (int)(Math.PI/normalIntervalRadian);
         int distance = mOutCircleRadius - mNormalScaleLen;
        for (int i = 0; i <= normalIntervalRadianCount; i++) {
            if (!(i%5 == 0) ){
                float cosValue = (float)Math.cos(-normalIntervalRadian*i);
                float sinValue = (float)Math.sin(-normalIntervalRadian*i);
                float startX = (distance*cosValue);
                float startY = (distance*sinValue);
                float endX = (float)((mOutCircleRadius-10)*Math.cos((-normalIntervalRadian*i)));
                float endY = (float)((mOutCircleRadius-10)*Math.sin((-normalIntervalRadian*i)));
                canvas.drawLine(startX,startY,endX,endY,mWhiteScalePaint);
            }
        }
    }

    private void drawIndicator(Path path, Canvas canvas) {
        path.lineTo(mIndicatorX, mIndicatorY);
        canvas.drawPath(path,mIndicatorPathPaint);
        path.reset();
    }

}
