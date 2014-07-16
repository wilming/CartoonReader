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
		//��admin�Ự,ʵ��Ӧ������Ҫ��admin�滻Ϊ��ʵ�Ŀͷ��û���			
		mKefuUsername = "wilming1";
			
		//���ÿ����ߵ���ģʽ��Ĭ��Ϊtrue����Ҫ�رտ�����ģʽ��������Ϊfalse
		KFSettingsManager.getSettingsManager(this).setDebugMode(true);
		//��һ������¼
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
		//�����������ӱ仯���
        intentFilter.addAction(KFMainService.ACTION_XMPP_CONNECTION_CHANGED);
        //������Ϣ
        intentFilter.addAction(KFMainService.ACTION_XMPP_MESSAGE_RECEIVED);
        //�����ͷ�����״̬֪ͨ
        intentFilter.addAction(KFMainService.ACTION_KEFU_ONLINE_CHECK_RESULT);       
        registerReceiver(mXmppreceiver, intentFilter);         
        //Intent intent = new Intent(KFMainService.ACTION_CONNECT);
        //bindService(intent, mMainServiceConnection, Context.BIND_AUTO_CREATE);
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	} 
	
	
	//����������״̬����ʱͨѶ��Ϣ���ͷ�����״̬
		private BroadcastReceiver mXmppreceiver = new BroadcastReceiver() 
		{
	        public void onReceive(Context context, Intent intent) 
	        {
	            String action = intent.getAction();
	            //����������״̬
	            if (action.equals(KFMainService.ACTION_XMPP_CONNECTION_CHANGED))//��������״̬
	            {
	                updateStatus(intent.getIntExtra("new_state", 0));        
	            }
	            //��������ʱͨѶ��Ϣ
	            else if(action.equals(KFMainService.ACTION_XMPP_MESSAGE_RECEIVED))//������Ϣ
	            {
	            	//��Ϣ����
	            	String body = intent.getStringExtra("body");
	            	//��Ϣ������
	            	String from = StringUtils.parseName(intent.getStringExtra("from"));
	            	
	            	KFSLog.d("body:"+body+" from:"+from);
	            }
	            //�������ͷ�����״̬
	            else if(action.equals(KFMainService.ACTION_KEFU_ONLINE_CHECK_RESULT))
	            {            	
	            	boolean isonline = intent.getBooleanExtra("isonline", false);
	            	if(isonline)
	            	{
	            		//mChatBtn.setText("��ѯ�ͷ�(����)");
	            	}
	            	else
	            	{
	            		//mChatBtn.setText("��ѯ�ͷ�(����)");
	            	}
	            }
	        }
	    };
	    
	    

		
		
		
		//���ݼ����������ӱ仯������½�����ʾ
	    private void updateStatus(int status) {

	    	switch (status) {
	            case KFXmppManager.CONNECTED:
	            	KFSLog.d("connected");
	            	//mTitle.setText("΢�ͷ�(�ͷ�Demo)");
	            	
	            	//���ͷ�����״̬ (�����ڵ�¼�ɹ�֮����ܵ��ã�����Ч)
	        		KFInterfaces.checkKeFuIsOnline(mKefuUsername, this);
	        		
	        		//�����ǳƣ������ڿͷ��ͻ��� �����Ļ���һ���ַ���(�����ڵ�¼�ɹ�֮����ܵ��ã�����Ч)
	        		//KFInterfaces.setVisitorNickname("�ÿ�1", this);
	                //chatWithKeFu(mKefuUsername);

	                break;
	            case KFXmppManager.DISCONNECTED:
	            	KFSLog.d("disconnected");
	            	//mTitle.setText("�ͷ�(δ����)");
	                break;
	            case KFXmppManager.CONNECTING:
	            	KFSLog.d("connecting");
	            	//mTitle.setText("�ͷ�(��¼��...)");
	            	break;
	            case KFXmppManager.DISCONNECTING:
	            	KFSLog.d("connecting");
	            	//mTitle.setText("�ͷ�(�ǳ���...)");
	                break;
	            case KFXmppManager.WAITING_TO_CONNECT:
	            case KFXmppManager.WAITING_FOR_NETWORK:
	            	KFSLog.d("waiting to connect");
	            	//mTitle.setText("�ͷ�(�ȴ���)");
	                break;
	            default:
	                throw new IllegalStateException();
	        }
	    }
	
	
    
    
 }