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
    	//��ȡxmlpull�����ǿ���ʹ��android�������ṩ��һ���򵥻�ȡ�ࣨ���ڿ��ٻ��pull��������
        XmlPullParser parser = Xml.newPullParser();
        //���б�������,��������Ҫ���������ݸ���
        parser.setInput(inStream, "UTF-8");
        //��ȡ�¼����Ϳ��Դ�����һ���������ַ���Ӧ���¼�
            //��Ҳ�ǲ��õ���ʽ����
        int enentType = parser.getEventType();
        //��������ǰ����¼�������
//            parser.next();
        List<note> notes = null;
        note noteitem = null;
        //�����ж�����¼���Ϊ�ĵ�ĩβ�¼������Ǿ�ѭ����ȥ
        while(enentType != XmlPullParser.END_DOCUMENT){
            //���¼������ж�
            switch (enentType) {
            //�Ƿ�Ϊ��ʼ�¼������Խ������ݳ�ʼ������
            case XmlPullParser.START_DOCUMENT:
                notes = new ArrayList<note>();
                break;
            //�Ƿ�Ϊ��ʼԪ���¼�����<Persons>
                //�����£�����<person id...Ҳ�ᴥ������¼�
                    //Ҳ����ÿ����һ���ַ��ͻᴥ������¼�
            case XmlPullParser.START_TAG:
                
                String tag = parser.getName();
                //�����ǰΪperson��ǩ��������Ҫ��ȡidֵ
                if("book".equals(tag)){
                    noteitem = new note();
                    noteitem.setId(new Integer(parser.getAttributeValue(0)));
                }else if(noteitem != null){
                    if("name".equals(tag)){
                        //parser.nextText(),��ǰΪname��һ���ڵ㣬Ϊ�ı�
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
             case XmlPullParser.END_TAG://����Ԫ���¼�
                 //���������person����Ԫ���¼�ʱ�����ǾͰѵ�ǰ��person�ӵ������
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