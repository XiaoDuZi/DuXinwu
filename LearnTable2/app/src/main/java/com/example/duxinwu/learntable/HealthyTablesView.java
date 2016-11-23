package com.example.duxinwu.learntable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Administrator on 2016/11/22 0022.
 */

public class HealthyTablesView extends View {

    private static final String TAG = "HealthyTablesView";

    private int mWidth;
    private int mHeight;

    /**
     * 坐标轴宽度
     */
    private int mCoordinatesLineWidth;
    /**
     * 坐标旁边字体颜色
     */
    private int mCoordinatesTextColor;

    /**
     * 坐标旁边字体大小
     */
    private int mCoordinatesTextSize;
    /**
     * 折线的颜色
     */
    private int mLineColor;
    /**
     * 圆点的半径
     */
    private int mCricleRadius;
    /**
     * 背景色
     */
    private int mBgColor;
    /**
     * 折线宽度
     */
    private int mLineWidth;
    /**
     * 小圆点填充色
     */
    private int mMinCircleColor;
    /**
     * 绘制类型，
     */
    private String mDrawType;
    /**
     * 大圆的填充颜色
     */
    private int mMaxCricleColor;

    private int xScale;
    private Paint xyPaint;

    private String[] weeks;
    private Paint textPaint;
    private int[] values;
    private Paint linePaint;
    private Paint maxCirclePaint;
    private Paint minCirclePaint;
    private Rect textBound;

    /**
     * @param context
     */
    public HealthyTablesView(Context context) {
        this(context, null);
    }

    public HealthyTablesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HealthyTablesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.HealthyTableView, defStyleAttr, 0);
        int index = array.getIndexCount();
        for (int i = 0; i < index; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.HealthyTableView_coordinatesLineWidth:
                    //这里将以px为单位,默认值为2px
                    mCoordinatesLineWidth = array.getDimensionPixelOffset(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 2,
                                    getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HealthyTableView_coordinatesTextColor:
                    mCoordinatesTextColor = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.HealthyTableView_coordinatesTextSize:
                    mCoordinatesTextSize = array.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11,
                                    getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HealthyTableView_bgColor:
                    mBgColor = array.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.HealthyTableView_lineWidth:
                    mLineWidth = array.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11,
                                    getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HealthyTableView_lineColor:
                    mLineColor = array.getColor(attr, Color.BLUE);
                    break;
                case R.styleable.HealthyTableView_averageCircleRadius:
                    mCricleRadius = array.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 10,
                            getResources().getDisplayMetrics()
                    ));
                    break;
                case R.styleable.HealthyTableView_maxCircleColor:
                    mMaxCricleColor = array.getColor(attr, Color.GREEN);
                    break;
                case R.styleable.HealthyTableView_minCircleColor:
                    mMinCircleColor = array.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.HealthyTableView_tableType:
                    mDrawType = array.getString(attr);
                    break;
                default:
                    break;
            }
        }
        array.recycle();//释放资源
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        /**
         * 自定义控件的宽高必须有调用者自己指定具体数值
         */
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 300;
        }

        if (heightSpecMode == MeasureSpec.EXACTLY) {
            //高是宽的3/5，
            mHeight = (mWidth / 5) * 3;
        } else {
            mHeight = 230;
        }
        Log.i(TAG, "width=" + mWidth + "---height=" + mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        xScale = (mWidth - getPaddingRight() - getPaddingLeft() - 40) / 6;
        canvas.drawColor(mBgColor);
        drawCoordinates(canvas);
        drawCoordinatesXValues(canvas);
    }


    /**
     * 绘制X轴上的值
     *
     * @param canvas
     */
    private void drawCoordinatesXValues(Canvas canvas) {
        for (int i = 0; i < weeks.length; i++) {
            textPaint.getTextBounds(weeks[i],0,weeks[i].length(),textBound);
            //绘制间断点
            canvas.drawLine(getPaddingLeft()+(i*xScale),mHeight-getPaddingBottom()-10,
                    getPaddingLeft()+(i*xScale),mHeight-getPaddingBottom(),xyPaint);
            canvas.drawText(weeks[i],getPaddingLeft()+(i*xScale)-textBound.width()/2,
                    mHeight-getPaddingBottom()+30,textPaint);
        }
    }

    /**
     * 画坐标系
     *
     * @param canvas
     */
    private void drawCoordinates(Canvas canvas) {
        Log.i(TAG, "drawCoordinates");
        //X轴
        canvas.drawLine(getPaddingLeft(), mHeight - getPaddingBottom(), mWidth - getPaddingRight(),
                mHeight - getPaddingBottom(), xyPaint);
        //X轴上的箭头
        canvas.drawLine(mWidth - getPaddingRight() - 20, mHeight - getPaddingBottom() - 10,
                mWidth - getPaddingRight(), mHeight - getPaddingBottom(), xyPaint);
        canvas.drawLine(mWidth - getPaddingRight() - 20, mHeight - getPaddingBottom()+10,
                mWidth - getPaddingRight(), mHeight - getPaddingBottom(), xyPaint);
        //绘制Y轴
        canvas.drawLine(getPaddingLeft(), getPaddingTop(), getPaddingLeft(),
                mHeight - getPaddingBottom(), xyPaint);
        //绘制X轴上的箭头
        canvas.drawLine(getPaddingLeft() - 10, getPaddingTop() + 20,
                getPaddingLeft(), getPaddingTop(), xyPaint);
        canvas.drawLine(getPaddingLeft() + 10, getPaddingTop() + 20,
                getPaddingLeft(), getPaddingTop(), xyPaint);
    }

    private void initView() {
        weeks = new String[]{"01", "02", "03", "04", "05", "06", "07"};
        values=new int[7];

        xyPaint=new Paint();
        xyPaint.setAntiAlias(true);
        xyPaint.setColor(mCoordinatesTextColor);
        xyPaint.setStrokeWidth(mCoordinatesLineWidth);
        xyPaint.setStyle(Paint.Style.FILL);

        textPaint=new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(mCoordinatesTextColor);
        textPaint.setTextSize(mCoordinatesTextSize);
        textPaint.setStyle(Paint.Style.STROKE);

        textBound=new Rect();

        minCirclePaint=new Paint();
        minCirclePaint.setStyle(Paint.Style.FILL);
        minCirclePaint.setColor(Color.WHITE);
        minCirclePaint.setAntiAlias(true);

        maxCirclePaint=new Paint();
        maxCirclePaint.setStyle(Paint.Style.FILL);
        maxCirclePaint.setColor(mMaxCricleColor);
        maxCirclePaint.setAntiAlias(true);

        linePaint=new Paint();
        linePaint.setColor(mLineColor);
        linePaint.setStrokeWidth(mLineWidth);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);

    }
}
