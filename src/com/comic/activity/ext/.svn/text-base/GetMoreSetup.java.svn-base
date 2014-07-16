package com.comic.activity.ext;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.cartoon.util.Utils;
import com.comic.R;
import com.comic.activity.core.MainActivity;
import com.comic.dialog.DefaultDialog;
import com.comic.util.Constants;

public class GetMoreSetup extends Dialog {
	private static final String TAG = "GetMoreSetup";
	RelativeLayout view;
	RadioGroup radioGroup;
	RadioButton second0;
	RadioButton second3;
	RadioButton second5;
	RadioButton leftRotate;
	RadioButton rightRotate;
	int autoTime;
	MainActivity activity;
	ImageView imageView;
	Map<String,String> imagePosition;//图片当前位置
	LinkedList<String> imageList;//数组集合
	Handler handler;
	String flag = null;
	Matrix matrix = new Matrix();
	String lastPic = "";
	
	//构造函数
	public GetMoreSetup(MainActivity activity,ImageView imageView,Map<String,String> imagePosition,
			LinkedList<String> imageList,Handler handler){
		super(activity);
		this.activity = activity;
		this.imageView = imageView;
		this.imagePosition = imagePosition;
		this.imageList = imageList;
		this.handler = handler;
	}
	
	/**
	 * 创建对话框
	 */
	public void showDialog(){
		String[] setupArray = new String[]{activity.getString(R.string.orderTimeBrowse),
				activity.getString(R.string.bookmarkBrowse),activity.getString(R.string.pictureRotate)};
		DefaultDialog more_dialog = new DefaultDialog(activity,setupArray){

			@Override
			protected void doPositive() {
				super.doPositive();
			}

			@Override
			protected void doItems(int which) {
				switch(which){
				case 0:
					//定时阅览
					getFixedTime();
					break;
				case 1:
					//书签列表
					Intent intent = new Intent(activity,BookMarkActivity.class);
					activity.startActivity(intent);
					activity.finish();
					break;
				case 2:
					//旋转
					getPicRotate();
				}
				super.doItems(which);
			}
		};
		more_dialog.setTitle(R.string.bookmarkTitle);
		more_dialog.show();
	}
	
	/**
	 * 自定义旋转 向左或右旋转45度
	 */
	private void getPicRotate() {
		final float degrees = 45;
		view = (RelativeLayout) getLayoutInflater().inflate(R.layout.layout_image_rotate, null);
		radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
		leftRotate = (RadioButton) view.findViewById(R.id.left);
		rightRotate = (RadioButton) view.findViewById(R.id.right);
		
		DefaultDialog rotate_dialog = new DefaultDialog(activity,null,true){

			@Override
			protected void doPositive() {
				float angle;
				//对旋转漫画图片的方法进行扩展
				if(leftRotate.isChecked()){
					angle = -degrees;
				}else{
					angle = degrees;
				}
				//如果操作的不是同一张图片，重设矩阵
				String picPath = Utils.getImagePath(imagePosition, imageList);
				if(!lastPic.equals(picPath)){
					matrix.reset();
					lastPic = picPath;
				}
				//设置旋转矩阵
				matrix.postRotate(angle, imageView.getWidth() / 2, imageView.getHeight() / 2);
				if(imageView != null){
					imageView.setImageMatrix(matrix);
				}
				super.doPositive();
			}

			@Override
			protected void doItems(int which) {
				super.doItems(which);
			}
		};
		rotate_dialog.setTitle(R.string.picRotate);
		//加载自定义对话框
		rotate_dialog.setView(view);
		rotate_dialog.show();
	}

	/**
	 * 定时浏览图像
	 */
	private void getFixedTime() {
		//获取到保存的值
		View view = activity.getLayoutInflater().inflate(R.layout.layout_image_fixedtime, null);
		RadioGroup fixedtime_radioGroup = (RadioGroup) view.findViewById(R.id.fixedtime_radioGroup);
		second0 = (RadioButton) view.findViewById(R.id.second0);
		second3 = (RadioButton) view.findViewById(R.id.second3);
		second5 = (RadioButton) view.findViewById(R.id.second5);
		
		fixedtime_radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//手动
				if(checkedId == second0.getId()){
					flag = "second0";
				}
				//3秒
				if(checkedId == second3.getId()){
					flag = "second3";
				}
				//5秒
				if(checkedId == second5.getId()){
					flag = "second5";
				}
			}
		});
		second0.setChecked(true);
		
		DefaultDialog time_dialog = new DefaultDialog(activity,null){

			@Override
			protected void doPositive() {
				handler.removeCallbacks(autoShow);
				//实现RadioGroup的监听
				Log.v(TAG,"执行定时阅览,flag值为 ：" + flag);
				if(flag != null && flag.equals("second0")){
					setAutoPlayTime(second0.getText().toString());
				}
				if(flag != null && flag.equals("second3")){
					setAutoPlayTime(second3.getText().toString());
					autoTime = Integer.parseInt(second3.getText().toString());
					Log.v(TAG,"执行时间为:" + autoTime + "秒");
					handler.postDelayed(autoShow, autoTime * 1000);
				}
				if(flag != null && flag.equals("second5")){
					setAutoPlayTime(second5.getText().toString());
					autoTime = Integer.parseInt(second5.getText().toString());
					Log.v(TAG,"执行时间为:" + autoTime + "秒");
					handler.postDelayed(autoShow, autoTime * 1000);
				}
				
				flag = null;
				super.doPositive();
			}

			@Override
			protected void doItems(int which) {
				super.doItems(which);
			}
			
		};
		time_dialog.setTitle(R.string.fixedtimetobrowse);
		time_dialog.setView(view);
		time_dialog.show();
	}
	
	/**
	 * 设置自动阅览漫画时间
	 * @param time
	 */
	public void setAutoPlayTime(String time) {
		//我觉得setAutoPlayTime和getAutoPlayTime这两个方法实在没必要
		//简单的设置一个变量记录自动播放时间不是很好吗？再说，向SD卡中存放写入文件
		//在runnable中读取SD卡这多耗时间啊，而且又不需要程序启动加载这个播放时间，真是让人困惑
		String content = "time=" + time;
		try {
			Utils.saveFile(Constants.AUTOSHOWTIME,content,false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取设置的阅览时间
	 * @return String
	 */
	public String getAutoPlayTime(){
		String content = Utils.getFileRead(Constants.AUTOSHOWTIME);
		if(content != null && content.indexOf("=") != -1){
			content = content.substring(content.indexOf("=") + 1);
		}
		return content;
	}
	
	/**
	 * 定时图像阅览
	 */
	private Runnable autoShow = new Runnable(){
		//循环原理就是不断的给handler对象postDelayed执行Runnable
		int showTime;//定时
		@Override
		public void run() {
			Log.v(TAG,"Runnable执行");
			int imageIndex = -1;
			String time = getAutoPlayTime();
			Log.v(TAG,"得到的time是:" + time);
			if(time != null && time.length() > 0){
				showTime = Integer.parseInt(time);
			}else{
				handler.removeCallbacks(autoShow);
				handler.removeMessages(1);
			}
			Log.v(TAG,"获得的执行时间为:" + showTime);
			if(imagePosition != null && imagePosition.size() > 0){
				String index = imagePosition.get("positionId");
				imageIndex = Integer.parseInt(index);
				imageIndex += 1;
				//i从0开始 imageList数组长度从1开始
				//下边这个判断现在根本没有意义，MainActivity中的handler没有重写handleMessage方法所以不会被处理法这样一个消息
				//现在完全没有意义，保证功能正常只需设置漫画的判断就够了
				//if(imageIndex == imageList.size() - 1){
				//	Message msg = handler.obtainMessage();
				//	msg.what = 1;
				//	handler.sendMessage(msg);
				//}
				if(imageIndex <= imageList.size() - 1){
					activity.setImageView(imageIndex);
					//当前漫画设置
					Utils.showMsg(activity,(imageIndex + 1) + "/" + imageList.size());
					handler.postDelayed(autoShow, showTime * 1000);
				}
			}
			//如果imagePosition为空则先缓冲3秒在循环
			if(imagePosition == null){
				handler.postDelayed(autoShow, 3000);
			}
		}
		
	};
}
