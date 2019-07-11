package com.example.imagepdf;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PdfFilesActivity extends AppCompatActivity {
    ListView lv;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    CustomAdapter customAdapter;
    ImageView selected_share_pdf, hide_checkBox;
    TextView no_files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_files);


        selected_share_pdf = findViewById(R.id.share_selected);
        hide_checkBox = findViewById(R.id.hide_checkbox);
        lv = (ListView) findViewById(R.id.list2);
        no_files = findViewById(R.id.no_pdf_file);

        if (getPDFs().isEmpty()) {
            no_files.setVisibility(View.VISIBLE);
        }
        lv.setLongClickable(true);
        customAdapter = new CustomAdapter(PdfFilesActivity.this, getPDFs(), false, selected_share_pdf, hide_checkBox);
        lv.setAdapter(customAdapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//
//            }

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                customAdapter = new CustomAdapter(PdfFilesActivity.this, getPDFs(), true, selected_share_pdf, hide_checkBox);
                lv.setAdapter(customAdapter);
                selected_share_pdf.setVisibility(View.VISIBLE);
                hide_checkBox.setVisibility(View.VISIBLE);
                customAdapter.notifyDataSetChanged();
                Log.e("Long Pressed", pos + "");


                Log.e("Long Pressed", pos + "");
                return true;
            }
        });
    }

    private ArrayList<PDFDoc> getPDFs() {
        ArrayList<PDFDoc> pdfDocs = new ArrayList<>();
        //TARGET FOLDER
        File downloadsFolder = new File(Environment.getExternalStorageDirectory() + "/AVI PDF FORMS//");

        PDFDoc pdfDoc;

        if (downloadsFolder.exists()) {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files = downloadsFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                if (file.getPath().endsWith("pdf")) {
                    pdfDoc = new PDFDoc();
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());

                    pdfDocs.add(pdfDoc);
                }

            }
        }

        return pdfDocs;
    }
}
