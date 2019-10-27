package com.example.myapplication;

import android.text.Html;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class Item implements Serializable {
    public String title = "";
    public String description = "";
    public String author = "";
    public String cover = "";
    public String publisher = "";
    public String itemId = "";
    public String pubDate = "";
    public String categoryName = "";
}

class AladdinOpenAPIHandler extends DefaultHandler {
    public ArrayList<Item> Items;
    int totalResults = -1;
    private Item currentItem;
    private boolean inItemElement = false;
    private String tempValue;
    private int idx;

    public AladdinOpenAPIHandler() {
        Items = new ArrayList<Item>();
    }

    public void startElement(String namespace, String localName, String qName, Attributes atts) {
        if (localName.equals("item")) {
            currentItem = new Item();
            inItemElement = true;
        } else if (localName.equals("title")) {
            tempValue = "";
        } else if (localName.equals("description")) {
            tempValue = "";
        } else if (localName.equals("author")) {
            tempValue = "";
        } else if (localName.equals("cover")) {
            tempValue = "";
        } else if (localName.equals("publisher")) {
            tempValue = "";
        } else if (localName.equals("itemId")) {
            tempValue = "";
        } else if (localName.equals("pubDate")) {
            tempValue = "";
        } else if (localName.equals("categoryName")) {
            tempValue = "";
        } else if (localName.equals("totalResults")) {
            tempValue = "";
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempValue = tempValue + new String(ch, start, length);
        idx = tempValue.indexOf("<br/>");
        if (idx != -1) {
            tempValue = tempValue.substring(idx + 5);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) {
        if (inItemElement) {
            if (localName.equals("item")) {
                Items.add(currentItem);
                currentItem = null;
                inItemElement = false;
            } else if (localName.equals("title")) {
                currentItem.title = tempValue;
            } else if (localName.equals("description")) {
                currentItem.description = String.valueOf(Html.fromHtml(tempValue));
            } else if (localName.equals("author")) {
                currentItem.author = tempValue;
            } else if (localName.equals("cover")) {
                currentItem.cover = tempValue;
            } else if (localName.equals("publisher")) {
                currentItem.publisher = tempValue;
            } else if (localName.equals("itemId")) {
                currentItem.itemId = tempValue;
            } else if (localName.equals("pubDate")) {
                currentItem.pubDate = tempValue;
            } else if (localName.equals("categoryName")) {
                currentItem.categoryName = tempValue;
            }
        } else if (localName.equals("totalResults")) {
            totalResults = Integer.parseInt(tempValue);

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

public class AladdinOpenAPI {
    private static final String BASE_URL = "http://www.aladdin.co.kr/ttb/api/ItemSearch.aspx?";

    public static String GetUrl(String queryType, String searchWord, String startPage) throws Exception {
        Map<String, String> hm = new HashMap<String, String>();
        hm.put("ttbkey", "ttb0318592203001");
        hm.put("Query", URLEncoder.encode(searchWord, "UTF-8"));
        hm.put("QueryType", queryType);
        hm.put("MaxResults", "10");
        hm.put("Cover", "Big");
        hm.put("start", startPage);
        hm.put("SearchTarget", "Book");
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