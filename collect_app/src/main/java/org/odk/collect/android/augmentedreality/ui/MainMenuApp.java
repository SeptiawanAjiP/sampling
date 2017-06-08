package org.odk.collect.android.augmentedreality.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.odk.collect.android.R;
import org.odk.collect.android.augmentedreality.aksesdata.Form;
import org.odk.collect.android.augmentedreality.scan.ARActivity;
import org.odk.collect.android.augmentedreality.scan.ARPortraitActivity;

import java.util.ArrayList;

/**
 * Created by Septiawan Aji Pradan on 6/3/2017.
 */

public class MainMenuApp extends AppCompatActivity {
    private BoomMenuButton bmb;
    private ArrayList<Pair> piecesAndButtons = new ArrayList<>();
    private LisfFormAdapter lisfFormAdapter;
    private ArrayList<Form> forms;
    private RecyclerView recyclerView;
    private ExpandGridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_skripsi);

        forms = new ArrayList<>();

//        recyclerView = (RecyclerView)findViewById(R.id.list_form_main);
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum((PiecePlaceEnum.HAM_2));
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_2);

        gridView = (ExpandGridView) findViewById(R.id.grid_view);
        gridView.setExpanded(true);
        gridView.setAdapter(new ImageAdapter(this));

        setData();
        HamButton.Builder builder1 = new HamButton.Builder()
                .normalImageRes(R.drawable.ic_camera)
                .normalText("Scan Bangunan")
                .normalTextColor(Color.WHITE)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intent = new Intent(getApplicationContext(), ARPortraitActivity.class);
                        intent.putExtra("path_form","");
                        startActivity(intent);
                    }
                });
        HamButton.Builder builder2 = new HamButton.Builder()
                .normalImageRes(R.drawable.ic_input_data)
                .normalText("Petunjuk")
                .normalTextColor(Color.WHITE)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
//                        Intent intent = new Intent(getApplicationContext(), ARPortraitActivity.class);
//                        startActivity(intent);
                        Toast.makeText(MainMenuApp.this, "About", Toast.LENGTH_SHORT).show();
                    }
                });
        bmb.addBuilder(builder1);
        bmb.addBuilder(builder2);

    }

    public void setData(){
        Form form = new Form("Susenas","14");
        forms.add(form);

        form = new Form("Sakernas 2016","23");
        forms.add(form);

        form = new Form("SDKI 2017","39");
        forms.add(form);

        form = new Form("Sensus Penduduk","20");
        forms.add(form);

        form = new Form("Sensus Ekonomi","15");
        forms.add(form);

//        formLayout();

    }

//    public void formLayout(){
//        lisfFormAdapter = new LisfFormAdapter(forms);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(lisfFormAdapter);
//    }
}
