package com.example.imagepdf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class UnlockPdfActivity extends AppCompatActivity {
    ListView lv;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    UnlockPdfAdapter unlockPdfAdapter;
    ImageView selected_share_pdf, hide_checkBox;
    TextView no_files;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_pdf);

        selected_share_pdf = findViewById(R.id.share_selected5);
        hide_checkBox = findViewById(R.id.hide_checkbox5);
        lv = (ListView) findViewById(R.id.list5);
        no_files = findViewById(R.id.no_pdf_file5);


        path = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";

        if (getPDFs().isEmpty()) {
            no_files.setVisibility(View.VISIBLE);
        }
        //lv.setLongClickable(true);
        unlockPdfAdapter = new UnlockPdfAdapter(UnlockPdfActivity.this, getPDFs(),path);
        lv.setAdapter(unlockPdfAdapter);
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
