//package org.odk.collect.android.augmentedreality.kamera;
//
//import android.content.ContentValues;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.Toast;
//
//
//import org.odk.collect.android.augmentedreality.Bangunan;
//import org.odk.collect.android.augmentedreality.DatabaseHandler;
//
///**
// * Created by Septiawan Aji Pradan on 4/6/2017.
// */
//
//public class KameraAmbilGambar extends AppCompatActivity {
//
//    private int selected_image_option;
//    private Uri captured_image_uri;
//    private final int IMAGE_EDITOR_REQUEST_CODE = 111;
//    private final int IMAGE_EDITOR_RESULT_CODE=222;
//    public static final int MEDIA_TYPE_IMAGE = 1;
//    DatabaseHandler db;
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        String fileName = "temp.jpg";
//        db = new DatabaseHandler(getApplicationContext());
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, fileName);
//        captured_image_uri = getContentResolver()
//                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, captured_image_uri);
//        selected_image_option = 1;
//        startActivityForResult(intent, IMAGE_EDITOR_REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Uri uri;
//        if(resultCode!=RESULT_OK)
//            return;
//        if(requestCode==IMAGE_EDITOR_REQUEST_CODE){
//            if(selected_image_option==1)
//                uri = captured_image_uri;
//            else if(selected_image_option==2)
//                uri=data.getData();
//            else
//                return;
///*
//                                Edit photo
//            data = new AdobeImageIntent.Builder(KameraAmbilGambar.this)
//                    .setData(uri) // Set in onActivityResult()
//                    .build();
//
//            startActivityForResult(data,IMAGE_EDITOR_RESULT_CODE);
//            */
//            uploadImage(uri);
//        }else if(requestCode==IMAGE_EDITOR_RESULT_CODE){
//
//        }
//    }
//
//    public void uploadImage(Uri uri){
//        /*
//            Mengubah uri menjadi filePath
//         */
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        int column_index =cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String s=cursor.getString(column_index);
//        cursor.close();
//
//        Bangunan bs = new Bangunan(getIntent().getStringExtra("nama"),getIntent().getDoubleExtra("lat",0),getIntent().getDoubleExtra("lon",0),s);
//
//        db.insertTabel(bs);
//        Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
//        finish();
//
//    }
//}
