package com.comic.activity.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.cartoon.util.Utils;
import com.comic.R;
import com.comic.util.Constants;

public class BaseActivity extends Activity {
	private static final String TAG = "BaseActivity";
	protected static Map<String,String> imagePosition = new HashMap<String,String>();
	protected static LinkedList<String> imageList = new LinkedList<String>();
	protected static ArrayList<Activity> allActivity = new ArrayList<Activity>();
	
	public static Activity getActivityByName(String name){
		
		return null;
	}
	
	/**
	 * 退出
	 * @param con
	 */
	public static void exitApp(Context con){
		//循环关闭Activity
		Iterator<Activity> it = allActivity.iterator();
		while(it.hasNext()){
			Activity act = it.next();
			if(act != null){
				act.finish();
			}
		}
		saveReaderState();
	}
	
	/**
	 * 保存当前阅读的漫画信息
	 */
	public static void saveReaderState(){
		try{
			String picPath = Utils.getImagePath(imagePosition, imageList);
			if(picPath != null){
				//false 表示不再源文件中追加，覆盖文件中的内容
				Utils.saveFile(Constants.HISTORY, picPath, false);
			}
		}catch(Exception e){
			Log.i(TAG,e.getMessage());
		}
	}
	
	/**
	 * 自定义退出对话框
	 * @param context
	 */
	public static void promptExit(final Context context){
		//自定义布局
		View view = LayoutInflater.from(context).inflate(R.layout.layout_logout, null);
		//创建提示对话框
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(R.string.logout_title);
		dialog.setView(view);
		//确定按钮
		dialog.setPositiveButton(R.string.logout_submit, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//弹出退出对话框并退出阅读器
				exitApp(context);
			}
		});
		
		//取消按钮
		dialog.setNegativeButton(R.string.logout_cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//关闭提示对话框
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	//按back键提示是否退出
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode && event.getRepeatCount() == 0){
			promptExit(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	
}
