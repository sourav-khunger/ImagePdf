package com.example.imagepdf;

import android.app.Activity;
import android.graphics.Color;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileOutputStream;
import java.io.IOException;


public class WatermarkUtils {

    private final Activity mContext;
    private Watermark mWatermark;
    String pathDir;

    public WatermarkUtils(Activity context) {
        mContext = context;
    }

    public void setWatermark(final String path, final String pdfName) {

        final MaterialDialog mDialog = new MaterialDialog.Builder(mContext)
                .title(R.string.add_watermark)
                .customView(R.layout.add_watermark_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build();

        final View mPositiveAction = mDialog.getActionButton(DialogAction.POSITIVE);

        this.mWatermark = new Watermark();

        Log.e("Adapter", "onClick: " + path);
        Log.e("Adapter removed", "onClick: " + pdfName);
        final EditText watermarkTextInput = mDialog.getCustomView().findViewById(R.id.watermarkText);
        final EditText angleInput = mDialog.getCustomView().findViewById(R.id.watermarkAngle);
        final ColorPickerView colorPickerInput = mDialog.getCustomView().findViewById(R.id.watermarkColor);
        final EditText fontSizeInput = mDialog.getCustomView().findViewById(R.id.watermarkFontSize);
        final Spinner fontFamilyInput = mDialog.getCustomView().findViewById(R.id.watermarkFontFamily);
        final Spinner styleInput = mDialog.getCustomView().findViewById(R.id.watermarkStyle);
        pathDir = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";
        fontFamilyInput.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                Font.FontFamily.values()));
        styleInput.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                mContext.getResources().getStringArray(R.array.fontStyles)));

        angleInput.setText("0");
        fontSizeInput.setText("50");

        watermarkTextInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mPositiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        if (watermarkTextInput.getText().equals(""))
                            Toast.makeText(mContext, R.string.snackbar_watermark_cannot_be_blank, Toast.LENGTH_SHORT).show();
                        else {
                            mWatermark.setWatermarkText(input.toString());
                        }
                    }
                });

        mPositiveAction.setEnabled(false);
        mPositiveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mWatermark.setWatermarkText(watermarkTextInput.getText().toString());
                    mWatermark.setFontFamily(((Font.FontFamily) fontFamilyInput.getSelectedItem()));
                    mWatermark.setFontStyle(getStyleValueFromName(((String) styleInput.getSelectedItem())));
                    if (StringUtils.isEmpty(angleInput.getText())) {
                        mWatermark.setRotationAngle(0);
                    } else {
                        mWatermark.setRotationAngle(Integer.valueOf(angleInput.getText().toString()));
                    }

                    if (StringUtils.isEmpty(fontSizeInput.getText())) {
                        mWatermark.setTextSize(50);
                    } else {
                        mWatermark.setTextSize(Integer.valueOf(fontSizeInput.getText().toString()));
                    }
                    mWatermark.setTextColor((new BaseColor(
                            Color.red(colorPickerInput.getColor()),
                            Color.green(colorPickerInput.getColor()),
                            Color.blue(colorPickerInput.getColor()),
                            Color.alpha(colorPickerInput.getColor())
                    )));
                    String filePath = createWatermark(path, pdfName);

                    Toast.makeText(mContext, "File at Location :" + pathDir + filePath, Toast.LENGTH_SHORT).show();
                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, R.string.cannot_add_watermark, Toast.LENGTH_SHORT).show();

                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private String createWatermark(final String path, String pdfName) throws IOException, DocumentException {
        String finalOutputFile =
                pdfName + mContext.getString(R.string.watermarked_file);
        Log.d("msg", "createWatermark: "+finalOutputFile);
        PdfReader reader = new PdfReader(pathDir + path);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(pathDir + finalOutputFile));
        Font font = new Font(this.mWatermark.getFontFamily(), this.mWatermark.getTextSize(),
                this.mWatermark.getFontStyle(), this.mWatermark.getTextColor());
        Phrase p = new Phrase(this.mWatermark.getWatermarkText(), font);

        PdfContentByte over;
        Rectangle pagesize;
        float x, y;
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {

            // get page size and position
            pagesize = reader.getPageSizeWithRotation(i);
            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
            y = (pagesize.getTop() + pagesize.getBottom()) / 2;
            over = stamper.getOverContent(i);

            ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, this.mWatermark.getRotationAngle());
        }

        stamper.close();
        reader.close();
//        new DatabaseHelper(mContext).insertRecord(finalOutputFile, mContext.getString(R.string.watermarked));
        return finalOutputFile;
    }

    public int getStyleValueFromName(String name) {
        switch (name) {
            case "NORMAL":
                return Font.NORMAL;
            case "BOLD":
                return Font.BOLD;
            case "ITALIC":
                return Font.ITALIC;
            case "UNDERLINE":
                return Font.UNDERLINE;
            case "STRIKETHRU":
                return Font.STRIKETHRU;
            case "BOLDITALIC":
                return Font.BOLDITALIC;
            default:
                return Font.NORMAL;
        }
    }

    public String getStyleNameFromFont(int font) {
        switch (font) {
            case Font.NORMAL:
                return "NORMAL";
            case Font.BOLD:
                return "BOLD";
            case Font.ITALIC:
                return "ITALIC";
            case Font.UNDERLINE:
                return "UNDERLINE";
            case Font.STRIKETHRU:
                return "STRIKETHRU";
            case Font.BOLDITALIC:
                return "BOLDITALIC";
            default:
                return "NORMAL";
        }
    }


}