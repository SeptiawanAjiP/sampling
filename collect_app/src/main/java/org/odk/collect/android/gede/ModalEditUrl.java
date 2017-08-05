package org.odk.collect.android.gede;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.odk.collect.android.R;
import org.odk.collect.android.augmentedreality.*;

/**
 * Created by aji on 7/23/2017.
 */

public class ModalEditUrl extends Dialog {
    private Activity activity;
    private EditText urlEt;
    private RelativeLayout rlSimpan;
    private SessionManager sessionManager;

    public ModalEditUrl(Activity activity){
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gede_edit_url);
        urlEt = (EditText)findViewById(R.id.url_tv);
        rlSimpan = (RelativeLayout)findViewById(R.id.simpan_atur);
        sessionManager = new SessionManager(activity.getApplicationContext());
        urlEt.setText(sessionManager.getAlamat());

        rlSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.setAlamat(urlEt.getText().toString());
                dismiss();
                Toast.makeText(activity, "Alamat url disimpan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
