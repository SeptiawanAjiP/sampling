/*
 * Decompiled with CFR 0_118.
 *
 * Could not load the following classes:
 *  android.os.AsyncTask
 *  android.util.Log
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.ClientProtocolException
 *  org.apache.http.client.entity.UrlEncodedFormEntity
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.apache.http.message.BasicNameValuePair
 */
package org.odk.collect.android.augmentedreality.arkit;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.opendatakit.httpclientandroidlib.impl.conn.DefaultHttpClientConnectionOperator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class PARDataCollector
extends AsyncTask<Void, Void, Void> {
    private String formUrl = "https://docs.google.com/a/dopanic.com/forms/d/1jNYcNDvtrz4DY4xjGOXblzknFkIJ-SrppRLKcSSS6fQ/formResponse";
    private ArrayList<BasicNameValuePair> data = new ArrayList();

    protected /* varargs */ Void doInBackground(Void ... voids) {
        this.post();
        return null;
    }

    public void addEntry(BasicNameValuePair data) {
        this.data.add(data);
    }

    public void post() {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(this.formUrl);
        try {
            post.setEntity((HttpEntity)new UrlEncodedFormEntity(this.data));
        }
        catch (UnsupportedEncodingException e) {
            Log.e((String)"DATA_COLLECTOR", (String)"Encoding not supported", (Throwable)e);
        }
        try {
            client.execute((HttpUriRequest)post);
        }
        catch (ClientProtocolException e) {
            Log.e((String)"DATA_COLLECTOR", (String)"client protocol exception", (Throwable)e);
        }
        catch (IOException e) {
            Log.e((String)"DATA_COLLECTOR", (String)"io exception", (Throwable)e);
        }
    }
}

