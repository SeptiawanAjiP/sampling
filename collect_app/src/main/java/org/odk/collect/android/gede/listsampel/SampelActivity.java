package org.odk.collect.android.gede.listsampel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.augmentedreality.DatabaseHandler;
import org.odk.collect.android.augmentedreality.aksesdata.ParsingInstances;
import org.odk.collect.android.gede.aksesdata.AksesDataOdk;
import org.odk.collect.android.gede.aksesdata.Instances;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.R.attr.order;

/**
 * Created by aji on 8/6/2017.
 */

public class SampelActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SampelAdapter sampelAdapter;
    String idForm;
    String id;
    String var;
    ParsingInstances parsingInstances;
    org.odk.collect.android.gede.databasehandler.DatabaseHandler databaseHandler;
    ArrayList<String> key;
    ArrayList<String> uuids;
    org.odk.collect.android.augmentedreality.aksesdata.AksesDataOdk aksesDataOdk;
    ArrayList<Instances> instances;
    TextView judulSampel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gede_list_sampel_activity);
        recyclerView = (RecyclerView)findViewById(R.id.rc_sampel);
        judulSampel = (TextView)findViewById(R.id.judul_sampel);

        parsingInstances = new ParsingInstances();
        key = new ArrayList<>();
        uuids = new ArrayList<>();
        aksesDataOdk = new org.odk.collect.android.augmentedreality.aksesdata.AksesDataOdk();
        instances= new ArrayList<>();
        idForm = getIntent().getStringExtra("idForm");
        id = getIntent().getStringExtra("id");
        var = getIntent().getStringExtra("key");
        Log.d("hasil_0",idForm+","+id+","+var);
        judulSampel.setText("Sampel dari Wilayah : "+id);
        databaseHandler = new org.odk.collect.android.gede.databasehandler.DatabaseHandler(getApplicationContext());
        uuids = databaseHandler.getAllByIdAndFormId(idForm,id);
        Log.d("hasil_0_0",uuids.toString());
        Log.d("hasil_0_0_size",""+uuids.size());

        orderList(uuids);
    }

    public void orderList(ArrayList<String> arrayOrder){
        Log.d("hasil_2",arrayOrder.toString());
        sampelAdapter = new SampelAdapter(arrayOrder,SampelActivity.this);
        sampelAdapter.notifyDataSetChanged();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sampelAdapter);

    }
}
