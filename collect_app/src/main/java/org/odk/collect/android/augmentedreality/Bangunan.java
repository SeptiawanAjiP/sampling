package org.odk.collect.android.augmentedreality;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Septiawan Aji Pradan on 3/14/2017.
 */

public class Bangunan {
    private int id;
    private Double lat;
    private Double lon;
    private String pathFoto;
    private String jarak;
    private ArrayList<String> keteranganBangunan;
    private HashMap<String,String> hashMap;

    public Bangunan(){

    }
    public Bangunan(Double lat, Double lon, String pathFoto){
        this.lat = lat;
        this.lon = lon;
        this.pathFoto = pathFoto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getPathFoto() {
        return pathFoto;
    }

    public void setPathFoto(String pathFoto) {
        this.pathFoto = pathFoto;
    }


    public ArrayList<String> getKeteranganBangunan() {
        return keteranganBangunan;
    }

    public void setKeteranganBangunan(ArrayList<String> keteranganBangunan) {
        this.keteranganBangunan = keteranganBangunan;
    }

    public String getJarak() {
        return jarak;
    }

    public void setJarak(String jarak) {
        this.jarak = jarak;
    }

    public HashMap<String, String> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, String> hashMap) {
        this.hashMap = hashMap;
    }
}
