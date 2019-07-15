package com.example.imagepdf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleMultiplePdf extends AppCompatActivity {
   LinearLayout ll_multiplepdf,ll_singlepdf;
    List<String> imagePathList=new ArrayList<>();
    List<String> imagesEncodedList=new ArrayList<>();
    String imageEncoded;
    Uri mImageUri;
    Dialog dialog1;
    String p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_multiple_pdf);
        ll_multiplepdf=findViewById(R.id.ll_multiplepdf);
        ll_singlepdf=findViewById(R.id.ll_singlepdf);

        p = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";
        File path = new File(p);
        if (!path.exists()) {
            path.mkdirs();
        }
        ll_singlepdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SingleMultiplePdf.this,CreatePdfActivity.class));
            }
        });

       ll_multiplepdf.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent();
               intent.setType("image/*");
               intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
               intent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
           }
       });
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
                        Log.d("msg", "onActivityResult: "+data);
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
                        Log.d("msg", "onActivityResult5: "+mArrayUri.get(0));

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
            Toast.makeText(this, "Something went wrong" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
            Log.v("Exception", e + "");

        }
    }


    void showSavePdfDialog(final boolean more) {
        dialog1=new Dialog(this);
        dialog1.setContentView(R.layout.save_pdf_dialog);
        dialog1.show();
        final EditText pdf_name = dialog1.findViewById(R.id.pdf_name);
        Button merge_files = dialog1.findViewById(R.id.save_pdf);

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
        dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
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
        PdfWriter.getInstance(document, new FileOutputStream(p + "/" + pdfFileName + ".pdf"));
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
        dialog1.dismiss();
    }

    public void createPdfSingleImage(String pdfFileName) throws IOException, DocumentException {
        Image img = Image.getInstance(getImagePath(mImageUri));
        Document document = new Document(img);
        PdfWriter.getInstance(document, new FileOutputStream(p + "/" + pdfFileName + ".pdf"));
        document.open();
        img = Image.getInstance(getImagePath(mImageUri));
        document.setPageSize(img);
        document.newPage();
        img.setAbsolutePosition(0, 0);
        document.add(img);

        document.close();
        dialog1.dismiss();
        Toast.makeText(this, "PDF Saved!", Toast.LENGTH_SHORT).show();
    }
}
