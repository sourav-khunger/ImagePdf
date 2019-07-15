package com.example.imagepdf;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout ll_createpdf, ll_mergepdf, ll_savedpdf, ll_lockpdf, ll_markpdf, ll_unlockpdf;
    List<InputStream> list = new ArrayList<InputStream>();
    Button btn_viewer, btn_app;
    Dialog d, dialog;
    EditText et_save_pdf_file;
    Button save_pdf_btn;
    String pdf;
    String root, watermarkpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        d = new Dialog(MainActivity.this);
        d.setContentView(R.layout.dialog_mergepdf);
        root = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";
        File path = new File(root);
        if (!path.exists()) {
            path.mkdirs();
//            waterpath.mkdirs();

        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.


            }
        }
        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/

        ll_createpdf = findViewById(R.id.ll_createpdf);
        ll_mergepdf = findViewById(R.id.ll_mergepdf);
        ll_savedpdf = findViewById(R.id.ll_savedpdf);
        ll_lockpdf = findViewById(R.id.ll_lockpdf);
        ll_markpdf = findViewById(R.id.ll_markpdf);
        ll_unlockpdf = findViewById(R.id.ll_unlockpdf);

        ll_createpdf.setOnClickListener(this);
        ll_mergepdf.setOnClickListener(this);
        ll_savedpdf.setOnClickListener(this);
        ll_lockpdf.setOnClickListener(this);
        ll_markpdf.setOnClickListener(this);
        ll_unlockpdf.setOnClickListener(this);




    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_createpdf:
                startActivity(new Intent(MainActivity.this, Createpdf2.class));
                break;

            case R.id.ll_mergepdf:
                d.show();
                btn_viewer = d.findViewById(R.id.btn_viewer);
                btn_app = d.findViewById(R.id.btn_app);

                // btn_merge = findViewById(R.id.btn_merge);



                btn_viewer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent filesIntent;
                        filesIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        filesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        filesIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        filesIntent.setType("application/pdf");  //use image/* for photos, etc.
                        startActivityForResult(filesIntent, 1);


                        //Toast.makeText(MergePdfActivity.this, "Done", Toast.LENGTH_SHORT).show();


                    }
                });

               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);*/
                break;

            case R.id.ll_savedpdf:
                startActivity(new Intent(MainActivity.this, PdfFilesActivity.class));
                break;

            case R.id.ll_lockpdf:
                startActivity(new Intent(MainActivity.this, LockPdfActivity.class));
                break;
            case R.id.ll_markpdf:
                startActivity(new Intent(MainActivity.this, AddWaterMark.class));
                break;

            case R.id.ll_unlockpdf:
                startActivity(new Intent(MainActivity.this, UnlockPdfActivity.class));

                break;
        }
    }

    public void startMerge(String pdf, ArrayList<Uri> filepaths){
        //Toast.makeText(this, ""+filepaths.get(0), Toast.LENGTH_SHORT).show();
        Log.d("msg", "startMerge: "+filepaths.get(0));
        try {
            list.add(new FileInputStream(new File(root + "/"  + "sneha.pdf")));
            list.add(new FileInputStream(new File(root + "/"  + "pp.pdf")));
            OutputStream out = null;

            out = new FileOutputStream(new File(root + "/"  +pdf+ ".pdf"));
            // Resulting pdf

            doMerge(list, out);
            Toast.makeText(MainActivity.this, "Merge Pdf Successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void doMerge(List<InputStream> list, OutputStream outputStream)
            throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();

        for (InputStream in : list) {
            PdfReader reader = new PdfReader(in);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                document.newPage();
                //import the page from source pdf
                PdfImportedPage page = writer.getImportedPage(reader, i);
                //add the page to the destination pdf
                cb.addTemplate(page, 0, 0);

            }
        }

        outputStream.flush();
        document.close();
        outputStream.close();
    }

    public void showDialog(final ArrayList<Uri> filepaths) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.save_as_dialog);
        dialog.show();
        et_save_pdf_file = dialog.findViewById(R.id.et_pdf_file_name);
        save_pdf_btn = dialog.findViewById(R.id.pdf_gen_btn);

        save_pdf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_save_pdf_file.getText().toString().equals("")) {
                    et_save_pdf_file.setError("Please Enter Name");
                } else {
                    pdf = et_save_pdf_file.getText().toString();
                    try {
                        startMerge(pdf,filepaths);
                    } catch (Exception e) {
                    }
                    Log.e("PDF", "PDF GENERATED!");
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
        String path = "";
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                ClipData clipData = data.getClipData();

                //null and not null path
                if(clipData == null){
                    path += data.getData().toString();
                }else{
                    for(int i=0; i<clipData.getItemCount(); i++){
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        path += uri.toString() + "\n";
                        mArrayUri.add(uri);
                    }
                }
            }
        }
        showDialog(mArrayUri);
    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    mImageUri = data.getData();
                    imagePathList.add(getImagePath(mImageUri));
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    showSavePdfDialog(false);
                    Log.v("Image Path SINGLE", imagePathList.get(0));

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            imagePathList.add(getImagePath(uri));
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size() + mArrayUri.get(0));
                        Log.v("Image Path", getImagePath(mArrayUri.get(0)));
                        showSavePdfDialog(true);

                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong" + e, Toast.LENGTH_LONG)
                    .show();
            Log.v("Exception", e + "");

        }
    }*/

}