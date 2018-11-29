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

    private boolean isActive;

    private int screenX, screenY;

    public Powerup(Context context, int screenX, int screenY){
        randomNumber = new Random();
        this.screenX = screenX;
        this.screenY = screenY;

        length = screenX / 20;
        height = screenY / 20;

        isActive = true;
        rect = new RectF();

        y = screenY - screenY;
        x = randomNumber.nextInt(screenX);

        type = randomNumber.nextInt(3);

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.powerup);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int)length, (int)height, false);
    }

    public void update(long fps){
        y = y + fallSpeed / fps;

        if(y > screenY){
            y = screenY-screenY;
            x = randomNumber.nextInt(screenX);
        }

        type = randomNumber.nextInt(3);

        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;
    }

    public void resetY(){
        y = screenY-screenY;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(){
        isActive = true;
    }
    public void setInactive(){
        isActive = false;
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
