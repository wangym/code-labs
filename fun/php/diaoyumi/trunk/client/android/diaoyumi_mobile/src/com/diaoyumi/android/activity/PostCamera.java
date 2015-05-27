package com.diaoyumi.android.activity;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.diaoyumi.android.etc.AbstractActivity;
import com.diaoyumi.android.etc.Constant;
import com.diaoyumi.android.etc.Diaoyumi;
import com.diaoyumi.android.etc.Util;

public class PostCamera extends AbstractActivity implements OnClickListener {
	private final int CAMERA_REQUEST_CODE = 10000;
	private final int PICKED_PHOTO_CODE = 10001;

	private Button btnCamera;
	private Button btnAlbum;
	private Button btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.post_camera);

		btnCamera = (Button) findViewById(R.id.btnCamera);
		btnAlbum = (Button) findViewById(R.id.btnAlbum);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		btnCamera.setOnClickListener(this);
		btnAlbum.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			Diaoyumi.go(this, Main.class);
			break;
		case R.id.btnCamera:
			Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Uri uri = Uri.fromFile(new File(Constant.CAMERA_TEMP_FILE));
			intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intentCamera, CAMERA_REQUEST_CODE);
			break;
		case R.id.btnAlbum:
			Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
			albumIntent.setType("image/*");
			startActivityForResult(albumIntent, PICKED_PHOTO_CODE);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean state = false;
		if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
			state = Util.copyFile(Constant.CAMERA_TEMP_FILE, Constant.NEW_JPEG_FILE,true);
		} else if (requestCode == PICKED_PHOTO_CODE && resultCode == RESULT_OK) {
			state = Diaoyumi.saveAlbumPhotoToJpegFile(data, Constant.NEW_JPEG_FILE);
		}
		
		if (state){
			Diaoyumi.putNew("pic", Constant.CAMERA_TEMP_FILE);
			Diaoyumi.go(this, PostLocation.class);
		}else{
			// @TODO 出错暂时返回主界面
			Diaoyumi.go(this, Main.class);
		}
		
	}

}
