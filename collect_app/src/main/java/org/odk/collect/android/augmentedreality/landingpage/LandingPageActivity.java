package org.odk.collect.android.augmentedreality.landingpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.odk.collect.android.augmentedreality.MainActivity;

import org.odk.collect.android.R;
import org.odk.collect.android.augmentedreality.ui.MainMenuApp;

/**
 * Created by Septiawan Aji Pradan on 5/31/2017.
 */

public class LandingPageActivity extends AppIntro2 {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage1= new SliderPage();


        addSlide(AppIntroFragment.newInstance("Penomoran Bangunan","Penomoran bangunan dilakukan secara otomatis melalui CAPI. Sistem mencatat latitude dan longitude suatu bangunan.",R.drawable.ic_house,Color.TRANSPARENT));


        addSlide(AppIntroFragment.newInstance("Stiker Digital","Stiker digambarkan secara digital menggunakan Augmented Reality, keterangan tiap bangunan akan muncul dilayar kamera",R.drawable.ic_ar,Color.TRANSPARENT));


        addSlide(AppIntroFragment.newInstance("Integerasi dengan ODK","Proses pengambilan data menggunakan Open Data Kit, kemudian ditampilkan dalam bentuk stiker digital",R.drawable.ic_odk_in,Color.TRANSPARENT));

        // Declare a new image view
        ImageView imageView = new ImageView(this);

        // Bind a drawable to the imageview
        imageView.setImageResource(R.drawable.welcome_bg_2);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // Set background color
        // Set layout params
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Bind the background to the intro
        setBackgroundView(imageView);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), MainMenuApp.class);
        startActivity(intent);
        finish();
    }
}
