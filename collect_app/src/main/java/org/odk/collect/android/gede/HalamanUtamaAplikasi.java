package org.odk.collect.android.gede;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import org.odk.collect.android.activities.InstanceChooserList;
import org.odk.collect.android.activities.MainMenuActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.augmentedreality.DatabaseHandler;
import org.odk.collect.android.augmentedreality.aksesdata.AksesDataOdk;
import org.odk.collect.android.augmentedreality.aksesdata.Form;
import org.odk.collect.android.augmentedreality.aksesdata.Instances;
import org.odk.collect.android.augmentedreality.aksesdata.ParsingForm;
import org.odk.collect.android.downloadinstance.Download;
import org.odk.collect.android.downloadinstance.DownloadInstances;
import org.odk.collect.android.downloadinstance.listener.DownloadPcl;
import org.odk.collect.android.gede.listsampel.SampelActivity;
import org.odk.collect.android.listeners.DiskSyncListener;
import org.odk.collect.android.tasks.InstanceSyncTask;
import org.odk.collect.android.utilities.ApplicationConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aji on 7/23/2017.
 */

public class HalamanUtamaAplikasi extends AppCompatActivity implements DownloadPcl,DiskSyncListener {
    private LinearLayout tarikLl,listLl,settingLl;
    private AksesDataOdk aksesDataOdk;
    private SessionManager sessionManager;
    int set;
    private ParsingForm parsingForm;
    int def;
    String pilihan;
    ArrayList<String> uuids;
    private static final Object bb= new Object();
    private InstanceSyncTask instanceSyncTask;
    private ProgressDialog progressDialog;

    private HashMap<String,String> keyForParse;
    private org.odk.collect.android.gede.databasehandler.DatabaseHandler databaseHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gede_halaman_utama_aplikasi);
        tarikLl = (LinearLayout)findViewById(R.id.ll_tarik);
        listLl = (LinearLayout)findViewById(R.id.ll_list);
        settingLl = (LinearLayout)findViewById(R.id.ll_setting);

        aksesDataOdk = new AksesDataOdk();
        parsingForm = new ParsingForm();
        keyForParse = new HashMap<>();
        databaseHandler = new org.odk.collect.android.gede.databasehandler.DatabaseHandler(getApplicationContext());
//        dummyData();
        uuids = new ArrayList<>();
        sessionManager = new SessionManager(getApplicationContext());
        if(sessionManager.getAlamat()==null){
            Toast.makeText(HalamanUtamaAplikasi.this, "Atur alamat url terlebih dahulu", Toast.LENGTH_SHORT).show();
        }

        settingLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setting();
            }
        });

        listLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekFormUdahDownload();
            }
        });

        tarikLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekForm();
            }
        });
    }


    public void setting(){
        final String[] pilihan = {"Atur url","ODK"};
        set = 0;
        AlertDialog dialog = new AlertDialog.Builder(HalamanUtamaAplikasi.this)
                .setTitle("Setting")
                .setSingleChoiceItems(pilihan, 0,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        set = which;
                    }
                })
                .setPositiveButton("Pilih", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(set==0){
                            ModalEditUrl modalEditUrl =  new ModalEditUrl(HalamanUtamaAplikasi.this);
                            modalEditUrl.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                            modalEditUrl.show();
                        }else if(set==1){
                            startActivity(new Intent(HalamanUtamaAplikasi.this, MainMenuActivity.class));
                        }
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }



    public void getBS(final String idForm){

        final ArrayList<String> bs = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sessionManager.getAlamat()+Koneksi.PHP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("_gede",response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("respon");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject j = jsonArray.getJSONObject(i);
                        bs.add(j.getString("var_id"));
                    }
                    if(bs.size()==0){
                        Toast.makeText(HalamanUtamaAplikasi.this, "Tidak ada wilayah tugas", Toast.LENGTH_SHORT).show();
                    }else{
                        pilihBS(bs,idForm);
                    }
                }catch (Exception e){

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("type","getID");
                map.put("form_id",idForm);
                map.put("user_id","90");
                return map;
            }
        };
        Collect.getInstance().addToRequestQueue(stringRequest);
    }

    public void pilihBS(ArrayList<String> bs, final String idForm){
        Log.d("_gede_2",bs.toString());
        final String[] pilihan = new String[bs.size()];
        for (int i=0;i<bs.size();i++){
            pilihan[i] = bs.get(i);
        }
        def = 0;

        AlertDialog dialog = new AlertDialog.Builder(HalamanUtamaAplikasi.this)
                .setTitle("Pilih wilayah tugas")
                .setSingleChoiceItems(pilihan, 0,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        def = which;
                    }
                })
                .setPositiveButton("Pilih", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDataFromServer(pilihan[def],idForm);
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

    public void getDataFromServer(final String var_id, final String form_id){
        showProgress();
        StringRequest request = new StringRequest(Request.Method.POST, sessionManager.getAlamat()+Koneksi.PHP, new Response.Listener<String>() {
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
                        databaseHandler.insertTabelSampel(form_id,array.getString(i),var_id);
                        uuids.add(array.getString(i));
                    }
                    Toast.makeText(HalamanUtamaAplikasi.this, "Berhasil Tarik Sampel", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
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
                map.put("type","sampling");
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
            DownloadInstances downloadIsian = new DownloadInstances(download, getApplicationContext(), this);
            downloadIsian.exscute();
        }
    }

    @Override
    public void onpostdownload(boolean mboolean, Download download) {
        if(mboolean){
            Toast.makeText(getApplicationContext(), "File Download Completed", Toast.LENGTH_SHORT)
                    .show();
        }else{
            Toast.makeText(getApplicationContext(),"File Download not Completed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void cekFormUdahDownload(){

        final String[] pilihan = new String[databaseHandler.getForm().size()];
        for (int i=0;i<databaseHandler.getForm().size();i++){
            pilihan[i] = databaseHandler.getForm().get(i);
        }

        def = 0;

        AlertDialog dialog = new AlertDialog.Builder(HalamanUtamaAplikasi.this)
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
//                        Intent i = new Intent(getApplicationContext(), InstanceChooserList.class);
//                        i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE,
//                                ApplicationConstants.FormModes.EDIT_SAVED);
//                        startActivity(i);
                        cekId(pilihan[def]);
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

    public void cekId(final String idForm){

        final String[] pilihan = new String[databaseHandler.getId(idForm).size()];
        for (int i=0;i<databaseHandler.getId(idForm).size();i++){
            pilihan[i] = databaseHandler.getId(idForm).get(i);
        }

        def = 0;

        AlertDialog dialog = new AlertDialog.Builder(HalamanUtamaAplikasi.this)
                .setTitle("Pilih Wilayah")
                .setSingleChoiceItems(pilihan, 0,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        def = which;
                    }
                })
                .setPositiveButton("Pilih", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent i = new Intent(getApplicationContext(), InstanceChooserList.class);
//                        i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE,
//                                ApplicationConstants.FormModes.EDIT_SAVED);
//                        startActivity(i);
                        getKeyForm(idForm,pilihan[def]);
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

    public void getKeyForm (final String idForm,final String id){
        ArrayList<String> keyPertama = new ArrayList<>();
        for(int i=0;i<parsingForm.getVariabelForm(aksesDataOdk.getKeteranganFormbyId(idForm)).size();i++){
            if(!parsingForm.getVariabelForm(aksesDataOdk.getKeteranganFormbyId(idForm)).get(i).equals(ParsingForm.LOKASI) && !parsingForm.getVariabelForm(aksesDataOdk.getKeteranganFormbyId(idForm)).get(i).equals(ParsingForm.FOTO_BANGUNAN)){
                keyPertama.add(parsingForm.getVariabelForm(aksesDataOdk.getKeteranganFormbyId(idForm)).get(i));
            }
        }
        final String[] variabel = new String[keyPertama.size()];
        for (int i=0;i<keyPertama.size();i++) {
            variabel[i] = keyPertama.get(i);
        }

        def = 0;
        AlertDialog dialog = new AlertDialog.Builder(HalamanUtamaAplikasi.this)
                .setTitle("Atur Variabel")

                .setSingleChoiceItems(variabel, 0,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        def = which;
                    }
                })
                .setPositiveButton("Pilih", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pilihan = variabel[def];
                        Intent intent = new Intent(HalamanUtamaAplikasi.this, SampelActivity.class);
                        intent.putExtra("idForm",idForm);
                        intent.putExtra("id",id);
                        intent.putExtra("key",pilihan);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
        Log.d("wulan_key",keyPertama.toString());
    }





    @Override
    public void syncComplete(String result) {

    }

    private void cekForm(){
        Log.d("_cuk","cuk");
        final ArrayList<Form> formHasil = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sessionManager.getAlamat()+Koneksi.PHP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Form> forms = aksesDataOdk.getKeteranganForm();
                Log.d("_gede",response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("respon");
                    for(int i=0;i<jsonArray.length();i++){
                        String idForm="";
                        JSONObject j = jsonArray.getJSONObject(i);
                        idForm = j.getString("form_id");
                        for(int k=0;k<forms.size();k++){
                            if(idForm.equals(forms.get(k).getIdForm())){
                                formHasil.add(forms.get(k));
                            }
                        }
                    }
                    Log.d("__formHasil",formHasil.toString());
                    if(formHasil.size()==0){
                        Toast.makeText(HalamanUtamaAplikasi.this, "Tidak ada form yang siap disampling", Toast.LENGTH_SHORT).show();
                    }else{
                        pilihForm(formHasil);
                    }
                }catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("cuk",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("type","getFormId");
                return map;
            }
        };
        Collect.getInstance2().addToRequestQueue(stringRequest);
    }

    public void pilihForm(ArrayList<Form> hasil){
        final String[] pilihan = new String[hasil.size()];
        for (int i=0;i<hasil.size();i++){
            pilihan[i] = hasil.get(i).getDisplayName();
        }

        def = 0;

        AlertDialog dialog = new AlertDialog.Builder(HalamanUtamaAplikasi.this)
                .setTitle("Daftar Kuesioner Siap Sampling")
                .setSingleChoiceItems(pilihan, 0,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        def = which;
                    }
                })
                .setPositiveButton("Pilih", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getBS(pilihan[def]);
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
    private void showProgress() {
        progressDialog = null;// Initialize to null
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void dummyData(){
        databaseHandler.insertTabelSampel("build_odk_form","uuid:989dfh20hdfi","asu_1");
        databaseHandler.insertTabelSampel("build_listing","uuid:989dfh20tetui","koko_1");
        databaseHandler.insertTabelSampel("build_odk_form","uuid:989dfiuouioi","dbm_1");
        databaseHandler.insertTabelSampel("build_listing","uuid:989dfhvbxvxvb","cece_1");
    }

}
