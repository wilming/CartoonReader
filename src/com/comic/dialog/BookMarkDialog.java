package com.comic.dialog;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cartoon.util.Utils;
import com.comic.R;
import com.comic.activity.core.MainActivity;
import com.comic.activity.ext.BookMarkActivity;
import com.comic.util.Constants;

public class BookMarkDialog extends DefaultDialog{
	private static final String TAG = "BookMarkDialog";
	private BookMarkActivity bookmark_act;
	private Map<String,Object> picInfo;
	
	public BookMarkDialog(BookMarkActivity activity, Map<String,Object> picInfo, String[] items) {
		super(activity, items);
		this.bookmark_act = activity;
		this.picInfo = picInfo;
	}

	@Override
	protected void doPositive() {
		super.doPositive();
	}

	@Override
	protected void doItems(int which) {
		super.doItems(which);
		//int index = which;
		switch(which){
		case 0:
			//查看当前书签
			if(picInfo != null && picInfo.size() > 0){
				String picPath = (String) picInfo.get("imagePath");
				Intent intent = new Intent(bookmark_act,MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("picPath", picPath);
				intent.putExtras(bundle);
				bookmark_act.startActivity(intent);
				bookmark_act.finish();
			}
			break;
		case 1:
			//创建提示对话框
			Builder dialog = new Builder(bookmark_act);
			//标题
			dialog.setTitle(R.string.bookmarkDelete);
			//确定
			dialog.setPositiveButton(R.string.bookmarkSubmit, 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//根据书签名称删除当前书签内容
					if(picInfo != null && picInfo.size() > 0){
						String bookMarkName = (String) picInfo.get("bookMarkName");
						removeBookMarkByName(bookMarkName);
						bookmark_act.setBookMarkAdapter();
					}
				}
			});
			
			//取消
			dialog.setNegativeButton(R.string.bookmarkCancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		case 2:
			//创建提示对话框
			Builder dialog_delall = new Builder(bookmark_act);
			//标题
			dialog_delall.setTitle(R.string.bookmarkDelete);
			//确定
			dialog_delall.setPositiveButton(R.string.bookmarkSubmit, 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//删除所有标签
					removeAllBookMarks();
					bookmark_act.setBookMarkAdapter();
				}

			});
			
			//取消
			dialog_delall.setNegativeButton(R.string.bookmarkCancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog_delall.show();
		}
	}

	/**
	 * 根据书签名删除书签
	 * @param bookMarkName 书签名
	 */
	private void removeBookMarkByName(String bookMarkName) {
		Log.v(TAG,"根据标签名删除标签,传递进来的标签名为:" + bookMarkName);
		String sdCardPath = Utils.getSdCardPath();
		String root_temp_path = sdCardPath + Constants.TEMPPATH;
		String bookMark = "";
		String[] array = Utils.getFileRead1(Constants.BOOKMARKS).split(";");
		
		if(array != null && array.length > 0){
			for(int i = 0;i < array.length;i++){
				if(array[i].indexOf(bookMarkName + "|") != -1){
					array[i] = "";
				}
				if(array[i] != null && array[i].length() > 0){
					if(i == array.length -1){
						bookMark += array[i];
					}else{
						bookMark += array[i] + "\r\n";
					}
				}
			}
			if(bookMark != null){
				//为什么执行这一步呢？感觉多次一举
				bookMark = bookMark.substring(0,bookMark.length() - 1);
			}
			if(sdCardPath != null){
				//获取到sdcard下的书签文件
				String filePath = root_temp_path + Constants.BOOKMARKS;
				File file = new File(filePath);
				if(file.exists()){
					file.delete();
				}
				try {
					Utils.saveFile1(Constants.BOOKMARKS, bookMark, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 删除全部书签
	 */
	private void removeAllBookMarks() {
		String sdCardPath = Utils.getSdCardPath();
		String root_temp_path = sdCardPath + Constants.TEMPPATH;
		if(sdCardPath != null){
			//获取到sdcard下的书签文件
			String filePath = root_temp_path + Constants.BOOKMARKS;
			File file = new File(filePath);
			//删除书签
			if(file.exists()){
				Log.v(TAG,"清理所有的bookmark,路径为:" + filePath);
				file.delete();
			}else{
				Log.v(TAG,"清理失败，文件不存在:" + filePath);
			}
			file = null;
		}
	}
}
