package com.example.imagepdf;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


import static com.example.imagepdf.Constants.MASTER_PWD_STRING;
import static com.example.imagepdf.Constants.appName;

public class PDFEncryptionUtility {

    private final Activity mContext;
    private final MaterialDialog mDialog;
    private String mPassword;
    private SharedPreferences mSharedPrefs;
    String pathDir;

    public PDFEncryptionUtility(Activity context) {
        this.mContext = context;
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mDialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.custom_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build();
    }

    /**
     * Opens the password mDialog to set Password for an existing PDF file.
     *
     * @param filePath Path of file to be encrypted
     */
    public void setPassword(final String filePath, final String pdfFilename) {
        Log.d("msg", "setPassword: "+filePath+""+pdfFilename);
        mDialog.setTitle(R.string.set_password);
        final View mPositiveAction = mDialog.getActionButton(DialogAction.POSITIVE);
        assert mDialog.getCustomView() != null;
        pathDir = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";

        EditText mPasswordInput = mDialog.getCustomView().findViewById(R.id.password);
        mPasswordInput.addTextChangedListener(
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
                        if (StringUtils.isEmpty(input))
                            Toast.makeText(mContext, R.string.snackbar_password_cannot_be_blank, Toast.LENGTH_SHORT).show();
                        else
                            mPassword = input.toString();
                    }
                });
        mDialog.show();
        mPositiveAction.setEnabled(false);
        mPositiveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    try {
                        String path = doEncryption(filePath, mPassword, pdfFilename);

                        Toast.makeText(mContext, R.string.snackbar_pdfCreated+path, Toast.LENGTH_SHORT).show();

                    } catch (IOException | DocumentException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, R.string.cannot_add_password, Toast.LENGTH_SHORT).show();
                    }
                    mDialog.dismiss();
                }
            }
        });
    }


    private String doEncryption(String path, String password, String pdfName) throws IOException, DocumentException {
        //File f=new File(path+pdfName+".pdf");
        String masterpwd = mSharedPrefs.getString(MASTER_PWD_STRING, appName);
        String finalOutputFile = pdfName + mContext.getString(R.string.encrypted_file);
        Log.d("msg", "doEncryption: "+finalOutputFile);

        PdfReader reader = new PdfReader(pathDir+pdfName+".pdf");
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(pathDir + finalOutputFile));
        stamper.setEncryption(password.getBytes(), masterpwd.getBytes(),
                PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);

        stamper.close();
        reader.close();
        //f.delete();
        return finalOutputFile;
    }

    /**
     * Checks if PDf is encrpyted
     *
     * @param file - path of PDF file
     * @return true, if PDF is encrypted, otherwise false
     */
    private boolean isPDFEncrypted(final String file) {
        PdfReader reader;
        String ownerPass = mContext.getString(R.string.app_name);
        try {
            reader = new PdfReader(file, ownerPass.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
        //Check if PDF is encrypted or not.
        if (!reader.isEncrypted()) {
            Log.e("PDFEncryption", "isPDFEncrypted: " + R.string.not_encrypted);
            return false;
        }
        return true;
    }

    /**
     * Uses PDF Reader to decrypt the PDF.
     *
     * @param file Path of pdf file to be decrypted
     */
    public void removePassword(final String file, final String pdfFileName) {
        if (!isPDFEncrypted(file))
            return;

        final String[] input_password = new String[1];
        mDialog.setTitle(R.string.enter_password);
        final View mPositiveAction = mDialog.getActionButton(DialogAction.POSITIVE);
        final EditText mPasswordInput = Objects.requireNonNull(mDialog.getCustomView()).findViewById(R.id.password);
        TextView text = mDialog.getCustomView().findViewById(R.id.enter_password);
        text.setText(R.string.decrypt_message);
        mPasswordInput.addTextChangedListener(
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
                        input_password[0] = input.toString();
                    }
                });
        mDialog.show();
        mPositiveAction.setEnabled(false);
        mPositiveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    // check for password
                    // our master password & their user password
                    // their master password
                    if (!removePasswordUsingDefMasterPAssword(file, input_password, pdfFileName)) {
                        if (!removePasswordUsingInputMasterPAssword(file, input_password, pdfFileName)) {
                            Toast.makeText(mContext, R.string.master_password_changed, Toast.LENGTH_SHORT).show();
                        }
                    }
                    mDialog.dismiss();
                }
            }
        });
    }

    /**
     * This function removes the password for encrypted files.
     *
     * @param file          - the path of the actual encrypted file.
     * @param inputPassword - the password of the encrypted file.
     * @return
     */
    public String removeDefPasswordForImages(final String file,
                                             final String[] inputPassword, final String pdfName) {
        String finalOutputFile;
        try {
            String masterpwd = mSharedPrefs.getString(MASTER_PWD_STRING, appName);
            PdfReader reader = new PdfReader(file, masterpwd.getBytes());
            byte[] password;
            finalOutputFile = pdfName + mContext.getString(R.string.decrypted_file);
            password = reader.computeUserPassword();
            byte[] input = inputPassword[0].getBytes();

            if (Arrays.equals(input, password)) {
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(file + finalOutputFile));
                stamper.close();
                reader.close();
                return finalOutputFile;
            }

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean removePasswordUsingDefMasterPAssword(final String file, final String[] inputPassword, final String pdfName) {
        String finalOutputFile;
        try {
            Log.d("msg", "removePasswordUsingDefMasterPAssword: "+file+""+pdfName);
            String masterpwd = mSharedPrefs.getString(MASTER_PWD_STRING, appName);
            PdfReader reader = new PdfReader(file+pdfName+".pdf", masterpwd.getBytes());
            Log.d("msg", "removePasswordUsingDefMasterPAssword2: "+masterpwd);
            byte[] password;
            finalOutputFile = pdfName + mContext.getString(R.string.decrypted_file);
            password = reader.computeUserPassword();
            byte[] input = inputPassword[0].getBytes();
            if (Arrays.equals(input, password)) {
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(file + finalOutputFile));
                stamper.close();
                reader.close();

                final String filepath = finalOutputFile;
                Log.d("msg", "removePasswordUsingDefMasterPAssword3: "+filepath);
                return true;
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Log.d("msg", "removePasswordUsingDefMasterPAssword4: "+e.getMessage());
        }

        return false;
    }


    private boolean removePasswordUsingInputMasterPAssword(final String file,
                                                           final String[] inputPassword, final String pdfName) {
        String finalOutputFile;
        try {
            PdfReader reader = new PdfReader(file, inputPassword[0].getBytes());
            finalOutputFile = pdfName + mContext.getString(R.string.decrypted_file);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(pathDir + finalOutputFile));
            stamper.close();
            reader.close();
            ;
            final String filepath = finalOutputFile;

            return true;

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
