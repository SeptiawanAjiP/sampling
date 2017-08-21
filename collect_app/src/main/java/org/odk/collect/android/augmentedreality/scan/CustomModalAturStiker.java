package org.odk.collect.android.augmentedreality.scan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.odk.collect.android.R;
import org.odk.collect.android.augmentedreality.DatabaseHandler;
import org.odk.collect.android.augmentedreality.aksesdata.ParsingForm;
import org.odk.collect.android.augmentedreality.arkit.PARController;
import org.odk.collect.android.augmentedreality.jajal.CustomDoalog;
import org.odk.collect.android.augmentedreality.tooltipWindow.TooltipWindow;
import org.odk.collect.android.utilities.ToastUtils;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Septiawan Aji Pradan on 6/2/2017.
 */

public class CustomModalAturStiker extends Dialog{

    private Activity activity;
    private String pathForm;
    private String idForm;
    private HashMap<String,String> keyForParse;
    private RelativeLayout saveHas;
    public static final String TEXTVIEW_ATAS = "textview_atas";
    public static final String TEXTVIEW_1 = "textview_1";
    public static final String TEXTVIEW_2 = "textview_2";
    public static final String TEXTVIEW_3 = "textview_3";
    public static final String TEXTVIEW_4 = "textview_4";
    private RelativeLayout rl_1,rl_2,rl_3,rl_4,rl_ket_sls;
    private TextView tv_1,tv_2,tv_3,tv_4,tv_atas;
    private ParsingForm parsingForm;
    int def;

    private ArrayList<String> kec;
    private ArrayList<String> keyPertama = new ArrayList<>();
    private String pilihan;

    private DatabaseHandler db;

    OnMyDialogAturStikerResult onMyDialogAturStikerResult;

   public CustomModalAturStiker(Activity activity, String pathForm, String idForm){
        super(activity);
        this.activity = activity;
        this.pathForm = pathForm;
        this.idForm = idForm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_ket_stiker);
        declaration();
        setData();

        rl_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihanVariabel(v,tv_1,TEXTVIEW_1);
            }
        });
        rl_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihanVariabel(v,tv_2,TEXTVIEW_2);
            }
        });
        rl_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihanVariabel(v,tv_3,TEXTVIEW_3);
            }
        });
        rl_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Untuk Jarak", Toast.LENGTH_SHORT).show();
            }
        });
        rl_ket_sls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihanVariabel(v,tv_atas,TEXTVIEW_ATAS);
            }
        });

        saveHas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getHasMap();
                if(getHasMap().size()!=4){
                    Toast.makeText(activity, "Ada yang belum di isi", Toast.LENGTH_SHORT).show();
                }else{
                    db.insertTabel(idForm,getHasMap().get(TEXTVIEW_ATAS),getHasMap().get(TEXTVIEW_1),getHasMap().get(TEXTVIEW_2),getHasMap().get(TEXTVIEW_3),getHasMap().get(TEXTVIEW_4));
                    if(onMyDialogAturStikerResult != null) {
                        onMyDialogAturStikerResult.finish(pathForm, idForm);
                        CustomModalAturStiker.this.dismiss();
                    }
                }
            }
        });
        getKeyForm();
    }

    public void declaration(){
        rl_1 = (RelativeLayout)findViewById(R.id.rl_kolom_1);
        rl_2 = (RelativeLayout)findViewById(R.id.rl_kolom_2);
        rl_3 = (RelativeLayout)findViewById(R.id.rl_kolom_3);
        rl_4 = (RelativeLayout)findViewById(R.id.rl_kolom_4);
        rl_ket_sls = (RelativeLayout)findViewById(R.id.rl_ket_sls);
        tv_1 = (TextView)findViewById(R.id.kolom_1);
        tv_2 = (TextView)findViewById(R.id.kolom_2);
        tv_3 = (TextView)findViewById(R.id.kolom_3);
        tv_4 = (TextView)findViewById(R.id.kolom_4);
        tv_atas = (TextView)findViewById(R.id.tv_atas);
        saveHas = (RelativeLayout) findViewById(R.id.rl_simpan_form);

        parsingForm = new ParsingForm();
        kec = new ArrayList<>();
        keyForParse = new HashMap<>();
        db = new DatabaseHandler(activity.getApplicationContext());
    }

    public void setData(){
        ArrayList<String> variabelnya = db.getAll(idForm);
        if(!variabelnya.isEmpty()){
            tv_atas.setText(variabelnya.get(0));
            tv_1.setText(variabelnya.get(1));
            tv_2.setText(variabelnya.get(2));
            tv_3.setText(variabelnya.get(3));
        }
    }

    public void getKeyForm (){
        for(int i=0;i<parsingForm.getVariabelForm(pathForm).size();i++){
            if(!parsingForm.getVariabelForm(pathForm).get(i).equals(ParsingForm.LOKASI) && !parsingForm.getVariabelForm(pathForm).get(i).equals(ParsingForm.FOTO_BANGUNAN)){
                keyPertama.add(parsingForm.getVariabelForm(pathForm).get(i));
            }
        }
        Log.d("wulan_key",keyPertama.toString());
    }


    public void pilihanVariabel(final View v,final TextView tv,final String key){
        final String[] variabel = new String[keyPertama.size()];
        for (int i=0;i<keyPertama.size();i++) {
            variabel[i] = keyPertama.get(i);
        }

            def = 0;
            AlertDialog dialog = new AlertDialog.Builder(activity)
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
                            keyPertama.remove(def);
                            tv.setText(pilihan);
                            setHasmap(key,pilihan);
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            dialog.show();
    }

    public void setHasmap(String key,String value){
        keyForParse.put(key,value);
    }

    public HashMap<String,String> getHasMap(){
        return keyForParse;
    }

    public void setDialog(CustomModalAturStiker.OnMyDialogAturStikerResult dialogResult){
        onMyDialogAturStikerResult = dialogResult;
    };

    public interface OnMyDialogAturStikerResult{
        void finish(String pathForm,String idForm);
    }

}
