package org.odk.collect.android.augmentedreality.aksesdata;

import android.content.ContentUris;
import android.database.Cursor;
import android.util.Log;

import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Septiawan Aji Pradan on 5/28/2017.
 */

public class AksesDataOdk {
    public ArrayList<Form> getKeteranganForm (){
        ArrayList<Form> forms = new ArrayList<>();
        FormsDao formDao = new FormsDao();
        Cursor cursor = null;
        try{
            cursor = formDao.getFormsCursor();
            if(cursor==null){
                Log.d("list_id_form","null");
            }

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()){
                Form form = new Form();
                form.setIdForm(cursor.getString(cursor.getColumnIndex(FormsProviderAPI.FormsColumns.JR_FORM_ID)));
                form.setPathForm(cursor.getString(cursor.getColumnIndex(FormsProviderAPI.FormsColumns.FORM_FILE_PATH)));
                form.setDisplayName(cursor.getString(cursor.getColumnIndex(FormsProviderAPI.FormsColumns.DISPLAY_NAME)));
                forms.add(form);
            }
        }catch (Exception e){
            Log.d("list_id_form",e.toString());
        }
        Log.d("aji_id_form",forms.get(0).getPathForm().toString());
        return forms;
    }

    public String getKeteranganFormbyId(String idForm){
        String pathFile="";
        FormsDao formDao = new FormsDao();
        Cursor cursor = null;
        try{
            cursor = formDao.getFormsCursorForFormId(idForm);
            if(cursor==null){
                Log.d("list_id_form","null");
            }

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()){
                pathFile = cursor.getString(cursor.getColumnIndex(FormsProviderAPI.FormsColumns.FORM_FILE_PATH));
            }
        }catch (Exception e){
            Log.d("list_id_form",e.toString());
        }
        Log.d("aji_id_form",pathFile);
        return pathFile;
    }

    public ArrayList<Instances> getKeteranganInstances (){
        ArrayList<Instances> instances = new ArrayList<>();
        InstancesDao instancesDao = new InstancesDao();
        Cursor cursor = null;
        try{
            String sortOrder = InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH + " ASC ";
//            cursor = Collect.getInstance().getContentResolver().query(InstanceProviderAPI.InstanceColumns.CONTENT_URI,null,null,null,sortOrder);
            cursor = instancesDao.getUnsentInstancesCursor();
            if(cursor==null){
                Log.d("instances_final","null");
            }

            cursor.moveToPosition(-1);

            while(cursor.moveToNext()){
                Instances instances1 = new Instances();
                instances1.setPathInstances(cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH)));
                instances1.setUuid(cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.UUID)));
                instances1.setFormId(cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.JR_FORM_ID)));
                instances.add(instances1);
            }
        }catch (Exception e){

        }
        Log.d("aji_instances",instances.toString());
        return instances;
    }

    public ArrayList<Instances> getKeteranganInstancesbyIdForm (String idForm){
        ArrayList<Instances> instances = new ArrayList<>();
        InstancesDao instancesDao = new InstancesDao();
        Cursor cursor = null;
        try{
            cursor = instancesDao.getFinalizedInstancesCursor();
            if(cursor==null){
                Log.d("instances_final","null");
            }
            cursor.moveToPosition(-1);

            while(cursor.moveToNext()){
                Instances instances1 = new Instances();
                instances1.setPathInstances(cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH)));
                instances1.setUuid(cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.UUID)));
                instances1.setFormId(cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.JR_FORM_ID)));
                if(instances1.getFormId().equals(idForm)){
                    instances.add(instances1);
                }
            }
        }catch (Exception e){

        }
        Log.d("aji_instances",instances.toString());
        //yo
        return instances;
    }

    public String getParentDir(String dir){
        File theFile = new File(dir);
        String parent = theFile.getParent();
        return parent;
    }

    public Instances getInstanceByUUID(String uuid){
        Log.d("__jerman",uuid);
        Instances instances = new Instances();
        InstancesDao instancesDao = new InstancesDao();
        Cursor cursor = null;
        try{
            cursor = instancesDao.getAllCompletedUndeletedInstancesCursor();
            if(cursor==null){
                Log.d("instances_final","null");
            }
            cursor.moveToPosition(-1);

            while(cursor.moveToNext()){
                Log.d("__jerman1","masuk");
                Instances instances1 = new Instances();
                instances1.setPathInstances(cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH)));
                instances1.setUuid(cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.UUID)));
                instances1.setFormId(cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.JR_FORM_ID)));
                instances1.setUri(ContentUris.withAppendedId(InstanceProviderAPI.InstanceColumns.CONTENT_URI,
                        cursor.getLong(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID))));
                Log.d("_makdim_path",instances1.getPathInstances());
                Log.d("_makdim_uuid",instances1.getUuid());
                Log.d("_makdim_form",instances1.getFormId());
                Log.d("_makdim_uri",instances1.getUri().toString());
                if(instances1.getUuid().equals(uuid)){
                    Log.d("__jerman2","masuk");
                    instances = instances1;
                }
            }
        }catch (Exception e){
            Log.d("_cuk_uuid",e.toString());
        }

        //yo
        return instances;
    }
}
