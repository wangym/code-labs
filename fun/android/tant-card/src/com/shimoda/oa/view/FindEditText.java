/**
 * 搜索关键词输入框
 * 特别定制输入框右侧的清除按钮功能
 */
package com.shimoda.oa.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * @author yumin
 * 
 */
public class FindEditText extends EditText {

	/**
	 * 
	 */
	private Drawable drawableRight;
	private Rect rectBounds;

	/**
	 * 
	 * @param context
	 */
	public FindEditText(Context context) {
		super(context);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public FindEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public FindEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 
	 */
	@Override
	public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
		if (right != null) {
			drawableRight = right;
		}
		super.setCompoundDrawables(left, top, right, bottom);
	}

	/**
	 * 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP && drawableRight != null) {
			rectBounds = drawableRight.getBounds();
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			if (x >= (this.getRight() - rectBounds.width()) && x <= (this.getRight() - this.getPaddingRight()) && y >= this.getPaddingTop() && y <= (this.getHeight() - this.getPaddingBottom())) {
				this.getText().clear();
				event.setAction(MotionEvent.ACTION_CANCEL);
			}
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 
	 */
	@Override
	protected void finalize() throws Throwable {

		drawableRight = null;
		rectBounds = null;
		super.finalize();
	}
}
