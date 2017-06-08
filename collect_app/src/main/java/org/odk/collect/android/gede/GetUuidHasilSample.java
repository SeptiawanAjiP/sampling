package org.odk.collect.android.gede;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.odk.collect.android.augmentedreality.MainActivity;
import org.odk.collect.android.augmentedreality.aksesdata.AksesDataOdk;
import org.odk.collect.android.downloadinstance.Download;
import org.odk.collect.android.downloadinstance.DownloadInstances;
import org.odk.collect.android.downloadinstance.listener.DownloadPcl;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Septiawan Aji Pradan on 6/7/2017.
 */

public class GetUuidHasilSample extends AppCompatActivity implements View.OnClickListener,DownloadPcl {
    private EditText alamatServerEt,varIdEt;
    private Button getFormId,getUUID;
    private TextView formIdTv,hasilUUIDtv;
    private String formId,varId,alamatSer;
    private static final Object bb= new Object();


    AksesDataOdk aksesDataOdk;
    int def;
    ArrayList<String> uuids;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gd_get_uuid_sample);

        alamatServerEt = (EditText)findViewById(R.id.alamart_server);
        varIdEt = (EditText)findViewById(R.id.var_id);
        getFormId = (Button)findViewById(R.id.button_form_id);
        getUUID = (Button)findViewById(R.id.btn_get_uuid);
        formIdTv = (TextView)findViewById(R.id.tv_form_id);
        hasilUUIDtv= (TextView)findViewById(R.id.hasil_uuid);

        aksesDataOdk = new AksesDataOdk();
        formId = "";
        uuids = new ArrayList<>();

        getFormId.setOnClickListener(this);
        getUUID.setOnClickListener(this);
    }

    public void getDataFromServer(final String alamat, final String var_id, final String form_id){
        StringRequest request = new StringRequest(Request.Method.POST, alamat, new Response.Listener<String>() {
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
                    hasilUUIDtv.setVisibility(View.VISIBLE);
                    hasilUUIDtv.setText(uuids.toString());
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

    @Override
    public void onClick(View v) {
        if(v==getFormId){
            pilihForm();
        }else if(v==getUUID){
            if(formId.equals("")){
                Toast.makeText(this, "Pilih Form Dulu", Toast.LENGTH_SHORT).show();
            }else{
                varId = varIdEt.getText().toString();
                alamatSer = alamatServerEt.getText().toString();
                getDataFromServer(alamatSer,varId,formId);
            }
        }
    }

    public void pilihForm(){
        String[] pilihan = new String[aksesDataOdk.getKeteranganForm().size()];
        for (int i=0;i<aksesDataOdk.getKeteranganForm().size();i++){
            pilihan[i] = aksesDataOdk.getKeteranganForm().get(i).getDisplayName();
        }

        def = 0;

        AlertDialog dialog = new AlertDialog.Builder(GetUuidHasilSample.this)
                .setTitle("Pilih Kuesioner")
                .setSingleChoiceItems(pilihan, 0,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        def = which;
                    }
                })
                .setPositiveButton("Pilih", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setIdForm(def);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
        dialog.setCancelable(false);
    }
    public void setIdForm(int def){
        formId = aksesDataOdk.getKeteranganForm().get(def).getIdForm();
        formIdTv.setVisibility(View.VISIBLE);
        formIdTv.setText(formId);
    }

    public void startDownload(Download download){
        Log.d("aji","lewat");
        synchronized (bb) {
            DownloadInstances downloadIsian = new DownloadInstances(download, GetUuidHasilSample.this, this);
            downloadIsian.exscute();
        }
    }

    @Override
    public void onpostdownload(boolean mboolean, Download download) {
        if(mboolean){
            Toast.makeText(this, "File Download Completed", Toast.LENGTH_SHORT)
                    .show();
        }else{
            Toast.makeText(this,"File Download not Completed", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
