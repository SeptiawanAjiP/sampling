package org.odk.collect.android.augmentedreality.scan;

import android.app.Activity;
import android.os.Bundle;

import org.odk.collect.android.R;


/*
* dummy activity to display PanicAR fragment
* */
public abstract class ARActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augmented_reality);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PanicARFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
