package com.example.imagepdf;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class CreatePdfActivity extends AppCompatActivity {
    ImageView iv_photo;
    Button btn_createpdf;
    Dialog dialog,dialog1;
    EditText et_save_pdf_file;
    Button save_pdf_btn;
    String pdf;
    String dirpath;
    Bitmap bitmap;
    private static String FILE = "";
    static Image image;
    static byte[] bArray;
    String p;
    List<String> imagePathList=new ArrayList<>();
    List<String> imagesEncodedList=new ArrayList<>();
    String imageEncoded;
    Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pdf);
        iv_photo = findViewById(R.id.iv_photo);
        btn_createpdf = findViewById(R.id.btn_createpdf);


        p = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";
        File path = new File(p);
        if (!path.exists()) {
            path.mkdirs();
        }

        selectImage();

        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btn_createpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                //imageToPDF();
            }
        });


    }

    //dialog for image chooser
    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePdfActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                if (options[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
                    /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);*/
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    bArray = stream.toByteArray();
                    iv_photo.setImageBitmap(bitmap);

                    String path = android.os.Environment.getExternalStorageDirectory() + File.separator + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {


                /*Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                bitmap = (BitmapFactory.decodeFile(picturePath));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                bArray = stream.toByteArray();


                iv_photo.setImageBitmap(bitmap);*/



                    // When an Image is picked
                        // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    mImageUri = data.getData();
                    imagePathList.add(getImagePath(mImageUri));
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    showSavePdfDialog(false);
                    Log.v("Image Path SINGLE", imagePathList.get(0));
                    Log.d("msg", "onActivityResult1: "+imagePathList.get(0));

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            imagePathList.add(getImagePath(uri));
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size() + mArrayUri.get(0));
                        Log.v("Image Path", getImagePath(mArrayUri.get(0)));
                        Log.d("msg", "onActivityResult2: "+mArrayUri.get(0));

                        showSavePdfDialog(true);

                    }
                }


            }
        }
    }

    //show dialog on create pdf
    void showDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.save_as_dialog);
        dialog.show();
        et_save_pdf_file = dialog.findViewById(R.id.et_pdf_file_name);
        save_pdf_btn = dialog.findViewById(R.id.pdf_gen_btn);

        save_pdf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_save_pdf_file.getText().toString().equals("")) {
                    et_save_pdf_file.setError("Please Enter Name");
                } else {
                    pdf = et_save_pdf_file.getText().toString();
                    try {
                        imageToPDF(pdf);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.e("PDF", "PDF GENERATED!");
                }

            }
        });
    }

    public void imageToPDF(String pdf) throws FileNotFoundException {
       /* try {
           Document document = new Document();
            dirpath = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/" + pdfFileName + ".pdf")); //  Change pdf's name.
            document.open();
            Image img = Image.getInstance(Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/" + File.separator + "image.png");
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 100) / img.getWidth()) * 100;
            img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
            float x = (PageSize.A4.getWidth() - img.getScaledWidth()) / 2;
            float y = (PageSize.A4.getHeight() - img.getScaledHeight()) / 2;
            img.setAbsolutePosition(x, y);
            document.add(img);
            document.close();
            Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } catch (Exception e) {

        }*/

        try {
            Document document = new Document();
            //PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/" + "Hello" + ".pdf")); //  Change pdf's name.
            PdfWriter.getInstance(document, new FileOutputStream(p + "/" + pdf + ".pdf")); //  Change pdf's name.
            document.open();
            addImage(document);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImage(Document document) {
        try {
            image = Image.getInstance(bArray);
            //image = Image.getInstance(Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/");
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 100) / image.getWidth()) * 100;
            image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
            float x = (PageSize.A4.getWidth() - image.getScaledWidth()) / 2;
            float y = (PageSize.A4.getHeight() - image.getScaledHeight()) / 2;
            image.setAbsolutePosition(x, y);
            document.add(image);
            document.close();
            Toast.makeText(this, "PDF generated successfully", Toast.LENGTH_SHORT).show();///Here i set byte array..you can do bitmap to byte array and set in image...
            dialog.dismiss();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        // image.scaleAbsolute(150f, 150f);
        try {
            document.add(image);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void showSavePdfDialog(final boolean more) {
        dialog1=new Dialog(this);
        dialog1.setContentView(R.layout.save_pdf_dialog);
        dialog1.show();
        final EditText pdf_name = dialog1.findViewById(R.id.pdf_name);
        Button merge_files = dialog1.findViewById(R.id.save_pdf);

        merge_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pdf_name.getText().toString().equals("")) {
                    pdf_name.setError("Enter Pdf Name");
                } else {
                    try {
                        if (more) {
                            createPdf(pdf_name.getText().toString());
                        } else {
                            createPdfSingleImage(pdf_name.getText().toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                imagePathList.clear();
            }
        });

    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public void createPdf(String pdfFileName) throws IOException, DocumentException {
        Image img = Image.getInstance(imagePathList.get(0));
        Document document = new Document(img);
        PdfWriter.getInstance(document, new FileOutputStream(p + "/" + pdfFileName + ".pdf"));
        document.open();
        for (String image : imagePathList) {
            img = Image.getInstance(image);
            document.setPageSize(img);
            document.newPage();
            img.setAbsolutePosition(0, 0);
            document.add(img);
        }
        document.close();
        Toast.makeText(this, "PDF Saved!", Toast.LENGTH_SHORT).show();
        dialog1.dismiss();
    }

    public void createPdfSingleImage(String pdfFileName) throws IOException, DocumentException {
        Image img = Image.getInstance(getImagePath(mImageUri));
        Document document = new Document(img);
        PdfWriter.getInstance(document, new FileOutputStream(p + "/" + pdfFileName + ".pdf"));
        document.open();
        img = Image.getInstance(getImagePath(mImageUri));
        document.setPageSize(img);
        document.newPage();
        img.setAbsolutePosition(0, 0);
        document.add(img);

        document.close();
        dialog1.dismiss();
        Toast.makeText(this, "PDF Saved!", Toast.LENGTH_SHORT).show();
    }
}
