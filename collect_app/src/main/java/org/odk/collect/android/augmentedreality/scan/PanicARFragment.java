package org.odk.collect.android.augmentedreality.scan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.odk.collect.android.R;
import org.odk.collect.android.augmentedreality.aksesdata.AksesDataOdk;
import org.odk.collect.android.augmentedreality.aksesdata.Instances;
import org.odk.collect.android.augmentedreality.aksesdata.ParsingForm;
import org.odk.collect.android.augmentedreality.aksesdata.ParsingInstances;
import org.odk.collect.android.augmentedreality.Bangunan;
import org.odk.collect.android.augmentedreality.DatabaseHandler;
import org.odk.collect.android.augmentedreality.arkit.PARController;
import org.odk.collect.android.augmentedreality.arkit.PARFragment;
import org.odk.collect.android.augmentedreality.arkit.PARPoiLabel;
import org.odk.collect.android.augmentedreality.arkit.PARPoiLabelAdvanced;
import org.odk.collect.android.augmentedreality.arkit.PARRadarView;
import org.odk.collect.android.augmentedreality.arkit.StikerLabel;
import org.odk.collect.android.augmentedreality.sensorkit.PSKDeviceAttitude;
import org.odk.collect.android.augmentedreality.sensorkit.enums.PSKDeviceOrientation;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by doPanic on 21.02.14.
 * Fragment providing AR functionality
 * configure and add content here
 */
public class PanicARFragment extends PARFragment {

    private static ArrayList<PARPoiLabel> labelRepo = new ArrayList<PARPoiLabel>();

    private ArrayList<Instances> getInstancesByIdForm;

    private ParsingInstances parsingInstances;
    private ImageView caution,cancel;
    private RelativeLayout keterangan;

    int def;
    private ParsingForm parsingForm;
    AksesDataOdk aksesDataOdk;
    ArrayList<String> pilihanForm ;
    private String pathForm;
    private String idForm;
    CustomModalAturStiker aturStikerDialog;
    BoomMenuButton bmb;

    PARRadarView radarView;
    DatabaseHandler databaseHandler;
    int muncul;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // add content using helper methods defined below
        // example to add costume drawable
//        bangunanSensusArrayList = databaseHandler.getAll();
        muncul = 1;
        getInstancesByIdForm = new ArrayList<>();
        parsingForm = new ParsingForm();

        aksesDataOdk = new AksesDataOdk();
        parsingInstances = new ParsingInstances();
        pilihanForm = new ArrayList<>();
        databaseHandler = new DatabaseHandler(getActivity());

        pilihForm();
//        if(!getActivity().getIntent().getStringExtra("path_form").equals("")){
//            //dari atur stiker activity
//            setAr(getActivity().getIntent().getStringExtra("path_form"),getActivity().getIntent().getStringExtra("id_form"));
//        }else{
//            //dari landing page
//            pilihForm();
//        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // FIRST: setup default resource IDs
        // IMPORTANT: call before super.onCreate()
        this.viewLayoutId = R.layout.panicar_view;

        View view = super.onCreateView(inflater, container, savedInstanceState);
        //radar
//        radarView = (PARRadarView)view.findViewById(R.id.arRadarView);
//        caution = (ImageView)view.findViewById(R.id.tombol_caution);
//        cancel = (ImageView)view.findViewById(R.id.tombol_cancel);
//        keterangan = (RelativeLayout)view.findViewById(R.id.rl_keterangan_form);
        bmb = (BoomMenuButton)view.findViewById(R.id.bmb_scan);

//        caution.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                keterangan.setVisibility(View.VISIBLE);
//                cancel.setVisibility(View.VISIBLE);
//                caution.setVisibility(View.GONE);
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                keterangan.setVisibility(View.GONE);
//                cancel.setVisibility(View.GONE);
//                caution.setVisibility(View.VISIBLE);
//            }
//        });

        //menu pada saat scan

        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.TextInsideCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_5_1);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_5_4);

        //set circle menu
        final TextInsideCircleButton.Builder aturStiker = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.horse)
                .normalText("Atur Stiker")
                .normalTextColor(Color.WHITE)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        aturStiker(getPathForm(),getIdForm());
                    }
                });
        TextInsideCircleButton.Builder bukaPeta = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.eagle)
                .normalText("Buka Peta")
                .normalTextColor(Color.WHITE)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Toast.makeText(getActivity(), "Buka Peta", Toast.LENGTH_SHORT).show();
                    }
                });
        TextInsideCircleButton.Builder syncDataServer = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.dolphin)
                .normalText("Sync Data Server")
                .normalTextColor(Color.WHITE)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Toast.makeText(getActivity(), "Sync Data Server ...", Toast.LENGTH_SHORT).show();
                    }
                });
        TextInsideCircleButton.Builder gantiKuesioner = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.elephant)
                .normalText("Pilih Kuesioner")
                .normalTextColor(Color.WHITE)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        pilihForm();
                    }
                });
        TextInsideCircleButton.Builder detailKuesioner = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.horse)
                .normalText("Detail Kuesioner")
                .normalTextColor(Color.WHITE)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        CustomModalDetailForm customModalDetailForm= new CustomModalDetailForm(getActivity(),"","");
                        customModalDetailForm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        customModalDetailForm.show();
                    }
                });



            bmb.addBuilder(aturStiker);
            bmb.addBuilder(bukaPeta);
            bmb.addBuilder(syncDataServer);
            bmb.addBuilder(gantiKuesioner);
            bmb.addBuilder(detailKuesioner);


        getRadarView().setRadarRange(500);
//
        return view;
    }

    public void hilangkanRadar(){
        radarView.setVisibility(View.GONE);
        muncul = 0;
    }

    public void munculkanRadar(){
        radarView.setVisibility(View.VISIBLE);
        muncul = 1;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_random_poi:
                int random = (new Random().nextInt(labelRepo.size()-1)+0);
                PARController.getInstance().addPoi(labelRepo.get(random));
                Toast.makeText(this.getActivity(),"Added: " + labelRepo.get(random).getTitle(), Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            case  R.id.action_add_cardinal_pois:
                createCDPOIs();
                return super.onOptionsItemSelected(item);
            case R.id.action_delete_last_poi:
                if (PARController.getInstance().numberOfObjects() > 0){
                    int lastObject = PARController.getInstance().numberOfObjects()-1;
                    Toast.makeText(this.getActivity(),"Removing: " + ((PARPoiLabel)PARController.getInstance().getObject(lastObject)).getTitle(), Toast.LENGTH_SHORT).show();
                    PARController.getInstance().removeObject(lastObject);
                }
                return super.onOptionsItemSelected(item);
            case R.id.action_delete_all_pois:
                PARController.getInstance().clearObjects();
                return super.onOptionsItemSelected(item);
            case R.id.set_ket_stiker:



//                Intent intent = new Intent(getActivity(),CustomModalAturStiker.class);
//                intent.putExtra("path_form",getPathForm());
//                startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    //==============================================================================================
    // Callback
    //==============================================================================================
    @Override
    public void onDeviceOrientationChanged(PSKDeviceOrientation newOrientation) {
        super.onDeviceOrientationChanged(newOrientation);
        Toast.makeText(getActivity(), "onDeviceOrientationChanged: " + PSKDeviceAttitude.rotationToString(newOrientation), Toast.LENGTH_LONG).show();
    }


    public StikerLabel createStiker(final ArrayList<String> bangunansensus, Location location,ArrayList<String> key){
        Log.d("aji_bangunan_sensus",bangunansensus.get(3));
        final StikerLabel stiker = new StikerLabel(key,location,bangunansensus,R.layout.stiker_label_2,R.drawable.ic_dot_blue);


        stiker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("aji_bangunan_sensus_67",bangunansensus.get(3));
                CustomModalFotoBs customModalScan = new CustomModalFotoBs(getActivity(),bangunansensus.get(0));
                customModalScan.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customModalScan.show();

            }
        });
        return stiker;
    }

    public PARPoiLabelAdvanced createPoi(String title, String description, double lat, double lon, double alt) {
        Location poiLocation = new Location(title);
        poiLocation.setLatitude(lat);
        poiLocation.setLongitude(lon);
        poiLocation.setAltitude(alt);

        final PARPoiLabelAdvanced parPoiLabel = new PARPoiLabelAdvanced(poiLocation, title, description, R.layout.sticker_label, R.drawable.radar_dot);
        parPoiLabel.setIsAltitudeEnabled(true);
        parPoiLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), parPoiLabel.getTitle() + " - " + parPoiLabel.getDescription(), Toast.LENGTH_LONG).show();
            }
        });

        return parPoiLabel;
    }

    private PARPoiLabelAdvanced createRepoPoi(
            String title,
            String description,
            double latitude,
            double longitude,
            double altitude) {
        return createPoi(title, description, latitude, longitude,altitude);
    }



    /**
     * adds 4 points in cardinal direction at current location
     */
    private void createCDPOIs(){

        double degreeCorrection = 0.1;    // approx 7700 meter
//        double degreeCorrection = 0.01; // approx 1000 meter
//        double degreeCorrection = 0.001; // approx 121 meter
//        double degreeCorrection = 0.0001; // approx 12 meter
        Location currentLocation = PSKDeviceAttitude.sharedDeviceAttitude().getLocation();

        PARController.getInstance().addPoi(createPoi("Utara", "", currentLocation.getLatitude()+degreeCorrection, currentLocation.getLongitude(),0.0));
        PARController.getInstance().addPoi(createPoi("Selatan", "", currentLocation.getLatitude()-degreeCorrection, currentLocation.getLongitude(),0.0));
        PARController.getInstance().addPoi(createPoi("Barat", "",  currentLocation.getLatitude(),                  currentLocation.getLongitude()-degreeCorrection,0.0));
        PARController.getInstance().addPoi(createPoi("Timur", "",  currentLocation.getLatitude(),                  currentLocation.getLongitude()+degreeCorrection,0.0));
    }

    public void pilihForm(){
        String[] pilihan = new String[aksesDataOdk.getKeteranganForm().size()];
        for (int i=0;i<aksesDataOdk.getKeteranganForm().size();i++){
            pilihan[i] = aksesDataOdk.getKeteranganForm().get(i).getDisplayName();
        }

        def = 0;

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
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
                        setPathForm(def);
                        setIdForm(def);
                        Log.d("wulan_d",getPathForm()+" "+getIdForm());
                        cekStiker(getPathForm(),getIdForm());
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

    public void setPathForm(int def){
        pathForm = aksesDataOdk.getKeteranganForm().get(def).getPathForm();
    }

    public String getPathForm(){
        return pathForm;
    }

    public void setIdForm(int def){
        idForm = aksesDataOdk.getKeteranganForm().get(def).getIdForm();
    }

    public String getIdForm(){
        return idForm;
    }

    public void cekStiker(String pathForm,String idForm){
        if(databaseHandler.getAll(idForm).isEmpty()){
            Log.d("wulan_d_2","sini");
            aturStiker(pathForm,idForm);
        }else{
            setAr(pathForm,idForm);
        }
    }
    public void setAr(String pathForm,String idForm){
        PARController.getInstance().clearObjects();
        ArrayList<String> key = databaseHandler.getAll(idForm);
        ArrayList<Bangunan> bangunanArrayList = new ArrayList<>();
        getInstancesByIdForm = aksesDataOdk.getKeteranganInstancesbyIdForm(idForm);
        key.add("foto_bangunan");
        key.add("location");
        Log.d("cinta",key.toString());
        for (Instances instances : getInstancesByIdForm){
            try{
                bangunanArrayList.add(parsingInstances.getValueHasMap(instances.getPathInstances(),key));
                Log.d("cinta_size",""+bangunanArrayList.size());
            }catch (Exception e){

            }
        }
        for (Bangunan bangunan : bangunanArrayList){
            Log.d("Cinta",bangunan.getHashMap().toString());
            ArrayList<String> parameter = new ArrayList<>();
            Location location = new Location("location");
            location.setLatitude(bangunan.getLat());
            location.setLongitude(bangunan.getLon());

            parameter.add(bangunan.getPathFoto());
            parameter.add(bangunan.getJarak());

            parameter.add(bangunan.getHashMap().get(key.get(0)));
            parameter.add(bangunan.getHashMap().get(key.get(1)));
            parameter.add(bangunan.getHashMap().get(key.get(2)));
            parameter.add(bangunan.getHashMap().get(key.get(3)));

            Log.d("wulan_8",parameter.toString());

            PARController.getInstance().addPoi( createStiker(parameter,location,key));
        }

    }

    public void aturStiker(String pathForm, String idForm){

        aturStikerDialog = new CustomModalAturStiker(getActivity(),pathForm,idForm);
        aturStikerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        aturStikerDialog.show();
        aturStikerDialog.setDialog(new CustomModalAturStiker.OnMyDialogAturStikerResult() {
            @Override
            public void finish(String pathForm, String idForm) {
                Log.d("wulan_wulan","path :"+pathForm+","+"id :"+idForm);
                setAr(pathForm,idForm);
            }
        });
//        Intent intent = new Intent(getActivity(),AturStikerActivity.class);
//        intent.putExtra("path_form",getPathForm());
//        intent.putExtra("id_form",getIdForm());
//        startActivity(intent);
    }

}
