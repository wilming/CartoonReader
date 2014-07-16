package com.comic.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.comic.shelf.BookShelfActivity;

public class fanhuiba implements OnClickListener{
	private Activity activity;
	
	public fanhuiba(Activity activity){
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(activity,BookShelfActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		activity.startActivity(intent);
		activity.finish();
		//还得保存阅览进度
	}
}
