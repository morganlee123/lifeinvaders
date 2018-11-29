package me.morgansandler.morgan.hypedgalaga;

import android.graphics.RectF;

public class Bullet {

    private float x;
    private float y;

    private RectF rect;

    public final int UP = 0;
    public final int DOWN = 1;

    int heading = -1;
    float speed = 350;

    private int width = 3;
    private int height;

    private boolean isActive;

    public Bullet(int screenY){
        height = screenY / 20;
        isActive = false;

        rect = new RectF();

    }

    public void update(long fps){
        if(heading == UP){
            y = y - speed / fps;
        }else{
            y = y + speed / fps;
        }


        rect.left = x;
        rect.right = x+width;
        rect.top = y;
        rect.bottom = y + height;
    }

    public boolean shoot(float startX, float startY, int direction){
        if(!isActive){
            x = startX;
            y = startY;
            heading = direction;
            isActive = true;
            return true;
        }
        return false;
    }

    public void powerupSpeed(){
        speed = 1300;
    }

    public void resetSpeed(){
        speed = 350;
    }

    public RectF getRect(){
        return rect;
    }

    public boolean getStatus(){
        return isActive;
    }

    public void setInactive(){
        isActive = false;
    }

    public float getImpactPointY(){
        if(heading == DOWN){
            return y+height;
        }else{
            return y;
        }
    }


}
