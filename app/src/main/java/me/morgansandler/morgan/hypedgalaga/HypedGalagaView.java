package me.morgansandler.morgan.hypedgalaga;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class HypedGalagaView extends SurfaceView implements Runnable {

    Context context;

    private Thread gameThread = null;
    private SurfaceHolder holder;
    private volatile boolean playing;
    private boolean paused = true;

    private Canvas canvas;
    private Paint paint;

    private long fps;
    private long timeThisFrame;

    private int screenX;
    private int screenY;

    private Player playerShip;
    private Bullet bullet;

    private Bullet[] enemyBullets = new Bullet[200];
    private int nextBullet;
    private int maxEnemyBullets = 10;

    Enemy[] enemies = new Enemy[60];
    int numEnemies = 0;

    private Brick[] bricks = new Brick[400];
    private int numBricks;

    //private Powerup powerup;

    private SoundPool soundPool;
    private int playerExplodeID = -1;
    private int enemyExplodeID = -1;
    private int shootID = -1;
    private int damageShelterID = -1;
    private int uhID = -1;
    private int ohID = -1;

    int score = 0;

    private int lives = 3;

    private long menaceInterval = 1000;
    private boolean uhOrOh;
    private long lastMenaceTime = System.currentTimeMillis();

    public HypedGalagaView(Context context, int x, int y){
        super(context);
        this.context = context;

        holder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("shoot.ogg");
            shootID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("invaderexplode.ogg");
            enemyExplodeID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("damageshelter.ogg");
            damageShelterID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("playerexplode.ogg");
            playerExplodeID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("uh.ogg");
            uhID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("oh.ogg");
            ohID = soundPool.load(descriptor, 0);
        }catch(IOException e){Log.e("error", "failed to load sound files");}

        prepareLevel();
    }

    private void prepareLevel(){
        menaceInterval = 1000;

        playerShip = new Player(context, screenX, screenY - 180);
        bullet = new Bullet(screenY);

        for(int i = 0; i<enemyBullets.length; i++){
            enemyBullets[i] = new Bullet(screenY);
        }

        numEnemies = 0;
        for(int column = 0; column < 6; column++){
            for(int row = 0; row < 5; row++){
                enemies[numEnemies] = new Enemy(context, row, column, screenX, screenY);
                numEnemies++;
            }
        }

        numBricks = 0;
        for(int shelterNumber = 0; shelterNumber < 4; shelterNumber++){
            for(int column = 0; column < 10; column ++ ) {
                for (int row = 0; row < 5; row++) {
                    bricks[numBricks] = new Brick(row, column, shelterNumber, screenX, screenY);
                    numBricks++;
                }
            }
        }

        //powerup = new Powerup(context, 300, screenY - screenY, screenX, screenY);
    }

    @Override
    public void run(){
        while(playing){
            long startFrameTime = System.currentTimeMillis();

            if(!paused){
                update();
            }

            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if(timeThisFrame >= 1){
                fps = 1000/timeThisFrame;
            }

            //
            if(!paused) {
                if ((startFrameTime - lastMenaceTime) > menaceInterval) {
                    if (uhOrOh) {
                        // Play Uh
                        soundPool.play(uhID, 1, 1, 0, 0, 1);

                    } else {
                        // Play Oh
                        soundPool.play(ohID, 1, 1, 0, 0, 1);
                    }

                    // Reset the last menace time
                    lastMenaceTime = System.currentTimeMillis();
                    // Alter value of uhOrOh
                    uhOrOh = !uhOrOh;
                }
            }
            //

        }


    }

    private void update(){
        boolean bumped = false;
        boolean lost = false;

        playerShip.update(fps);

        // update enemies if invisible
        for(int i = 0; i< numEnemies; i++){
            if(enemies[i].getVisibility()){
                enemies[i].update(fps);

                if(enemies[i].takeAim(playerShip.getX(), playerShip.getLength())){
                    if(enemyBullets[nextBullet].shoot(enemies[i].getX() + enemies[i].getLength() / 2, enemies[i].getY(), bullet.DOWN)){
                        nextBullet++;

                        if(nextBullet == maxEnemyBullets){
                            nextBullet = 0;
                        }
                    }
                }

                if(enemies[i].getX() > screenX - enemies[i].getLength() || enemies[i].getX() < 0){
                    bumped = true;
                }
            }
        }

        if(bumped){
            for(int i = 0; i<numEnemies; i++){
                enemies[i].dropDownAndReverse();
                if(enemies[i].getY() > screenY - screenY / 10){
                    lost = true;
                }
            }

            menaceInterval = menaceInterval - 80;
        }
        // update all the enemies bullets if active

        // check bounds of enemies


        if(lost){
            prepareLevel();
        }


        // update the players bullet
        if(bullet.getStatus()){
            bullet.update(fps);
        }

        for(int i =0; i<enemyBullets.length; i++){
            if(enemyBullets[i].getStatus()){
                enemyBullets[i].update(fps);
            }
        }


        if(bullet.getImpactPointY() < 0){
            bullet.setInactive();
        }

        for(int i = 0; i < enemyBullets.length; i++){
            if(enemyBullets[i].getImpactPointY() > screenY){
                enemyBullets[i].setInactive();
            }
        }

        if(bullet.getStatus()) {
            for (int i = 0; i < numEnemies; i++) {
                if (enemies[i].getVisibility()) {
                    if (RectF.intersects(bullet.getRect(), enemies[i].getRect())) {
                        enemies[i].setInvisible();
                        soundPool.play(enemyExplodeID, 1, 1, 0, 0, 1);
                        bullet.setInactive();
                        score = score + 10;

                        if(score == numEnemies * 10){
                            paused = true;
                            score = 0;
                            lives = 3;
                            prepareLevel();
                        }
                    }
                }
            }
        }

        for(int i = 0; i < enemyBullets.length; i++){
            if(enemyBullets[i].getStatus()){
                for(int j = 0; j < numBricks; j++){
                    if(bricks[j].getVisibility()){
                        if(RectF.intersects(enemyBullets[i].getRect(), bricks[j].getRect())){
                            // Boom
                            enemyBullets[i].setInactive();
                            bricks[j].setInvisible();
                            soundPool.play(damageShelterID, 1, 1, 0, 0, 1);
                        }
                    }
                }
            }

        }


        if(bullet.getStatus()){
            for(int i = 0; i < numBricks; i++){
                if(bricks[i].getVisibility()){
                    if(RectF.intersects(bullet.getRect(), bricks[i].getRect())){
                        // boom
                        bullet.setInactive();
                        bricks[i].setInvisible();
                        soundPool.play(damageShelterID, 1, 1, 0, 0, 1);
                    }
                }
            }
        }


        for(int i = 0; i < enemyBullets.length; i++) {
            if (enemyBullets[i].getStatus()) {
                if (RectF.intersects(playerShip.getRect(), enemyBullets[i].getRect())) {
                    enemyBullets[i].setInactive();
                    lives--;
                    soundPool.play(playerExplodeID, 1, 1, 0, 0, 1);

                    // Is it game over?
                    if (lives == 0) {
                        paused = true;
                        lives = 3;
                        score = 0;
                        prepareLevel();

                    }
                }
            }
        }

        /*
        // check for powerup hit
        if(powerup.exists()) {
            powerup.update(fps);
            if (RectF.intersects(bullet.getRect(), powerup.getRect())) {
                switch(powerup.getType()){
                    case 0:
                        // proj speed
                        break;
                    case 1:
                        // burst
                        break;
                    case 2:
                        // extra lives
                        break;
                    case 3:
                        // replenish bricks
                        break;

                }
            }
        }
        */
        // end update
    }

    Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    private void draw(){
        if(holder.getSurface().isValid()){
            canvas = holder.lockCanvas();

            canvas.drawColor(Color.argb(255, 26, 128, 182));
            paint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawBitmap(background, 0, 0, paint);

            // draw ships
            canvas.drawBitmap(playerShip.getBitmap(), playerShip.getX(), screenY - 120, paint);

            // draw enemies
            for(int i = 0; i < numEnemies; i++){
                if(enemies[i].getVisibility()) {
                    if(uhOrOh) {
                        canvas.drawBitmap(enemies[i].getBitmap(), enemies[i].getX(), enemies[i].getY(), paint);
                    }else{
                        canvas.drawBitmap(enemies[i].getBitmap2(), enemies[i].getX(), enemies[i].getY(), paint);
                    }
                }
            }
            paint.setColor(Color.argb(255, 0, 255, 0));
            // draw player bullets
            if(bullet.getStatus()){
                canvas.drawRect(bullet.getRect(), paint);
            }

            paint.setColor(Color.argb(255, 255, 100, 0));
            for(int i = 0; i<enemyBullets.length; i++){
                if(enemyBullets[i].getStatus()){
                    canvas.drawRect(enemyBullets[i].getRect(), paint);
                }
            }

            paint.setColor(Color.argb(255, 0, 255, 0));
            for(int i = 0; i < numBricks; i++){
                if(bricks[i].getVisibility()) {
                    canvas.drawRect(bricks[i].getRect(), paint);
                }
            }

            /*
            // draw powerup
            if(powerup.exists()){
                canvas.drawBitmap(powerup.getBitmap(), powerup.getX(), powerup.getY(), paint);
            }*/

            // draw score and remaining lives
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(80);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 50, 80, paint);

            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause(){
        playing = false;
        try{
            gameThread.join();
        }catch (InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    public void resume(){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:

                paused = false;

                if(motionEvent.getY() > screenY - screenY / 8){
                    if(motionEvent.getX() > screenX / 2){
                        playerShip.setMovementState(playerShip.RIGHT);
                    }else{
                        playerShip.setMovementState(playerShip.LEFT);
                    }
                }
                if(motionEvent.getY() < screenY - screenY / 8 ){
                    // shoot
                    if(bullet.shoot(playerShip.getX()+playerShip.getLength()/2, screenY, bullet.UP)){
                        soundPool.play(shootID, 1, 1, 0,0, 1);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:

                if(motionEvent.getY() > screenY - screenY ){
                    playerShip.setMovementState(playerShip.STOPPED);
                }

                break;
        }
        return true;
    }

}
