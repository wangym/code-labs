/**
 * 
 */
package me.yumin.android.bodyshow.activity;

import java.io.File;
import me.yumin.android.bodyshow.etc.Constant;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author yumin
 * 
 */
public class PostActivity extends BaseActivity {

	/**
	 * 
	 */
	private final int VAL_CAMERA_SHOOTING = 0;
	private final int VAL_PHONE_ALBUM = 1;

	/**
	 * 
	 */
	private File file = new File(Constant.SDCARD_FILE_PATH, Constant.UPLOAD_FILE_NAME);
	private ImageButton btnBack;
	private ImageView ivShow;

	// ====================
	// override methods
	// ====================

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (!this.isLogged()) {
			this.goToAnyActivity(LoginActivity.class);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);

		initWidget();
		initListener();

		// 选择来源
		callSelectShowDialog();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			this.goToAnyActivity(ShowActivity.class);
			return true;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	// ====================
	// private methods
	// ====================

	/**
	 * 秀图选择来源
	 * 
	 * @return
	 */
	private void callSelectShowDialog() {

		final CharSequence[] items = { Constant.TIP_CAMERA_SHOOTING, Constant.TIP_PHONE_ALBUM };

		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(Constant.TIP_SELECT);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				switch (item) {
				case VAL_CAMERA_SHOOTING:

					// 相机拍摄
					break;

				case VAL_PHONE_ALBUM:

					// 手机相册
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					intent.putExtra("crop", "true");
					// intent.putExtra("aspectX", 1);
					// intent.putExtra("aspectY", 1); // 强制比例如1:1
					intent.putExtra("output", Uri.fromFile(file));
					intent.putExtra("outputFormat", "PNG");
					startActivityForResult(Intent.createChooser(intent, Constant.TIP_SELECT), item);
					break;

				default:

					break;
				}
			}
		});
		builder.show();
	}

	/**
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (RESULT_OK == resultCode) {

			switch (requestCode) {
			case VAL_CAMERA_SHOOTING:

				break;

			case VAL_PHONE_ALBUM:

				String path = file.getAbsolutePath();
				ivShow.setBackgroundDrawable(Drawable.createFromPath(path));
				break;

			default:

				break;
			}
		}
	}

	/**
	 * 
	 */
	private void initWidget() {

		// 头部标题
		TextView tvTitle = (TextView) findViewById(R.id.topV1TvCenter);
		tvTitle.setText(Constant.TIP_TITLE_SHOW_ME);
		// 左侧返回
		btnBack = (ImageButton) findViewById(R.id.topV1ImgBtnLeft);
		btnBack.setVisibility(Button.VISIBLE);
		btnBack.setBackgroundResource(R.drawable.back_btn);
		// 图显示框
		ivShow = (ImageView) findViewById(R.id.postIvShow);
	}

	/**
	 * 
	 */
	private void initListener() {

		// 返回按钮监听
		btnBack.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用返回逻辑
				btnBackOnClick();
			}
		});
	}

	/**
	 * 返回按钮逻辑
	 */
	private void btnBackOnClick() {

		this.goToAnyActivity(ShowActivity.class);
	}
}
