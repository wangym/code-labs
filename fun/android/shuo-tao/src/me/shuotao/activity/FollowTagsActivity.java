/**
 * 关注标签
 */
package me.shuotao.activity;

import me.shuotao.etc.Constant;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * @author yumin
 * 
 */
public class FollowTagsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/*
		 * 已登录则跳转
		 */
		if (this.isLogged()) {
			goToAnyActivity(IndexActivity.class);
		}

		this.callCustomTitleBar(R.layout.follow_tags, R.layout.titlebar);
		super.onCreate(savedInstanceState);

		// 控件初始设置
		init();
		// 设置监听事件
		setOnClickListener();
	}

	/**
	 * 
	 */
	private void init() {

		// 获取按钮控件
		initTitlebarControl();

		// 设置按钮标题
		leftBtn.setVisibility(Button.INVISIBLE);
		centerTV.setText(Constant.TITLE_FOLLOW_TAGS);
		rightBtn.setText(Constant.BTN_CONTINUE);

		// 取服务端标签
	}

	/**
	 * 
	 */
	private void setOnClickListener() {

		// 右按钮监听
		rightBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 调用右按钮点击逻辑
				rightBtnOnClick();
			}
		});
	}

	/**
	 * 右按钮点击逻辑
	 */
	private void rightBtnOnClick() {

		goToAnyActivity(IndexActivity.class);
	}

}
