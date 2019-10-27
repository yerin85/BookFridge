package com.example.myapplication;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

class ItemDetail {
    public String pubDate = "";
    public String categoryName = "";
}

class ItemOpenAPIHandler extends DefaultHandler {
    private ItemDetail currentItemDetail;
    private boolean inItemElement = false;
    private String tempValue;
    private int idx;

    public ItemOpenAPIHandler() {}

    public void startElement(String namespace, String localName, String qName, Attributes atts) {
        if (localName.equals("item")) {
            currentItemDetail = new ItemDetail();
            inItemElement = true;
        } else if (localName.equals("pubDate")) {
            tempValue = "";
        } else if (localName.equals("categoryName")) {
            tempValue = "";
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempValue = tempValue + new String(ch, start, length);
    }

    public void endElement(String namespaceURI, String localName, String qName) {
        if (inItemElement) {
            if (localName.equals("item")) {
                currentItemDetail = null;
                inItemElement = false;
            } else if (localName.equals("pubDate")) {
                currentItemDetail.pubDate = tempValue;
            } else if (localName.equals("categoryName")) {
                currentItemDetail.categoryName = tempValue;
            }
        }
    }

    public void parseXml(String xmlUrl) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        ParserAdapter pa = new ParserAdapter(sp.getParser());
        pa.setContentHandler(this);
        pa.parse(xmlUrl);
    }
}

public class ItemOpenAPI {
    private static final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?";

    public static String GetUrl(String itemId, String startPage) throws Exception {
        Map<String, String> hm = new HashMap<String, String>();
        hm.put("ttbkey", "ttb0318592203001");
        hm.put("ItemId", itemId);
        hm.put("ItemIdType", "ItemId");
        hm.put("output", "xml");

        StringBuffer sb = new StringBuffer();
        Iterator<String> iter = hm.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String val = hm.get(key);
            sb.append(key).append("=").append(val).append("&");
        }

        return BASE_URL + sb.toString();
    }
}