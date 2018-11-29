package me.morgansandler.morgan.hypedgalaga;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class Player {

    RectF rect;

    private Bitmap bitmap;

    private float length;
    private float height;

    private float x;
    private float y;
    private float shipSpeed;

    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    private int shipMoving = STOPPED;

    private int screenX;

    public Player(Context context, int screenX, int screenY){
        rect = new RectF();
        this.screenX = screenX;

        length = screenX / 12;
        height = screenY / 10;

        x = screenX / 2;
        y = screenY - 20;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int)length, (int)height, false);

        shipSpeed = 350;
    }

    public void update(long fps){
        if(shipMoving == LEFT){
            x = x - shipSpeed / fps;
        }

        if(shipMoving == RIGHT){
            x = x + shipSpeed / fps;
        }

        if(getX() > screenX - getLength() || getX() < 0){
            shipMoving = STOPPED;
        }

        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;
    }

    public RectF getRect(){
        return rect;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public float getX(){
        return x;
    }

    public float getLength(){
        return length;
    }

    public void setMovementState(int state){
        shipMoving = state;
    }


}
