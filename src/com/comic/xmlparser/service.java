package com.comic.xmlparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class service {
    public static List<note> readXML(FileInputStream inStream) throws XmlPullParserException, IOException {
    	//获取xmlpull，我们可以使用android给我们提供的一个简单获取类（便于快速获得pull解析器）
        XmlPullParser parser = Xml.newPullParser();
        //进行编码设置,把我们需要解析的内容给他
        parser.setInput(inStream, "UTF-8");
        //获取事件，就可以触发第一解析到的字符对应的事件
            //他也是采用的流式触发
        int enentType = parser.getEventType();
        //这个方法是把流事件往后退
//            parser.next();
        List<note> notes = null;
        note noteitem = null;
        //就是判断这个事件不为文档末尾事件，我们就循环下去
        while(enentType != XmlPullParser.END_DOCUMENT){
            //对事件进行判断
            switch (enentType) {
            //是否为开始事件，可以进行数据初始化处理
            case XmlPullParser.START_DOCUMENT:
                notes = new ArrayList<note>();
                break;
            //是否为开始元素事件，如<Persons>
                //再往下，处理到<person id...也会触发这个事件
                    //也就是每解析一个字符就会触发这个事件
            case XmlPullParser.START_TAG:
                
                String tag = parser.getName();
                //如果当前为person标签，我们需要获取id值
                if("book".equals(tag)){
                    noteitem = new note();
                    noteitem.setId(new Integer(parser.getAttributeValue(0)));
                }else if(noteitem != null){
                    if("name".equals(tag)){
                        //parser.nextText(),当前为name下一个节点，为文本
                        noteitem.setName(parser.nextText());
                    }else if("pagenum".equals(tag)){
                        noteitem.setPagenum(new Integer(parser.nextText()));
                    }else if("size".equals(tag)){
                        noteitem.setSize(parser.nextText());
                    }else if("sumary".equals(tag)){
                        noteitem.setSumary(parser.nextText());
                    }else if("url".equals(tag)){
                     noteitem.setUrl(parser.nextText());
                    }else if("imgsrc".equals(tag)){
                     noteitem.setImgsrc(parser.nextText());
                    }
                }                
                break;
             case XmlPullParser.END_TAG://结束元素事件
                 //如果解析到person结束元素事件时，我们就把当前的person加到结合中
                 if (parser.getName().equalsIgnoreCase("book") && noteitem != null) {
                        notes.add(noteitem);
                        noteitem = null;
                    }
            }
            enentType = parser.next();
        }
        System.out.println("book"+notes.size());
        return notes;
    }
}