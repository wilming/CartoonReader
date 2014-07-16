 package com.comic.kefu;

import org.jivesoftware.smack.util.StringUtils;

import com.appkefu.lib.interfaces.KFInterfaces;
import com.appkefu.lib.service.KFMainService;
import com.appkefu.lib.service.KFSettingsManager;
import com.appkefu.lib.service.KFXmppManager;
import com.appkefu.lib.utils.KFSLog;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
 
 public class kefuService extends Service{
	 private String  mKefuUsername;
    @Override  
    public void onCreate() {  
    	super.onCreate();
		//与admin会话,实际应用中需要将admin替换为真实的客服用户名			
		mKefuUsername = "wilming1";
			
		//设置开发者调试模式，默认为true，如要关闭开发者模式，请设置为false
		KFSettingsManager.getSettingsManager(this).setDebugMode(true);
		//第一步：登录
		KFInterfaces.visitorLogin(this);
    }  
  
   
    
    
    @Override  
    public void onDestroy() {  
    	super.onDestroy();
        unregisterReceiver(mXmppreceiver);
    }  
  
    @Override  
    public void onStart(Intent intent, int startId) {  
    	super.onStart(intent, startId);
		IntentFilter intentFilter = new IntentFilter();
		//监听网络连接变化情况
        intentFilter.addAction(KFMainService.ACTION_XMPP_CONNECTION_CHANGED);
        //监听消息
        intentFilter.addAction(KFMainService.ACTION_XMPP_MESSAGE_RECEIVED);
        //监听客服在线状态通知
        intentFilter.addAction(KFMainService.ACTION_KEFU_ONLINE_CHECK_RESULT);       
        registerReceiver(mXmppreceiver, intentFilter);         
        //Intent intent = new Intent(KFMainService.ACTION_CONNECT);
        //bindService(intent, mMainServiceConnection, Context.BIND_AUTO_CREATE);
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	} 
	
	
	//监听：连接状态、即时通讯消息、客服在线状态
		private BroadcastReceiver mXmppreceiver = new BroadcastReceiver() 
		{
	        public void onReceive(Context context, Intent intent) 
	        {
	            String action = intent.getAction();
	            //监听：连接状态
	            if (action.equals(KFMainService.ACTION_XMPP_CONNECTION_CHANGED))//监听链接状态
	            {
	                updateStatus(intent.getIntExtra("new_state", 0));        
	            }
	            //监听：即时通讯消息
	            else if(action.equals(KFMainService.ACTION_XMPP_MESSAGE_RECEIVED))//监听消息
	            {
	            	//消息内容
	            	String body = intent.getStringExtra("body");
	            	//消息来自于
	            	String from = StringUtils.parseName(intent.getStringExtra("from"));
	            	
	            	KFSLog.d("body:"+body+" from:"+from);
	            }
	            //监听：客服在线状态
	            else if(action.equals(KFMainService.ACTION_KEFU_ONLINE_CHECK_RESULT))
	            {            	
	            	boolean isonline = intent.getBooleanExtra("isonline", false);
	            	if(isonline)
	            	{
	            		//mChatBtn.setText("咨询客服(在线)");
	            	}
	            	else
	            	{
	            		//mChatBtn.setText("咨询客服(离线)");
	            	}
	            }
	        }
	    };
	    
	    

		
		
		
		//根据监听到的连接变化情况更新界面显示
	    private void updateStatus(int status) {

	    	switch (status) {
	            case KFXmppManager.CONNECTED:
	            	KFSLog.d("connected");
	            	//mTitle.setText("微客服(客服Demo)");
	            	
	            	//检测客服在线状态 (必须在登录成功之后才能调用，才有效)
	        		KFInterfaces.checkKeFuIsOnline(mKefuUsername, this);
	        		
	        		//设置昵称，否则在客服客户端 看到的会是一串字符串(必须在登录成功之后才能调用，才有效)
	        		//KFInterfaces.setVisitorNickname("访客1", this);
	                //chatWithKeFu(mKefuUsername);

	                break;
	            case KFXmppManager.DISCONNECTED:
	            	KFSLog.d("disconnected");
	            	//mTitle.setText("客服(未连接)");
	                break;
	            case KFXmppManager.CONNECTING:
	            	KFSLog.d("connecting");
	            	//mTitle.setText("客服(登录中...)");
	            	break;
	            case KFXmppManager.DISCONNECTING:
	            	KFSLog.d("connecting");
	            	//mTitle.setText("客服(登出中...)");
	                break;
	            case KFXmppManager.WAITING_TO_CONNECT:
	            case KFXmppManager.WAITING_FOR_NETWORK:
	            	KFSLog.d("waiting to connect");
	            	//mTitle.setText("客服(等待中)");
	                break;
	            default:
	                throw new IllegalStateException();
	        }
	    }
	
	
    
    
 }