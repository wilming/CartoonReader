package com.comic.dialog;

import com.comic.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class DefaultDialog extends AlertDialog.Builder{
	private String[] items;
	
	public DefaultDialog(Activity activity,String[] pageArray,boolean showOkButton){
		this(activity,pageArray);
	}
	
	public DefaultDialog(Activity activity,String[] items){
		super(activity);
		this.items = items;
		init();
	}
	
	private void init(){
		//点击确定后的事件
		this.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doPositive();
			}
		});
		//取消事件
		this.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		this.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doItems(which);
			}
		});
	}
	
	protected void doPositive(){}
	protected void doItems(int which){}
}
