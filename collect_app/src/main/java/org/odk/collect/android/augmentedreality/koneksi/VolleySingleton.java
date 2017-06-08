package org.odk.collect.android.augmentedreality.koneksi;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Septiawan Aji Pradan on 4/12/2017.
 */

public class VolleySingleton {
    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private Context mContext;

    private VolleySingleton(Context context){
        mContext = context;
        requestQueue = Volley.newRequestQueue(mContext);
    }

    public static synchronized VolleySingleton getInstance(Context context){
        if (instance == null){
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return requestQueue;
    }


    public<T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
