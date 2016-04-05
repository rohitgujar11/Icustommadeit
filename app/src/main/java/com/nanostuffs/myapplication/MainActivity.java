package com.nanostuffs.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class MainActivity extends Activity implements OnClickListener {

    float[] lastEvent = null;
    float d = 0f;
    float newRot = 0f;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private float scale = 0;
    private float newDist = 0;
    int n;
    // Fields
    private String TAG = this.getClass().getSimpleName();
    TextView text;
    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    float oldDist = 1f;

    ImageView zoomI, mainImg;
    Button share, resize;
    static Bitmap b, r;
    static File f;
    static Bitmap imgFull = null;

    private float dx; // postTranslate X distance
    private float dy; // postTranslate Y distance
    private float[] matrixValues = new float[9];
    float matrixX = 0; // X coordinate of matrix inside the ImageView
    float matrixY = 0; // Y coordinate of matrix inside the ImageView
    float width = 0; // width of drawable
    float height = 0; // height of drawable

    float MAX_ZOOM = 2;
    float MIN_ZOOM = 0.09f;
    float newScale = 0f;

    static Dialog dialog;

    private GestureDetector gestureDetector;
    OnTouchListener gestureListener;
    Intent go;
    RelativeLayout l;
    ImageView testImg;
    public final int CHOOSE_PHOTO = 112;
    public final int TAKE_PHOTO = 113;
    private String filePath = null;
    private Uri uri = null;
    int device_width, device_height;
    String msg = "";
    private android.widget.RelativeLayout.LayoutParams layoutParams;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testImg = (ImageView) findViewById(R.id.tv);
        Display mDisplay = getWindowManager().getDefaultDisplay();
        device_width = mDisplay.getWidth();
        text = (TextView) findViewById(R.id.text);
        device_height = mDisplay.getHeight();
        ImageButton plus = (ImageButton) findViewById(R.id.imageButton);
        plus.setOnClickListener(this);
        testImg.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                ImageView view = (ImageView) v;
                // lin1.setLayoutParams(new LayoutParams(
                // LinearLayout.LayoutParams.FILL_PARENT,
                // LinearLayout.LayoutParams.FILL_PARENT));
                // LinearLayout.LayoutParams params = new
                // LinearLayout.LayoutParams(
                // LinearLayout.LayoutParams.FILL_PARENT,
                // LinearLayout.LayoutParams.FILL_PARENT);
                // params.setMargins(0, 0, 0, 300);
                // lin1.setLayoutParams(params);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        start.set(event.getX(), event.getY());
                        mode = DRAG;
                        lastEvent = null;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        lastEvent = new float[4];
                        lastEvent[0] = event.getX(0);
                        lastEvent[1] = event.getX(1);
                        lastEvent[2] = event.getY(0);
                        lastEvent[3] = event.getY(1);
                        d = rotation(event);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        lastEvent = null;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            // ...
                            matrix.set(savedMatrix);

                            matrix.getValues(matrixValues);
                            matrixX = matrixValues[2];
                            matrixY = matrixValues[5];
                            width = matrixValues[0]
                                    * (((ImageView) view).getDrawable()
                                    .getIntrinsicWidth());
                            height = matrixValues[4]
                                    * (((ImageView) view).getDrawable()
                                    .getIntrinsicHeight());

                            dx = event.getX() - start.x;
                            dy = event.getY() - start.y;

                            // if image will go outside left bound
                            if (matrixX + dx < 0) {
                                dx = -matrixX;
                            }
                            // if image will go outside right bound
                            if (matrixX + dx + width > view.getWidth()) {
                                dx = view.getWidth() - matrixX - width;
                            }
                            // if image will go oustside top bound
                            if (matrixY + dy < 0) {
                                dy = -matrixY;
                            }
                            // if image will go outside bottom bound
                            if (matrixY + dy + height > view.getHeight()) {
                                dy = view.getHeight() - matrixY - height;
                            }
                            matrix.postTranslate(dx, dy);
                        } else if (mode == ZOOM && event.getPointerCount() == 2) {
                        /*
                         * if (newDist > 10f) { float scale = newDist / oldDist;
						 * matrix.postScale(scale, scale, mid.x, mid.y); }
						 */

                            float newDist = spacing(event);
                            if (newDist > 10f) {

                                matrix.set(savedMatrix);
                                matrix.getValues(matrixValues);

                                if (matrixValues[0] < MAX_ZOOM
                                        && matrixValues[0] > MIN_ZOOM) {
                                    scale = newDist / oldDist;
                                    scale = Math.max(MIN_ZOOM,
                                            Math.min(scale, MAX_ZOOM));
                                    matrix.postScale(scale, scale, mid.x, mid.y);
                                    view.setImageMatrix(matrix);
                                } else if (matrixValues[0] >= MAX_ZOOM) {
                                    newScale = newDist / oldDist;
                                    if (newScale < scale) {
                                        scale = newScale;
                                        scale = Math.max(MIN_ZOOM,
                                                Math.min(scale, MAX_ZOOM));
                                        matrix.postScale(scale, scale, mid.x, mid.y);
                                        view.setImageMatrix(matrix);
                                    }
                                } else if (matrixValues[0] <= MIN_ZOOM) {
                                    newScale = newDist / oldDist;
                                    if (newScale > scale) {
                                        scale = newScale;
                                        scale = Math.max(MIN_ZOOM,
                                                Math.min(scale, MAX_ZOOM));
                                        matrix.postScale(scale, scale, mid.x, mid.y);
                                        view.setImageMatrix(matrix);
                                    }
                                }
                            }

						/*
                         * if (lastEvent != null) { newRot = rotation(event);
						 * float r = newRot - d; matrix.postRotate(r,
						 * view.getMeasuredWidth() / 2, view.getMeasuredHeight()
						 * / 2); }
						 */
                        }
                        break;
                }

                view.setImageMatrix(matrix);
                return true;
            }
        });

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
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.imageButton:
                showMainDialog();
                break;
            default:
                break;
        }
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);

        return (float) Math.toDegrees(radians);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void showPictureDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("CHOOSE PICTURE");
        String[] items = {"GALLERY", "CAMERA"};

        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        choosePhotoFromGallary();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;

                }
            }
        });
        dialog.show();
    }

    private void showMainDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SELECT ONE OF FOLLWOING");
        String[] items = {"TEXT", "IMAGE"};

        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        dialog_edit();
                    break;
                    case 1:
                        text.setText("");
                        showPictureDialog();
                        break;

                }
            }
        });
        dialog.show();
    }

    private void choosePhotoFromGallary() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, CHOOSE_PHOTO);

    }

    private void takePhotoFromCamera() {
        Calendar cal = Calendar.getInstance();
        File file = new File(Environment.getExternalStorageDirectory(),
                (cal.getTimeInMillis() + ".jpg"));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {

            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        uri = Uri.fromFile(file);
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(i, TAKE_PHOTO);
    }

    public void dialog_edit() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        alertDialog.setMessage("Type Here");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();
                        if (password.compareTo("") == 0) {

                        } else {
                            dialog.cancel();
                            text.setText(input.getText().toString());
                        }
                    }
                });


        alertDialog.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (data != null) {

                    uri = data.getData();
                    if (uri != null) {

                        beginCrop(uri);

                    } else {
                        Toast.makeText(this, "unable to select image",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (uri != null) {
                        beginCrop(uri);


                    } else {
                        Toast.makeText(this, "unable to select image",
                                Toast.LENGTH_LONG).show();
                    }
                }

                break;
            case Crop.REQUEST_CROP:

                if (data != null)
                    handleCrop(resultCode, data);

                break;
        }

    }

    private void beginCrop(Uri source) {
        // Uri outputUri = Uri.fromFile(new File(registerActivity.getCacheDir(),
        // "cropped"));
        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), (Calendar.getInstance()
                .getTimeInMillis() + ".jpg")));
        new Crop(source).output(outputUri).asSquare().withMaxSize(80, 80)
                .start(this);

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null,
                null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            filePath = getRealPathFromURI(Crop.getOutput(result));

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

            int rotate = 0;
            try {
                File imageFile = new File(filePath);
                ExifInterface exif = new ExifInterface(
                        imageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();

            matrix.postRotate(rotate);


            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight()
                    ,
                    matrix, true);
            createDirectoryAndSaveFile(bitmap, filePath);


            testImg.setImageBitmap(bitmap);


        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory()
                + "/DirName");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/DirName/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/DirName/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}