package net.hongqianfly.drawabledemo;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by HongQian.Wang on 2017/10/27.
 */

public class Rect1Drawable extends Drawable {
    private Paint mPaint;
    private Bitmap mBitmap;
    private RectF rect;
    private final BitmapShader bitmapShader;

    public Rect1Drawable(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
        //着色器
        bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);

    }
    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawRoundRect(rect,60,60,mPaint);
    }


    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        rect=new RectF(left, top, right, bottom);
    }

    @Override
    public void setAlpha(int i) {
            mPaint.setAlpha(i);//设置画笔的透明度
    }
    @Override //设置画笔的ColorFilter
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
          mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }
    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }
}
