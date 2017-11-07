package net.hongqianfly.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by HongQian.Wang on 2017/11/7.
 */

public class MyView extends View {
    private Context mContext;

    private Drawable background;
    private float textSize;
    private String title;

    private Paint mPaint;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{50, 25}, 0);
      mPaint.setDither(true);

        String string = mContext.getString(R.string.app_name);
        int color = mContext.getResources().getColor(R.color.colorAccent);


        mPaint.setPathEffect(dashPathEffect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(500, 500);
        mPaint.setPathEffect(new DashPathEffect(new float[]{10, 15, 20, 25}, 0));
        canvas.drawPath(path, mPaint);

    }
}


