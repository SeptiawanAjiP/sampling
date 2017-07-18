package org.odk.collect.android.gede;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.odk.collect.android.R;

/**
 * Created by Septiawan Aji Pradan on 7/18/2017.
 */

public class DialogAlamat extends Dialog {
    Activity activity;
    String alamat;
    private EditText editText;
    private Button button;
    AlamatSession sessionManager;
    public DialogAlamat(Activity activity){
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gede_modal_alamat);
        editText= (EditText)findViewById(R.id.alamat_server);
        button = (Button)findViewById(R.id.btn_simpan_alamat);
        sessionManager = new AlamatSession(activity.getApplicationContext());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setAlamat(editText.getText().toString());
                Toast.makeText(activity, "Berhasil Simpan Alamat", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
