package com.example.imagepdf;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class UnlockPdfAdapter extends BaseAdapter {

    ArrayList<PDFDoc> pdfDocs;
    Context c;
    String path;
    SharedPreferences sp_password,mSharedPrefs;
    SharedPreferences.Editor editor_password;
    private PDFEncryptionUtility mPDFEncryptionUtils;

    public UnlockPdfAdapter(Context c, ArrayList<PDFDoc> pdFs, String path) {
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
    public View getView(int i, View view, ViewGroup viewGroup)   {
        if (view == null) {
            //INFLATE CUSTOM LAYOUT
            view = LayoutInflater.from(c).inflate(R.layout.custom_unlock_pdf, viewGroup, false);
        }
        mPDFEncryptionUtils = new PDFEncryptionUtility((Activity) c);

        final PDFDoc pdfDoc = (PDFDoc) this.getItem(i);
        final TextView nameTxt = (TextView) view.findViewById(R.id.name_pdf3);
        ImageView unlock_pdf = (ImageView) view.findViewById(R.id.lock_pdf3);
        ImageView img = (ImageView) view.findViewById(R.id.pdfImage3);

        final Dialog dialog = new Dialog(c);
        dialog.setContentView(R.layout.layout_password);
        final EditText ed_password = (EditText) dialog.findViewById(R.id.ed_password);
        final Button btn_lockpdf = (Button) dialog.findViewById(R.id.btn_lockpdf);
        final String password = ed_password.getText().toString().trim();

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        nameTxt.setText(pdfDoc.getName());
        img.setImageResource(R.drawable.pdf_icon);

        unlock_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPDFEncryptionUtils.removePassword(path,removeExt(pdfDoc.getName()));

            }
        });

        return  view;
    }

    public String removeExt(String fileName) {
        if (fileName.indexOf(".") > 0)
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return fileName;
    }
}
