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
	 * 退出
	 * @param con
	 */
	public static void exitApp(){
		//循环关闭Activity
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
	 * 保存当前阅读的漫画信息
	 */
	public static void saveReaderState(){
		try{
			String picPath = Utils.getImagePath(imagePosition, imageList);
			if(picPath != null){
				//false 表示不再源文件中追加，覆盖文件中的内容
				Utils.saveFile1(Constants.HISTORY, picPath, false);
			}
		}catch(Exception e){
			//Log.i(TAG,e.getMessage());
		}
	}
	
	/**
	 * 自定义退出对话框
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
	        customBuilder.setTitle("退出").setMessage("确定退出吗？")
	                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int which) {
	                                	dialog.dismiss();
	                                }
	                        })
	                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int which) {
	                                	exitApp();
	                                }
	                        });	        
	                                dialog = customBuilder.create();
	                                break;
		 case 2:
		 CustomDialog.Builder customBuilder2 = new CustomDialog.Builder(BaseActivity.this);
	        customBuilder2.setTitle("删除电子书").setMessage("确定删除吗？")
	                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int which) {
	                                	dialog.dismiss();
	                                }
	                        })
	                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        Boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
	
    
    
	
}
