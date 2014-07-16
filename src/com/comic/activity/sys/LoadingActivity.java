package com.comic.activity.sys;
//打开桌面图标后进入的第一个界面，选择是进入书架还是进入阅读历史
import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cartoon.util.Utils;
import com.comic.R;
import com.comic.shelf.BookShelfActivity;
import com.comic.util.Constants;

public class LoadingActivity extends Activity {
	private static final String TAG = "Loading Activity...";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_loading);
		if(!createFile()){
			Utils.showMsg(this,R.string.sdcard_no_sdcard);
			this.finish();
		}else{
			onLoading();
		}
	}
	
	public void onLoading(){
		
		new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					Log.i(TAG, e.getMessage());
				} finally{
					Intent intent = new Intent();
					intent.setClass(LoadingActivity.this, BookShelfActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					Log.v(TAG,"跳转到书架");
					startActivity(intent);
					finish();
				}
			}
			
		}.start();
	}
	
	public boolean createFile(){
		String sdCardPath = Utils.getSdCardPath();
		if(sdCardPath != null){
			String root_temp_path = sdCardPath + Constants.TEMPPATH;
			File dir = new File(root_temp_path);
			
			if(!dir.exists()){
				dir.mkdirs();
			}
			dir = null;
			return true;
		}
		return false;
	}
	
}
