package org.odk.collect.android.gede;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.odk.collect.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Septiawan Aji Pradan on 7/14/2017.
 */

public class BlokSensusActivity extends AppCompatActivity{
    private RecyclerView rcy;
    private BlokSensusAdapter blokSensusAdapter;
    String status;
    int lastId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gede_ls_activity);
        rcy = (RecyclerView)findViewById(R.id.recycler_order);
        orderList();
    }


    public void orderList(){
        ArrayList<String> bs = new ArrayList<>();
        bs.add("5201003");
        bs.add("5201004");
        blokSensusAdapter = new BlokSensusAdapter(BlokSensusActivity.this,bs,getIntent().getStringExtra("form_id"));
        blokSensusAdapter.notifyDataSetChanged();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rcy.setLayoutManager(layoutManager);
        rcy.setItemAnimator(new DefaultItemAnimator());
        rcy.setAdapter(blokSensusAdapter);
    }


}

