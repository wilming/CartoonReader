package com.comic.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comic.R;

public class ImageAdapter extends BaseAdapter{
	private static final String TAG = "ImageAdapter";
	Activity activity;
	List<Map<String,Object>> imageList;
	private static Map<String,Bitmap> icons = new HashMap<String, Bitmap>();
	private LayoutInflater inflater;
	
	public ImageAdapter(List<Map<String, Object>> list,
			Activity activity) {
		super();
		this.imageList = list;
		this.activity = activity;
		inflater = (LayoutInflater) LayoutInflater.from(activity);
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return imageList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * 书签太多的时候getView方法很卡,试试用ViewHolder看看能不能解决这问题？
	 * 经过测试，卡的问题主要出现在每次getView都要访问SD卡从中创建bitmap,速度非常的慢，使用ViewHolder对于卡的问题帮助不大，
	 * 但是使用Map将加载过的图片存放在内存中可以解决这个问题，但是这样做会消耗更多内存，但是非常流畅，体验很好
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(null == convertView){
			convertView = inflater.inflate(R.layout.layout_bookmark, null);
			holder = new ViewHolder();
			holder.imagePath = (ImageView) convertView.findViewById(R.id.imagePath);
			holder.bookMarkName = (TextView) convertView.findViewById(R.id.listbookMarkName);
			holder.saveTime = (TextView) convertView.findViewById(R.id.saveTime);
			holder.imageId = (TextView) convertView.findViewById(R.id.imageId);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		//对自定义书签布局文件的扩展
		//书签保存的图片
		//ImageView imagePath = (ImageView) convertView.findViewById(R.id.imagePath);
		//书签名 layout_bookmark_add之中已经定义了bookMarkName这个Id了，这样不会冲突吗？
		//TextView bookMarkName = (TextView) convertView.findViewById(R.id.listbookMarkName);
		//书签创建时间
		//TextView saveTime = (TextView) convertView.findViewById(R.id.saveTime);
		//书签保存图片ID
		//TextView imageId = (TextView) convertView.findViewById(R.id.imageId);
		
		//将图片缩小为合适的大小
		Map<String,Object> bookmark = imageList.get(position);
		
		String imagePath = (String) bookmark.get("imagePath");
		Bitmap bmp;
		if(!icons.containsKey(imagePath)){
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			bmp = BitmapFactory.decodeFile(imagePath,opts);
			//opts.inSampleSize = (opts.outWidth / holder.imagePath.getWidth() + opts.outHeight / holder.imagePath.getHeight()) / 2;
			//这里因为使用holder.imagePath.getWidth()和getHeight()获得内容值为零，造成除零异常，所以只好先写死了
			//原因待测
			opts.inSampleSize = (opts.outWidth / 90 + opts.outHeight / 90) / 2;
			opts.inJustDecodeBounds = false;
			Log.v(TAG,"书签图片地址:" + (String) bookmark.get("imagePath"));
			bmp = BitmapFactory.decodeFile(imagePath,opts);
			Log.v(TAG,"得到的位图大小:" + bmp.getWidth() + ":" + bmp.getHeight());
			icons.put(imagePath, bmp);
		}else{
			bmp = icons.get(imagePath);
		}
		
		holder.imagePath.setImageBitmap(bmp);
		holder.bookMarkName.setText((CharSequence) bookmark.get("bookMarkName"));
		holder.saveTime.setText((CharSequence) bookmark.get("saveTime"));
		holder.imageId.setText((CharSequence) bookmark.get("imageId"));

		return convertView;
	}
	
	public class ViewHolder {
		public ImageView imagePath;
		public TextView bookMarkName;
		public TextView saveTime;
		public TextView imageId;
	}
}
