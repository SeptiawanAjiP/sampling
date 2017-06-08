package org.odk.collect.android.activities;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.downloadinstance.Download;
import org.odk.collect.android.downloadinstance.listener.DownloadPcl;
import org.odk.collect.android.listeners.DiskSyncListener;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.tasks.DiskSyncTask;
import org.odk.collect.android.utilities.VersionHidingCursorAdapter;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Septiawan Aji Pradan on 5/28/2017.
 */

public class ListFormForDownload extends FormListActivity implements DiskSyncListener,DownloadPcl {
    private static final String FORM_CHOOSER_LIST_SORTING_ORDER = "formChooserListSortingOrder";

    private static final boolean EXIT = true;
    private static final String syncMsgKey = "syncmsgkey";

    private DiskSyncTask mDiskSyncTask;

    private AlertDialog mAlertDialog;
    private static final Object bb= new Object();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // must be at the beginning of any activity that can be called from an external intent
        try {
            Collect.createODKDirs();
        } catch (RuntimeException e) {
            createErrorDialog(e.getMessage(), EXIT);
            return;
        }

        setContentView(R.layout.chooser_list_layout);
        setTitle(getString(R.string.enter_data));

        setupAdapter();

        if (savedInstanceState != null && savedInstanceState.containsKey(syncMsgKey)) {
            TextView tv = (TextView) findViewById(R.id.status_text);
            tv.setText((savedInstanceState.getString(syncMsgKey)).trim());
        }

        // DiskSyncTask checks the disk for any forms not already in the content provider
        // that is, put here by dragging and dropping onto the SDCard
        mDiskSyncTask = (DiskSyncTask) getLastNonConfigurationInstance();
        if (mDiskSyncTask == null) {
            Timber.i("Starting new disk sync task");
            mDiskSyncTask = new DiskSyncTask();
            mDiskSyncTask.setDiskSyncListener(this);
            mDiskSyncTask.execute((Void[]) null);
        }
        mSortingOptions = new String[]{
                getString(R.string.sort_by_name_asc), getString(R.string.sort_by_name_desc),
                getString(R.string.sort_by_date_asc), getString(R.string.sort_by_date_desc),
        };
        getIdForm();

    }


    @Override
    public Object onRetainNonConfigurationInstance() {
        // pass the thread on restart
        return mDiskSyncTask;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TextView tv = (TextView) findViewById(R.id.status_text);
        outState.putString(syncMsgKey, tv.getText().toString().trim());
    }


    /**
     * Stores the path of selected form and finishes.
     */
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        // get uri to form
        long idFormsTable = getListAdapter().getItemId(position);
        Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, idFormsTable);
        Toast.makeText(this, formUri.toString(), Toast.LENGTH_SHORT).show();
        Log.d("septiawan_form_choser",formUri.toString());

        Collect.getInstance().getActivityLogger().logAction(this, "onListItemClick",
                formUri.toString());
        String formId = FormsProviderAPI.FormsColumns.FORM_FILE_PATH;
        String action = getIntent().getAction();
        Log.d("aji_form_id",formId);
        if (Intent.ACTION_PICK.equals(action)) {
            // caller is waiting on a picked form
            setResult(RESULT_OK, new Intent().setData(formUri));
        } else {
//            Download download = new Download();
//            download.setFormPath(formId);
//            download.setUuid("uuid:42d6492f-5903-40ac-8106-8903cfa5685e");
//            startDownload(download);
            Toast.makeText(this, formId, Toast.LENGTH_SHORT).show();
            // caller wants to view/edit a form, so launch formentryactivity

//            Intent intent = new Intent(Intent.ACTION_EDIT, formUri);
//            intent.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
//            startActivity(intent);
        }

        finish();
    }


    @Override
    protected void onResume() {
        mDiskSyncTask.setDiskSyncListener(this);
        super.onResume();

        if (mDiskSyncTask.getStatus() == AsyncTask.Status.FINISHED) {
            syncComplete(mDiskSyncTask.getStatusMessage());
        }
    }


    @Override
    protected void onPause() {
        mDiskSyncTask.setDiskSyncListener(null);
        super.onPause();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Collect.getInstance().getActivityLogger().logOnStart(this);
    }

    @Override
    protected void onStop() {
        Collect.getInstance().getActivityLogger().logOnStop(this);
        super.onStop();
    }


    /**
     * Called by DiskSyncTask when the task is finished
     */

    @Override
    public void syncComplete(String result) {
        Timber.i("Disk sync task complete");
        TextView tv = (TextView) findViewById(R.id.status_text);
        tv.setText(result.trim());
    }

    private void setupAdapter() {
        String[] data = new String[]{
                FormsProviderAPI.FormsColumns.DISPLAY_NAME, FormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT, FormsProviderAPI.FormsColumns.JR_VERSION
        };
        int[] view = new int[]{
                R.id.text1, R.id.text2, R.id.text3
        };

        mListAdapter =
                new VersionHidingCursorAdapter(FormsProviderAPI.FormsColumns.JR_VERSION, this, R.layout.two_item, getCursor(), data, view);

        setListAdapter(mListAdapter);
    }

    @Override
    protected String getSortingOrderKey() {
        return FORM_CHOOSER_LIST_SORTING_ORDER;
    }

    @Override
    protected void updateAdapter() {
        mListAdapter.changeCursor(getCursor());
    }

    private Cursor getCursor() {
        return new FormsDao().getFormsCursor(getFilterText(), getSortingOrder());
    }

    /**
     * Creates a dialog with the given message. Will exit the activity when the user preses "ok" if
     * shouldExit is set to true.
     */
    private void createErrorDialog(String errorMsg, final boolean shouldExit) {

        Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog", "show");

        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);
        mAlertDialog.setMessage(errorMsg);
        DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Collect.getInstance().getActivityLogger().logAction(this,
                                "createErrorDialog",
                                shouldExit ? "exitApplication" : "OK");
                        if (shouldExit) {
                            finish();
                        }
                        break;
                }
            }
        };
        mAlertDialog.setCancelable(false);
        mAlertDialog.setButton(getString(R.string.ok), errorListener);
        mAlertDialog.show();
    }



    @Override
    public void onpostdownload(boolean mboolean, Download download) {
        if(mboolean){
            Toast.makeText(this, "File Download Completed", Toast.LENGTH_SHORT)
                    .show();
//            ItemsetDbAdapter dbas = new ItemsetDbAdapter();
//            dbas.open();
//            dbas.updatedown(mnotif);
//            dbas.close();
        }else{
            Toast.makeText(this,"File Download not Completed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public ArrayList<String> getIdForm (){
        ArrayList<String> idForms = new ArrayList<>();
        FormsDao formDao = new FormsDao();
        Cursor cursor = null;
        try{
            cursor = formDao.getFormsCursor();
            if(cursor==null){
                Log.d("list_id_form","null");
            }

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()){
                String idForm = cursor.getString(cursor.getColumnIndex(FormsProviderAPI.FormsColumns.JR_FORM_ID));
                idForms.add(idForm);
            }
        }catch (Exception e){
            Log.d("list_id_form",e.toString());
        }
        Log.d("aji_id_form",idForms.toString());
        return idForms;
    }
}
