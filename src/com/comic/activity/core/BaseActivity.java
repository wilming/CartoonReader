package com.comic.activity.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.cartoon.util.Utils;
import com.comic.dialog.CustomDialog;
import com.comic.shelf.BookShelfActivity;
import com.comic.util.Constants;

public class BaseActivity extends Activity {
	protected static Map<String,String> imagePosition = new HashMap<String,String>();
	protected static LinkedList<String> imageList = new LinkedList<String>();
	protected static ArrayList<Activity> allActivity = new ArrayList<Activity>();
	public static String shanchu="";
	public Activity baseActivity=this;
	/**
	 * �˳�
	 * @param con
	 */
	public static void exitApp(){
		//ѭ���ر�Activity
		Iterator<Activity> it = allActivity.iterator();
		while(it.hasNext()){
			Activity act = it.next();
			if(act != null){
				act.finish();
			}
		}
		//saveReaderState();
		System.exit(0);
	}
	
	/**
	 * ���浱ǰ�Ķ���������Ϣ
	 */
	public static void saveReaderState(){
		try{
			String picPath = Utils.getImagePath(imagePosition, imageList);
			if(picPath != null){
				//false ��ʾ����Դ�ļ���׷�ӣ������ļ��е�����
				Utils.saveFile1(Constants.HISTORY, picPath, false);
			}
		}catch(Exception e){
			//Log.i(TAG,e.getMessage());
		}
	}
	
	/**
	 * �Զ����˳��Ի���
	 * @param context
	 */
	public  void promptExit(final Context context){
		((Activity) context).showDialog(1);
	 }
	
	public  void promptDelete(final Context context,String arg2){
		 shanchu=arg2;
		((Activity) context).showDialog(2);
	 }
	
	
    @Override
	 public Dialog onCreateDialog(int dialogId) {
		 Dialog dialog = null;
		 switch(dialogId){
		 case 1:
		 CustomDialog.Builder customBuilder = new CustomDialog.Builder(BaseActivity.this);
	        customBuilder.setTitle("�˳�").setMessage("ȷ���˳���")
	                        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int which) {
	                                	dialog.dismiss();
	                                }
	                        })
	                        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int which) {
	                                	exitApp();
	                                }
	                        });	        
	                                dialog = customBuilder.create();
	                                break;
		 case 2:
		 CustomDialog.Builder customBuilder2 = new CustomDialog.Builder(BaseActivity.this);
	        customBuilder2.setTitle("ɾ��������").setMessage("ȷ��ɾ����")
	                        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int which) {
	                                	dialog.dismiss();
	                                }
	                        })
	                        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int which) {
	                                 if(shanchu!=""){
	                                deleteDirectory(Utils.getSdCardPath()+"/cartoonReader/books/"+shanchu);	
	                                Intent intent = new Intent(baseActivity,BookShelfActivity.class);
	              					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	              					baseActivity.startActivity(intent);
	                                  dialog.dismiss();
	                                 }
	                                }
	                        });	        
	                                dialog = customBuilder2.create();
	                                break;                                
		                      }
		                           return  dialog;
		                           
		              }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	
    /**
     * ɾ��Ŀ¼���ļ��У��Լ�Ŀ¼�µ��ļ�
     * @param   sPath ��ɾ��Ŀ¼���ļ�·��
     * @return  Ŀ¼ɾ���ɹ�����true�����򷵻�false
     */
    public boolean deleteDirectory(String sPath) {
        //���sPath�����ļ��ָ�����β���Զ�����ļ��ָ���
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        Boolean flag = true;
        //ɾ���ļ����µ������ļ�(������Ŀ¼)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //ɾ�����ļ�
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //ɾ����Ŀ¼
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //ɾ����ǰĿ¼
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ɾ�������ļ�
     * @param   sPath    ��ɾ���ļ����ļ���
     * @return �����ļ�ɾ���ɹ�����true�����򷵻�false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // ·��Ϊ�ļ��Ҳ�Ϊ�������ɾ��
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
	
    
    
	
}
