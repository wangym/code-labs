package com.shimoda.oa.view;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * 索引View
 * 
 * @author youcai.lu
 * 
 */
public class ContactIndexView extends View {
	// 定义Context的类
	
	// 自定义的一个宽度和高度
	private int width;
	private int height;
	
	// 定义外面ListView所拥有索引值的集合
	private List<Map<String,Object>> data;
	
	private ListView listView;
	
	// 通过构造函数进行赋值
	public ContactIndexView(Context c, ListView listView, List<Map<String,Object>> list, float scale) {
		super(c);
		data = list;
		this.listView = listView;
		width = (int)(20*scale);
		height = (int)(20*scale);
		// 设置允许Touch
		setFocusable(true);
//		setFocusableInTouchMode(true);
	}

	// 重写onTouchEvent方法来实现相应ListView的跳转
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 当发生Touch和移动操作时进行相应ListView的跳转和背景的变化
		// 当手指离开时背景返回原样
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {

			int tempY = (int) ((event.getY() - 6) / height);
			// 判断Touch的位置是否在View的范围内，并且索引值集合内含有所选择的值
			if (tempY < data.size()) {
				if (event.getY() > 0) {
					listView.setSelection((Integer)data.get(tempY).get("start"));
				}
			}
			setBackgroundColor(Color.argb(200, 0, 0, 0));
			return true;
		} else {
			setBackgroundDrawable(null);
			return super.onTouchEvent(event);
		}
	}

	// 当View被实例时首先被运行的方法，早于onDraw方法
	// 给自定义的宽度和高度赋值
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	// 绘制自定义View的内容
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 循环对strArray的内容进行绘制
		for (int i = 0; i < data.size(); i++) {
			// 设置Paint的属性
			Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
			foreground.setFakeBoldText(true);
			foreground.setColor(Color.WHITE);
			foreground.setStyle(Style.FILL);
			foreground.setTextSize(height * 0.6f);
			foreground.setTextAlign(Paint.Align.CENTER);
			FontMetrics fm = foreground.getFontMetrics();
			float x = width / 2;
			float y = height / 2 - (fm.ascent + fm.descent) / 2;

			canvas.drawText((String)data.get(i).get("index"), x, i * height + y + 6, foreground);
		}
	}
}
