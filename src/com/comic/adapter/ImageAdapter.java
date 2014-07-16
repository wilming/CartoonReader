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
	 * ��ǩ̫���ʱ��getView�����ܿ�,������ViewHolder�����ܲ��ܽ�������⣿
	 * �������ԣ�����������Ҫ������ÿ��getView��Ҫ����SD�����д���bitmap,�ٶȷǳ�������ʹ��ViewHolder���ڿ��������������
	 * ����ʹ��Map�����ع���ͼƬ������ڴ��п��Խ��������⣬���������������ĸ����ڴ棬���Ƿǳ�����������ܺ�
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
		//���Զ�����ǩ�����ļ�����չ
		//��ǩ�����ͼƬ
		//ImageView imagePath = (ImageView) convertView.findViewById(R.id.imagePath);
		//��ǩ�� layout_bookmark_add֮���Ѿ�������bookMarkName���Id�ˣ����������ͻ��
		//TextView bookMarkName = (TextView) convertView.findViewById(R.id.listbookMarkName);
		//��ǩ����ʱ��
		//TextView saveTime = (TextView) convertView.findViewById(R.id.saveTime);
		//��ǩ����ͼƬID
		//TextView imageId = (TextView) convertView.findViewById(R.id.imageId);
		
		//��ͼƬ��СΪ���ʵĴ�С
		Map<String,Object> bookmark = imageList.get(position);
		
		String imagePath = (String) bookmark.get("imagePath");
		Bitmap bmp;
		if(!icons.containsKey(imagePath)){
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			bmp = BitmapFactory.decodeFile(imagePath,opts);
			//opts.inSampleSize = (opts.outWidth / holder.imagePath.getWidth() + opts.outHeight / holder.imagePath.getHeight()) / 2;
			//������Ϊʹ��holder.imagePath.getWidth()��getHeight()�������ֵΪ�㣬��ɳ����쳣������ֻ����д����
			//ԭ�����
			opts.inSampleSize = (opts.outWidth / 90 + opts.outHeight / 90) / 2;
			opts.inJustDecodeBounds = false;
			Log.v(TAG,"��ǩͼƬ��ַ:" + (String) bookmark.get("imagePath"));
			bmp = BitmapFactory.decodeFile(imagePath,opts);
			Log.v(TAG,"�õ���λͼ��С:" + bmp.getWidth() + ":" + bmp.getHeight());
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
