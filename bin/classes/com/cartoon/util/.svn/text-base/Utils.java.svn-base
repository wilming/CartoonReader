package com.cartoon.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.comic.R;
import com.comic.util.Constants;

public class Utils {
	private static final String TAG = "Utils";
	static Bitmap bitmap;
	static String filePath = null;
	static int bmpWidth = 0;
	static int bmpHeight = 0;
	static float scaleWidth = 1;
	static float scaleHeight = 1;
	
	private static ArrayList<String> img = new ArrayList<String>();
	static{
		String[] simg = new String[]{"png","jpg","jpeg","gif","bmp","tiff"};
		for(String item : simg){
			img.add(item);
		}
	}
	
	public static void showNoPicMsg(Activity activity){
		showMsg(activity,R.string.noPic);
	}
	
	public static void showMsg(Activity activity,Object info){
		if(info instanceof Integer){
			Toast.makeText(activity, (Integer)info, Toast.LENGTH_SHORT).show();
		}else if(info instanceof String){
			Toast.makeText(activity, String.valueOf(info), Toast.LENGTH_SHORT).show();
		}
	}
	
	public static String getSdCardPath(){
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File sdCard = Environment.getExternalStorageDirectory();
			return sdCard.getPath();
		}
		return null;
	}
	
	public static String getFileRead(String fileName){
		String content = "";
		String path = getSdCardPath() + Constants.TEMPPATH;
		path += fileName;
		File f = new File(path);
		if(f.exists()){
			Log.v(TAG,"执行Utils.getFileRead(" + path + ")");
			try{
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine()) != null){
					content += line + ";";
				}
			}catch(Exception e){
				Log.i(TAG,e.getMessage());
			}
		}else{Log.v(TAG,"文件不存在,路径为:" + path);}
		if(content.length() > 0){
			return content.substring(0,content.length() - 1);
		}else{
			return content;
		}
	}
	
	public static boolean getShowHistory(){
		try{
			String picPath = Utils.getFileRead(Constants.HISTORY);
			if(picPath != null && picPath.length() > 0){
				return true;
			}
		}catch(Exception e){
			Log.i(TAG,e.getMessage());
		}
		return false;
	}
	
	public static boolean isPic(String path){
		File file = new File(path);
		String ext = getFileExt(file.getName());
		if(img.contains(ext.toLowerCase())){
			return true;
		}
		return false;
	}
	
	public static String getFileExt(String fileName){
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
	
	public static File[] getPicOrder(String path){
		ArrayList<File> fs = new ArrayList<File>();
		
		File file = new File(path);
		File[] files ;
		if(!file.exists()){
			return null;
		}
		files = file.listFiles();
		Arrays.sort(files,FileComparator.getInstance());
		for(File f:files){
			if(f.isDirectory()){
				fs.add(f);
			}
		}
		for(File f:files){
			if(f.isFile()){
				fs.add(f);
			}
		}
		
		return fs.toArray(new File[]{});
		
	}
	
	public static void saveFile(String fileName,String fileParam,boolean flag) throws IOException{
		String sdCardPath = getSdCardPath();
		String root_temp_path = sdCardPath + Constants.TEMPPATH;
		String filePath = null;
		BufferedWriter bw = null;
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		filePath = root_temp_path;
		filePath += fileName;
		prepareSave(filePath);
		
		if(sdCardPath != null){
			try{
				File file = new File(filePath);
				File parentDir = file.getParentFile();
				if(!parentDir.exists()){
					parentDir.mkdirs();
				}
				fos = new FileOutputStream(file,flag);
				osw = new OutputStreamWriter(fos);
				bw = new BufferedWriter(osw);
				bw.write(fileParam);
				bw.newLine();
				bw.flush();
				file = null;
			}catch(Exception e){
				Log.i("Utils",e.getMessage());
			}finally{
				if(bw != null){
					bw.close();
				}
				if(osw != null){
					osw.close();
				}
				if(fos != null){
					fos.close();
				}
			}
		}
	}
	
	private static void prepareSave(String path) {
		Log.v(TAG,"prepareSave，path为:" + path);
		File file = new File(path);
		File parent = file.getParentFile();
		if(!parent.exists()){
			Log.v(TAG,"创建目录：" + parent.mkdirs());
		}
		if(!file.exists()){
			try{
				Log.v(TAG,"创建文件：" + file.createNewFile());
			}catch(Exception e){
				Log.v(TAG,"创建文件发生异常,路径为:" + path + "   " + e.getMessage());
			}
		}
	}

	public static String getImagePath(Map<String,String> imagePosition,LinkedList<String> imageList){
		
		String picPath = null;
		if(imagePosition != null){
			//获取当前图片所在的位置ID
			int position = Integer.valueOf(imagePosition.get("positionId"));
			if(imageList.size() > 0){
				picPath = imageList.get(position);
			}
		}
		if(picPath == null){
			Log.v(TAG,"获得路径为空，显示调试信息");
			Log.v(TAG,"imagePostion的键值信息:");
			Set<String> keys = imagePosition.keySet();
			Iterator<String> itKey = keys.iterator();
			while(itKey.hasNext()){
				String key = itKey.next();
				String value = imagePosition.get(key);
				String path = imageList.get(Integer.parseInt(value));
				Log.v(TAG,"键为：" + key + "，值为：" + value + "，路径为:" + path);
			}
		}
		Log.v(TAG,"获取图片路径为:" + picPath);
		return picPath;
	}

	/**
	 * 缩放图片
	 * @param picPath    图片路径
	 * @param disWidth   屏幕宽度
	 * @param disHeight  屏幕高度
	 * @param action     放大后的位图
	 * @return
	 */
	public static Bitmap imageZoom(String picPath, int disWidth, int disHeight,
			String action) {
		Bitmap newBitmap = null;
		if((filePath != null && !picPath.equals(filePath)) || bmpWidth == 0 || bmpHeight == 0){
			BitmapFactory.Options opts = new BitmapFactory.Options();
			//视频上这里inSampleSize设置为2。这样也会出现一个问题。当点击放大或者缩小时，图片会突然变的很小然后才继续放大或者缩小
			//改成1之后正常，为原图。可能是MainActivity中setImageView(int)这个方法的逻辑问题，待测
			opts.inSampleSize = 1;
			bitmap = BitmapFactory.decodeFile(picPath, opts);
			bmpWidth = bitmap.getWidth();
			bmpHeight = bitmap.getHeight();
			scaleWidth = 1;
			scaleHeight = 1;
			filePath = picPath;
		}
		
		//这里视频上说让图片每次在上次scaleWidth和scaleHeight上乘以0.8或者1.2。但是这样的话 原图变换之后就回不去原图了
		//所以我将这里修改成每次在上次基础上加减0.2
		double scale = 0;
		if(action.equalsIgnoreCase("small")){
			if(bmpWidth > disWidth / 4 && bmpHeight > disHeight / 4){
				scale = -0.2;
			}
		}else if(action.equalsIgnoreCase("big")){
			if(bmpWidth < disWidth * 2 && bmpHeight < disHeight * 2){
				scale = 0.2;
			}
		}
		scaleWidth = (float) (scaleWidth + scale);
		scaleHeight = (float) (scaleHeight + scale);
		if(bitmap != null){
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Log.v(TAG,"原图宽高：" + bitmap.getWidth() + ":" + bitmap.getHeight());
			newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bmpWidth = newBitmap.getWidth();
			bmpHeight = newBitmap.getHeight();
		}
		return newBitmap;
	}
	
}
