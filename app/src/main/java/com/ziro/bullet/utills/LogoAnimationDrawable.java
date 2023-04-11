package com.ziro.bullet.utills;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import java.io.IOException;

public abstract class LogoAnimationDrawable extends AnimationDrawable {
    private Context context;
    private int current;
    private int reqWidth;
    private int reqHeight;
    private int totalTime;
    Bitmap bmp;

    public LogoAnimationDrawable(Context context, int reqWidth, int reqHeight) {
        this.context = context;
        this.current = 35;
        //In my case size of screen to scale Drawable
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
        this.totalTime = 0;
    }

    @Override
    public void addFrame(Drawable frame, int duration) {
        super.addFrame(frame, duration);
        totalTime += duration;
    }

    @Override
    public void start() {
        super.start();
        new Handler().postDelayed(new Runnable() {

            public void run() {
                onAnimationFinish();
            }
        }, totalTime);
    }

    public int getTotalTime() {
        return totalTime;
    }

    @Override
    public void draw(Canvas canvas) {
        try {
            if (bmp != null) {
                bmp.recycle();
            }

            bmp = BitmapFactory.decodeStream(context.getAssets().open("logo"+(current > 89 ? "35" : current)+".png"));
            //Scaling image to fitCenter
            Matrix m = new Matrix();
            m.setRectToRect(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), new RectF(0, 0, reqWidth, reqHeight), Matrix.ScaleToFit.CENTER);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
            //Calculating the start 'x' and 'y' to paint the Bitmap
            int x = (reqWidth - bmp.getWidth()) / 2;
            int y = (reqHeight - bmp.getHeight()) / 2;
            //Painting Bitmap in canvas
            canvas.drawBitmap(bmp, x, y, null);
            //Jump to next item
            current++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void onAnimationFinish();
}
