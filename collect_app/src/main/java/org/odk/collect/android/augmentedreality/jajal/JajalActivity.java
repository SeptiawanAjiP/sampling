package org.odk.collect.android.augmentedreality.jajal;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.odk.collect.android.R;

/**
 * Created by Septiawan Aji Pradan on 6/6/2017.
 */

public class JajalActivity extends AppCompatActivity {
    Button  b;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jajal_act);
        b = (Button)findViewById(R.id.jajal);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDoalog customDoalog = new CustomDoalog(JajalActivity.this);
                customDoalog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDoalog.show();
                customDoalog.setDialog(new CustomDoalog.OnMyDialogResult() {
                    @Override
                    public void finish(String result) {
                        Toast.makeText(JajalActivity.this,"cucuc" +result, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}
