package com.example.imagepdf;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.codec.wmf.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AddWaterMark extends AppCompatActivity {
    PDFDoc file;
    ListView lv;
    AddWatermarkAdapter customAdapter;
    ImageView selected_share_pdf, hide_checkBox;
    TextView no_files;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_water_mark);
        selected_share_pdf = findViewById(R.id.share_selected2);
        hide_checkBox = findViewById(R.id.hide_checkbox2);
        lv = (ListView) findViewById(R.id.list4);
        no_files = findViewById(R.id.no_pdf_file2);

        if (getPDFs().isEmpty()) {
            no_files.setVisibility(View.VISIBLE);
        }
        lv.setLongClickable(true);
        customAdapter = new AddWatermarkAdapter(AddWaterMark.this, getPDFs(), false, selected_share_pdf, hide_checkBox);
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
                customAdapter = new AddWatermarkAdapter(AddWaterMark.this, getPDFs(), false, selected_share_pdf, hide_checkBox);
                lv.setAdapter(customAdapter);
                selected_share_pdf.setVisibility(View.VISIBLE);
                hide_checkBox.setVisibility(View.VISIBLE);
                customAdapter.notifyDataSetChanged();
                Log.e("Long Pressed", pos + "");
                return true;
            }
        });
    }

    private ArrayList<PDFDoc> getPDFs() {
        ArrayList<PDFDoc> pdfDocs = new ArrayList<>();
        //TARGET FOLDER
        File downloadsFolder = new File(Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/");

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
