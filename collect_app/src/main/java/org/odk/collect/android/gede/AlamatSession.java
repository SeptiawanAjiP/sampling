package org.odk.collect.android.gede;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Septiawan Aji Pradan on 7/18/2017.
 */

public class AlamatSession {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public AlamatSession(Context context){
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
