package org.odk.collect.android.augmentedreality;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Septiawan Aji Pradan on 3/14/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 9;
    private static final String DATABASE_NAME = "skripsi";
    private static final String TABLE_STIKER = "table_stiker_3";

    private static final String ID= "id";
    private static final String ID_FORM = "id_form";
    private static final String TEXTVIEW_ATAS = "text_view_atas";
    private static final String TEXTVIEW_1 = "text_view_1";
    private static final String TEXTVIEW_2 = "text_view_2";
    private static final String TEXTVIEW_3 = "text_view_3";
    private static final String TEXTVIEW_4 = "text_view_4";

    public DatabaseHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_STIKER_TABLE = "CREATE TABLE "+ TABLE_STIKER+" ("
                +ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +ID_FORM+" TEXT,"
                +TEXTVIEW_ATAS+" TEXT, "
                +TEXTVIEW_1+" TEXT, "
                +TEXTVIEW_2+" TEXT, "
                +TEXTVIEW_3+" TEXT, "
                +TEXTVIEW_4+" TEXT)";
        db.execSQL(CREATE_STIKER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_STIKER);
        onCreate(db);
    }

    public void insertTabel(String idForm,String tv_atas,String tv_1,String tv_2,String tv_3,String tv_4){
        SQLiteDatabase db = this.getWritableDatabase();
        if(cekRow(idForm).equals("ada")){
            db.execSQL("UPDATE " + TABLE_STIKER + " SET " + TEXTVIEW_ATAS+"='"+tv_atas+"' , "+ TEXTVIEW_1+"='"+tv_1+"' , "+ TEXTVIEW_2+"='"+tv_2+"' , "+ TEXTVIEW_3+"='"+tv_3+"'"+ TEXTVIEW_4+"='"+tv_4+"'"+" WHERE "+ID_FORM+"='"+idForm+"'");
        }else{
            try{
                ContentValues values= new ContentValues();
                values.put(ID_FORM,idForm);
                values.put(TEXTVIEW_ATAS,tv_atas);
                values.put(TEXTVIEW_1,tv_1);
                values.put(TEXTVIEW_2,tv_2);
                values.put(TEXTVIEW_3,tv_3);
                values.put(TEXTVIEW_4,tv_4);
                db.insert(TABLE_STIKER,null,values);
            }catch (Exception e ){
                Log.d("insert_gps",e.toString());
            }
        }
    }
    public ArrayList<String> getAll(String idForm){
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase sql = this.getReadableDatabase();
        String query = "SELECT "+TEXTVIEW_ATAS+","+TEXTVIEW_1+","+TEXTVIEW_2+","+TEXTVIEW_3+","+TEXTVIEW_4+" FROM "+TABLE_STIKER+" WHERE "+ID_FORM+"='"+idForm+"'";
        Cursor c= sql.rawQuery(query,null);

        if(c.moveToFirst()){
            String tv_atas,tv_1,tv_2,tv_3,tv_4;
            tv_atas = c.getString(c.getColumnIndex(TEXTVIEW_ATAS));
            tv_1 = c.getString(c.getColumnIndex(TEXTVIEW_1));
            tv_2 = c.getString(c.getColumnIndex(TEXTVIEW_2));
            tv_3 = c.getString(c.getColumnIndex(TEXTVIEW_3));
            tv_4 = c.getString(c.getColumnIndex(TEXTVIEW_4));
            arrayList.add(tv_atas);
            arrayList.add(tv_1);
            arrayList.add(tv_2);
            arrayList.add(tv_3);
            arrayList.add(tv_4);
        }else{
            Log.d("getGps","not move to first");
            return arrayList;
        }
        Log.d("aji___has",arrayList.toString());
        return arrayList ;
    }

    public String cekRow(String idForm){
        String cek="";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c  = db.rawQuery("SELECT " + ID + " FROM " + TABLE_STIKER+" WHERE "+ID_FORM+"='"+idForm+"'" , null);

        if(c!=null && c.moveToFirst()){
            cek="ada";
        }
        return cek;
    }
}
