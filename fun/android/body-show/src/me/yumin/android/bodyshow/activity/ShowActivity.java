/**
 * 
 */
package me.yumin.android.bodyshow.activity;

import me.yumin.android.bodyshow.etc.Constant;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author yumin
 * 
 */
public class ShowActivity extends BaseActivity {

	/**
	 * 
	 */
	private ImageButton btnPost;
	private ImageButton btnReload;

	// ====================
	// override methods
	// ====================

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (!this.isLogged()) {
			this.goToAnyActivity(LoginActivity.class);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);

		initWidget();
		initListener();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			this.callWhetherToExitDialog();
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
	 * 
	 */
	private void initWidget() {

		// 头部标题
		TextView tvTitle = (TextView) findViewById(R.id.topV1TvCenter);
		tvTitle.setText(Constant.TIP_TITLE_SHOW_TA);
		// 左侧发布
		btnPost = (ImageButton) findViewById(R.id.topV1ImgBtnLeft);
		btnPost.setVisibility(Button.VISIBLE);
		btnPost.setBackgroundResource(R.drawable.post_btn);
		// 右侧刷新
		btnReload = (ImageButton) findViewById(R.id.topV1ImgBtnRight);
		btnReload.setVisibility(Button.VISIBLE);
		btnReload.setBackgroundResource(R.drawable.reload_btn);
	}

	/**
	 * 
	 */
	private void initListener() {

		// 发布按钮监听
		btnPost.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用发布逻辑
				btnPostOnClick();
			}
		});
		// 刷新按钮监听
		btnReload.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用发布逻辑
				btnReloadOnClick();
			}
		});
	}

	/**
	 * 
	 */
	private void btnPostOnClick() {

		this.goToAnyActivity(PostActivity.class);
	}

	/**
	 * 
	 */
	private void btnReloadOnClick() {

		// TODO
	}
}
