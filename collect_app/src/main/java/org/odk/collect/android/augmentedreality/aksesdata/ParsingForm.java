package org.odk.collect.android.augmentedreality.aksesdata;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Created by Septiawan Aji Pradan on 6/1/2017.
 */

public class ParsingForm {

    public static final String LOKASI = "location";
    public static final String FOTO_BANGUNAN = "foto_bangunan";
    private ArrayList<String> arrayVariabel = new ArrayList();

    public ArrayList<String> getVariabelForm (String dir){
        arrayVariabel = new ArrayList<>();
        try {
            FileInputStream is = new FileInputStream(new File(dir));
            Document doc =
                    DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .parse(is);

            NodeList nl = doc.getElementsByTagName("data");
            NodeList nld = nl.item(0).getChildNodes();
            for (int i=0; i<nld.getLength(); i++){
                Node ndc = nld.item(i);
                if (!ndc.getNodeName().equals("meta")){
                    if (ndc.getChildNodes().getLength()>0){
                        getChildNode(ndc.getChildNodes());
                    } else if (!ndc.getNodeName().startsWith("#")){
                        arrayVariabel.add(ndc.getNodeName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayVariabel;
    }

    private void getChildNode(NodeList nodeList){
        for (int j=0; j<nodeList.getLength(); j++){
            Node ndcc = nodeList.item(j);
            if (ndcc.getChildNodes().getLength()>0){
                getChildNode(ndcc.getChildNodes());
            } else if (!ndcc.getNodeName().startsWith("#")){
                arrayVariabel.add(ndcc.getNodeName());
            }
        }
    }
}