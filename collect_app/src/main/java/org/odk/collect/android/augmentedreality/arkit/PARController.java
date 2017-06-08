/*
 * Decompiled with CFR 0_118.
 *
 * Could not load the following classes:
 *  android.content.Context
 *  android.location.Location
 *  android.os.AsyncTask
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.util.Log
 *  com.dopanic.panicsensorkit.PSKEventListener
 *  com.dopanic.panicsensorkit.PSKSensorManager
 *  com.dopanic.panicsensorkit.enums.PSKDeviceOrientation
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.apache.http.message.BasicNameValuePair
 */
package org.odk.collect.android.augmentedreality.arkit;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.odk.collect.android.augmentedreality.sensorkit.PSKEventListener;
import org.odk.collect.android.augmentedreality.sensorkit.PSKSensorManager;
import org.odk.collect.android.augmentedreality.sensorkit.enums.PSKDeviceOrientation;
import org.opendatakit.httpclientandroidlib.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class PARController
implements PSKEventListener {
    public static boolean DEBUG = false;
    public static float CLIP_POIS_NEARER_THAN = 5.0f;
    public static float CLIP_POIS_FARER_THAN = 1.0E7f;
    public static PARDataCollector dataCollector;
    private static PARController _sharedPARController;
    private static Context _context;
    private static String _apiKey;
    private static boolean _hasValidApiKey;
    private static ArrayList<PARPoi> _pois;
    private String TAG = "PARController";

    public static String getFrameworkVersion() {
        return "1.0.1562";
    }

    private PARController() {
        _pois = new ArrayList();
    }

    public static Context getContext() {
        return _context;
    }

    public static PARController getInstance() {
        return _sharedPARController;
    }

    public static ArrayList<PARPoi> getPois() {
        return _pois;
    }

    public void init(Context ctx, String apiKey) {
        _context = ctx;
        Log.i((String)this.TAG, (String)"init()");
        this.setApiKey(apiKey);
        PARDataCollector dataCollector = new PARDataCollector();
        String osVersion = Build.VERSION.RELEASE;
        String deviceId = PARInstallation.id(ctx);
        dataCollector.addEntry(new org.apache.http.message.BasicNameValuePair(URLEncoder.encode("entry.355335633"), URLEncoder.encode(deviceId)));
        dataCollector.addEntry(new org.apache.http.message.BasicNameValuePair(URLEncoder.encode("entry.990059982"), URLEncoder.encode(osVersion)));
        PARController.dataCollector = dataCollector;
        new ReportStatsTask().execute(new Object[0]);
        PSKSensorManager.getSharedSensorManager().setEventListener((PSKEventListener)this);
    }

    public void setApiKey(String apiKey) {
        _apiKey = apiKey;
        Log.wtf((String)this.TAG, (String)("API-Key set to " + _apiKey));
        _hasValidApiKey = this.validateApiKey(_apiKey);
    }

    public boolean hasValidApiKey() {
        return _hasValidApiKey;
    }

    public void addPoi(PARPoi poi) {
        if (_pois.contains(poi)) {
            Log.e((String)this.TAG, (String)"PARPoi not added (same PARPoi already added to PARController).");
            return;
        }
        _pois.add(poi);
        poi.updateLocation();
        poi.onAddedToARController();
    }

    public void addPois(ArrayList<PARPoi> anArray) {
        for (PARPoi arPoi : anArray) {
            this.addPoi(arPoi);
        }
    }

    public void removeObject(PARPoi poi) {
        if (!_pois.contains(poi)) {
            Log.e((String)this.TAG, (String)"PARPoi not removed (not added to PARController).");
            return;
        }
        poi.onRemovedFromARController();
        _pois.remove(poi);
    }

    public void removeObject(int index) {
        try {
            if (index >= 0 && index < _pois.size()) {
                this.removeObject(_pois.get(index));
            }
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            aioobe.printStackTrace();
        }
    }

    public void clearObjects() {
        try {
            ListIterator<PARPoi> iterator = _pois.listIterator();
            while (iterator.hasNext()) {
                PARPoi poi = iterator.next();
                poi.onRemovedFromARController();
                Log.wtf((String)this.TAG, (String)("Removing: " + ((PARPoiLabel)poi).getTitle()));
                iterator.remove();
            }
        }
        catch (NoSuchElementException nsee) {
            nsee.printStackTrace();
        }
        catch (UnsupportedOperationException uoe) {
            uoe.printStackTrace();
        }
        catch (Exception e) {
            try {
                while (_pois.size() > 0) {
                    this.removeObject(0);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public int numberOfObjects() {
        Log.wtf((String)this.TAG, (String)("_poi.size = " + _pois.size()));
        return _pois.size();
    }

    public PARPoi getObject(int index) {
        if (index >= 0 && index < _pois.size()) {
            return _pois.get(index);
        }
        return null;
    }

    public void onLocationChangedEvent(Location location) {
        for (PARPoi poi : _pois) {
            poi.updateLocation();
        }
        this.sortMarkersByDistance();
        if (PARFragment.getActiveFragment() != null) {
            PARFragment.getActiveFragment().onLocationChangedEvent(location);
        }
    }

    public void onDeviceOrientationChanged(PSKDeviceOrientation newOrientation) {
        if (PARFragment.getActiveFragment() != null) {
            PARFragment.getActiveFragment().onDeviceOrientationChanged(newOrientation);
        }
    }

    private void sortMarkersByDistance() {
        try {
            Collections.sort(_pois, new Comparator<PARPoi>(){

                @Override
                public int compare(PARPoi parPoi1, PARPoi parPoi2) {
                    if (parPoi1.distanceToUser >= parPoi2.distanceToUser) {
                        return 1;
                    }
                    if (parPoi1.distanceToUser < parPoi2.distanceToUser) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        catch (NullPointerException ne) {
            Log.wtf((String)this.TAG, (String)"Sort objects failed.");
            ne.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateApiKey(String apiKey) {
        if (apiKey.equals("")) {
            Log.wtf((String)this.TAG, (String)"no API-Key!\nUsing Demo-Version of Framework (Limitations enabled)!\nNot intended for Release!");
            return false;
        }
        if (apiKey.equals("Override the setApiKey method in your PARApplication class!")) {
            return false;
        }
        String secret = "636Ux372^Q?6}CZ7^#/2Vk.;p.6j}s7%a3E3?$m4+{[(6HuW9k#6:q[q494z";
        try {
            String bundleIdentifier = PARApplication.getAppContext().getPackageName();
            String hash = bundleIdentifier.toLowerCase() + "636Ux372^Q?6}CZ7^#/2Vk.;p.6j}s7%a3E3?$m4+{[(6HuW9k#6:q[q494z";
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(hash.getBytes());
            byte[] byteData = md.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; ++i) {
                hexString.append(Integer.toString((byteData[i] & 255) + 256, 16).substring(1));
            }
            if (hexString.toString().trim().equals(apiKey.trim())) {
                Log.wtf((String)this.TAG, (String)"API-Key valid!");
                return true;
            }
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            Log.e((String)this.TAG, (String)"Could not read package name from AndroidManifest.xml\nUsing Demo-Version of Framework (Limitations enabled)!\nNot intended for Release!");
            return false;
        }
        Log.e((String)this.TAG, (String)"API-Key not valid for BundleID of this App!\nUsing Demo-Version of Framework (Limitations enabled)!\nNot intended for Release!");
        return false;
    }

    static {
        _sharedPARController = new PARController();
        _apiKey = "";
        _hasValidApiKey = false;
    }

    private class ReportStatsTask
    extends AsyncTask {
        private ReportStatsTask() {
        }

        protected Object doInBackground(Object[] objects) {
            String identifier = PARApplication.getAppContext().getPackageName();
            String urlString = String.format("http://panicar.dopanic.com/framework.php?app=%s&platform=android&version=%s", identifier, PARController.getFrameworkVersion());
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urlString);
            String response = "";
            try {
                try {
                    org.apache.http.HttpResponse execute = client.execute((HttpUriRequest) httpGet);
                    InputStream content = execute.getEntity().getContent();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response = response + s;
                    }
                }
                catch (Exception e) {
                }
            }
            catch (Exception e) {
                // empty catch block
            }
            return null;
        }
    }

}

