package com.dashingqi.networktest;


import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by zhangqi on 2017/5/21.
 */

public class ConterHandler extends DefaultHandler {

    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;

    private String nodeName;

    @Override
    public void startDocument() throws SAXException {
        id = new StringBuilder();
        name = new StringBuilder();
        version = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        nodeName=localName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("id".equals(nodeName)){
            id.append(ch,start,length);
        }else if ("name".equals(nodeName)){
            name.append(ch,start,length);
        }else if ("version".equals(nodeName)){
            version.append(ch,start,length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("app".equals(localName)){
            Log.d("ConterHandler","id is "+id.toString().trim());
            Log.d("ConterHandler","name is "+name.toString().trim());
            Log.d("ConterHandler","version is"+version.toString().trim());

            //要将StringBuilder清空
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
