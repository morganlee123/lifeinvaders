package me.morgansandler.morgan.hypedgalaga;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class HypedGalagaActivity extends Activity {

    HypedGalagaView hgView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        hgView = new HypedGalagaView(this, size.x, size.y);
        setContentView(hgView);
    }

    @Override
    protected void onResume(){
        super.onResume();

        hgView.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();

        hgView.pause();
    }
}
