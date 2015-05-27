package com.shimoda.oa.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * 继承ImageView 实现了双击处理
 *
 */
public class DoubleClickResizeImageView extends AutoAlignImageView
{
    private GestureDetector gestureDetector;
	
    public DoubleClickResizeImageView(Context context,int w,int h)
	{
		super(context,w,h);
	}
    
    /**
     * 处理双击
     */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{	
		super.onTouchEvent(event);
		if(this.gestureDetector!=null){
			if(gestureDetector.onTouchEvent(event)){
				return false;
			}
		}
		
		return true;
	}

	public GestureDetector getGestureDetector() {
		return gestureDetector;
	}

	public void setGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}
}
