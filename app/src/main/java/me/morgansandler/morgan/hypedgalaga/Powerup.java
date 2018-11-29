package me.morgansandler.morgan.hypedgalaga;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

public class Powerup {

    private int type;

    RectF rect;

    public final int PROJECTILE_SPEED = 0;
    public final int BURST = 1;
    public final int EXTRA_LIVES = 2;
    public final int REPLENISH_BRICKS = 3;

    private Random randomNumber;

    private int length;
    private int height;

    private float x;
    private float y;

    private Bitmap bitmap = null;

    private int fallSpeed = 350;

    public Powerup(Context context, int x, int y, int screenX, int screenY){
        randomNumber = new Random();

        this.x = x;
        this.y = y;

        length = screenX / 20;
        height = screenY / 20;

        type = randomNumber.nextInt(3);

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.powerup);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int)length, (int)height, false);
    }

    public void update(long fps){
        y = y + fallSpeed / fps;

        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;
    }

    public boolean exists(){
        if(randomNumber.nextInt(1000) == 0){
            return true;
        }
        return false;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public float getX(){
        return x;
    }

    public float getY() {
        return y;
    }

    public RectF getRect() {
        return rect;
    }

    public int getType(){
        return type;
    }

}
