package com.example.shenglin.easynote;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.bitmap;
import static android.os.Build.VERSION_CODES.O;
import static com.example.shenglin.easynote.R.id.imageView;

import com.example.shenglin.*;
import com.example.tesscv.tesscv;

import org.opencv.android.OpenCVLoader;

public class OCR extends AppCompatActivity {
    private static final int READ_WRITE_EXTERNAL_STORAGE_CODE = 0;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    private EditText editText;
    private ImageButton imageView;
    private Button chi_sim;
    private Button eng;

    private Bitmap m_phone;
    private String m_ocrOfBitmap;
    private InputStream m_instream;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        imageView = (ImageButton) findViewById(R.id.imageID);
        editText = (EditText) findViewById(R.id.OCREditText);
        chi_sim = (Button) findViewById(R.id.chi_sim);
        eng = (Button) findViewById(R.id.eng);

        chi_sim.setFocusable(false);
        eng.setFocusable(false);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] chooseWay = new String[]{"拍照","相册选取"};
                final AlertDialog.Builder choosePic_builder = new AlertDialog.Builder(OCR.this);
                choosePic_builder.setCancelable(true);
                choosePic_builder.setTitle("选择获取方式");
                choosePic_builder.setSingleChoiceItems(chooseWay, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            AddPicture();
                        }
                        else if (i == 1) {
                            ChooseFromAlbum();
                        }
                    }
                });
                AlertDialog choosePic_alert = choosePic_builder.create();
                choosePic_alert.show();
            }
        });
    }

    public void hhh_chi_sim(View v) {
        chi_sim.setBackgroundResource(R.drawable.roundbutton_press);
        AssetManager assetManager = getAssets();
        try {
            m_instream = assetManager.open("tessdata/chi_sim.traineddata");
        } catch (IOException e) {
            e.printStackTrace();
        }
        OCRtext(m_phone, "chi_sim", m_instream);
        chi_sim.setBackgroundResource(R.drawable.roundbutton1);
    }
    public void hhh_eng(View v) {
        eng.setBackgroundResource(R.drawable.roundbutton_press);
        AssetManager assetManager = getAssets();
        try {
            m_instream = assetManager.open("tessdata/eng.traineddata");
        } catch (IOException e) {
            e.printStackTrace();
        }
        OCRtext(m_phone, "eng", m_instream);
        eng.setBackgroundResource(R.drawable.roundbutton1);
    }

    public void OCRtext(Bitmap bitmap, String Language, InputStream inputStream) {
        if (OpenCVLoader.initDebug()) {
            tesscv ocr = new tesscv(bitmap, Language, inputStream);
            m_ocrOfBitmap = ocr.getOcrOfBitmap();
        }
        editText.setText(m_ocrOfBitmap);
    }

    public void AddPicture() {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(OCR.this,
                    "com.example.shenglin.image.fileprovider", outputImage);
        }
        else {
            imageUri = Uri.fromFile(outputImage);
        }

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    public void ChooseFromAlbum() {
        if (ContextCompat.checkSelfPermission(OCR.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OCR.this,
                    new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, WRITE_EXTERNAL_STORAGE_CODE);
        }
        else {
            openAlbum();
        }
    }
    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }
                else {
                    Toast.makeText(OCR.this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        m_phone = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(m_phone);
                        chi_sim.setFocusable(true);
                        eng.setFocusable(true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上版本系统
                        handleImageOnKitKat(data);
                    }
                    else {
                        //4.4以下版本系统
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if (uri.getAuthority().equals("com.android.providers.media.documents")) {
                String id = docId.split(":")[1]; //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            else if (uri.getAuthority().equals("com.android.providers.downloads.documents")) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }
        else if (uri.getScheme().equalsIgnoreCase("content")) {
            //如果是content类型的uri，则使用普通方法处理
            imagePath = getImagePath(uri, null);
        }
        else if (uri.getScheme().equalsIgnoreCase("file")) {
            //如果是file类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        //通过uri和selection来获取真实的图片路径
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath) {
        //根据路径显示图片
        if (imagePath != null) {
            m_phone = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(m_phone);
            chi_sim.setFocusable(true);
            eng.setFocusable(true);
        }
        else {
            Toast.makeText(OCR.this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}
