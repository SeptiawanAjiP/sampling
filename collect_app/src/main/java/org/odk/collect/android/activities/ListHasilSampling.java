/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.adapters.ViewSentListAdapter;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.gede.InstanceSyncHasilSampling;
import org.odk.collect.android.listeners.DiskSyncListener;
import org.odk.collect.android.logic.FormController;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI.InstanceColumns;
import org.odk.collect.android.tasks.InstanceSyncTask;
import org.odk.collect.android.utilities.ApplicationConstants;

/**
 * Responsible for displaying all the valid instances in the instance directory.
 *
 * @author Yaw Anokwa (yanokwa@gmail.com)
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class ListHasilSampling extends InstanceListActivity implements DiskSyncListener {
    private static final String INSTANCE_LIST_ACTIVITY_SORTING_ORDER = "instanceListActivitySortingOrder";
    private static final String VIEW_SENT_FORM_SORTING_ORDER = "ViewSentFormSortingOrder";

    private static final boolean EXIT = true;
    private static final boolean DO_NOT_EXIT = false;
    private AlertDialog mAlertDialog;

    private InstanceSyncHasilSampling instanceSyncTask;

    private boolean mEditMode;

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

        String formMode = getIntent().getStringExtra(ApplicationConstants.BundleKeys.FORM_MODE);
        if (formMode == null || ApplicationConstants.FormModes.EDIT_SAVED.equalsIgnoreCase(formMode)) {
            setTitle(getString(R.string.review_data));
            mEditMode = true;
            mSortingOptions = new String[]{
                    getString(R.string.sort_by_name_asc), getString(R.string.sort_by_name_desc),
                    getString(R.string.sort_by_date_asc), getString(R.string.sort_by_date_desc),
                    getString(R.string.sort_by_status_asc), getString(R.string.sort_by_status_desc)
            };
        } else {
            setTitle(getString(R.string.view_sent_forms));
            mSortingOptions = new String[]{
                    getString(R.string.sort_by_name_asc), getString(R.string.sort_by_name_desc),
                    getString(R.string.sort_by_date_asc), getString(R.string.sort_by_date_desc)
            };
        }
        setupAdapter();

        instanceSyncTask = new InstanceSyncHasilSampling();
        instanceSyncTask.setDiskSyncListener(this);
        instanceSyncTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Stores the path of selected instance in the parent class and finishes.
     */
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Cursor c = (Cursor) getListAdapter().getItem(position);
        startManagingCursor(c);
        Uri instanceUri =
                ContentUris.withAppendedId(InstanceColumns.CONTENT_URI,
                        c.getLong(c.getColumnIndex(InstanceColumns._ID)));

        Collect.getInstance().getActivityLogger().logAction(this, "onListItemClick",
                instanceUri.toString());
        Log.d("septiawan_uri_getPath",instanceUri.getPath());
        Log.d("septiawan_uri_toString",instanceUri.toString());

        if (view.findViewById(R.id.visible_off).getVisibility() != View.VISIBLE) {
            String action = getIntent().getAction();
            setResult(RESULT_OK, new Intent().setData(instanceUri));
            if (Intent.ACTION_PICK.equals(action)) {
                // caller is waiting on a picked form
                setResult(RESULT_OK, new Intent().setData(instanceUri));
            } else {
                // the form can be edited if it is incomplete or if, when it was
                // marked as complete, it was determined that it could be edited
                // later.
                String status = c.getString(c.getColumnIndex(InstanceColumns.STATUS));
                String strCanEditWhenComplete =
                        c.getString(c.getColumnIndex(InstanceColumns.CAN_EDIT_WHEN_COMPLETE));

                boolean canEdit = status.equals(InstanceProviderAPI.STATUS_INCOMPLETE)
                        || Boolean.parseBoolean(strCanEditWhenComplete);
                if (!canEdit) {
                    createErrorDialog(getString(R.string.cannot_edit_completed_form),
                            DO_NOT_EXIT);
                    return;
                }
                // caller wants to view/edit a form, so launch formentryactivity
                Intent parentIntent = this.getIntent();
                Intent intent = new Intent(Intent.ACTION_EDIT, instanceUri);
                String formMode = parentIntent.getStringExtra(ApplicationConstants.BundleKeys.FORM_MODE);
                if (formMode == null || ApplicationConstants.FormModes.EDIT_SAVED.equalsIgnoreCase(formMode)) {
                    intent.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
                } else {
                    intent.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.VIEW_SENT);
                }
                startActivity(intent);
            }
            finish();
        }

//        final FormController formController = Collect.getInstance()
//                .getFormController();
//
//        Log.d("septiawan_uuid_yeah",formController.getSubmissionMetadata().instanceId);
    }

    @Override
    protected void onResume() {
        if (instanceSyncTask != null) {
            instanceSyncTask.setDiskSyncListener(this);
        }
        super.onResume();

        if (instanceSyncTask.getStatus() == AsyncTask.Status.FINISHED) {
            syncComplete(instanceSyncTask.getStatusMessage());
        }
    }

    @Override
    protected void onPause() {
        if (instanceSyncTask != null) {
            instanceSyncTask.setDiskSyncListener(null);
        }
        super.onPause();
    }

    @Override
    public void syncComplete(String result) {
        TextView textView = (TextView) findViewById(R.id.status_text);
        textView.setText(result);
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

    private void setupAdapter() {
        String[] data = new String[]{
                InstanceColumns.DISPLAY_NAME, InstanceColumns.DISPLAY_SUBTEXT, InstanceColumns.DELETED_DATE
        };
        int[] view = new int[]{
                R.id.text1, R.id.text2, R.id.text4
        };

        if (mEditMode) {
            mListAdapter = new SimpleCursorAdapter(this, R.layout.two_item, getCursor(), data, view);
        } else {
            mListAdapter = new ViewSentListAdapter(this, R.layout.two_item, getCursor(), data, view);
        }
        setListAdapter(mListAdapter);
    }

    @Override
    protected String getSortingOrderKey() {
        return mEditMode ? INSTANCE_LIST_ACTIVITY_SORTING_ORDER : VIEW_SENT_FORM_SORTING_ORDER;
    }

    @Override
    protected void updateAdapter() {
        mListAdapter.changeCursor(getCursor());
    }

    private Cursor getCursor() {
        Cursor cursor;
        if (mEditMode) {
            cursor = new InstancesDao().getUnsentInstancesCursor(getFilterText(), getSortingOrder());
        } else {
            cursor = new InstancesDao().getSentInstancesCursor(getFilterText(), getSortingOrder());
        }

        Log.d("septiawan_cursor",Integer.toString(cursor.getColumnCount()));
        if(cursor.getCount()>0){
            cursor.moveToPosition(-1);
            while(cursor.moveToNext()){
                Log.d("septiawan_cursor_ket",cursor.getString(cursor.getColumnIndex(InstanceColumns.JR_FORM_ID)));
                if(cursor.getString(cursor.getColumnIndex(InstanceColumns.INSTANCE_FILE_PATH)) != null){
                    Log.d("septiawan_cursor_ket",cursor.getString(cursor.getColumnIndex(InstanceColumns.INSTANCE_FILE_PATH)));
                }
            }
        }


        return cursor;
    }

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


}
