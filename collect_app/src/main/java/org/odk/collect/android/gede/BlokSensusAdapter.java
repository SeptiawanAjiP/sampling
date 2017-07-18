package org.odk.collect.android.gede;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.augmentedreality.aksesdata.AksesDataOdk;
import org.odk.collect.android.augmentedreality.koneksi.AlamatServer;
import org.odk.collect.android.downloadinstance.Download;
import org.odk.collect.android.downloadinstance.DownloadInstances;
import org.odk.collect.android.downloadinstance.listener.DownloadPcl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Septiawan Aji Pradan on 4/21/2017.
 */

public class BlokSensusAdapter extends RecyclerView.Adapter<BlokSensusAdapter.MyViewHolder> implements DownloadPcl {
    private ArrayList<String> bs;
    private Activity activity;
    AksesDataOdk aksesDataOdk;
    String formId;

    private static final Object bb= new Object();

    ArrayList<String> uuids;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView idBlokSensus;
        public CardView cardView;
        public MyViewHolder(View view){
            super(view);
            idBlokSensus = (TextView) view.findViewById(R.id.id_blok_sensus);
            cardView = (CardView) view.findViewById(R.id.card);
            aksesDataOdk = new AksesDataOdk();

            uuids = new ArrayList<>();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDataFromServer(bs.get(getAdapterPosition()),formId);
                }
            });
        }
    }

    public BlokSensusAdapter(Activity activity, ArrayList<String> bs,String formId){
        this.bs = bs;
        this.activity = activity;
        this.formId = formId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gede_list_blok_sensus,null);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.idBlokSensus.setText(bs.get(position));
    }

    @Override
    public int getItemCount() {
        return bs.size();
    }



    public void getDataFromServer(final String var_id, final String form_id){
        AlamatSession alamatSession = new AlamatSession(activity.getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, alamatSession.getAlamat().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.d("dede_1",response);
                    JSONArray array = new JSONArray(response);
                    Log.d("dede_2",array.toString());
                    for(int i=0;i<array.length();i++){
                        Log.d("dede_2",array.toString());
                        Download download = new Download();
                        download.setFormId(form_id);
                        download.setUuid(array.getString(i));
                        download.setFormPath(aksesDataOdk.getKeteranganFormbyId(form_id));
                        Log.d("dede_3",download.getFormId());
                        Log.d("dede_4",download.getUuid());
                        startDownload(download);
                        uuids.add(array.getString(i));
                    }
                    Toast.makeText(activity, uuids.toString(), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.d("error_json",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("id",var_id);
                map.put("form_id",form_id);
                return map;
            }
        };

        Collect.getInstance2().addToRequestQueue(request);
    }

    public void startDownload(Download download){
        Log.d("aji","lewat");
        synchronized (bb) {
            DownloadInstances downloadIsian = new DownloadInstances(download, activity.getApplicationContext(), this);
            downloadIsian.exscute();
        }
    }

    @Override
    public void onpostdownload(boolean mboolean, Download download) {
        if(mboolean){
            Toast.makeText(activity, "File Download Completed", Toast.LENGTH_SHORT)
                    .show();
        }else{
            Toast.makeText(activity,"File Download not Completed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
