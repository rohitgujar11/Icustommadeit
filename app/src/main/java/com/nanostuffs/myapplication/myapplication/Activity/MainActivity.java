package com.nanostuffs.myapplication.myapplication.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.nanostuffs.myapplication.R;


public class MainActivity extends Activity  {


    //private MenuClass menuClass;
    private RelativeLayout.LayoutParams layoutParams;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    /*    menuClass = new MenuClass();
        menuClass.simpleSlidingDrawer(this, "Home_Activity", 0);*/



    }

    public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter
        // 'String loc' if you
        // want to save the new
        // image - left some
        // code to do that at
        // the bottom
        Bitmap cs = null;

        int width, height = 0;

        if (c.getWidth() > s.getWidth()) {
            width = c.getWidth();
            height = c.getHeight() + s.getHeight();
        } else {
            width = s.getWidth();
            height = c.getHeight() + s.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(), null);

        // this is an extra bit I added, just incase you want to save the new
        // image somewhere and then return the location
        /*
         * String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";
		 *
		 * OutputStream os = null; try { os = new FileOutputStream(loc +
		 * tmpImg); cs.compress(CompressFormat.PNG, 100, os); }
		 * catch(IOException e) { Log.e("combineImages",
		 * "problem combining images", e); }
		 */

        return cs;
    }

    /*
     * //Method of creating mask runtime public void makeMaskImage(ImageView
     * mImageView, int mContent) { Bitmap original =
     * BitmapFactory.decodeResource(getResources(), mContent); Bitmap mask =
     * BitmapFactory.decodeResource(getResources(),R.drawable.mask); Bitmap
     * result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),
     * Config.ARGB_8888); Canvas mCanvas = new Canvas(result); Paint paint = new
     * Paint(Paint.ANTI_ALIAS_FLAG); paint.setXfermode(new
     * PorterDuffXfermode(PorterDuff.Mode.DST_IN)); mCanvas.drawBitmap(original,
     * 0, 0, null); mCanvas.drawBitmap(mask, 0, 0, paint);
     * paint.setXfermode(null); mImageView.setImageBitmap(result);
     * mImageView.setScaleType(ScaleType.CENTER);
     * mImageView.setBackgroundResource(R.drawable.frame); }
     */






}