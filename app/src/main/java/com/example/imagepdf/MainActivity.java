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
import com.itextpdf.text.pdf.PdfWriter;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
LinearLayout ll_createpdf,ll_mergepdf,ll_savedpdf,ll_lockpdf,ll_markpdf,ll_unlockpdf;
    List<String> imagePathList=new ArrayList<>();
    List<String> imagesEncodedList=new ArrayList<>();
    String imageEncoded;
    Uri mImageUri;

    EditText et_save_pdf_file;
    Button save_pdf_btn;
    Dialog dialog;
    static Image image;
    String root,watermarkpath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root= Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";
        watermarkpath= Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/WATERMARK";
        File path = new File(root);
        File waterpath = new File(watermarkpath);
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
                != PackageManager.PERMISSION_GRANTED ) {

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


            }}
        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/

        ll_createpdf=findViewById(R.id.ll_createpdf);
        ll_mergepdf=findViewById(R.id.ll_mergepdf);
        ll_savedpdf=findViewById(R.id.ll_savedpdf);
        ll_lockpdf=findViewById(R.id.ll_lockpdf);
        ll_markpdf=findViewById(R.id.ll_markpdf);
        ll_unlockpdf=findViewById(R.id.ll_unlockpdf);

        ll_createpdf.setOnClickListener(this);
        ll_mergepdf.setOnClickListener(this);
        ll_savedpdf.setOnClickListener(this);
        ll_lockpdf.setOnClickListener(this);
        ll_markpdf.setOnClickListener(this);
        ll_unlockpdf.setOnClickListener(this);



    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_createpdf:
                startActivity(new Intent(MainActivity.this,CreatePdfActivity.class));
                break;

            case R.id.ll_mergepdf:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                break;

            case R.id.ll_savedpdf:
                startActivity(new Intent(MainActivity.this,PdfFilesActivity.class));
                break;

            case R.id.ll_lockpdf:
                startActivity(new Intent(MainActivity.this,LockPdfActivity.class));
                break;
            case R.id.ll_markpdf:
                startActivity(new Intent(MainActivity.this,AddWaterMark.class));
                break;

            case R.id.ll_unlockpdf:
                startActivity(new Intent(MainActivity.this,UnlockPdfActivity.class));

                break;
        }
    }

    @Override
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
    }

    void showSavePdfDialog(final boolean more) {
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.save_pdf_dialog);
        dialog.show();
        final EditText pdf_name = dialog.findViewById(R.id.pdf_name);
        Button merge_files = dialog.findViewById(R.id.save_pdf);

        merge_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pdf_name.getText().toString().equals("")) {
                    pdf_name.setError("Enter Pdf Name");
                } else {
                    try {
                        if (more) {
                            createPdf(pdf_name.getText().toString());
                        } else {
                            createPdfSingleImage(pdf_name.getText().toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                imagePathList.clear();
            }
        });

    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public void createPdf(String pdfFileName) throws IOException, DocumentException {
        Image img = Image.getInstance(imagePathList.get(0));
        Document document = new Document(img);
        PdfWriter.getInstance(document, new FileOutputStream(root + "/" + pdfFileName + ".pdf"));
        document.open();
        for (String image : imagePathList) {
            img = Image.getInstance(image);
            document.setPageSize(img);
            document.newPage();
            img.setAbsolutePosition(0, 0);
            document.add(img);
        }
        document.close();
        Toast.makeText(this, "PDF Saved!", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    public void createPdfSingleImage(String pdfFileName) throws IOException, DocumentException {
        Image img = Image.getInstance(getImagePath(mImageUri));
        Document document = new Document(img);
        PdfWriter.getInstance(document, new FileOutputStream(root + "/" + pdfFileName + ".pdf"));
        document.open();
        img = Image.getInstance(getImagePath(mImageUri));
        document.setPageSize(img);
        document.newPage();
        img.setAbsolutePosition(0, 0);
        document.add(img);

        document.close();
        dialog.dismiss();
        Toast.makeText(this, "PDF Saved!", Toast.LENGTH_SHORT).show();
    }
}