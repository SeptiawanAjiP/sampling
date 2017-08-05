package org.odk.collect.android.gede.aksesdata;

import android.util.Log;

import org.odk.collect.android.augmentedreality.Bangunan;
import org.odk.collect.android.augmentedreality.aksesdata.AksesDataOdk;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Septiawan Aji Pradan on 5/31/2017.
 */

public class ParsingInstances {
    private XmlPullParserFactory xmlPullParserFactory;

    public Bangunan getValue(String directory, ArrayList<String> key) throws IOException{
        Log.d("cinta_parsing",key.toString());
        Bangunan bangunan = new Bangunan();
        try{
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(false);
            XmlPullParser parser = xmlPullParserFactory.newPullParser();

            File file = new File(directory);
            FileInputStream is = new FileInputStream(file);
            parser.setInput(is,null);
            bangunan = parseXml(parser,key,directory);

        }catch (XmlPullParserException e){
            e.printStackTrace();
        }
        return bangunan;
    }

    private Bangunan parseXml(XmlPullParser parser, ArrayList<String> ket, String dir) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        AksesDataOdk aksesDataOdk = new AksesDataOdk();
        String name = null;
        ArrayList<String> prse = new ArrayList<>();
        Bangunan bangunan  = new Bangunan();
        while(eventType != XmlPullParser.END_DOCUMENT){
            if(eventType == XmlPullParser.START_TAG){
                name = parser.getName();
                bangunan.setJarak("");
                if(name.equals("location")){
                    String[] poin = parser.nextText().split(" ");
                    bangunan.setLat(Double.parseDouble(poin[0]));
                    bangunan.setLon(Double.parseDouble(poin[1]));
                }else if(name.equals("foto_bangunan")){
                    String imgPath= aksesDataOdk.getParentDir(dir)+File.separator+ parser.nextText();
                    bangunan.setPathFoto(imgPath);
                }else{
                    if(!name.equals("location") && !name.equals("foto_bangunan")){
                        for (int i=0;i<ket.size();i++){
                            if(name.equals(ket.get(i))){
                                prse.add(parser.nextText());
                            }
                        }
                    }
                }
                Log.d("septiawan_aji",prse.toString());
            }
            eventType = parser.next();
        }
        return bangunan;
    }

    public String getValueByKey(String directory,String key) throws IOException{
        String value = "";
        try{
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(false);
            XmlPullParser parser = xmlPullParserFactory.newPullParser();

            File file = new File(directory);
            FileInputStream is = new FileInputStream(file);
            parser.setInput(is,null);
            value = parseXmlByKey(parser,key);

        }catch (XmlPullParserException e){
            e.printStackTrace();
        }
        return value;
    }

    private String parseXmlByKey(XmlPullParser parser,String key) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        String name = null;
        String value = "";
        while(eventType != XmlPullParser.END_DOCUMENT){
            if(eventType == XmlPullParser.START_TAG){
                name = parser.getName();
                if(name.equals(key)){
                  value = parser.nextText();
                }
                Log.d("septiawan_aji",value.toString());
            }
            eventType = parser.next();
        }
        return value;
    }

    public Bangunan getValueHasMap(String directory, ArrayList<String> key) throws IOException{
        Log.d("cinta_parsing",key.toString());
        Bangunan bangunan = new Bangunan();
        HashMap<String,String> has= new HashMap<>();
        try{
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(false);
            XmlPullParser parser = xmlPullParserFactory.newPullParser();

            File file = new File(directory);
            FileInputStream is = new FileInputStream(file);
            parser.setInput(is,null);
            bangunan = parseXmlHas(parser,key,directory);


        }catch (XmlPullParserException e){
            e.printStackTrace();
        }
        return bangunan;
    }

    private Bangunan parseXmlHas(XmlPullParser parser, ArrayList<String> ket, String dir) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        AksesDataOdk aksesDataOdk = new AksesDataOdk();
        String name = null;
//        ArrayList<String> prse = new ArrayList<>();
        HashMap<String,String> has = new HashMap<>();
        Bangunan bangunan  = new Bangunan();
        while(eventType != XmlPullParser.END_DOCUMENT){
            if(eventType == XmlPullParser.START_TAG){
                name = parser.getName();
                bangunan.setJarak("");
                if(name.equals("location")){
                    String[] poin = parser.nextText().split(" ");
                    bangunan.setLat(Double.parseDouble(poin[0]));
                    bangunan.setLon(Double.parseDouble(poin[1]));
                }else if(name.equals("foto_bangunan")){
                    String imgPath= aksesDataOdk.getParentDir(dir)+File.separator+ parser.nextText();
                    bangunan.setPathFoto(imgPath);
                }else{
                    if(!name.equals("location") && !name.equals("foto_bangunan")){
                        for (int i=0;i<ket.size();i++){
                            if(name.equals(ket.get(i))){
                                has.put(ket.get(i),parser.nextText());
                            }
                        }
                    }
                }
                bangunan.setHashMap(has);
                Log.d("septiawan_aji",has.toString());

            }
            eventType = parser.next();
        }
        return bangunan;
    }


}
