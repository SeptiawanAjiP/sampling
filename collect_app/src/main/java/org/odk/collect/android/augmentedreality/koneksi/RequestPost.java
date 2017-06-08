package org.odk.collect.android.augmentedreality.koneksi;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

/**
 * Created by Septiawan Aji Pradan on 4/11/2017.
 */

public class RequestPost {
    private Context context;
    private HashMap<String,String> parameter;
    private RequestQueue requestQueue;
    private String respon;
    private String url;

    public RequestPost(Context context, HashMap<String,String> parameter,String url){
        this.context = context;
        this.parameter = parameter;
        this.url = url;
    }

    public String getRespon(){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                  respon = response;
                    Log.d("bismillah",response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("error", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected HashMap<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = parameter;
                return param;
            }
        };
        requestQueue.add(request);

        Log.d("bismillah2",respon);
        return respon;
    }
}
