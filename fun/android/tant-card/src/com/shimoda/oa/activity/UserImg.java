package com.shimoda.oa.activity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.shimoda.oa.R;
import com.shimoda.oa.service.ContactService;
import com.shimoda.oa.util.BaseActivity;
import com.shimoda.oa.util.Constants;
import com.shimoda.oa.util.EnvironmentUtil;

public class UserImg extends BaseActivity {
	private GestureDetector gestureDetector;

	private Bitmap image;

	private ImageView imgView;

	private float scaleRate = 0f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Bundle bundle = getIntent().getExtras();
		String tantcardId = bundle.getString(Constants.TANT_CARD_ID);
		ContactService contactService = new ContactService(this);
		image = contactService.getUserImgByTantcardId(tantcardId);
		if (null == image) {
			// 返回
			finish();
			return;
		}

		setContentView(R.layout.user_img);

		gestureDetector = new GestureDetector(new SimpleGesture());

		imgView = (ImageView) findViewById(R.id.user_img);
		imgView.setFocusable(true);
		imgView.setClickable(true);
		
		if (EnvironmentUtil.aboveDonut()) {
			imgView.setOnTouchListener(new MulitPointTouchListener());
		}else{
			imgView.setOnTouchListener(new MulitClickListener());
		}

		ImageButton btnClose = (ImageButton) findViewById(R.id.user_img_btn_close);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				goToMenuActivity(true);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		showUserImg();
	}

	private void showUserImg() {
		if (scaleRate == 0) {
			// 初始化，按可视区域的4/5显示
			getSize();
			int w = widthPixels * 4 / 5;
			int h = heightPixels * 4 / 5;

			int iw = image.getWidth();
			int ih = image.getHeight();

			if (iw > w) {
				ih = ih * w / iw;
				iw = w;
			}

			if (ih > h) {
				iw = iw * h / ih;
				ih = h;
			}

			// 计算sizeRate
			scaleRate = (float) (ih * 1.0 / image.getHeight());
		}

		Bitmap bmp = zoomBitmap(image);
		imgView.setImageBitmap(bmp);
	}

	private Bitmap zoomBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();

		matrix.postScale(scaleRate, scaleRate);
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newBmp;
	}

	@SuppressWarnings("unused")
	private Bitmap zoomBitmap(Bitmap bitmap, float dltX, float dltY) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float rateX = Math.abs(dltX / width);
		float rateY = Math.abs(dltY / height);

		if (rateX < rateY) {
			scaleRate = scaleRate * rateX;
		} else {
			scaleRate = scaleRate * rateY;
		}

		if (scaleRate > 1) {
			scaleRate = 1;
		}

		matrix.postScale(scaleRate, scaleRate);
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newBmp;
	}

	private class SimpleGesture extends SimpleOnGestureListener {
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			if (e.getAction() == MotionEvent.ACTION_UP) {
				// 重新计算比率
				if (scaleRate < 1) {
					scaleRate = (float) (scaleRate * 1.5);
					if (scaleRate > 1) {
						scaleRate = 1;
					}
				} else {
					scaleRate = 0;
				}
				showUserImg();
			}
			return true;
		}
	}
	
	/**
	 * @author yumin
	 * 
	 */
	public class MulitClickListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			return gestureDetector.onTouchEvent(event);
		}
	}

	/**
	 * @author yumin
	 * 
	 */
	@SuppressWarnings("unused")
	public class MulitPointTouchListener implements OnTouchListener {

		/**
		 * 
		 */
		private static final String TAG = "Touch";
		private Matrix matrix = new Matrix();
		private Matrix savedMatrix = new Matrix();
		private static final int NONE = 0;
		private static final int DRAG = 1;
		private static final int ZOOM = 2;
		private int mode = NONE;
		private PointF start = new PointF();
		private PointF mid = new PointF();
		private float oldDist = 1f;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView view = (ImageView) v;
			view.setScaleType(ScaleType.CENTER);
			
			//如果触发了双击事件，处理后返回
			if(gestureDetector.onTouchEvent(event)){
				return true;
			}
			
//			dumpEvent(event);

			// 特别说明:ImageView若设的是matrix则不会居中,但不设matrix又不支持手势缩放
			// 故在layout中center,在进行手势前临时改为matrix,目的是同时兼容上述两种情况
			imgView.setScaleType(ScaleType.MATRIX);

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:

				matrix.set(view.getImageMatrix());
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				mode = DRAG;

				break;

			case MotionEvent.ACTION_POINTER_DOWN:

				oldDist = spacing(event);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
				}
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:

				mode = NONE;
				break;

			case MotionEvent.ACTION_MOVE:

				if (mode == DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
				} else if (mode == ZOOM) {

					float newDist = spacing(event);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}

				break;
			}
			view.setImageMatrix(matrix);

			return true;
		}

		private void dumpEvent(MotionEvent event) {

			String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
					"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
			StringBuilder sb = new StringBuilder();
			int action = event.getAction();
			int actionCode = action & MotionEvent.ACTION_MASK;
			sb.append("event ACTION_").append(names[actionCode]);
			if (actionCode == MotionEvent.ACTION_POINTER_DOWN
					|| actionCode == MotionEvent.ACTION_POINTER_UP) {
				sb.append("(pid ").append(
						action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
				sb.append(")");
			}
			sb.append("[");
			for (int i = 0; i < event.getPointerCount(); i++) {
				sb.append("#").append(i);
				sb.append("(pid ").append(event.getPointerId(i));
				sb.append(")=").append((int) event.getX(i));
				sb.append(",").append((int) event.getY(i));
				if (i + 1 < event.getPointerCount()) {
					sb.append(";");
				}
			}
			sb.append("]");
		}

		private float spacing(MotionEvent event) {

			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		private void midPoint(PointF point, MotionEvent event) {

			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}
	}
}