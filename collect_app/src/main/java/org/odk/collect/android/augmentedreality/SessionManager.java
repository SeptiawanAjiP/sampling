package org.odk.collect.android.augmentedreality;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Septiawan Aji Pradan on 6/4/2017.
 */

public class SessionManager {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static final String WELCOME = "welcome";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("session",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setFirst(){
        editor.putString(WELCOME,"true");
        editor.commit();
    }

    public String getFirst(){
        String status = sharedPreferences.getString(WELCOME,null);
        return status;
    }
}
