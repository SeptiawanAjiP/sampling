package org.odk.collect.android.augmentedreality.scan;

import android.app.Activity;
import android.app.Dialog;
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
 * Created by Septiawan Aji Pradan on 6/5/2017.
 */

public class CustomModalDetailForm extends Dialog {
    private Activity activity;
    private String namaForm;
    private String totalIsian;
    private TextView namaFormTv,totalIsianTv;
    CustomModalDetailForm(Activity activity,String namaForm,String totalIsian){
        super(activity);
        this.activity = activity;
        this.namaForm = namaForm;
        this.totalIsian = totalIsian;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_detail_kuesioner);
    }

}
