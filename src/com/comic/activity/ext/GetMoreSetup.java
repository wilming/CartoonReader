package com.comic.activity.ext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
	Map<String,String> imagePosition;//ͼƬ��ǰλ��
	LinkedList<String> imageList;//���鼯��
	Handler handler;
	String flag = null;
	Matrix matrix = new Matrix();
	String lastPic = "";
	
	//���캯��
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
	 * �����Ի���
	 */
	public void showDialog(){
		String[] setupArray = new String[]{
				activity.getString(R.string.orderTimeBrowse),
				activity.getString(R.string.bookmarkBrowse),
				activity.getString(R.string.shezhishuqian)};
		DefaultDialog more_dialog = new DefaultDialog(activity,setupArray){

			@Override
			protected void doPositive() {
				super.doPositive();
			}

			@Override
			protected void doItems(int which) {
				switch(which){
				case 0:
					//��ʱ����
					getFixedTime();
					break;
				case 1:
					//��ǩ�б�
					Intent intent = new Intent(activity,BookMarkActivity.class);
					activity.startActivity(intent);
					activity.finish();
					break;
				case 2:
					//������ǩ
					shuqian();
				}
				super.doItems(which);
			}
		};
		more_dialog.setTitle(R.string.xuanzeshezhi);
		more_dialog.show();
	}
	

	/**
	 * ��ʱ���ͼ��
	 */
	private void getFixedTime() {
		//��ȡ�������ֵ
		View view = activity.getLayoutInflater().inflate(R.layout.layout_image_fixedtime, null);
		RadioGroup fixedtime_radioGroup = (RadioGroup) view.findViewById(R.id.fixedtime_radioGroup);
		second0 = (RadioButton) view.findViewById(R.id.second0);
		second3 = (RadioButton) view.findViewById(R.id.second3);
		second5 = (RadioButton) view.findViewById(R.id.second5);
		
		fixedtime_radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//�ֶ�
				if(checkedId == second0.getId()){
					flag = "second0";
				}
				//3��
				if(checkedId == second3.getId()){
					flag = "second3";
				}
				//5��
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
				//ʵ��RadioGroup�ļ���
				Log.v(TAG,"ִ�ж�ʱ����,flagֵΪ ��" + flag);
				if(flag != null && flag.equals("second0")){
					setAutoPlayTime(second0.getText().toString());
				}
				if(flag != null && flag.equals("second3")){
					setAutoPlayTime(second3.getText().toString());
					autoTime = Integer.parseInt(second3.getText().toString());
					Log.v(TAG,"ִ��ʱ��Ϊ:" + autoTime + "��");
					handler.postDelayed(autoShow, autoTime * 1000);
				}
				if(flag != null && flag.equals("second5")){
					setAutoPlayTime(second5.getText().toString());
					autoTime = Integer.parseInt(second5.getText().toString());
					Log.v(TAG,"ִ��ʱ��Ϊ:" + autoTime + "��");
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
	 * �����Զ���������ʱ��
	 * @param time
	 */
	public void setAutoPlayTime(String time) {
		//�Ҿ���setAutoPlayTime��getAutoPlayTime����������ʵ��û��Ҫ
		//�򵥵�����һ��������¼�Զ�����ʱ�䲻�Ǻܺ�����˵����SD���д��д���ļ�
		//��runnable�ж�ȡSD������ʱ�䰡�������ֲ���Ҫ�������������������ʱ�䣬������������
		String content = "time=" + time;
		try {
			Utils.saveFile1(Constants.AUTOSHOWTIME,content,false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��ȡ���õ�����ʱ��
	 * @return String
	 */
	public String getAutoPlayTime(){
		String content = Utils.getFileRead1(Constants.AUTOSHOWTIME);
		if(content != null && content.indexOf("=") != -1){
			content = content.substring(content.indexOf("=") + 1);
		}
		return content;
	}
	
	/**
	 * ������ǩ
	 */
	public void shuqian(){				
				final RelativeLayout view = (RelativeLayout) LayoutInflater.from(this.activity).
						inflate(R.layout.layout_bookmark_add, null);
				AlertDialog.Builder dialog = new AlertDialog.Builder(this.activity);
				dialog.setView(view);
				final EditText bookMarkName = (EditText) view.findViewById(R.id.bookMarkName);
				//����dialogͼƬ$bookmark��������ͼ����
				dialog.setIcon(R.raw.bookmark);
				dialog.setTitle(R.string.bookmarkTitle);
				dialog.setPositiveButton(R.string.bookmarkSubmit, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int bookId = Integer.parseInt(imagePosition.get("positionId")) + 1;
						//�������·��
						String picPath = Utils.getImagePath(imagePosition, imageList);
						//��ʽ����ǩ����|��ǰʱ��,picPath#bookId
						String title = bookMarkName.getText().toString();
						String bookmark = "";
						if(title != null){
							bookmark = title;
						}
						String bookMarks = bookmark + "|" + nowDateTime() + "," + picPath + "#" + bookId;
						//������ǩ
						try {
							Utils.saveFile1(Constants.BOOKMARKS, bookMarks, true);
						} catch (IOException e) {
							e.printStackTrace();
						}
						dialog.dismiss();
					}
				});
				//ȡ����ť�¼�(�رնԻ���) ʲô��˼��
				dialog.show();
	    }
	
	/**
	 * ��ȡ��ǰʱ��
	 * @return String ��ǰʱ��,��ʽ(yyyy-MM-dd HH:mm:ss)
	 */
	private String nowDateTime() {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	    }	
	
	/**
	 * ��ʱͼ������
	 */
	private Runnable autoShow = new Runnable(){
		//ѭ��ԭ����ǲ��ϵĸ�handler����postDelayedִ��Runnable
		int showTime;//��ʱ
		@Override
		public void run() {
			Log.v(TAG,"Runnableִ��");
			int imageIndex = -1;
			String time = getAutoPlayTime();
			Log.v(TAG,"�õ���time��:" + time);
			if(time != null && time.length() > 0){
				showTime = Integer.parseInt(time);
			}else{
				handler.removeCallbacks(autoShow);
				handler.removeMessages(1);
			}
			Log.v(TAG,"��õ�ִ��ʱ��Ϊ:" + showTime);
			if(imagePosition != null && imagePosition.size() > 0){
				String index = imagePosition.get("positionId");
				imageIndex = Integer.parseInt(index);
				imageIndex += 1;
				//i��0��ʼ imageList���鳤�ȴ�1��ʼ
				//�±�����ж����ڸ���û�����壬MainActivity�е�handlerû����дhandleMessage�������Բ��ᱻ��������һ����Ϣ
				//������ȫû�����壬��֤��������ֻ�������������жϾ͹���
				//if(imageIndex == imageList.size() - 1){
				//	Message msg = handler.obtainMessage();
				//	msg.what = 1;
				//	handler.sendMessage(msg);
				//}
				if(imageIndex <= imageList.size() - 1){
					activity.setImageView(imageIndex);
					//��ǰ��������
					Utils.showMsg(activity,(imageIndex + 1) + "/" + imageList.size());
					handler.postDelayed(autoShow, showTime * 1000);
				}
			}
			//���imagePositionΪ�����Ȼ���3����ѭ��
			if(imagePosition == null){
				handler.postDelayed(autoShow, 3000);
			}
		}
		
	};
}
