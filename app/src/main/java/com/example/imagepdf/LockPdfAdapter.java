package com.example.imagepdf;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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

import com.example.imagepdf.database.DatabaseHelper;
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

import static com.example.imagepdf.Constants.MASTER_PWD_STRING;
import static com.example.imagepdf.Constants.appName;

public class LockPdfAdapter extends BaseAdapter {
    ArrayList<PDFDoc> pdfDocs;
    Context c;
    String path;
    SharedPreferences sp_password,mSharedPrefs;
    SharedPreferences.Editor editor_password;
    private PDFEncryptionUtility mPDFEncryptionUtils;




    public LockPdfAdapter(Context c, ArrayList<PDFDoc> pdFs, String path) {
        this.c = c;
        this.pdfDocs = pdFs;
        this.path = path;
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
        mPDFEncryptionUtils = new PDFEncryptionUtility((Activity) c);

        final PDFDoc pdfDoc = (PDFDoc) this.getItem(i);
        final TextView nameTxt = (TextView) view.findViewById(R.id.name_pdf);
        ImageView lock_pdf = (ImageView) view.findViewById(R.id.lock_pdf);
        ImageView img = (ImageView) view.findViewById(R.id.pdfImage1);

        final Dialog dialog = new Dialog(c);
        dialog.setContentView(R.layout.layout_password);
        final EditText ed_password = (EditText) dialog.findViewById(R.id.ed_password);
        final Button btn_lockpdf = (Button) dialog.findViewById(R.id.btn_lockpdf);
        final String password = ed_password.getText().toString().trim();

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        sp_password=c.getSharedPreferences("STORE_PASSWORDS",Context.MODE_PRIVATE);
        editor_password=sp_password.edit();

//        final String USER_PASSWORD = ed_password.getText().toString().trim();
//        final String OWNER_PASSWORD = ed_password.getText().toString().trim();

        nameTxt.setText(pdfDoc.getName());
        img.setImageResource(R.drawable.pdf_icon);

        //VIEW ITEM CLICK

        lock_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Document document = new Document();
                try {
                    PDDocument doc = PDDocument.load(new File(path + pdfDoc.getName()), password);
                    if (doc.isEncrypted()) {

                        Toast.makeText(c, "PDF Already locked", Toast.LENGTH_SHORT).show();
                    } else
                        {
//                        dialog.show();
                            mPDFEncryptionUtils.setPassword(path,removeExt(pdfDoc.getName()));

                     /*   btn_lockpdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                File f = new File(path + pdfDoc.getName());
                                String finalOutputFile = path + "encrypted_" + pdfDoc.getName();
                                PdfReader reader = null;
                                try {
                                    String masterpwd = mSharedPrefs.getString(MASTER_PWD_STRING, appName);
                                    reader = new PdfReader(path + pdfDoc.getName());
                                    PdfStamper stamper = null;
                                    stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
                                    stamper.setEncryption(password.getBytes(), masterpwd.getBytes(),
                                            PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);

                                    stamper.close();
                                    reader.close();
                                    new DatabaseHelper(c).insertRecord(finalOutputFile, c.getString(R.string.encrypted));

                                } catch (DocumentException e) {
                                    e.printStackTrace();
                                    Toast.makeText(c, "PDF Already Locked", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(c, "PDF Already Locked", Toast.LENGTH_SHORT).show();

                                }




                                f.delete();
                                //dialog.dismiss();
//                        pdfDocs.remove(i);
//                        notifyDataSetChanged();
                                editor_password.putString("PASSWORD1",ed_password.getText().toString().trim());
                                editor_password.putString("PASSWORD2",ed_password.getText().toString().trim());
                                editor_password.commit();
                                Toast.makeText(c, "Locked PDF Successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });*/


                    }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(c, "PDF Already Locked", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(c, "PDF Already Locked", Toast.LENGTH_SHORT).show();


                }

            }
        });

        return view;

    }

    public String removeExt(String fileName) {
        if (fileName.indexOf(".") > 0)
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return fileName;
    }
}
