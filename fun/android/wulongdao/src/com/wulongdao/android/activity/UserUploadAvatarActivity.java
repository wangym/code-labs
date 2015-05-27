/**
 * 上传新头像
 */
package com.wulongdao.android.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import com.wulongdao.android.domain.dataobject.UserDO;
import com.wulongdao.android.domain.enumtype.APIEnum;
import com.wulongdao.android.domain.resultobject.UserResult;
import com.wulongdao.android.etc.Constant;
import com.wulongdao.android.etc.Util;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.wulongdao.etc.BitmapUtil;
import android.wulongdao.etc.CommonUtil;
import android.wulongdao.etc.HTTPUtil;

/**
 * @author yumin
 * 
 */
public class UserUploadAvatarActivity extends BaseActivity {

	/**
	 * 
	 */
	private ImageButton btnSkip;
	private ImageView ivImage;

	/**
	 * 
	 */
	private File fileAvatar = new File(Constant.PATH_SDCARD, Constant.FILE_AVATAR);

	// ====================
	// override methods
	// ====================

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (!isLoggedLocal()) {
			goToActivity(UserLoginActivity.class);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_upload_avatar);

		initActivity();
		initData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 屏蔽按键功能
		return true;
	}

	/**
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param intent
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (RESULT_OK == resultCode) {
			String path = null;
			switch (requestCode) {
			case Constant.V_CAMERA_SHOOTING:
				// 相机拍摄
				path = fileAvatar.getAbsolutePath();
				break;
			case Constant.V_PHONE_ALBUM:
				// 手机相册
				path = getPathFromUri(intent.getData());
				if (!CommonUtil.isNotEmpty(path)) {
					path = intent.getData().getPath();
				}
				break;
			default:
				break;
			}

			if (CommonUtil.isNotEmpty(path)) {
				Bitmap bitmap = BitmapUtil.resizeBitmap(path, Constant.PATH_SDCARD_FILE_AVATAR);
				if (null != bitmap) {
					ivImage.setVisibility(View.VISIBLE);
					ivImage.setImageBitmap(bitmap);
					// 图片预览监听
					ivImage.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View view) {
							// 显示图片逻辑
							openFile(fileAvatar);
						}
					});
					if (null != fileAvatar && fileAvatar.exists() && 0 < fileAvatar.length()) {
						// 自动上传逻辑
						progressDialog = ProgressDialog.show(this, "", getString(R.string.prompt_progress_post), true, false);
						new Thread() {
							@Override
							public void run() {
								int code = 0;
								String msg = null;
								// 上传头像
								String avatar = null;
								try {
									String response = HTTPUtil.postFile(APIEnum.UPLOAD_IMAGE.URL(), Constant.K_FILE, fileAvatar);
									JSONObject json = CommonUtil.toJSONObject(response);
									if (null != json) {
										code = json.isNull(Constant.K_CODE) ? 0 : json.getInt(Constant.K_CODE);
										msg = json.isNull(Constant.K_MSG) ? null : json.getString(Constant.K_MSG);
										json = json.isNull(Constant.K_RESULT) ? null : json.getJSONObject(Constant.K_RESULT);
										avatar = (null == json || json.isNull(Constant.K_FILE_PATH) ? null	: json.getString(Constant.K_FILE_PATH));
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								// 更新资料
								if (200 == code) {
									UserResult result = new UserResult();
									try {
										String timestamp = CommonUtil.getTimestamp() + "";
										Map<String, String> map = new HashMap<String, String>();
										map.put(Constant.K_USER_ID, getUserId() + "");
										map.put(Constant.K_AVATAR, avatar);
										map.put(Constant.K_TIMESTAMP, timestamp);
										map.put(Constant.K_SIGN, Util.getSign(getPassword(), getEmail(), timestamp));
										String response = HTTPUtil.postParameters(APIEnum.USER_UPLOAD_AVATAR.URL(), map);
										JSONObject json = CommonUtil.toJSONObject(response);
										if (null != json) {
											result.setCode(json.isNull(Constant.K_CODE) ? 0 : json.getInt(Constant.K_CODE));
											result.setMsg(json.isNull(Constant.K_MSG) ? null : json.getString(Constant.K_MSG));
											result.setUserDO(json.isNull(Constant.K_RESULT) ? null : UserDO.fromJSONObject(json.getJSONObject(Constant.K_RESULT)));
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

									if (result.isSuccess()) {
										/* 上传成功逻辑 */
										// 保存免登文件
										userDO = result.getUserDO();
										storeCookie(userDO);
										// 跳至信息首页
										goToIndex();
									} else {
										progressMsg = result.getMsg();
									}
								} else {
									progressMsg = msg;
								}
								progressHandler.sendEmptyMessage(0);
							}
						}.start();
					}
				}
			}
		}
	}

	// ====================
	// private methods
	// ====================

	/**
	 * 
	 */
	private void initActivity() {

		// 顶部标题
		TextView tvTitle = (TextView) findViewById(R.id.top_v1_tv_center);
		tvTitle.setText(getString(R.string.title_user_avatar));
		// 初始控件
		btnSkip = (ImageButton) findViewById(R.id.top_v1_ib_right);
		btnSkip.setVisibility(Button.VISIBLE);
		btnSkip.setBackgroundResource(R.drawable.next_btn);
		// 上传区域
		ivImage = (ImageView) findViewById(R.id.user_upload_avatar_iv_image);

		// 跳过按钮监听
		btnSkip.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用跳过逻辑
				doSkip();
			}
		});
		// 上传按钮监听
		ivImage.setOnClickListener(new ImageView.OnClickListener() {
			public void onClick(View view) {
				// 调用上传逻辑
				doImage();
			}
		});
	}

	/**
	 * 
	 */
	private void initData() {

		CommonUtil.deleteFile(Constant.PATH_SDCARD_FILE_AVATAR);

		if (!CommonUtil.isNotEmpty(getAvatar())) {
			ivImage.setImageResource(R.drawable.avatar_default_large);
		} else {
			ivImage.setImageBitmap(BitmapUtil.getBitmap(getAvatar() + Constant.IMAGE_SIZE_LIST));
		}
	}

	/**
	 * 跳过逻辑
	 */
	private void doSkip() {

		goToIndex();
	}

	/**
	 * 上传逻辑
	 */
	private void doImage() {

		chooseImage(fileAvatar);
	}

}
