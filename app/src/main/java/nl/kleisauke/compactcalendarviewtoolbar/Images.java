package nl.kleisauke.compactcalendarviewtoolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Images extends AppCompatActivity {

    private String[]        FilePathStrings;
    private File[]          listFile;
    GridView                grid;
    GridViewAdapter         adapter;
    File                    file;
    public static Bitmap    bmp = null;
    ImageView               imageview;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        // Check for SD Card
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this, "Error! No SDCARD Found!",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            // Locate the image folder in your SD Card
            file = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/saved_signature");
        }
        if (file.isDirectory())
        {
            listFile = file.listFiles();
            FilePathStrings = new String[listFile.length];
            for (int i = 0; i < listFile.length; i++)
            {
                FilePathStrings[i] = listFile[i].getAbsolutePath();
            }
        }
        grid = (GridView)findViewById(R.id.gridview);
        adapter = new GridViewAdapter(this, FilePathStrings);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view,
                                     int position, long id)
            {
                imageview = (ImageView)findViewById(R.id.imageView1);
                int targetWidth = 700;
                int targetHeight = 500;
                BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
                bmpOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(FilePathStrings[position],
                        bmpOptions);
                int currHeight = bmpOptions.outHeight;
                int currWidth = bmpOptions.outWidth;
                int sampleSize = 1;
                if (currHeight > targetHeight || currWidth > targetWidth)
                {
                    if (currWidth > currHeight)
                        sampleSize = Math.round((float)currHeight
                                / (float)targetHeight);
                    else
                        sampleSize = Math.round((float)currWidth
                                / (float)targetWidth);
                }
                bmpOptions.inSampleSize = sampleSize;
                bmpOptions.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeFile(FilePathStrings[position],
                        bmpOptions);
                imageview.setImageBitmap(bmp);
                imageview.setScaleType(ImageView.ScaleType.FIT_XY);
                bmp = null;

            }
        });

    }
    @Override
    public void onStart() {

        super.onStart();
        file = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/saved_signature");

    }
}

