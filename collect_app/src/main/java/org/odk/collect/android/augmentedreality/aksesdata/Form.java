package org.odk.collect.android.augmentedreality.aksesdata;

/**
 * Created by Septiawan Aji Pradan on 5/28/2017.
 */

public class Form {
    private String pathForm;
    private String idForm;
    private String displayName;
    private String totalIsian;

    public Form(){

    }

    public Form(String displayName,String totalIsian){
        this.displayName = displayName;
        this.totalIsian = totalIsian;
    }

    public String getPathForm() {
        return pathForm;
    }

    public void setPathForm(String pathForm) {
        this.pathForm = pathForm;
    }

    public String getIdForm() {
        return idForm;
    }

    public void setIdForm(String idForm) {
        this.idForm = idForm;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTotalIsian() {
        return totalIsian;
    }

    public void setTotalIsian(String totalIsian) {
        this.totalIsian = totalIsian;
    }
}
