package com.example.imagepdf;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MergePdfActivity extends AppCompatActivity {
    List<InputStream> list = new ArrayList<InputStream>();
    String pathmerge;
    Button btn_viewer, btn_app;
    Dialog d, dialog;
    EditText et_save_pdf_file;
    Button save_pdf_btn;
    String pdf;
    List<String> imagePathList=new ArrayList<>();
    List<String> imagesEncodedList=new ArrayList<>();
    String imageEncoded;
    Uri mImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_pdf);
        d = new Dialog(MergePdfActivity.this);
        d.setContentView(R.layout.dialog_mergepdf);
        d.show();
        btn_viewer = d.findViewById(R.id.btn_viewer);
        btn_app = d.findViewById(R.id.btn_app);

        // btn_merge = findViewById(R.id.btn_merge);

        pathmerge = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";
        File path = new File(pathmerge);
        if (!path.exists()) {
            path.mkdirs();
        }


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


    }

    public void startMerge(String pdf, ArrayList<Uri> filepaths){
         try {
                    list.add(new FileInputStream(new File(pathmerge + "/"  + "sneha.pdf")));
                    list.add(new FileInputStream(new File(pathmerge + "/"  + "pp.pdf")));
                    OutputStream out = null;

                    out = new FileOutputStream(new File(pathmerge + "/"  +pdf+ ".pdf"));
                    // Resulting pdf

                    doMerge(list, out);
             Toast.makeText(MergePdfActivity.this, "Merge Pdf Successfully", Toast.LENGTH_SHORT).show();
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

  /*  public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (null != data) { // checking empty selection
                if (null != data.getClipData()) { // checking multiple selection or not
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                    }
                } else {
                    Uri uri = data.getData();
                }
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                    for(int j=0;j<mArrayUri.size();j++){
                        Log.d("msg", "onActivityResult8: "+mArrayUri.get(j));
                    }
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
                showDialog();
                Log.v("LOG_TAG", "Selected Images" + mArrayUri.size() + mArrayUri.get(0));
                //Log.v("Image Path", getImagePath(mArrayUri.get(0)));

            }
        }

    }*/

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
}
