package com.comic.activity.sys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.comic.R;
import com.comic.activity.sdcard.TabMainActivity;

public class InitActivity extends Activity {
	private ImageButton btn_choose;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_init);
		btn_choose = (ImageButton) findViewById(R.id.choose);
		btn_choose.setOnClickListener(new TabMainSpace());
	}
	
	class TabMainSpace implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(InitActivity.this,TabMainActivity.class);
			startActivity(intent);
			finish();
		}
		
	}
}
