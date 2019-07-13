package com.example.imagepdf;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LockPdfAdapter extends BaseAdapter {
    ArrayList<PDFDoc> pdfDocs;
    Context c;
    private static String USER_PASSWORD = "password";
    private static String OWNER_PASSWORD = "password";
    String path;

    public LockPdfAdapter(Context c, ArrayList<PDFDoc> pdFs, String path) {
        this.c=c;
        this.pdfDocs=pdFs;
        this.path=path;
    }

    @Override
    public int getCount() {
        return pdfDocs.size();
    }

    @Override
    public Object getItem(int i) {
        return pdfDocs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            //INFLATE CUSTOM LAYOUT
            view = LayoutInflater.from(c).inflate(R.layout.custom_pdf_lock, viewGroup, false);
        }

        final PDFDoc pdfDoc = (PDFDoc) this.getItem(i);
        TextView nameTxt = (TextView) view.findViewById(R.id.name_pdf);
        ImageView lock_pdf = (ImageView) view.findViewById(R.id.lock_pdf);
        ImageView img = (ImageView) view.findViewById(R.id.pdfImage1);



        nameTxt.setText(pdfDoc.getName());
        img.setImageResource(R.drawable.pdf_icon);

        //VIEW ITEM CLICK
        nameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        lock_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*final Dialog dialog = new Dialog(c);
                dialog.setContentView(R.layout.layout_password);
                dialog.show();
                final EditText ed_password= (EditText) dialog.findViewById(R.id.ed_password);
                final Button btn_lockpdf= (Button) dialog.findViewById(R.id.btn_lockpdf);

                final String password=ed_password.getText().toString().trim();
                btn_lockpdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });*/
                Document document = new Document();
                try {

                    PDDocument doc = PDDocument.load(new File(path +pdfDoc.getName()), "password");
                    if (doc.isEncrypted()) {

                        Toast.makeText(c, "PDF Already locked", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        File f = new File(path +pdfDoc.getName());
                        String finalOutputFile =path+"Encrypt"+pdfDoc.getName();
                        PdfReader reader = new PdfReader(path+pdfDoc.getName());
                        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
                        stamper.setEncryption(USER_PASSWORD.getBytes(), OWNER_PASSWORD.getBytes(),
                                PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);
                        stamper.close();
                        reader.close();

                       /* PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path+pdfDoc.getName()));
                        writer.setEncryption(USER_PASSWORD.getBytes(),OWNER_PASSWORD.getBytes(), PdfWriter.ALLOW_PRINTING,PdfWriter.ENCRYPTION_AES_128);
                        document.open();
                        //document.add(new Paragraph("This is Password Protected PDF document."));
                        document.add();
                        document.close();
                        writer.close();*/
                        f.delete();
                        //dialog.dismiss();
//                        pdfDocs.remove(i);
//                        notifyDataSetChanged();
                        Toast.makeText(c, "Locked PDF Successfully", Toast.LENGTH_SHORT).show();

                    }


                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                }

            }
        });

        return view;

    }


}
