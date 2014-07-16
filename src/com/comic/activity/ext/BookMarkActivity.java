package com.comic.activity.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.cartoon.util.Utils;
import com.comic.R;
import com.comic.activity.core.BaseActivity;
import com.comic.adapter.ImageAdapter;
import com.comic.dialog.BookMarkDialog;
import com.comic.dialog.DefaultDialog;
import com.comic.listener.GobackListener;
import com.comic.util.BookMark;
import com.comic.util.Constants;

public class BookMarkActivity extends BaseActivity {
	private static final String TAG = "BookMarkActivity";
	private ListView bookMarkList;
	private List<Map<String,Object>> list;
	private Map<String,Object> picInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_bookmark_list);
		allActivity.add(this);
		//初始化返回监听器
		GobackListener goback = new GobackListener(this);
		
		bookMarkList = (ListView) findViewById(R.id.bookmarkList);
		bookMarkList.setOnItemClickListener(goBookMarkInfo);
		ImageButton goBack = (ImageButton) findViewById(R.id.goBack);
		goBack.setOnClickListener(goback);
		
		//对书签列表适配器的扩展
		setBookMarkAdapter();
	}
	
	private OnItemClickListener goBookMarkInfo = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adp, View view, int position,
				long id) {
			//对获取到某一书签信息及对话框的扩展
			picInfo = list.get(position);
			showDialog();
		}
	};
	
	/**
	 * 书签适配器
	 */
	public void setBookMarkAdapter(){
		list = getBookMarkList();
		//实现对ImageAdapter类的实现
		ImageAdapter image_adapter = new ImageAdapter(list,BookMarkActivity.this);
		
		if(bookMarkList != null){
			Log.v(TAG,"bookmarkList is not null");
		}else{
			Log.v(TAG,"bookmarkList is null");
		}
		bookMarkList.setAdapter(image_adapter);
		//为bookmarkList添加监听事件对象goBookMarkInfo
		//onCreate方法中已经设置了啊
	}
	
	public void showDialog() {
		//查看、删除、删除全部
		String[] bookMarkArray = new String[]{getString(R.string.check),getString(R.string.delete),getString(R.string.deleteAll)};
		DefaultDialog dialog = new BookMarkDialog(this,picInfo,bookMarkArray);
		//新增对查看当前书签、根据书签名称删除当前书签内容及删除所有书签的类的实现扩展
		dialog.setTitle(R.string.bookmarkSetUp);
		dialog.show();
	}

	/**
	 * 获取图片内容
	 */
	private List<Map<String,Object>> getBookMarkList(){
		List<BookMark> BookMarks = getFileContent();
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		//实现新增类BookMark
		for(BookMark bookMark:BookMarks){
			Map<String,Object> bm = new HashMap<String,Object>();
			bm.put("imagePath",bookMark.getImagePath());
			bm.put("bookMarkName",bookMark.getBookMarkName());
			bm.put("saveTime",bookMark.getSaveTime());
			bm.put("position",String.valueOf(bookMark.getPosition()));
			bm.put("imageId",bookMark.getImageId());
			list.add(bm);
		}
		return list;
	}
	
	/**
	 * 获取到保存的漫画书信息
	 * @return List<BookMark>
	 */
	private List<BookMark> getFileContent(){
		List<BookMark> list = new ArrayList<BookMark>();
		Log.v(TAG,"Constants.BOOKMARKS：" + Constants.BOOKMARKS);
		String content = Utils.getFileRead1(Constants.BOOKMARKS);
		Log.v(TAG,"返回的书签内容：" + content);
		if(content != null && content.length() > 0){
			String[] bookMarks = content.split(";");
			for(int i = 0;i < bookMarks.length;i++){
				BookMark bm = new BookMark();
				String book = bookMarks[i];
				Log.v(TAG,"获得的book字符串：" + book);
				Log.v(TAG,"获得的索引：" + book.indexOf("|"));
				Log.v(TAG,book.substring(0,book.indexOf("|")));
				//book内容：3344|2011-10-10 02:27:57,/mnt/sdcard/cartoonReader/temp/017.png#0
				bm.setBookMarkName(book.substring(0,book.indexOf("|")));
				bm.setSaveTime(book.substring(book.indexOf("|") + 1,book.indexOf(",")));
				bm.setImagePath(book.substring(book.indexOf(",") + 1,book.indexOf("#")));
				bm.setPosition(Integer.parseInt(book.substring(book.indexOf("#") + 1)) + 1);
				bm.setImageId(book.substring(book.indexOf("#") + 1));
				list.add(bm);
				bm = null;
			}
		}else{
			Utils.showMsg(this, R.string.nobookmark);
		}
		
		return list;
	}
}
