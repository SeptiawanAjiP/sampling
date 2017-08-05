package org.odk.collect.android.gede.databasehandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;

/**
 * Created by Septiawan Aji Pradan on 3/14/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "_sampel";
    private static final String TABLE_SAMPEL = "_tabel_sampel";

    private static final String TABLE_PARAMETER = "_tabel_parameter";
    private static final String FORM_ID = "_form_id";
    private static final String ID_AUTO= "_id_auto";
    private static final String ID = "_id";
    private static final String UUID = "_uuid";

    private static final String TEXT_1 = "text_1";
    private static final String TEXT_2 = "text_2";
    private static final String TEXT_3 = "text_3";
    private static final String TEXT_4 = "text_4";

    public DatabaseHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_SAMPLE_TABLE = "CREATE TABLE "+ TABLE_SAMPEL+" ("
                +ID_AUTO+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +ID+" TEXT,"
                +FORM_ID+" TEXT, "
                +UUID+" TEXT)";
        String CREATE_PARAMETER_TABLE = "CREATE TABLE "+ TABLE_PARAMETER+" ("
                +ID_AUTO+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +FORM_ID+" TEXT,"
                +TEXT_1+" TEXT, "
                +TEXT_2+" TEXT, "
                +TEXT_3+" TEXT, "
                +TEXT_4+" TEXT)";

        db.execSQL(CREATE_SAMPLE_TABLE);
        db.execSQL(CREATE_PARAMETER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SAMPEL);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PARAMETER);
        onCreate(db);
    }

    public void insertTabelSampel(String idForm,String uuid,String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("_gede",idForm+","+uuid);
        if(cekRow(idForm).equals("ada")){

        }else{
            try{
                ContentValues values= new ContentValues();
                values.put(FORM_ID,idForm);
                values.put(UUID,uuid);
                values.put(ID,id);
                db.insert(TABLE_SAMPEL,null,values);
            }catch (Exception e ){
                Log.d("insert_gps",e.toString());
            }
        }
    }


    public ArrayList<String> getForm(){
        ArrayList<String> forms = new ArrayList<>();
        SQLiteDatabase sql = this.getReadableDatabase();
        String query = "SELECT "+FORM_ID+" FROM "+TABLE_SAMPEL;
        Cursor c= sql.rawQuery(query,null);

        if(c.moveToFirst()){
            String uuid = c.getString(c.getColumnIndex(FORM_ID));
            forms.add(uuid);
        }else{
            Log.d("getGps","not move to first");
            return forms;
        }
        Log.d("aji___has",forms.toString());
        return forms;
    }

    public ArrayList<String> getAllByIdAndFormId(String idForm,String id){
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase sql = this.getReadableDatabase();
        String query = "SELECT "+UUID+" FROM "+TABLE_SAMPEL+" WHERE "+FORM_ID+"='"+idForm+"' AND "+ID+"='"+ID+"'";
        Cursor c= sql.rawQuery(query,null);

        if(c.moveToFirst()){
            String uuid = c.getString(c.getColumnIndex(UUID));
            arrayList.add(uuid);
        }else{
            Log.d("getGps","not move to first");
            return arrayList;
        }
        Log.d("aji___has",arrayList.toString());
        return arrayList ;
    }

    public String cekRow(String uuid){
        String cek="";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c  = db.rawQuery("SELECT " + ID_AUTO + " FROM " + TABLE_SAMPEL+" WHERE "+UUID+"='"+uuid+"'" , null);

        if(c!=null && c.moveToFirst()){
            cek="ada";
        }
        return cek;
    }

    public void insertTabelParameter(String idForm,String text1,String text2,String text3,String text4){
        SQLiteDatabase db = this.getWritableDatabase();
        if(cekRow(idForm).equals("ada")){
            db.execSQL("UPDATE " + TABLE_PARAMETER + " SET " + TEXT_1+"='"+text1+"' , "+ TEXT_2+"='"+text2+"' , "+ TEXT_3+"='"+text3+"' , "+ TEXT_4+"='"+text4+"'"+" WHERE "+FORM_ID+"='"+idForm+"'");
        }else{
            try{
                ContentValues values= new ContentValues();
                values.put(FORM_ID,idForm);
                values.put(TEXT_1,text1);
                values.put(TEXT_2,text2);
                values.put(TEXT_3,text3);
                values.put(TEXT_4,text4);
                db.insert(TABLE_PARAMETER,null,values);
            }catch (Exception e ){
                Log.d("insert_gps",e.toString());
            }
        }
    }

    public ArrayList<String> getParameter(String idForm){
        ArrayList<String> parameter = new ArrayList<>();
        SQLiteDatabase sql = this.getReadableDatabase();
        String query = "SELECT "+TEXT_1+","+TEXT_2+","+TEXT_3+","+TEXT_4+" FROM "+TABLE_PARAMETER+" WHERE "+FORM_ID+"='"+idForm+"'";
        Cursor c= sql.rawQuery(query,null);

        if(c.moveToFirst()){
            String text1 = c.getString(c.getColumnIndex(TEXT_1));
            String text2 = c.getString(c.getColumnIndex(TEXT_2));
            String text3 = c.getString(c.getColumnIndex(TEXT_3));
            String text4 = c.getString(c.getColumnIndex(TEXT_4));
            parameter.add(text1);
            parameter.add(text2);
            parameter.add(text3);
            parameter.add(text4);

        }else{
            Log.d("getGps","not move to first");
            return parameter;
        }
        Log.d("aji___has",parameter.toString());
        return parameter;
    }
}
