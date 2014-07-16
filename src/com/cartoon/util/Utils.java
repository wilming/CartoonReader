package com.cartoon.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import com.comic.xmlparser.note;
import com.comic.xmlparser.service;

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
	
	@SuppressWarnings("resource")
	public static String getFileRead1(String fileName){
		String content = "";
		String path = getSdCardPath() + Constants.TEMPPATH;
		path += fileName;
		File f = new File(path);
		if(f.exists()){
			Log.v(TAG,"ִ��Utils.getFileRead(" + path + ")");
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
		}else{Log.v(TAG,"�ļ�������,·��Ϊ:" + path);}
		if(content.length() > 0){
			return content.substring(0,content.length() - 1);
		}else{
			return content;
		}
	}
	
	public static boolean getShowHistory(String filepath){
		try{
			String picPath = Utils.getFileRead1(filepath+Constants.HISTORY);
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
		Log.i("getpicOder", "path"+path);
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
	
	public static void saveFile1(String fileName,String fileParam,boolean flag) throws IOException{
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
		Log.v(TAG,"prepareSave��pathΪ:" + path);
		File file = new File(path);
		File parent = file.getParentFile();
		if(!parent.exists()){
			Log.v(TAG,"����Ŀ¼��" + parent.mkdirs());
		}
		if(!file.exists()){
			try{
				Log.v(TAG,"�����ļ���" + file.createNewFile());
			}catch(Exception e){
				Log.v(TAG,"�����ļ������쳣,·��Ϊ:" + path + "   " + e.getMessage());
			}
		}
	}

	public static String getImagePath(Map<String,String> imagePosition,LinkedList<String> imageList){
		
		String picPath = null;
		if(imagePosition != null){
			//��ȡ��ǰͼƬ���ڵ�λ��ID
			int position = Integer.valueOf(imagePosition.get("positionId"));
			if(imageList.size() > 0){
				picPath = imageList.get(position);
			}
		}
		if(picPath == null){
			Log.v(TAG,"���·��Ϊ�գ���ʾ������Ϣ");
			Log.v(TAG,"imagePostion�ļ�ֵ��Ϣ:");
			Set<String> keys = imagePosition.keySet();
			Iterator<String> itKey = keys.iterator();
			while(itKey.hasNext()){
				String key = itKey.next();
				String value = imagePosition.get(key);
				String path = imageList.get(Integer.parseInt(value));
				Log.v(TAG,"��Ϊ��" + key + "��ֵΪ��" + value + "��·��Ϊ:" + path);
			}
		}
		Log.v(TAG,"��ȡͼƬ·��Ϊ:" + picPath);
		return picPath;
	}

	/**
	 * ����ͼƬ
	 * @param picPath    ͼƬ·��
	 * @param disWidth   ��Ļ����
	 * @param disHeight  ��Ļ�߶�
	 * @param action     �Ŵ���λͼ
	 * @return
	 */
	public static Bitmap imageZoom(String picPath, int disWidth, int disHeight) {
		Bitmap newBitmap = null;
		if((filePath != null && !picPath.equals(filePath)) || bmpWidth == 0 || bmpHeight == 0){
			BitmapFactory.Options opts = new BitmapFactory.Options();
			//��Ƶ������inSampleSize����Ϊ2������Ҳ�����һ�����⡣������Ŵ������Сʱ��ͼƬ��ͻȻ��ĺ�СȻ��ż����Ŵ������С
			//�ĳ�1֮��������Ϊԭͼ��������MainActivity��setImageView(int)����������߼����⣬����
			opts.inSampleSize = 1;
			bitmap = BitmapFactory.decodeFile(picPath, opts);
			bmpWidth = bitmap.getWidth();
			bmpHeight = bitmap.getHeight();
			scaleWidth = 1;
			scaleHeight = 1;
			filePath = picPath;
		}
		
		//������Ƶ��˵��ͼƬÿ�����ϴ�scaleWidth��scaleHeight�ϳ���0.8����1.2�����������Ļ� ԭͼ�任֮��ͻز�ȥԭͼ��
		//�����ҽ������޸ĳ�ÿ�����ϴλ����ϼӼ�0.2
		double scale = 0;
		if((float)(bmpHeight/bmpWidth) >= (float)(disHeight/disWidth)){
			scale=disHeight/(double)bmpHeight;
		}else{
			scale=disWidth/(double)bmpWidth;
		}
		scaleWidth = (float) (scaleWidth*scale+0.16);
		scaleHeight = (float) (scaleHeight*scale+0.16);
		if(bitmap != null){
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bmpWidth = newBitmap.getWidth();
			bmpHeight = newBitmap.getHeight();
		}
		return newBitmap;
	}
	
	
	
	
	 public static String getFileNameNoEx(String filename) {   
	        if ((filename != null) && (filename.length() > 0)) {   
	            int dot = filename.lastIndexOf('.');
	            if ((dot >-1) && (dot < (filename.length()))) {   
	                return filename.substring(0, dot);   
	            }   
	        }   
	        return filename;   
	    }
	 
	 public static List<note> readXML(String path) throws Exception{
		    FileInputStream inStream = new FileInputStream(path+"/note.xml");
	        List<note> notes = service.readXML(inStream);
	        return notes;
	    }
	 
	 public static String getBookName(String path){
		 List<note> note1=null;
		 try {
			 note1=readXML(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 try{
			 return  note1.get(0).getName();	
		 }catch(Exception e){
			 return  "null";
		 }
			 		 
	 }
	
	
   public static void xuliehua(ArrayList<HashMap<String, String>> songsList) throws Exception, IOException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Utils.getSdCardPath()+Constants.TEMPPATH+"/objectFile.obj"));   
        out.writeObject(songsList);
        out.close();	
	}
	
   public static ArrayList<HashMap<String, String>> fanxuliehua() throws Exception, FileNotFoundException, IOException{
	   ObjectInputStream in = new ObjectInputStream(new FileInputStream(Utils.getSdCardPath()+Constants.TEMPPATH+"/objectFile.obj"));
	   @SuppressWarnings("unchecked")
	ArrayList<HashMap<String, String>> object=( ArrayList<HashMap<String, String>>)in.readObject();
       in.close();
	  return object;   	   
   }
   
   
   
	
	
	
}