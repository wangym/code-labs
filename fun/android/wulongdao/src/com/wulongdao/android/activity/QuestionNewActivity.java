/**
 * 发布新求助
 */
package com.wulongdao.android.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import com.wulongdao.android.domain.enumtype.APIEnum;
import com.wulongdao.android.domain.resultobject.QuestionResult;
import com.wulongdao.android.etc.Constant;
import com.wulongdao.android.etc.Util;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OutputFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class QuestionNewActivity extends BaseActivity {

	/**
	 * 
	 */
	private ImageButton btnBack;
	private ImageButton btnSubmit;
	private EditText etMessage;
	private ImageView ivImage;
	private ImageButton btnImage;
	private ImageView ivRecord;
	private ImageButton btnRecord;

	/**
	 * 
	 */
	private File fileImage = new File(Constant.PATH_SDCARD, Constant.FILE_IMAGE);
	private File fileAudio = new File(Constant.PATH_SDCARD, Constant.FILE_AUDIO);
	private MediaRecorder recorder = null; // 录音控件对象
	private boolean isRecording = false; // 是否正在录音

	// ====================
	// override methods
	// ====================

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (!isLoggedLocal()) {
			goToActivity(UserLoginActivity.class);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_new);

		initActivity();
		initData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// TODO:保存草稿
			goToIndex();
			return true;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
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
				path = fileImage.getAbsolutePath();
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
				Bitmap bitmap = BitmapUtil.resizeBitmap(path, Constant.PATH_SDCARD_FILE_IMAGE);
				if (null != bitmap) {
					ivImage.setVisibility(View.VISIBLE);
					ivImage.setImageBitmap(bitmap);
					// 图片预览监听
					ivImage.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View view) {
							// 显示图片逻辑
							openFile(fileImage);
						}
					});
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
		tvTitle.setText(getString(R.string.title_question_post));
		// 初始控件
		btnSubmit = (ImageButton) findViewById(R.id.top_v1_ib_left);
		btnSubmit.setVisibility(Button.VISIBLE);
		btnSubmit.setBackgroundResource(R.drawable.send_btn);
		btnBack = (ImageButton) findViewById(R.id.top_v1_ib_right);
		btnBack.setVisibility(Button.VISIBLE);
		btnBack.setBackgroundResource(R.drawable.back_btn);
		etMessage = (EditText) findViewById(R.id.question_post_et_message);
		ivImage = (ImageView) findViewById(R.id.question_post_iv_image);
		btnImage = (ImageButton) findViewById(R.id.question_post_ib_image);
		ivRecord = (ImageView) findViewById(R.id.question_post_iv_record);
		btnRecord = (ImageButton) findViewById(R.id.question_post_ib_record);

		// 提交按钮监听
		btnSubmit.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用提交逻辑
				doSubmit();
			}
		});
		// 返回按钮监听
		btnBack.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用返回逻辑
				doBack();
			}
		});
		// 照片按钮监听
		btnImage.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用照片逻辑
				doImage();
			}
		});
		// 录音按钮监听
		btnRecord.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用录音逻辑
				doRecord();
			}
		});
	}

	/**
	 * 
	 */
	private void initData() {

		CommonUtil.deleteFile(Constant.PATH_SDCARD_FILE_IMAGE);
		CommonUtil.deleteFile(Constant.PATH_SDCARD_FILE_AUDIO);
	}

	/**
	 * 提交逻辑
	 */
	private void doSubmit() {

		// 获取控件内容
		final String message = etMessage.getText().toString();
		// 校验发布参数
		String prompt = validator.forQuestionPost(message);
		if (CommonUtil.isNotEmpty(prompt)) {
			showToast(prompt);
			return;
		}
		// 隐藏虚拟键盘
		hideSoftInputFromWindow(etMessage.getWindowToken());
		//
		progressDialog = ProgressDialog.show(this, "", getString(R.string.prompt_progress_post), true, false);
		new Thread() {
			@Override
			public void run() {
				int code = 0;
				String msg = null;
				// 发布图片
				String image = null;
				if (null != fileImage && fileImage.exists()) {
					try {
						String response = HTTPUtil.postFile(APIEnum.UPLOAD_IMAGE.URL(), Constant.K_FILE, fileImage);
						JSONObject json = CommonUtil.toJSONObject(response);
						if (null != json) {
							code = json.isNull(Constant.K_CODE) ? 0 : json.getInt(Constant.K_CODE);
							msg = json.isNull(Constant.K_MSG) ? null : json.getString(Constant.K_MSG);
							json = json.isNull(Constant.K_RESULT) ? null : json.getJSONObject(Constant.K_RESULT);
							image = (null == json || json.isNull(Constant.K_FILE_PATH) ? null : json.getString(Constant.K_FILE_PATH));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					code = 200;
				}
				// 发布录音
				String audio = null;
				if (null != fileAudio && fileAudio.exists()) {
					try {
						String response = HTTPUtil.postFile(APIEnum.UPLOAD_AUDIO.URL(), Constant.K_FILE, fileAudio);
						JSONObject json = CommonUtil.toJSONObject(response);
						if (null != json) {
							code = json.isNull(Constant.K_CODE) ? 0 : json.getInt(Constant.K_CODE);
							msg = json.isNull(Constant.K_MSG) ? null : json.getString(Constant.K_MSG);
							json = json.isNull(Constant.K_RESULT) ? null : json.getJSONObject(Constant.K_RESULT);
							audio = json.isNull(Constant.K_FILE_PATH) ? null : json.getString(Constant.K_FILE_PATH);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					code = 200;
				}
				// 发布正文
				if (200 == code) {
					QuestionResult result = new QuestionResult();
					try {
						String timestamp = CommonUtil.getTimestamp() + "";
						Map<String, String> map = new HashMap<String, String>();
						if (CommonUtil.isNotEmpty(image)) { map.put(Constant.K_IMAGE, image); }
						if (CommonUtil.isNotEmpty(audio)) { map.put(Constant.K_AUDIO, audio); }
						map.put(Constant.K_MESSAGE, message);
						map.put(Constant.K_USER_ID, getUserId() + "");
						map.put(Constant.K_LONGITUDE, "120.023736");
						map.put(Constant.K_LATITUDE, "30.243585");
						map.put(Constant.K_TIMESTAMP, timestamp);
						map.put(Constant.K_SIGN, Util.getSign(getPassword() + getEmail(), timestamp));
						String response = HTTPUtil.postParameters(APIEnum.QUESTION_NEW.URL(), map);
						JSONObject json = CommonUtil.toJSONObject(response);
						if (null != json) {
							result.setCode(json.isNull(Constant.K_CODE) ? 0 : json.getInt(Constant.K_CODE));
							result.setMsg(json.isNull(Constant.K_MSG) ? null : json.getString(Constant.K_MSG));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (result.isSuccess()) {
						/* 发布成功逻辑 */
						// 跳至信息首页
						goToIndex();
					} else {
						/* 发布失败逻辑 */
						progressMsg = result.getMsg();
					}
				} else {
					progressMsg = msg;
				}
				progressHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * 返回逻辑
	 */
	private void doBack() {

		goToIndex();
	}

	/**
	 * 照片逻辑
	 */
	private void doImage() {

		chooseImage(fileImage);
	}

	/**
	 * 录音逻辑
	 */
	private void doRecord() {

		try {
			if (!isRecording) {
				// 开始录音
				showToast(getString(R.string.prompt_record_start));
				isRecording = true;
				recorder = new MediaRecorder();
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(OutputFormat.RAW_AMR);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				recorder.setOutputFile(Constant.PATH_SDCARD_FILE_AUDIO);
				recorder.prepare();
				recorder.start();
			} else if (isRecording) {
				if (null != recorder) {
					// 结束录音
					showToast(getString(R.string.prompt_record_stop));
					isRecording = false;
					recorder.stop();
					recorder.release();
					// 是否成功
					if (null != fileAudio && fileAudio.exists() && 0 < fileAudio.length()) {
						ivRecord.setVisibility(View.VISIBLE);
						ivRecord.setBackgroundResource(R.drawable.ic_sound);
						// 录音预览监听
						ivRecord.setOnClickListener(new ImageView.OnClickListener() {
							public void onClick(View view) {
								// 播放录音逻辑
								openFile(fileAudio);
							}
						});
					} else {
						// 录音失败
						// TODO
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
