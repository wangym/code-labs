package com.diaoyumi.android.activity.element;

import com.diaoyumi.android.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class ExtendImageView extends ImageView {
	private int color = R.drawable.black;
	private Bitmap bmp;

	public ExtendImageView(Context context) {
		super(context);
	}

	public ExtendImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public void setBitmap(Bitmap bmp) {
		this.bmp = bmp;
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int borderWidth= 12;
		
		Rect rect = canvas.getClipBounds();
		
		Paint paint = new Paint();
		
		//白框
		paint.setColor(Color.WHITE);
		//左
		canvas.drawRect(new Rect(rect.left, rect.top,rect.left + borderWidth, rect.bottom), paint);
		//右
		canvas.drawRect(new Rect(rect.right - borderWidth, rect.top,rect.right, rect.bottom), paint);
		//上
		canvas.drawRect(new Rect(rect.left, rect.top,rect.right, rect.top + borderWidth), paint);
		//下
		canvas.drawRect(new Rect(rect.left, rect.bottom - borderWidth,rect.right, rect.bottom), paint);
		
		//边
		paint.setColor(Color.rgb(240, 240, 240));
		//上
		canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
	
		//左
		canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
		paint.setColor(Color.rgb(169, 169, 169));
		//右
		canvas.drawLine(rect.right - 1, rect.top, rect.right - 1, rect.bottom, paint);
		//阴影
		paint.setColor(Color.rgb(169, 169, 169));
		//下
		canvas.drawLine(rect.left, rect.bottom - 1, rect.right, rect.bottom - 1, paint);
	}


}
