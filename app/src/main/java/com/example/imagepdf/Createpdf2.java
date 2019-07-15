package com.example.imagepdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Createpdf2 extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_pic;
    EditText ed_pdf_name;
    Button btn_createe_pdf;
    Bitmap bitmap;
    static byte[] bArray;
    Dialog dialog, dialog1;
    EditText et_save_pdf_file;
    Button save_pdf_btn;
    String pdf;
    static Image image;
    String p;
    RecyclerView gridview;
    //public SharedPref prefs;
    //ImageListAdapter gridAdapter;
    List<String> imagePathList=new ArrayList<>();
    List<String> imagesEncodedList=new ArrayList<>();
    String imageEncoded;
    Uri mImageUri;
    private  List<String> listOfImagesPath;
    public String GridViewDemo_ImagePath="";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ArrayList<CapturedImageData> myChimImagesList = new ArrayList<CapturedImageData>();
    private String[] arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpdf2);
        iv_pic = findViewById(R.id.iv_picc);
        btn_createe_pdf = findViewById(R.id.btn_createe_pdf);
        ed_pdf_name = findViewById(R.id.ed_pdf_name);
        gridview=findViewById(R.id.gridview);
        listOfImagesPath=new ArrayList<>();
      /*  gridAdapter = new ImageListAdapter(getApplicationContext());
        gridview.setAdapter(gridAdapter);*/
        p = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";
        GridViewDemo_ImagePath = Environment.getExternalStorageDirectory() + "/AVI PDF FORMS/";
        File path = new File(p);
        File path1 = new File(GridViewDemo_ImagePath);
        if (!path.exists()) {
            path.mkdirs();
            path1.mkdirs();
        }

        /*listOfImagesPath.clear();
        listOfImagesPath = RetriveCapturedImagePath();
        if(listOfImagesPath!=null){
            gridview.setAdapter(new ImageListAdapter(this,listOfImagesPath));
        }*/
        this.setTitle("CREATE PDF");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        iv_pic.setOnClickListener(this);
        btn_createe_pdf.setOnClickListener(this);
        iv_pic.setOnClickListener(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_picc:
                selectImage();
               /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, 1);*/
                break;

            case R.id.btn_createe_pdf:
                showDialog();
                break;


        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }


    //dialog for image chooser
    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Createpdf2.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                if (options[item].equals("Take Photo")) {
                   /* try {
                        //use standard intent to capture an image
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //we will handle the returned data in onActivityResult
                        startActivityForResult(captureIntent, 1);
                    } catch(ActivityNotFoundException e){
                        //display an error message
                        String errorMessage = "Whoops - your device doesn't support capturing images!";
                        Toast.makeText(Createpdf2.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }*/
                  /*  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);*/

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    Uri fileUri = getOutputMediaFile();


                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    intent.putExtra("aspectY", 0);
                    intent.putExtra("outputX", 150);
                    intent.putExtra("outputY", 150);
                    startActivityForResult(intent, 8888);
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String _path = null;
        if (resultCode == RESULT_OK) {

            //user is returning from capturing an image using the camera
            if(requestCode == 1){
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                File imageDirectory = new File(p);
                imageDirectory.mkdirs();
                for(int z=0;z<imageDirectory.length();z++){
                    _path = GridViewDemo_ImagePath+z+"image.jpg";
                }

                try {
                    FileOutputStream out = new FileOutputStream(_path);
                    thePic.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //listOfImagesPath = null;
                listOfImagesPath = RetriveCapturedImagePath();
                if(listOfImagesPath!=null){
               // gridview.setAdapter(new ImageListAdapter(this,listOfImagesPath));
            }
        }
        }
    }
/*
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
                    listOfImagesPath = null;
                    listOfImagesPath = RetriveCapturedImagePath();
                    Log.d("msg", "onActivityResult7: "+listOfImagesPath);
                    if(listOfImagesPath!=null){
                        gridview.setAdapter(new ImageListAdapter(this,listOfImagesPath));

                    }
                  //  iv_pic.setImageBitmap(bitmap);

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


                *//*Uri selectedImage = data.getData();
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


                iv_photo.setImageBitmap(bitmap);*//*


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
    }*/

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

    private List<String> RetriveCapturedImagePath() {
        List<String> tFileList = new ArrayList<String>();
        File f = new File(p);
        if (f.exists()) {
            File[] files=f.listFiles();
            Arrays.sort(files);

            for(int i=0; i<files.length; i++){
                File file = files[i];
                if(file.isDirectory())
                    continue;
                tFileList.add(file.getPath());
            }
        }
        return tFileList;
    }




    public Uri getOutputMediaFile() {

        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(), "GalleryAppCameraImages");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath(), "IMG_" + timeStamp + ".jpg");

        Uri uri = null;
        if (mediaFile != null) {

            uri = FileProvider.getUriForFile(Createpdf2.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    mediaFile);

           // prefs.setImagePath(this, mediaFile.getPath());
        }
        addChimImages();
        return uri;
    }

    void addChimImages() {
        if (myChimImagesList != null) {
            myChimImagesList.add(new CapturedImageData());
        }

        gridview.setAdapter(new ImageListAdapter(this, myChimImagesList));
    }

}
