package com.comic.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.cartoon.util.Utils;
import com.comic.R;
import com.comic.activity.ext.CustomListView;
import com.comic.activity.ext.CustomListView.OnRefreshListener;
import com.comic.shelf.BookShelfActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class CustomizedListView extends Activity {
	static final String URL = "http://wilming.sinaapp.com/list.php";
	// XML node keys
	public static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	public static ArrayList<String> yixiazaiList = new ArrayList<String>();
	static final String KEY_SONG = "book"; // parent node
	static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_PAGENUM = "pagenum";
	public static final String KEY_SIZE = "size";
	public static final String KEY_SUMARY = "sumary";
	public static final String KEY_URL = "url";
	public static final String KEY_IMGSRC = "imgsrc";
	
	private static final int LOAD_DATA_FINISH = 10;
	private static final int REFRESH_DATA_FINISH = 11;
	
	public boolean fflag=true;
	private CustomListView mListView;
    LazyAdapter adapter;
    Button button1;
	Activity mActivity;
	long lastUpdatedTime = 0;
	
	private static final int UPDATE_PROGRESS = 1025;
	private static final int UPDATE_VIEW = 1028;
	private static final int DOWNLOAD_COMPLETE = 1026;
	private static final int DOWNLOAD_ERROR=1027;
	private static final int UPDATE_GAP = 1000;// 1 second
		
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_PROGRESS:
				// 视野内的文件 的进度发生改时才会刷新，并且刷新间隔不大于UPDATE_GAP
				// 当ListView中的按钮被按下时刷新界面会出新一些问题，比如按下了第2个按钮，当界面刷新的时候有可能
				// 显示第4个按钮被按下了，所以先作个检查，当有ListView当中的按钮被按下时，不进行刷新
				if (System.currentTimeMillis() - lastUpdatedTime > UPDATE_GAP) {
					adapter.notifyDataSetChanged();
					lastUpdatedTime = System.currentTimeMillis();
				}
				break;
				
			case DOWNLOAD_ERROR:
				songsList.get(msg.arg1).put("flag","0");
				adapter.notifyDataSetChanged();
			    break;
			    
			case UPDATE_VIEW:
				adapter.notifyDataSetChanged();
			     break;
			     
			case DOWNLOAD_COMPLETE:
				songsList.get(msg.arg1).put("flag","2");
				songsList.get(msg.arg1).put("progress", "100");
				adapter.notifyDataSetChanged();
				Toast.makeText(mActivity, songsList.get(msg.arg1).get("name") +"下载完成",Toast.LENGTH_SHORT).show();
				break;
				
			case REFRESH_DATA_FINISH:				
				if(adapter!=null){
					adapter.notifyDataSetChanged();
				}
				mListView.onRefreshComplete();	//下拉刷新完成
				break;
				
			default:
				break;
			}
		}
	};    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);
		mActivity=this;
		button1=(Button) findViewById(R.id.fanhui2);
		 button1.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					//其实如果有正在下载的任务怎么办？
					Intent intent = new Intent(CustomizedListView.this,BookShelfActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					CustomizedListView.this.startActivity(intent);
					mActivity.finish();
				}       	
	        });
		 		 
		//反序列化得到数据，
		 try {
			  songsList=com.cartoon.util.Utils.fanxuliehua();
			   System.out.println(com.cartoon.util.Utils.fanxuliehua());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				//刷新url
				getDatafromURL();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}		
		////////////////////////////////////////////////////////////////////////
	    //扫描文件夹看有哪些电子书是已经下载的,将sonsList中显示为以完成，但实际不存的书删了
		removeDuplicateWithOrder();
	    System.out.println(yixiazaiList);	    	    
		gengxin();

		//list=(ListView)findViewById(R.id.list);
		mListView = (CustomListView) findViewById(R.id.mListView);		
		// Getting adapter by passing xml data ArrayList
        adapter=new LazyAdapter(this, songsList);        
        mListView.setAdapter(adapter);
        // Click event for single list row
        
        mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				vibrator.vibrate(40);
				adapter.currentPosition = position;
				adapter.notifyDataSetChanged();
			}
		});	
        		
		mListView.setonRefreshListener(new OnRefreshListener() {			
			@Override
			public void onRefresh() {
				loadData(0);
			}
		});
	}
	
	@Override
	public void onPause(){
		super.onPause();
		try {
			gengxin();
			com.cartoon.util.Utils.xuliehua(songsList);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onResume(){
		super.onResume();
		gengxin();
		if(!adapter.equals(null))
			adapter.notifyDataSetChanged();
	}
	
    public void gengxin(){
    	chushihuayixiazai();
    	if(!(songsList.equals(null))&&!(yixiazaiList.equals(null))){
    	for(int i=0; i<songsList.size();i++){
 		   if(!yixiazaiList.contains(songsList.get(i).get(KEY_NAME))){
 			   System.out.println(songsList.get(i).get(KEY_NAME)+"不包含");
 			     if(songsList.get(i).get("flag").equals("2")){
 				       songsList.get(i).put("flag","0");
 				       songsList.get(i).put("progress","0");
 			        }   
 		   }else{
 			   songsList.get(i).put("flag","2");
 			   songsList.get(i).put("progress","100");
 			   System.out.println(songsList.get(i).get(KEY_NAME)+"包含");
 		   }			
 		 }
       }
    }
	
	//刷新后重新处理数据，序列化在此做
	public void loadData(final int type){
		new Thread(){
			@Override
			public void run() {
				//从url获取数据，然后更新
				getDatafromURL();				
				try {
					System.out.println(songsList);
					com.cartoon.util.Utils.xuliehua(songsList);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(type==0){	//下拉刷新
					mHandler.sendEmptyMessage(REFRESH_DATA_FINISH);
				}else if(type==1){
					mHandler.sendEmptyMessage(LOAD_DATA_FINISH);
				}
				
			}
		}.start();
	}
				
	public class DownloadThread extends Thread {
		int index;
		int flag=1;
		private static final int UPDATE_PROGRESS = 1025;
		private static final int DOWNLOAD_COMPLETE = 1026;
		private static final int UPDATE_GAP = 1000;// 1 second
	    String path=com.cartoon.util.Utils.getSdCardPath()+"/cartoonReader/books/";
	    String url;
	    String fName;
	    DownLoaderTask task;
	    Message msg;
		
		DownloadThread(int index){
			this.index = index;
			url=CustomizedListView.songsList.get(index).get("url");
			task = new DownLoaderTask(url,path);
		}

		@Override
		public void run() {
			System.out.println(index+":"+url);
			task.execute();	
			int k=new Integer(CustomizedListView.songsList.get(index).get("progress"));
			while ((flag==1)&&(k < 100)) {
				flag=new Integer(songsList.get(index).get("flag"));//这句不能删吧
				k=task.progress;
				System.out.println("目前进度:"+k);
				CustomizedListView.songsList.get(index).put("progress", k+"");										
				msg = mHandler.obtainMessage();
				msg.what = UPDATE_PROGRESS;
				msg.arg1 = this.index;
				msg.sendToTarget();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(flag!=1){ 
					msg = mHandler.obtainMessage();
					msg.what = DOWNLOAD_ERROR;
					msg.arg1 = this.index;
					msg.sendToTarget();
					task.cancel(true);
					break;
				}
			}
			if(flag==1){
			// download is complete and we should notify the user.
			Message msg = mHandler.obtainMessage();
			msg.what = DOWNLOAD_COMPLETE;
			msg.arg1 = this.index;
			msg.sendToTarget();
			// there is a chance that mHandler din't update our progressbar for
			// us if this is the last to finish
			// just to make sure that the last finished file's progress get updated
			Message finalUpdate = mHandler.obtainMessage();
			finalUpdate.what = UPDATE_PROGRESS;
			mHandler.sendMessageDelayed(finalUpdate, UPDATE_GAP + 100);
	    	}
			//等解压线程做完了再删除以下
//			File mInput = new File(path+fName);
//			if(mInput.exists()){
//			   mInput.delete();
//			 }
		}
		
	}
   	
	public class LazyAdapter extends BaseAdapter {
	    public int flag=0; //0表示"下载"，1表示"暂停" 2表示"完成"
	    private Activity activity;
	    private ArrayList<HashMap<String, String>> data;
	    private LayoutInflater inflater=null;
	    public ImageLoader imageLoader; 
	    public int currentPosition=-1;	    	    
	    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
	        activity = a;
	        data=d;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        imageLoader=new ImageLoader(activity.getApplicationContext());
	     }
	    public int getCount() {
	        return data.size();
	    }
	    public Object getItem(int position) {
	        return position;
	    }
	    public long getItemId(int position) {
	        return position;
	    }
	    
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View vi=convertView;
	        if(convertView==null)
	        vi = inflater.inflate(R.layout.list_row, null);
	        TextView title = (TextView)vi.findViewById(R.id.title); // title
	        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
	        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
	        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
	        HashMap<String, String> book = new HashMap<String, String>();
	        book = data.get(position); 
	        //其中有一个重要的东西就是url
	        // Setting all values in listview
	        title.setText(book.get(CustomizedListView.KEY_NAME));
	        if(book.get(CustomizedListView.KEY_SUMARY).length()>29){
	           artist.setText(book.get(CustomizedListView.KEY_SUMARY).substring(0, 27)+"...");
	        }else{
	           artist.setText(book.get(CustomizedListView.KEY_SUMARY)+"...");
	        }
	        duration.setText(Html.fromHtml("<html><body>"+book.get(CustomizedListView.KEY_PAGENUM)
	        		+"<font face=\"Times\" color=\"#00bbaa\"><small><small>元/㎡</small></small></font></body></html>"));
	        imageLoader.DisplayImage(book.get(CustomizedListView.KEY_IMGSRC), thumb_image);
	        
	        
			if (position == currentPosition-1) {
				vi.findViewById(R.id.layout_other).setVisibility(View.VISIBLE);
				vi.findViewById(R.id.layout_other).setClickable(true);
				((ProgressBar) vi.findViewById(R.id.downloadProgress)).setProgress(new Integer(book.get("progress")));
				Button button=(Button) vi.findViewById(R.id.download_button);
				flag=new Integer(book.get("flag"));
				if(flag==0){
					button.setText("下载");
					System.out.println(position);
				}else if(flag==1){
					button.setText("取消");
				} else if(flag==2){
					button.setText("删除");
					//button.setEnabled(false);
					//button.setClickable(false);
				}else{
					System.out.println(flag);
					button.setText("错误");
				}
				
				final int newposition=position;
				button.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						if(flag==0){//开始线程下载
							songsList.get(newposition).put("flag","1");
							new DownloadThread(newposition).start();
						}else if(flag==1){//断掉线程
							songsList.get(newposition).put("flag","0");
						}else if(flag==2){//删除
							Delete(songsList.get(newposition).get(CustomizedListView.KEY_NAME).toString());
							songsList.get(newposition).put("flag","0");
							songsList.get(newposition).put("progress", "0");
							Message msg = mHandler.obtainMessage();
							msg.what = UPDATE_VIEW;
							msg.sendToTarget();
						}else{}					
					}					
				});
				vi.findViewById(R.id.layout_other).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						currentPosition = -1;
					}
				});				
			}else {
				vi.findViewById(R.id.layout_other).setVisibility(View.GONE);
				vi.findViewById(R.id.layout_other).setClickable(false);
			}         
			  return vi;

	    }

	}
	
	public boolean getDatafromURL(){
		System.out.print("进入到getDatafromURL");
		//以下是只有在刷新时才需要做的，不刷新就只要反序列化
		try{
				XMLParser parser = new XMLParser();
				String xml = parser.getXmlFromUrl(URL); // getting XML from URL
				System.out.print(xml);
				Document doc = parser.getDomElement(xml); // getting DOM element		
				NodeList nl = doc.getElementsByTagName(KEY_SONG);
				// looping through all song nodes <song>
				for (int i = 0; i < nl.getLength(); i++) {
					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();
					Element e = (Element) nl.item(i);
					// adding each child node to HashMap key => value			
					map.put(KEY_ID, parser.getValue(e, KEY_ID));
					map.put(KEY_NAME, parser.getValue(e, KEY_NAME));
					map.put(KEY_PAGENUM, parser.getValue(e, KEY_PAGENUM));
					map.put(KEY_SIZE, parser.getValue(e, KEY_SIZE));
					map.put(KEY_SUMARY, parser.getValue(e, KEY_SUMARY));
					map.put(KEY_URL, parser.getValue(e, KEY_URL));
					map.put(KEY_IMGSRC, parser.getValue(e, KEY_IMGSRC));
					map.put("progress","0");
					map.put("flag", "0");//这个表示状态
					// adding HashList to ArrayList
					
					for(HashMap<String, String> list:songsList){
						if(list.get(KEY_NAME).equals(map.get(KEY_NAME))){
						   fflag=false;
							break;
						     }else{fflag=true;}	
					      }
					if(fflag){
					   songsList.add(0,map);
					   fflag=true;
					}
				}
				System.out.println("离开了");
				return true;
			
		 }catch(Exception e){
			    return false;
		 }
	}
		
	public void chushihuayixiazai(){
		String path=com.cartoon.util.Utils.getSdCardPath()+"/cartoonReader/books";
		File file = new File(path);
		File[] files ;
		if(!file.exists()){
			return;
			}
		files = file.listFiles();
		yixiazaiList.clear();
		for(File f:files){
			if(f.isDirectory()){
				yixiazaiList.add(com.cartoon.util.Utils.getBookName(f.getAbsolutePath()).toString());
			}
		}		 
	}
	
	public void  removeDuplicateWithOrder(){ 
	      Set<HashMap<String, String>> set  =   new  HashSet<HashMap<String, String>>(); 
	      ArrayList<HashMap<String, String>> newList  =   new  ArrayList<HashMap<String, String>>(); 
	   for  (Iterator<HashMap<String, String>> iter  =  songsList.iterator(); iter.hasNext();)   { 
	         HashMap<String, String> element  =  iter.next(); 
	         if(set.add(element)) 
	           newList.add(element); 
	     } 
	     songsList.clear(); 
	     songsList.addAll(newList); 
	} 

	public void Delete(String fileName){		
        File file = new File(Utils.getSdCardPath()+"/cartoonReader/books");
        File[] files = file.listFiles();
		if(files != null && files.length > 0){//存在电子书
			for(int i=0;i < files.length;i++){
				if(files[i].isDirectory()){
//					Log.i("文件夹",files[i].getName());
//					System.out.println("遍历的文件夹："+Utils.getBookName(files[i].getAbsolutePath()));
					if(Utils.getBookName(files[i].getAbsolutePath()).toString().equals(fileName))
					{
						deleteDirectory(files[i].getAbsolutePath());
					    break;
					}
			         	}
		              }	
	    	       }
                }
	
    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        Boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
}