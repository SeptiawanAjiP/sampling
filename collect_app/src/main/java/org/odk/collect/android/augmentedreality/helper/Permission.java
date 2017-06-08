package org.odk.collect.android.augmentedreality.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Septiawan Aji Pradan on 4/30/2017.
 */

public class Permission {


    private final static int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    private final static int REQUEST_LOCATION_PERMISSION_CODE = 13;
    private final static int REQUEST_STORAGE_PERMISSION_CODE = 15;

    public static void requestCameraPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS_CODE);
        } else {

        }
    }

    public static boolean cekCameraPermission(Activity activity) {
        boolean bol = false;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            bol = true;
        } else {
            bol = false;
        }
        return bol;
    }

    public static void requestLocationPermission(Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION_CODE);
        }
    }

    public static boolean cekLocationPermission(Activity activity){
        boolean bol = false;
        if(ActivityCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            bol = true;
        }else{
            bol = false;
        }
        return bol;
    }

    public static  void  requestStoragePermission(Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE_PERMISSION_CODE);
        }
    }

    public static boolean cekStoragePermission(Activity activity){
        boolean bol = false;
        if(ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            bol = true;
        }else{
            bol = false;
        }
        return bol;
    }
}