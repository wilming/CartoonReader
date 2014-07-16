package com.comic.shelf;
import java.io.File;
import java.util.ArrayList;

import com.appkefu.lib.ui.activity.KFChatActivity;
import com.cartoon.util.Utils;
import com.comic.R;
import com.comic.activity.core.BaseActivity;
import com.comic.activity.core.MainActivity;
import com.comic.download.CustomizedListView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


public class BookShelfActivity extends BaseActivity {
    private GridView bookShelf;
    private ImageView button_right;
    private ImageView button_left;
    private ArrayList<Drawable> data=new ArrayList<Drawable>();
    private ArrayList<String> name=new ArrayList<String>();
    public ShlefAdapter adapter;
    
    @Override
    public void onResume(){
    	super.onResume();
    	chushihua();
    	if(!adapter.equals(null))
       adapter.notifyDataSetChanged();
    }
    
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allActivity.add(this);
        chushihua();        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);       
        button_right=(ImageView) findViewById(R.id.btn_rightTop);
        button_right.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//promptExit(null);	���ؽ���		
				Intent intent = new Intent(BookShelfActivity.this,CustomizedListView.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				BookShelfActivity.this.startActivity(intent);
			}       	
        });
        
        button_left=(ImageView) findViewById(R.id.btn_leftTop);
        button_left.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//promptExit(null);	�ͷ��������	
				chatWithKeFu("wilming1");
			}       	
        });
       
        bookShelf = (GridView) findViewById(R.id.bookShelf);
        adapter=new ShlefAdapter();
        bookShelf.setAdapter(adapter);
        bookShelf.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg2>=data.size()){}else{
					//����������ת,�����arg2�����Ǹ�Mainactivity,����������������
					Log.i("·��",Utils.getSdCardPath()+"/cartoonReader/books/"+name.get(arg2)+"/1.jpg");
					if(Utils.isPic(Utils.getSdCardPath()+"/cartoonReader/books/"+name.get(arg2)+"/1.jpg"))
					{
					getPicPath(Utils.getSdCardPath()+"/cartoonReader/books/"+name.get(arg2));
				  // Toast.makeText(getApplicationContext(), ""+arg2, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
        
        bookShelf.setOnItemLongClickListener(new OnItemLongClickListener() {//����ɾ��
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				promptDelete(name.get(arg2));
				Toast.makeText(getApplicationContext(), ""+name.get(arg2), Toast.LENGTH_SHORT).show();
				return true;
			}
		});
    }
    
    public void promptDelete(String arg){
    	super.promptDelete(this, arg);
    }
    @Override
    public void onStart(){
    	super.onStart();
    	Intent intent = new Intent(this,com.comic.kefu.kefuService.class);
		startService(intent);
    }
    
    class ShlefAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return data.size()+5;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			contentView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.item1, null);
			
			TextView view=(TextView) contentView.findViewById(R.id.imageView1);
			if(data.size()>position){
//				if(position<name.size()){
//				   view.setText((CharSequence) name.get(position));
//				}
				view.setBackgroundDrawable(data.get(position));
			}else{
				//view.setBackgroundDrawable(data.get(0));
				view.setClickable(false);
				view.setVisibility(View.INVISIBLE);
			}
			return contentView;
		}
    	
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.promptExit(this);
		return super.onKeyDown(keyCode, event);
	}
     	
	private void getPicPath(String picPath){
			Intent intent = new Intent(this,MainActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("picPath", picPath+"/1.php");
			intent.putExtras(bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			Log.v("TAG","2���ݵ�MainActivity��picPath:" + picPath);
			this.startActivity(intent);
			//this.finish();
		}
	
	public void chushihua(){
        File file = new File(Utils.getSdCardPath()+"/cartoonReader/books");
        File[] files = file.listFiles();
		if(files != null && files.length > 0){//���ڵ�����
			name.clear();
			data.clear();
			for(int i=0;i < files.length;i++){
				if(files[i].isDirectory()){
					Log.i("�ļ���",files[i].getName());
					//������ļ���cover��ȡ����ͼƬ(data)������(name)
					//Ҳ����˵��ÿ���ļ������и�������������coverͼƬ
					String temppath=Utils.getSdCardPath()+"/cartoonReader/books/"+files[i].getName()+"/cover";
					File temp=new File(temppath);
					File[] temps=temp.listFiles();
					try{
					if(temps[0].isFile()){
						if(Utils.isPic(temps[0].getName())){
							if(!name.contains(files[i].getName())){
					name.add(files[i].getName());//����name
					Drawable d = Drawable.createFromPath(temppath+"/"+temps[0].getName());//dataֵ
					data.add(d);}
						}
					} }catch(Exception e){}
					try{
						if(temps[1].isFile()){
							if(Utils.isPic(temps[1].getName())){
								if(!name.contains(files[i].getName())){
						name.add(files[i].getName());//����name
						Drawable d = Drawable.createFromPath(temppath+"/"+temps[1].getName());//dataֵ
						data.add(d);}
							}
						} }catch(Exception e){}
			         	}
		              }	
	    	       }else{
	    	    	   name.clear();
	    			   data.clear();
	    			   //������͸������
	    			  SharedPreferences times=getSharedPreferences("times", 0);
	    			   if(times.getBoolean("fist_time", true)){
	    				   times.edit().putBoolean("fist_time", false).commit();
	    		        final Dialog dialog = new Dialog(this, R.style.Dialog_Fullscreen);        
	    		        dialog.setContentView(R.layout.sina);
	    		        ImageView iv = (ImageView)dialog.findViewById(R.id.ivNavigater_clickable);
	    		        iv.setOnClickListener(new OnClickListener() {	    					
	    					@Override
	    					public void onClick(View v) {
	    						dialog.dismiss();
	    					}
	    				});
	    		        dialog.show();
	    			   }
	    	       }
                }
		
	  //������ѯ�Ի���
	private void chatWithKeFu(String kefuUsername)
		{
			Intent intent = new Intent(this, KFChatActivity.class);
			intent.putExtra("username", kefuUsername);	//�ͷ��û���
			intent.putExtra("greetings", "���ã�����X��˾С���飬������ʲô���԰�����?");//�ʺ���
			intent.putExtra("title", "��ѯ�ͷ�");//�Ự���ڱ���
			startActivity(intent);
		}
	
}