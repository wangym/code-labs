package com.shimoda.oa.view;

import android.content.Context;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * 自动对齐边缘的imageview
 *
 */
public class AutoAlignImageView extends ImageView
{
    protected int screenW;
    protected int screenH;
    
    private TranslateAnimation trans; //处理超出边界的动画
    
    public AutoAlignImageView(Context context,int w,int h)
	{
		super(context);
		this.setPadding(0, 0, 0, 0);
		screenW = w;
		screenH = h;
		
	}
    
    public void move(){
    	int disX = 0;
		int disY = 0;
//		System.out.println("screenW:"+screenW);
//		System.out.println("screenH:"+screenH);
//		System.out.println("getHeight:"+getHeight());
//		System.out.println("getWidth:"+getWidth());
//		System.out.println("getTop:"+getTop());
//		System.out.println("getBottom:"+getBottom());
//		System.out.println("getLeft:"+getLeft());
//		System.out.println("getRight:"+getRight());
		
		//横向边界判断
		if(this.getLeft()>0 && this.getRight()>screenW){
			//判断距离，向近的一端对齐
			if(this.getLeft()>this.getRight()-screenW){
				disX = screenW-this.getRight();
			}else{
				disX = 0 - this.getLeft();
			}
		}
		
		if(this.getLeft()<0 && this.getRight()<screenW){
			//判断距离，向近的一端对齐
			if(0-this.getLeft()>screenW-this.getRight()){
				disX = screenW-this.getRight();
			}else{
				disX = 0 - this.getLeft();
			}
		}
		
		if(this.getLeft()<0 && this.getRight()>screenW){
			//判断距离，向近的一端对齐
			if(0-this.getLeft()>this.getRight()-screenW){
				disX = screenW-this.getRight();
			}else{
				disX = 0 - this.getLeft();
			}
		}
		
		//纵向边界判断
		if(this.getTop()>0 && this.getBottom()>screenH){
			//判断距离，向近的一端对齐
			if(this.getTop()>this.getBottom()-screenH){
				disY = screenH-this.getBottom();
			}else{
				disY = 0 - this.getTop();
			}
		}
		
		if(this.getTop()<0 && this.getBottom()<screenH){
			//判断距离，向近的一端对齐
			if(0-this.getTop()>screenH-this.getBottom()){
				disY = screenH-this.getBottom();
			}else{
				disY = 0 - this.getTop();
			}
		}
		
		if(this.getTop()<0 && this.getBottom()>screenH){
			//判断距离，向近的一端对齐
			if(0-this.getTop()>this.getBottom()-screenH){
				disY = screenH-this.getBottom();
			}else{
				disY = 0 - this.getTop();
			}
		}
		
		System.out.println("disX:"+disX+",disY:"+disY);
    	if(disX!=0 || disY!=0)
    	{
    		this.layout(this.getLeft()+disX, this.getTop()+disY, this.getRight()+disX, this.getBottom()+disY);
    		
//    		trans = new TranslateAnimation(disX, 0, disY, 0);
//    		trans.setDuration(500);
//    		this.startAnimation(trans);
    	}
    }
    
	/**
	 * 实现处理拖动
	 */
    public void setPosition(int left,int top,int right,int bottom) {  
    	this.layout(left,top,right,bottom);
    }
}
