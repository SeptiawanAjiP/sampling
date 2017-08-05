package org.odk.collect.android.downloadinstance;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.downloadinstance.BriefcaseFormDefinition;
import org.odk.collect.android.downloadinstance.FormStatus;
import org.odk.collect.android.downloadinstance.Notifikasi;
import org.odk.collect.android.downloadinstance.OdkCollectFormDefinition;
import org.odk.collect.android.downloadinstance.ServerConnectionInfo;
import org.odk.collect.android.downloadinstance.GetXml;
import org.odk.collect.android.downloadinstance.ParamsGet;
import org.odk.collect.android.downloadinstance.listener.DownloadPcl;
import org.odk.collect.android.preferences.PreferenceKeys;
import org.odk.collect.android.preferences.PreferencesActivity;

import java.io.File;

/**
 * Created by Cloud Walker on 24/02/2016.
 */
public class  DownloadInstances {
    private Download download;
    private Context mcontext;
    private DownloadPcl mdownloadpcl;

    public DownloadInstances(Download download, Context mcontext, DownloadPcl mdownloadpcl){
        this.download=download;
        this.mcontext=mcontext;
        this.mdownloadpcl=mdownloadpcl;
    }
    public void exscute(){
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mcontext);
//        final String server = settings.getString(PreferencesActivity.KEY_SERVER_URL,  Collect.getInstance().getString(R.string.default_server_url));
        final String server = settings.getString(PreferencesActivity.KEY_SERVER_URL, PreferenceKeys.KEY_SERVER_URL);

        final String storedUsername = settings.getString(PreferencesActivity.KEY_USERNAME, "");
        final String storedPassword = settings.getString(PreferencesActivity.KEY_PASSWORD, "");

        ServerConnectionInfo serverinfo = new ServerConnectionInfo(server, storedUsername, storedPassword.toCharArray());
//        ServerFetcher serverFetcher = new ServerFetcher(serverinfo);
        BriefcaseFormDefinition lfd = null;
        FormStatus fs = null;
        File dir = null;
        File formxml = null;
        try {
            formxml = new File(download.getFormPath());
            Log.d("MainActiviry xml", formxml.getAbsolutePath());
            Log.d("MainActiviry xml", FileUtils.readFileToString(formxml));
        } catch (Exception Ex) {
            Log.d("MainActivity", "formxml " + Ex);
        }
        try {
            dir = new File(Collect.INSTANCES_PATH);
        } catch (Exception ex) {
            Log.d("MainActivity", "dir path" + ex);
        }
        try {
            lfd = new BriefcaseFormDefinition(dir, formxml);
        } catch (Exception ex) {
            Log.d("MainActivity", "lfd " + ex);
        }
        try {
            fs = new FormStatus(FormStatus.TransferType.GATHER, new OdkCollectFormDefinition(formxml));
        } catch (Exception ex) {
            Log.d("MainActivity", "form stat" + ex);
        }
        ParamsGet as = new ParamsGet(dir, lfd, fs, download.getUuid(), Collect.INSTANCES_PATH, serverinfo);
        GetXml get = new GetXml(mcontext);
        get.setDownloadpcl(mdownloadpcl, download);
        get.doInBackground(as);
    }

}
