package com.example.imagepdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.File;

public class PDFActivity extends AppCompatActivity {
    TextView pdf_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        PDFView pdfView = findViewById(R.id.pdfView);
        pdf_name = findViewById(R.id.pdffile_name);
        //SCROLLBAR TO ENABLE SCROLLING
//        ScrollBar scrollBar = (ScrollBar) findViewById(R.id.scrollBar);
//        pdfView.setScrollBar(scrollBar);
        //VERTICAL SCROLLING
//        scrollBar.setHorizontal(false);
        //SACRIFICE MEMORY FOR QUALITY
        //pdfView.useBestQuality(true)

        //UNPACK OUR DATA FROM INTENT
        Intent i = this.getIntent();
        String path = i.getExtras().getString("PATH");
        String name = i.getExtras().getString("name");
        pdf_name.setText(name);

        //GET THE PDF FILE
        File file = new File(path);

        if (file.canRead()) {
            //LOAD IT
            pdfView.fromFile(file).defaultPage(1).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    Toast.makeText(PDFActivity.this, String.valueOf(nbPages), Toast.LENGTH_LONG).show();
                }
            }).load();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
