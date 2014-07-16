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
	//RelativeLayout layout2;//�˵�����
	//RelativeLayout layout3;//�˵��в�
	ImageView imageView;//����ͼƬ
	//TextView imageName;//��������
	TextView pagePosition;//����ͼƬ��ǰλ��
	//ImageButton setup;//����
	//Button fanhui;
	//protected ImageButton lastPage;//��һҳ
	//ImageButton nextPage;//��һҳ
	String bookMarks;
	String[] imageArray;//ͼƬ����
	Map<String,String> startPosition;//ͼƬ��ʼλ��
	int imageIndex;//ͼƬ����
	int disWidth;//��
	int disHeight;//��
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
		//��ø������
		//layout2 = (RelativeLayout) findViewById(R.id.layout2);
		//layout3 = (RelativeLayout) findViewById(R.id.layout3);
		//setup = (ImageButton) findViewById(R.id.setup);
		//fanhui=(Button) findViewById(R.id.fanhui);
		imageView = (ImageView) findViewById(R.id.imageView);
		//lastPage = (ImageButton) findViewById(R.id.lastPage);
		//nextPage = (ImageButton) findViewById(R.id.nextPage);
		//imageName = (TextView) this.findViewById(R.id.imageName);
		//pagePosition = (TextView) findViewById(R.id.pagePosition);
		//�����¼�
		gestureScanner = new GestureDetector(gestureListener_adpter);
		//��ȡͼƬ�б�
		imageList = loadImages();
		if(imageList != null && imageList.size() > 0){
			imageArray = imageList.toArray(new String[]{});
		}else{
			//û������ͼƬʱ�����еĲ˵�����ʾ����
			//layout2.setVisibility(View.VISIBLE);
			//layout3.setVisibility(View.VISIBLE);
			Utils.showMsg(this, R.string.noPic);
		}
		//����ֻ��ֱ���
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		disWidth = dm.widthPixels;
		disHeight = dm.heightPixels;
		//���ü����¼�
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
		//ʵ����Handler
		handler = new Handler();
		//��ʼ������
		initLoadImages();
		//��������
		gms = new GetMoreSetup(this,imageView,imagePosition,imageList,handler);
	}
	
	/**
	 * ���ص�������ͼƬ���ļ����µ�����ͼƬ�б�
	 * @return LinkedList
	 */
	public LinkedList<String> loadImages(){
		LinkedList<String> list = new LinkedList<String>();
		//����ļ�·��
		String filePath = getPicPath();
		if(filePath != null){
			//���filePath��·���������ļ���������File[] files������
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
	 * ��ʼ��ͼƬ
	 */
	public void initLoadImages(){
		//���ͼƬ·��
		String picPath = getPicPath();
		Log.v(TAG,"initLoadImages:" + picPath);
		if(picPath != null){
			if(imageArray != null){
				//��ȡ��ǰͼƬλ��
				int i = getPicPosition(picPath,imageArray);
				//������ʾ��ͼƬ
				setImageView(i);
			}
		}
	}
	
	//�����¼�
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		gestureScanner.onTouchEvent(ev);
		imageView.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * ����Intent��õ�����ͼƬ��·��
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
	 * �ѷŴ������С��λͼ��ֵ��ImageView
	 * @param index ������ͼƬ���±�
	 */
	public void setImageView(int index){	
		imagePosition.put("positionId", "" + index);		
		if(imageList != null && imageList.size() > 0){
			//��ȡ��ǰͼƬ·��
			String picPath = Utils.getImagePath(imagePosition, imageList);
			//ʵ�ֶ�ͼƬ���ŷ���
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
	 * ��ȡ��ǰͼƬ��λ��
	 * @param picPath
	 * @param imagesArray
	 * @return
	 */
	public int getPicPosition(String picPath,String[] imagesArray){
		int position = 0;
		for(int i= 0;i < imagesArray.length;i++){
			Log.v(TAG,"ͼƬ�б��е�ǰΪ:" + imagesArray[i]);
			if(picPath.equals(imagesArray[i])){
				Log.v(TAG,"����������Ϊ:" + i);
				position = i;
				break;
			}
		}
		Log.v(TAG,"��������������Ϊ:" + position);
		return position;
	}
	
	public OnClickListener hiddenMenu = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//����������ͼƬʱ�����ػ���ʾ�˵�
			if(imageList != null){
				//��ʾ�����²˵�
				//layout2.setVisibility(View.VISIBLE);
				//layout3.setVisibility(View.VISIBLE);
				//5s֮���Զ����ز˵�
				handler.postDelayed(hideMenu, 5000);
			}
		}
	};

	//Come on����û��GestureListenerAdapter
	/**
	 * �����¼� ���Լ�������һ�� Ŀǰû�з������ﶨ���˴���
	 */
	public GestureListenerAdapter gestureListener_adpter = new GestureListenerAdapter(){
		@Override
		public void doOnFling(String action) {
			if(imageList != null && imageList.size() > 0){
				if(action.equals("right")){
					//��һҳ
					if(picIndex > 0){
						picIndex -= 1;
						setImageView(picIndex);
					}
				}else if(action.equals("left")){
					//��һҳ
					if(picIndex < imageList.size() - 1){
						picIndex += 1;
						setImageView(picIndex);
					}
				}
			}
		}
	};
	
	/**
	 * ͨ��Handler���ز˵�
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
	 * ����ͼƬ��Ϣ
	 */
	
	public void setPageInfo() throws Exception{
		if(imageList != null){
			if(imagePosition != null){
				//��չ
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
	 * ��ȡ��ǰͼƬ��λ��
	 * @param imagePosition
	 * @param imageList
	 * @return String
	 */
	public String getImagePagePosition(Map<String,String> imagePosition,LinkedList<String> imageList){
		String pagePosition = null;
		if(imagePosition != null){
			StringBuffer sbf = new StringBuffer();
			//��ǰҳ��������ϵ�� page = index + 1
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
	 * ���浱ǰ�Ķ���������Ϣ
	 */
	@Override
	protected void onPause() {
		Log.v(TAG,"MainActivity-onPause");
		try{
			//��ȡ��ǰͼƬ·��
			//�Ի�ȡ��ǰͼƬ·��������չ
			String picPath = Utils.getImagePath(imagePosition, imageList);
			if(picPath != null){
				//false��ʾ����Դ�ļ�������׷�ӣ����½���һ�����ļ�
				Utils.saveFile1(Constants.HISTORY, picPath, false);
			}
		}catch(Exception e){
			Log.i(TAG,e.getMessage() + "");
		}
		super.onPause();
	}
	
	//��back����ʾ�Ƿ��˳�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode && event.getRepeatCount() == 0){
			saveReaderState();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	/**
	 * ����ָ�����ҳ����ת��leftΪ��һҳ��rightΪ��һҳ
	 * @param String left��right
	 */
	public void getArrayAtBitmap(String str){
		if(imageList != null){
			//��ʼ��imagePosition��������Һܶ�ط�����ʼ����Ϊʲô����baseActivity����ֱ�ӳ�ʼ���أ�
			int len = imageArray.length;
			//��һҳ����
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
			//��һҳ����
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
		//���õ�ǰ������ͼƬ��Ϣ
		try {
			setPageInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//���м�����
//	private OnClickListener setupButton = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			//���ø������öԻ���
//			gms.showDialog();
//			//ʵ�ֶԸ������õĶԻ��������չ��3.11��
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
