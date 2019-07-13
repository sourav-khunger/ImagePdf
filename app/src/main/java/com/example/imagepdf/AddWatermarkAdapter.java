package com.example.imagepdf;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by Oclemy on 7/28/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 */
public class AddWatermarkAdapter extends BaseAdapter {
    CheckBox checkBox;
    Context c;
    String path;
    int index;
    ArrayList<PDFDoc> pdfDocs;
    boolean showCheckBox;
    ImageView selected_share_pdf, hide_checkBox;
    ArrayList<Uri> selectedStrings = new ArrayList<Uri>();
    Button delete_yes_btn, delete_no_btn;
    private WatermarkUtils mWatermakrUtils;
    PDFDoc file;

    public AddWatermarkAdapter(Context c) {
        this.c = c;
    }

    public AddWatermarkAdapter(Context c, ArrayList<PDFDoc> pdfDocs, boolean showCheckBox, ImageView selected_share_pdf, ImageView hide_checkBox) {
        this.c = c;
        this.pdfDocs = pdfDocs;
        this.showCheckBox = showCheckBox;
        this.selected_share_pdf = selected_share_pdf;
        this.hide_checkBox = hide_checkBox;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            //INFLATE CUSTOM LAYOUT
            view = LayoutInflater.from(c).inflate(R.layout.pdf_list_view_for_mark, viewGroup, false);
        }
        index = i;
        file = (PDFDoc) this.getItem(i);
        viewHolder = new ViewHolder();
        mWatermakrUtils = new WatermarkUtils((Activity) c);
        path = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";
        final PDFDoc pdfDoc = (PDFDoc) this.getItem(i);
        TextView nameTxt = (TextView) view.findViewById(R.id.nameTxt);
        ImageView img = (ImageView) view.findViewById(R.id.pdfImage);
        ImageView share_pdf = (ImageView) view.findViewById(R.id.share_pdf);
        ImageView delete_file = (ImageView) view.findViewById(R.id.delete_file);
        checkBox = view.findViewById(R.id.checkbox);
        viewHolder.linearLayout = view.findViewById(R.id.linearLayout);
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fileName = removeExt(pdfDoc.getName());
                mWatermakrUtils.setWatermark(pdfDoc.getName(), fileName);
                notifyDataSetChanged();

            }
        });
        hide_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCheckBox = false;
                notifyDataSetChanged();
                selected_share_pdf.setVisibility(GONE);
                checkBox.setVisibility(GONE);
                hide_checkBox.setVisibility(GONE);

            }
        });

        if (showCheckBox) {
            hide_checkBox.setVisibility(View.VISIBLE);
            selected_share_pdf.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
            share_pdf.setVisibility(GONE);
            notifyDataSetChanged();
        } else {
            selected_share_pdf.setVisibility(GONE);
            checkBox.setVisibility(GONE);
            hide_checkBox.setVisibility(GONE);
            share_pdf.setVisibility(View.VISIBLE);
            notifyDataSetChanged();

        }
        delete_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(pdfDoc.getName(), index);
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    String filename = pdfDoc.getName();
                    File filelocation = new File(Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/", filename);
                    Log.e("ADDED!", "onClick: " + filelocation);
                    Uri path = Uri.fromFile(filelocation);
                    selectedStrings.add(path);
                } else {
                    String filename = pdfDoc.getName();
                    File filelocation = new File(Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/", filename);
                    Log.e("REMOVED!", "onClick: " + filelocation);
                    Uri path = Uri.fromFile(filelocation);
                    selectedStrings.remove(path);
                }
            }
        });
        //BIND DATA
        nameTxt.setText(pdfDoc.getName());
        img.setImageResource(R.drawable.pdf_icon);

        //VIEW ITEM CLICK
        nameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //openPDFView(pdfDoc.getPath(), pdfDoc.getName());

                File file = new File(path+pdfDoc.getName());
                if (file.exists()) {
                    Uri path = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        c.startActivity(intent);
                    }
                    catch (ActivityNotFoundException e) {
                        Toast.makeText(c, "No Application Available to View PDF", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openPDFView(pdfDoc.getPath(), pdfDoc.getName());
            }
        });
        selected_share_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = pdfDoc.getName();
                File filelocation = new File(Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/", filename);
                Log.e("File Location", "onClick: " + filelocation);
                Uri path = Uri.fromFile(filelocation);
                Log.e("File Location", "URI PATH SELECTED: " + path);
                Log.d("msg", "onClick1: " + path);
                Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.setType("application/pdf");
                String[] to = {"sourav@doozycod.in"};
                shareIntent.putExtra(Intent.EXTRA_EMAIL, to);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CUSTOMER FEEDBACK");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "PLEASE FIND THE ATTACHMENTS");
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedStrings);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                c.startActivity(shareIntent);
            }
        });
        share_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = pdfDoc.getName();
                File filelocation = new File(Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/", filename);
                Log.e("File Location", "onClick: " + filelocation);
                Uri path = Uri.fromFile(filelocation);
                ;
                Log.e("File Location", "URI PATH: " + path);
                Log.d("msg", "onClick1: " + path);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                String[] to = {"sourav@doozycod.in"};
                shareIntent.putExtra(Intent.EXTRA_EMAIL, to);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CUSTOMER FEEDBACK");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "PLEASE FIND THE ATTACHMENTS");
                shareIntent.putExtra(Intent.EXTRA_STREAM, path);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                c.startActivity(shareIntent);

            }
        });
        return view;
    }

    class ViewHolder {
        LinearLayout linearLayout;
        TextView catlogTitle;
        ImageView icon;
        int position;
    }


    private void showDeleteDialog(final String pdfFileName, final int index) {
        final Dialog dialog = new Dialog(c);
        dialog.setContentView(R.layout.delete_pdf_dialog);
        dialog.show();
        delete_yes_btn = dialog.findViewById(R.id.yes_delete);
        delete_no_btn = dialog.findViewById(R.id.no_delete);
        delete_no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        delete_yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = pdfFileName;
                File filelocation = new File(Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/", filename);
                filelocation.delete();
                pdfDocs.remove(index);
                notifyDataSetChanged();

                dialog.dismiss();
            }
        });

    }

    public String removeExt(String fileName) {
        if (fileName.indexOf(".") > 0)
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return fileName;
    }

    //OPEN PDF VIEW
    private void openPDFView(String path, String name) {
        Log.e("String Path", path);
        Intent i = new Intent(c, PDFActivity.class);
        i.putExtra("PATH", path);
        i.putExtra("name", name);
        c.startActivity(i);
    }
}
