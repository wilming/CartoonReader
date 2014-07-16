package com.comic.activity.sys;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cartoon.util.Utils;
import com.comic.R;
import com.comic.activity.core.MainActivity;
import com.comic.util.Constants;

public class LoadingActivity extends Activity {
	private static final String TAG = "LoadingActivity";
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
					if(Utils.getShowHistory()){
						intent.setClass(LoadingActivity.this, MainActivity.class);
						Log.v(TAG,"跳转到mainact");
					}else{
						intent.setClass(LoadingActivity.this, InitActivity.class);
						Log.v(TAG,"跳转到INITact");
					}
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
