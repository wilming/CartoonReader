package com.comic.adapter;

import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public abstract class GestureListenerAdapter implements  OnGestureListener{
	private static final String TAG = "GestureListenerAdapter";
	private static final double DISTANCE = 150;
	private static final double VELOCTIY = 150;

	public abstract void doOnFling(String action);

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.v(TAG,"onFling时各参数：{" + e1.getX() + ":" + e2.getX() + ":" + velocityX + "}");
		if(e1.getX() - e2.getX() > DISTANCE  && Math.abs(velocityX) > VELOCTIY){
			doOnFling("left");
		}
		if(e1.getX() - e2.getX() < -DISTANCE  && Math.abs(velocityX) > VELOCTIY){
			doOnFling("right");
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
