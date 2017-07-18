package org.odk.collect.android.augmentedreality.koneksi;

/**
 * Created by Septiawan Aji Pradan on 5/28/2017.
 */

public class AlamatServer {

    private String alamat;
    private static final String ALAMAT_SERVER = "http://septiawanajipradana.000webhostapp.com/skripsi_api/index.php/instances";
    private static final String INSERT = "/insert";
    private static final String GET = "/instances";

    public static String getAlamatServer() {
        return ALAMAT_SERVER;
    }

    public static String getINSERT() {
        return INSERT;
    }

    public static String getGET() {
        return GET;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
