package org.odk.collect.android.augmentedreality.jajal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.odk.collect.android.R;

/**
 * Created by Septiawan Aji Pradan on 6/6/2017.
 */

public class CustomDoalog extends Dialog {
    Activity activity;
    EditText editText;
    Button button;
    String a;
    OnMyDialogResult myDialogResult;
    CustomDoalog(Activity activity){
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jajal_dial);
        editText = (EditText)findViewById(R.id.jajal_masuk);
        button = (Button)findViewById(R.id.jajal_dial_simpan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myDialogResult != null){
                    myDialogResult.finish(String.valueOf(editText.getText().toString()));
                }
                CustomDoalog.this.dismiss();
            }
        });
    }

    public void setDialog(OnMyDialogResult dialogResult){
        myDialogResult = dialogResult;
    };

    public interface  OnMyDialogResult{
        void finish(String result);
    }
}
