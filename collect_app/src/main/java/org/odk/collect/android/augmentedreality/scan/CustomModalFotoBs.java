package org.odk.collect.android.augmentedreality.scan;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import org.odk.collect.android.R;
import org.odk.collect.android.augmentedreality.koneksi.VolleySingletonImage;

/**
 * Created by Septiawan Aji Pradan on 4/1/2017.
 */

public class CustomModalFotoBs extends Dialog{
    private Activity activity;
    private TextView ketBangunanSesus;
    private ImageView fotoBangunan;
    private ImageLoader imageLoader;
    private String pathFoto;


    CustomModalFotoBs(Activity activity,String pathFoto){
        super(activity);
        this.activity = activity;
        this.pathFoto = pathFoto;
        Log.d("aji_custom",pathFoto);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_foto_bs_modal);
        Toast.makeText(activity, pathFoto, Toast.LENGTH_SHORT).show();
        imageLoader = VolleySingletonImage.getInstance(activity).getImageLoader();
        fotoBangunan = (ImageView)findViewById(R.id.gambar_bangunan_sensus);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Log.d("aji_custom_22",pathFoto);
        options.inSampleSize = 8;
        if(!pathFoto.equals("")){
            final Bitmap bitmap = BitmapFactory.decodeFile(pathFoto, options);
            fotoBangunan.setImageBitmap(bitmap);
        }
    }



}
