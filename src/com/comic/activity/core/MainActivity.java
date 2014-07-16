package com.comic.activity.core;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import com.comic.listener.*;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cartoon.util.Utils;
import com.comic.R;
import com.comic.activity.ext.GetMoreSetup;
import com.comic.adapter.GestureListenerAdapter;
import com.comic.util.Constants;

public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	//RelativeLayout layout2;//菜单顶部
	//RelativeLayout layout3;//菜单中部
	ImageView imageView;//动漫图片
	//TextView imageName;//动漫名称
	TextView pagePosition;//动漫图片当前位置
	//ImageButton setup;//设置
	//Button fanhui;
	//protected ImageButton lastPage;//上一页
	//ImageButton nextPage;//下一页
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
	public OnClickListener fanhuiba=new fanhuiba(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		allActivity.add(this);
		//获得各个组件
		//layout2 = (RelativeLayout) findViewById(R.id.layout2);
		//layout3 = (RelativeLayout) findViewById(R.id.layout3);
		//setup = (ImageButton) findViewById(R.id.setup);
		//fanhui=(Button) findViewById(R.id.fanhui);
		imageView = (ImageView) findViewById(R.id.imageView);
		//lastPage = (ImageButton) findViewById(R.id.lastPage);
		//nextPage = (ImageButton) findViewById(R.id.nextPage);
		//imageName = (TextView) this.findViewById(R.id.imageName);
		//pagePosition = (TextView) findViewById(R.id.pagePosition);
		//手势事件
		gestureScanner = new GestureDetector(gestureListener_adpter);
		//获取图片列表
		imageList = loadImages();
		if(imageList != null && imageList.size() > 0){
			imageArray = imageList.toArray(new String[]{});
		}else{
			//没有漫画图片时将所有的菜单都显示出来
			//layout2.setVisibility(View.VISIBLE);
			//layout3.setVisibility(View.VISIBLE);
			Utils.showMsg(this, R.string.noPic);
		}
		//获得手机分辨率
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		disWidth = dm.widthPixels;
		disHeight = dm.heightPixels;
		//设置监听事件
		imageView.setOnClickListener(hiddenMenu);
		imageView.setLongClickable(true);
//		imageView.setHorizontalScrollBarEnabled(false);
//		imageView.setVerticalScrollBarEnabled(false);
//		imageView.setHorizontalFadingEdgeEnabled(false);
//		imageView.setVerticalFadingEdgeEnabled(false);
//		imageView.setPadding(0, 0, 0, 0);
//		imageView.setScrollbarFadingEnabled(false);
		//imageView.setScaleType(null);;
		//logout.setOnClickListener(logoutButton);
		//setup.setOnClickListener(setupButton);
		//fanhui.setOnClickListener(fanhuiba);
		//lastPage.setOnClickListener(pageTurnButton);
		//nextPage.setOnClickListener(pageTurnButton);
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
		if((bundle != null)&&(bundle.getString("picPath")!=null)){
			Log.v(TAG,"getPicPath --- picPath:" + bundle.getString("picPath"));
			return bundle.getString("picPath");
		}else{
			return Utils.getFileRead1(Constants.HISTORY);
		}
	}
	
	/**
	 * 把放大或者缩小的位图赋值给ImageView
	 * @param index 数组中图片的下标
	 */
	public void setImageView(int index){	
		imagePosition.put("positionId", "" + index);		
		if(imageList != null && imageList.size() > 0){
			//获取当前图片路径
			String picPath = Utils.getImagePath(imagePosition, imageList);
			//实现对图片缩放方法
			Bitmap bitmap = Utils.imageZoom(picPath,disWidth,disHeight);
			if(bitmap != null){
				setImageView(bitmap);
				bitmap = null;
			}
		}		
		try {
			setPageInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setImageView(Bitmap target){
		//imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
				//layout2.setVisibility(View.VISIBLE);
				//layout3.setVisibility(View.VISIBLE);
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
			//layout2.setVisibility(View.GONE);
			//layout3.setVisibility(View.GONE);
			handler.removeCallbacks(hideMenu);
		}
	};
	
	/**
	 * 设置图片信息
	 */
	
	public void setPageInfo() throws Exception{
		if(imageList != null){
			if(imagePosition != null){
				//扩展
				String picPath = Utils.getImagePath(imagePosition, imageList);
				if(picPath != null){
					File file = new File(picPath);
					picPath = file.getParent();
					Log.v(TAG,"setPageInfo:" + picPath);
					//imageName.setText(Utils.getBookName(picPath));
					//imageName.setText((new File(picPath)).getName());
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
				Utils.saveFile1(Constants.HISTORY, picPath, false);
			}
		}catch(Exception e){
			Log.i(TAG,e.getMessage() + "");
		}
		super.onPause();
	}
	
	//按back键提示是否退出
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode && event.getRepeatCount() == 0){
			saveReaderState();
		}
		return super.onKeyDown(keyCode, event);
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
		try {
			setPageInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//所有监听器
//	private OnClickListener setupButton = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			//调用更多设置对话框
//			gms.showDialog();
//			//实现对更多设置的对话框进行扩展（3.11）
//		}
//	};
	
	@Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	  //return super.onCreateOptionsMenu(menu);
		gms.showDialog();
		return false;
	 }
	
//	public OnClickListener page_slideButton = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			if(v == lastPage){
//				getArrayAtBitmap("left");
//			}else{
//				getArrayAtBitmap("right");
//			}
//		}
//	};
	
//	public OnClickListener pageTurnButton = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			int position = Integer.parseInt(imagePosition.get("positionId"));
//			switch(v.getId()){
//			case R.id.lastPage:
//				if(position >= 1){
//					imageIndex = position - 1;
//					picIndex = position - 1;
//					setImageView(imageIndex);
//				}else{
//					Utils.showMsg(MainActivity.this, R.string.pageFirst);
//				}
//				break;
//			case R.id.nextPage:
//				if(position < imageArray.length - 1){
//					imageIndex = position + 1;
//					picIndex = position + 1;
//					setImageView(imageIndex);
//				}else{
//					Utils.showMsg(MainActivity.this, R.string.pageEnd);
//				}
//			}
//		}
//	};
}
