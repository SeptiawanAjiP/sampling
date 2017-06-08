package org.odk.collect.android.augmentedreality.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.odk.collect.android.R;

import java.util.ArrayList;

/**
 * Created by Septiawan Aji Pradan on 6/4/2017.
 */

public class TextInsideMenu extends AppCompatActivity {

    private BoomMenuButton bmb;
    private ArrayList<Pair> piecesAndButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_skripsi);

        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.TextInsideCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_4);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_4);
        bmb.addBuilder(BuilderManager.getTextInsideCircleButtonBuilder());

    }
}