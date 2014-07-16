package com.comic.activity.core;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cartoon.util.Utils;
import com.comic.R;
import com.comic.activity.ext.GetMoreSetup;
import com.comic.activity.sdcard.TabMainActivity;
import com.comic.adapter.GestureListenerAdapter;
import com.comic.dialog.DefaultDialog;
import com.comic.util.Constants;

public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	RelativeLayout layout2;//菜单顶部
	RelativeLayout layout3;//菜单中部
	RelativeLayout layout4;//菜单底部
	ImageView imageView;//动漫图片
	TextView imageName;//动漫名称
	TextView pagePosition;//动漫图片当前位置
	ImageButton openSDcard;//打开SDcard卡
	ImageButton page;//分页
	ImageButton zoomSmall;//缩放 缩小
	ImageButton zoomBig;//缩放 放大
	ImageButton bookmark;//书签
	ImageButton logout;//退出
	ImageButton setup;//设置
	protected ImageButton lastPage;//上一页
	ImageButton nextPage;//下一页
	String bookMarks;
	String[] imageArray;//图片数组
	Map<String,String> startPosition;//图片开始位置
	int imageIndex;//图片索引
	int disWidth;//宽
	int disHeight;//高
	Handler handler;
	protected GestureDetector gestureScanner;
	int picIndex;
	GetMoreSetup gms;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		allActivity.add(this);
		//获得各个组件
		layout2 = (RelativeLayout) findViewById(R.id.layout2);
		layout3 = (RelativeLayout) findViewById(R.id.layout3);
		layout4 = (RelativeLayout) findViewById(R.id.layout4);
		openSDcard = (ImageButton) findViewById(R.id.openSDcard);
		page = (ImageButton) findViewById(R.id.page);
		zoomSmall = (ImageButton) findViewById(R.id.zoomSmall);
		zoomBig = (ImageButton) findViewById(R.id.zoomBig);
		bookmark = (ImageButton) findViewById(R.id.bookmark);
		logout = (ImageButton) findViewById(R.id.logout);
		setup = (ImageButton) findViewById(R.id.setup);
		imageView = (ImageView) findViewById(R.id.imageView);
		lastPage = (ImageButton) findViewById(R.id.lastPage);
		nextPage = (ImageButton) findViewById(R.id.nextPage);
		imageName = (TextView) this.findViewById(R.id.imageName);
		pagePosition = (TextView) findViewById(R.id.pagePosition);
		//手势事件
		gestureScanner = new GestureDetector(gestureListener_adpter);
		//获取图片列表
		imageList = loadImages();
		if(imageList != null && imageList.size() > 0){
			imageArray = imageList.toArray(new String[]{});
		}else{
			//没有漫画图片时将所有的菜单都显示出来
			layout2.setVisibility(View.VISIBLE);
			layout3.setVisibility(View.VISIBLE);
			layout4.setVisibility(View.VISIBLE);
			Utils.showMsg(this, R.string.noPic);
		}
		//获得手机分辨率
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		disWidth = dm.widthPixels;
		disHeight = dm.heightPixels;
		//设置监听事件
		imageView.setOnClickListener(hiddenMenu);
		imageView.setLongClickable(true);
		openSDcard.setOnClickListener(openSDcardButton);
		page.setOnClickListener(pageButton);
		zoomSmall.setOnClickListener(zoomButton);
		zoomBig.setOnClickListener(zoomButton);
		bookmark.setOnClickListener(bookmarkButton);
		logout.setOnClickListener(logoutButton);
		setup.setOnClickListener(setupButton);
		lastPage.setOnClickListener(pageTurnButton);
		nextPage.setOnClickListener(pageTurnButton);
		//实例化Handler
		handler = new Handler();
		//初始化加载
		initLoadImages();
		//更多设置
		gms = new GetMoreSetup(this,imageView,imagePosition,imageList,handler);
	}
	
	/**
	 * 加载到该漫画图片的文件夹下的所有图片列表
	 * @return LinkedList
	 */
	public LinkedList<String> loadImages(){
		LinkedList<String> list = new LinkedList<String>();
		//获得文件路径
		String filePath = getPicPath();
		if(filePath != null){
			//获得filePath父路径下所有文件对象数组File[] files并排序
			File[] files = Utils.getPicOrder((new File(filePath)).getParent());
			if(files != null){
				for(File file:files){
					if(Utils.isPic(file.getPath())){
						list.add(file.getPath());
					}
				}
				int i = 0;
				for(String file:list){
					if(file.equals(filePath)){
						picIndex = i;
						break;
					}
					i += 1;
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 初始化图片
	 */
	public void initLoadImages(){
		//获得图片路径
		String picPath = getPicPath();
		Log.v(TAG,"initLoadImages:" + picPath);
		if(picPath != null){
			if(imageArray != null){
				//获取当前图片位置
				int i = getPicPosition(picPath,imageArray);
				//设置显示的图片
				setImageView(i);
			}
		}
	}
	
	//手势事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		gestureScanner.onTouchEvent(ev);
		imageView.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 根据Intent获得到漫画图片的路径
	 * @return String
	 */
	public String getPicPath(){
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null){
			Log.v(TAG,"getPicPath --- picPath:" + bundle.getString("picPath"));
			return bundle.getString("picPath");
		}else{
			return Utils.getFileRead(Constants.HISTORY);
		}
	}
	
	/**
	 * 把放大或者缩小的位图赋值给ImageView
	 * @param index 数组中图片的下标
	 */
	public void setImageView(int index){
		imagePosition.put("positionId", "" + index);
		imageView.getImageMatrix().reset();
		imageView.setImageBitmap(BitmapFactory.decodeFile(imageArray[index]));
		setPageInfo();
	}
	
	public void setImageView(Bitmap target){
		//imagePosition.put("positionId", "" + index);
		//setPageInfo();
		imageView.setImageBitmap(target);
	}
	
	/**
	 * 获取当前图片的位置
	 * @param picPath
	 * @param imagesArray
	 * @return
	 */
	public int getPicPosition(String picPath,String[] imagesArray){
		int position = 0;
		for(int i= 0;i < imagesArray.length;i++){
			Log.v(TAG,"图片列表中当前为:" + imagesArray[i]);
			if(picPath.equals(imagesArray[i])){
				Log.v(TAG,"索引，索引为:" + i);
				position = i;
				break;
			}
		}
		Log.v(TAG,"返回索引，索引为:" + position);
		return position;
	}
	
	public OnClickListener hiddenMenu = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//加载有漫画图片时才隐藏或显示菜单
			if(imageList != null){
				//显示上中下菜单
				layout2.setVisibility(View.VISIBLE);
				layout3.setVisibility(View.VISIBLE);
				layout4.setVisibility(View.VISIBLE);
				//5s之后自动隐藏菜单
				handler.postDelayed(hideMenu, 5000);
			}
		}
	};

	//Come on根本没有GestureListenerAdapter
	/**
	 * 手势事件 我自己定义了一个 目前没有发现哪里定义了此类
	 */
	public GestureListenerAdapter gestureListener_adpter = new GestureListenerAdapter(){
		@Override
		public void doOnFling(String action) {
			if(imageList != null && imageList.size() > 0){
				if(action.equals("right")){
					//上一页
					if(picIndex > 0){
						picIndex -= 1;
						setImageView(picIndex);
					}
				}else if(action.equals("left")){
					//下一页
					if(picIndex < imageList.size() - 1){
						picIndex += 1;
						setImageView(picIndex);
					}
				}
			}
		}
	};
	
	/**
	 * 通过Handler隐藏菜单
	 */
	private Runnable hideMenu = new Runnable(){
		@Override
		public void run() {
			layout2.setVisibility(View.GONE);
			layout3.setVisibility(View.GONE);
			layout4.setVisibility(View.GONE);
			handler.removeCallbacks(hideMenu);
		}
	};
	
	/**
	 * 设置图片信息
	 */
	
	public void setPageInfo(){
		if(imageList != null){
			if(imagePosition != null){
				//扩展
				String picPath = Utils.getImagePath(imagePosition, imageList);
				if(picPath != null){
					File file = new File(picPath);
					picPath = file.getParent();
					Log.v(TAG,"setPageInfo:" + picPath);
					imageName.setText((new File(picPath)).getName());
				}
				String page = getImagePagePosition(imagePosition,imageList);
				pagePosition.setText(page);
			}
		}else{
			Utils.showNoPicMsg(this);
		}
	}
	
	/**
	 * 获取当前图片的位置
	 * @param imagePosition
	 * @param imageList
	 * @return String
	 */
	public String getImagePagePosition(Map<String,String> imagePosition,LinkedList<String> imageList){
		String pagePosition = null;
		if(imagePosition != null){
			StringBuffer sbf = new StringBuffer();
			//当前页与索引关系是 page = index + 1
			int page = Integer.parseInt(imagePosition.get("positionId")) + 1;
			int totalPages = imageList.size();
			sbf.append(String.valueOf(page));
			sbf.append("/");
			sbf.append(String.valueOf(totalPages));
			pagePosition = sbf.toString();
			sbf = null;
		}
		return pagePosition;
	}
	/**
	 * 获取手机分辨率
	 */
	public void getDisplayMetrics(){
		/* 取得分辨率的大小*/
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		disWidth = dm.widthPixels;
		disHeight = dm.heightPixels;
	}
	
	/**
	 * 保存当前阅读的漫画信息
	 */
	@Override
	protected void onPause() {
		Log.v(TAG,"MainActivity-onPause");
		try{
			//获取当前图片路径
			//对获取当前图片路径进行扩展
			String picPath = Utils.getImagePath(imagePosition, imageList);
			if(picPath != null){
				//false表示不在源文件基础上追加，重新建立一个新文件
				Utils.saveFile(Constants.HISTORY, picPath, false);
			}
		}catch(Exception e){
			Log.i(TAG,e.getMessage() + "");
		}
		super.onPause();
	}
	
	/**
	 * 根据指令进行页面跳转，left为上一页，right为下一页
	 * @param String left或right
	 */
	public void getArrayAtBitmap(String str){
		if(imageList != null){
			//初始化imagePosition对象，这个我很多地方都初始化，为什么不在baseActivity类中直接初始化呢？
			int len = imageArray.length;
			//上一页操作
			if(str.equals("left")){
				if(picIndex >= 1){
					int i = picIndex - 1;
					imagePosition.put("positionId", "" + i);
					setImageView(i);
					picIndex = i;
				}else{
					Utils.showMsg(this,R.string.pageFirst);
				}
			}
			//下一页操作
			if(str.equals("right")){
				if(picIndex < len -1){
					int i = picIndex + 1;
					imagePosition.put("positionId", "" + i);
					setImageView(i);
					picIndex = i;
				}else{
					Utils.showMsg(this,R.string.pageEnd);
				}
			}
		}else{
			Utils.showNoPicMsg(this);
		}
		//设置当前的漫画图片信息
		setPageInfo();
	}
	
	
	//所有监听器
	private OnClickListener openSDcardButton = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this,TabMainActivity.class);
			//获得当前图片路径
			if(imageList != null && imageList.size() > 0){
				String picPath = Utils.getImagePath(imagePosition,imageList);
				if(picPath != null && picPath.length() > 0){
					Bundle bundle = new Bundle();
					bundle.putString("picPath", picPath);
					intent.putExtras(bundle);
				}
			}
			startActivity(intent);
		}
	};
	
	/**
	 * 翻页功能
	 */
	private OnClickListener pageButton = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//该数组用于显示第一页，上一页，下一页，最后一页
			String[] pageArray = new String[]{getString(R.string.firstPage),getString(R.string.beforePage),
					getString(R.string.nextPage),getString(R.string.lastPage)};
			//调用自定义对话框，false为不显示确定按钮，反之显示
			//自定义对话框
			DefaultDialog pageDialog = new DefaultDialog(MainActivity.this,pageArray,false){
				@Override
				protected void doItems(int which){
					int position = Integer.parseInt(imagePosition.get("positionId"));
					switch(which){
					case 0:
						if(position > 1){
							imageIndex = 0;
							picIndex = 0;
							setImageView(imageIndex);
						}else{
							Utils.showMsg(MainActivity.this, R.string.pageFirst);
						}
						break;
					case 1:
						if(position >= 1){
							imageIndex = position - 1;
							picIndex = position - 1;
							setImageView(imageIndex);
						}else{
							Utils.showMsg(MainActivity.this, R.string.pageFirst);
						}
						break;
					case 2:
						if(position < imageArray.length - 1){
							imageIndex = position + 1;
							picIndex = position + 1;
							setImageView(imageIndex);
						}else{
							Utils.showMsg(MainActivity.this, R.string.pageEnd);
						}
						break;
					case 3:
						if(position != imageArray.length - 1){
							imageIndex = imageArray.length - 1;
							picIndex = imageArray.length -1;
							setImageView(imageIndex);
						}else{
							Utils.showMsg(MainActivity.this, R.string.pageEnd);
						}
					}
				}
				@Override
				protected void doPositive(){}
			};
			pageDialog.setTitle(R.string.pageTitle);
			pageDialog.show();
		}
	};
	
	private OnClickListener zoomButton = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(imageList != null && imageList.size() > 0){
				//获取当前图片路径
				String picPath = Utils.getImagePath(imagePosition, imageList);
				String action = "";
				if(v == zoomSmall){
					action = "small";
				}else{
					action = "big";
				}
				Log.v(TAG,"点击缩放图片按钮 ，屏幕高宽：{" + disWidth + ":" + disHeight + "}");
				//实现对图片缩放方法
				Bitmap bitmap = Utils.imageZoom(picPath,disWidth,disHeight,action);
				if(bitmap != null){
					setImageView(bitmap);
					bitmap = null;
				}
			}
		}
	};
	
	
	private OnClickListener bookmarkButton = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//这是搞什么嘛 前边使用DefaultDialog这里又要用AlertDialog,这称之为扩展？
			
			final RelativeLayout view = (RelativeLayout) LayoutInflater.from(MainActivity.this).
					inflate(R.layout.layout_bookmark_add, null);
			/*
			DefaultDialog dialog = new DefaultDialog(MainActivity.this,null,true){
				@Override
				protected void doPositive() {
					this.setContentView(view);
					super.doPositive();
				}

				@Override
				protected void doItems(int which) {
					super.doItems(which);
				}
			};
			*/
			AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setView(view);
			final EditText bookMarkName = (EditText) view.findViewById(R.id.bookMarkName);
			//设置dialog图片$bookmark？是设置图标吗？
			dialog.setIcon(R.raw.bookmark);
			dialog.setTitle(R.string.bookmarkTitle);
			dialog.setPositiveButton(R.string.bookmarkSubmit, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int bookId = Integer.parseInt(imagePosition.get("positionId")) + 1;
					//获得漫画路径
					String picPath = Utils.getImagePath(imagePosition, imageList);
					//格式：书签名称|当前时间,picPath#bookId
					String title = bookMarkName.getText().toString();
					String bookmark = "";
					if(title != null){
						bookmark = title;
					}
					String bookMarks = bookmark + "|" + nowDateTime() + "," + picPath + "#" + bookId;
					//保存书签
					try {
						Utils.saveFile(Constants.BOOKMARKS, bookMarks, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
					dialog.dismiss();
				}
			});
			//取消按钮事件(关闭对话框) 什么意思？
			dialog.show();
		}
	};
	
	/**
	 * 获取当前时间
	 * @return String 当前时间,格式(yyyy-MM-dd HH:mm:ss)
	 */
	private String nowDateTime() {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}
	
	private OnClickListener logoutButton = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//调用BaseActivity中的退出
			promptExit(MainActivity.this);
			//实现对退出对话框进行扩展（3.10）
		}
	};

	private OnClickListener setupButton = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//调用更多设置对话框
			gms.showDialog();
			//实现对更多设置的对话框进行扩展（3.11）
		}
	};
	
	public OnClickListener page_slideButton = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == lastPage){
				getArrayAtBitmap("left");
			}else{
				getArrayAtBitmap("right");
			}
		}
	};
	
	public OnClickListener pageTurnButton = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = Integer.parseInt(imagePosition.get("positionId"));
			switch(v.getId()){
			case R.id.lastPage:
				if(position >= 1){
					imageIndex = position - 1;
					picIndex = position - 1;
					setImageView(imageIndex);
				}else{
					Utils.showMsg(MainActivity.this, R.string.pageFirst);
				}
				break;
			case R.id.nextPage:
				if(position < imageArray.length - 1){
					imageIndex = position + 1;
					picIndex = position + 1;
					setImageView(imageIndex);
				}else{
					Utils.showMsg(MainActivity.this, R.string.pageEnd);
				}
			}
		}
	};
}
