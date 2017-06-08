package org.odk.collect.android.downloadinstance;

/**
 * Created by Septiawan Aji Pradan on 5/17/2017.
 */

public class Download {

    public static final String UUID = "uuid";
    public static final String FORM_NAME = "form_name";
    public static final String FORM_ID = "form_id";
    public static final String FORM_PATH = "form_path";

    public static final String INSTANCE_PATH_FILE = "instance_path_file";
    public static final String MEDIA_PATH_FILE = "media_path_file";



    private String uuid;
    private String formName;
    private String formId;
    private String formPath;
    private String instancePath;
    private String mediaPath;
    private String fileName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormPath() {
        return formPath;
    }

    public void setFormPath(String pathForm) {
        this.formPath = pathForm;
    }

    public String getInstancePath() {
        return instancePath;
    }

    public void setInstancePath(String instancePath) {
        this.instancePath = instancePath;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
