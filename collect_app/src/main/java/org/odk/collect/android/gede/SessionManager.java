package org.odk.collect.android.gede;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * Created by Septiawan Aji P on 12/6/2016.
 */
public class SessionManager {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("session",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setAlamat(String alamat){
        editor.putString("alamat",alamat);
        editor.commit();
    }

    public String getAlamat(){
        String install = sharedPreferences.getString("alamat",null);
        return  install;
    }

}
