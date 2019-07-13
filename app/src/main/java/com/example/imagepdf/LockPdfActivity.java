package com.example.imagepdf;

import androidx.appcompat.app.AppCompatActivity;

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

public class LockPdfActivity extends AppCompatActivity {
    ListView lv;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    LockPdfAdapter lockPdfAdapter;
    ImageView selected_share_pdf, hide_checkBox;
    TextView no_files;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_pdf);

        selected_share_pdf = findViewById(R.id.share_selected1);
        hide_checkBox = findViewById(R.id.hide_checkbox1);
        lv = (ListView) findViewById(R.id.list3);
        no_files = findViewById(R.id.no_pdf_file1);


        path = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";

        if (getPDFs().isEmpty()) {
            no_files.setVisibility(View.VISIBLE);
        }
        //lv.setLongClickable(true);
        lockPdfAdapter = new LockPdfAdapter(LockPdfActivity.this, getPDFs(),path);
        lv.setAdapter(lockPdfAdapter);


      /*  lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                lockPdfAdapter = new LockPdfAdapter(LockPdfActivity.this, getPDFs(),path);
                lv.setAdapter(lockPdfAdapter);
                selected_share_pdf.setVisibility(View.VISIBLE);
                hide_checkBox.setVisibility(View.VISIBLE);
                lockPdfAdapter.notifyDataSetChanged();
                Log.e("Long Pressed", pos + "");


                Log.e("Long Pressed", pos + "");
                return true;
            }
        });*/
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
