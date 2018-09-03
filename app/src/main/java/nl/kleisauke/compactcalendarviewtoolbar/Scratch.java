package nl.kleisauke.compactcalendarviewtoolbar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Scratch extends AppCompatActivity {

    SignatureView signatureView;
    public File myDir;
    public String fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch);

        this.signatureView = new SignatureView(Scratch.this);
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.ll);
        ll.addView(signatureView);

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout); // get the reference of TabLayout
////        TabLayout.Tab firstTab = tabLayout.newTab();
//        TabLayout.Tab thirdTab = tabLayout.newTab();
//        thirdTab.setIcon(R.drawable.basel);// Create a new Tab names "First Tab"
//        // set the Text for the first Tab
////        firstTab.setIcon(R.drawable.bas); // set an icon for the first tab
////        tabLayout.addTab(firstTab);
//        TabLayout.Tab secondTab = tabLayout.newTab();
//        secondTab.setIcon(R.drawable.baseline_list);
//        tabLayout.addTab(secondTab);
//        tabLayout.addTab(thirdTab);

        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.save);
        FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete);
        FloatingActionButton share = (FloatingActionButton) findViewById(R.id.share);
        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.back);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage(signatureView.getSignature());
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearSignature();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveImage1(signatureView.getSignature());

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Scratch.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });


    }

    final void saveImage(Bitmap signature) {

//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                Uri.fromParts("package", getContext().getPackageName(), null));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        getContext().startActivity(intent);

        isStoragePermissionGranted();


        String root = Environment.getExternalStorageDirectory().toString();

        // the directory where the signature will be saved
        File myDir = new File(root + "/saved_signature");



        double r = Math.random();

        // make the directory if it does not exist yet
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        // set the file name of your choice
        String fname = r + "signature.png";
        // in our case, we delete the previous file, you can remove this
        File file = new File(myDir, fname);


//        if (file.exists()) {
//            file.delete();
//        }

        try {
            // save the signature
            addJpgSignatureToGallery(signature);
            FileOutputStream out = new FileOutputStream(file);
            Toast.makeText(this,"started",Toast.LENGTH_LONG).show();
            signature.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));

            Toast.makeText(this, "Signature saved.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Enable Storage permission in app settings..",Toast.LENGTH_LONG).show();
        }
    }
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }
    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        Scratch.this.sendBroadcast(mediaScanIntent);
    }

    private boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    final void saveImage1(Bitmap signature) {

//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                Uri.fromParts("package", getContext().getPackageName(), null));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        getContext().startActivity(intent);

        isStoragePermissionGranted();


        String root = Environment.getExternalStorageDirectory().toString();

        // the directory where the signature will be saved
        File myDir = new File(root + "/saved_signature");



        double r = Math.random();

        // make the directory if it does not exist yet
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        // set the file name of your choice
        String fname = r + "signature.png";
        // in our case, we delete the previous file, you can remove this
        File file = new File(myDir, fname);


//        if (file.exists()) {
//            file.delete();
//        }

        saveImage(signatureView.getSignature());
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        final File photoFile = new File(myDir, fname);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
        startActivity(Intent.createChooser(shareIntent, "Share image using"));

        try {
            // save the signature
            addJpgSignatureToGallery(signature);
            FileOutputStream out = new FileOutputStream(file);
            Toast.makeText(this,"started",Toast.LENGTH_LONG).show();
            signature.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));

            Toast.makeText(this, "Signature saved.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Enable Storage permission in app settings..",Toast.LENGTH_LONG).show();
        }
    }

    private boolean isStoragePermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("MAina","Permission is granted");
                return true;
            } else {

                Log.v("MAina","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Maina","Permission is granted");
            return true;
        }
    }

    private class SignatureView extends View {

        // set the stroke width
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public SignatureView(Context context) {

            super(context);
//
//            String selected = spinner.getSelectedItem().toString();
//            if (selected.equals("Black")) {
//                paint.setColor(Color.BLACK);
//            }
//            else if (selected.equals("Green")) {
//                paint.setColor(Color.GREEN);
//
//            }
//            else if (selected.equals("Cyan")) {
//                paint.setColor(Color.CYAN);
//
//            }
//            else if (selected.equals("Red")) {
//                paint.setColor(Color.RED);
//            }
//            else if (selected.equals("Gray")) {
//                paint.setColor(Color.GRAY);
//
//            }

            paint.setAntiAlias(true);
//          paint.setColor(Color.CYAN);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);

            // set the bg color as white
            setBackgroundColor(Color.WHITE);

            // width and height should cover the screen
//            setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));


        }


        /**
         * Get signature
         *
         * @return
         */
        protected Bitmap getSignature() {

            Bitmap signatureBitmap = null;

            // set the signature bitmap
            if (signatureBitmap == null) {
                signatureBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.RGB_565);
            }

            // important for saving signature
            final Canvas canvas = new Canvas(signatureBitmap);
            this.draw(canvas);

            return signatureBitmap;
        }

        /**
         * clear signature canvas
         */
        private void clearSignature() {
            path.reset();
            this.invalidate();
        }

        // all touch events during the drawing
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(this.path, this.paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    path.moveTo(eventX, eventY);

                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);

                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:

                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }


        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }

    }

}

