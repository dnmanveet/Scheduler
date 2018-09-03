package nl.kleisauke.compactcalendarviewtoolbar;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.TimeZoneFormat;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class notes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout); // get the reference of TabLayout
//        TabLayout.Tab firstTab = tabLayout.newTab();
//        TabLayout.Tab thirdTab = tabLayout.newTab();
//        thirdTab.setIcon(R.drawable.basel);// Create a new Tab names "First Tab"
//        // set the Text for the first Tab
//        firstTab.setIcon(R.drawable.bas); // set an icon for the first tab
//        tabLayout.addTab(firstTab,true);
//        TabLayout.Tab secondTab = tabLayout.newTab();
//        secondTab.setIcon(R.drawable.baseline_list);
//        tabLayout.addTab(secondTab);
//        tabLayout.addTab(thirdTab);

        // Find the button which will start editing process.
        FloatingActionButton buttonShowIme = (FloatingActionButton) findViewById(R.id.key);
        FloatingActionButton buttonsave = (FloatingActionButton) findViewById(R.id.save);
        EditText editText2 = (EditText) findViewById(R.id.quoteTextArea);
        EditText editText1 = (EditText) findViewById(R.id.TitleTextArea);
        KeyListener originalKeyListener = editText2.getKeyListener();
        // Attach an on-click listener.
        buttonShowIme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Restore key listener - this will make the field editable again.
                editText2.setKeyListener(originalKeyListener);
                // Focus the field.
                editText2.requestFocus();
                // Show soft keyboard for the user to enter the value.
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText2, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText2.getText().toString() == null || editText1.getText().toString() == null || editText2.getText().toString() == "" || editText1.getText().toString() == "") {
                    View parentLayout = findViewById(R.id.notes_writing);
                    Snackbar.make(parentLayout, "Content is empty", Snackbar.LENGTH_LONG).setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright)).show();
                } else if (editText2.getText().toString() != null || editText1.getText().toString() != null ||editText2.getText().toString() != "" || editText1.getText().toString() != "" ) {
                    createandDisplayPdf(editText1.getText().toString(), editText2.getText().toString());
                }
            }

            private void createandDisplayPdf(String s, String s1) {
                Document doc = new Document();

                try {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";

                    File dir = new File(path);
                    if (!dir.exists()) dir.mkdirs();

                    File file = new File(dir, s+".pdf");
                    FileOutputStream fOut = new FileOutputStream(file);

                    PdfWriter.getInstance(doc, fOut);

                    //open the document
                    doc.open();

                    Paragraph p1 = new Paragraph(s);
                    p1.setAlignment(Paragraph.ALIGN_CENTER);
                    Paragraph p2 = new Paragraph(s1);
                    p2.setAlignment(Paragraph.ALIGN_JUSTIFIED);

                    //add paragraph to document
                    doc.add(p1);
                    doc.add(p2);

                } catch (DocumentException de) {
                    Log.e("PDFCreator", "DocumentException:" + de);
                } catch (IOException e) {
                    Log.e("PDFCreator", "ioException:" + e);
                } finally {
                    doc.close();
                }

                viewPdf(s+".pdf", "Dir");
            }

            // Method for opening a pdf file
            private void viewPdf(String file, String directory) {

                File pdffile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+directory+"/"+file);
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(pdffile),"application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    isIntPermissionGranted();
                    startActivity(intent);
                    View parentLayout = findViewById(R.id.notes_writing);
                    Snackbar.make(parentLayout, "Saving file as..."+file+"In location:"+Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+directory+"/"+file, Snackbar.LENGTH_LONG).setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright)).show();

                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                    Toast.makeText(notes.this,"Cannot open...",Toast.LENGTH_LONG).show();
                }
            }

        });

        }
    private boolean isReadPermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("MAina","Permission is granted");
                return true;
            } else {

                Log.v("MAina","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Maina","Permission is granted");
            return true;
        }

    }
    private boolean isIntPermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("MAina","Permission is granted");
                return true;
            } else {

                Log.v("MAina","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Maina","Permission is granted");
            return true;
        }
    }
    }

