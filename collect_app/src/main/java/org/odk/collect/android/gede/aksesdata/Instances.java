package org.odk.collect.android.gede.aksesdata;

import android.net.Uri;

/**
 * Created by Septiawan Aji Pradan on 5/28/2017.
 */

public class Instances {
    private String uuid;
    private String pathFoto;
    private String pathInstances;
    private String formId;
    private Uri uri;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPathFoto() {
        return pathFoto;
    }

    public void setPathFoto(String pathFoto) {
        this.pathFoto = pathFoto;
    }

    public String getPathInstances() {
        return pathInstances;
    }

    public void setPathInstances(String pathInstances) {
        this.pathInstances = pathInstances;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
