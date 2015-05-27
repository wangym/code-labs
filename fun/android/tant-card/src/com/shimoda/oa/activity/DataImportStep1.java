package com.shimoda.oa.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.shimoda.oa.R;
import com.shimoda.oa.util.BaseActivity;

public class DataImportStep1 extends BaseActivity {
	/**
	 * 菜单对话框
	 */
	private AlertDialog menuDlg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 自定义标题
		callTopBar(R.layout.data_import_step_1, R.layout.top_v2);
		// 设置标题文字
		setTextViewText(R.id.top_v1_tv_center, getString(R.string.title_import));

		// 取db文件列表
		List<File> files = getDbFiles();
		if (!files.isEmpty()) {
			// 根据文件名创建菜单按钮
			int size = files.size();
			// 移除后面的文件
			for (int i = size - 1; i >= 5; i--) {
				files.remove(i);
			}
		}
		initMenu(files);

		// 设置返回按钮的事件
		initReturnButton();
	}

	private void initMenu(List<File> files) {
		menuDlg = new AlertDialog.Builder(DataImportStep1.this).create();
		Window window = menuDlg.getWindow();
		window.setGravity(Gravity.BOTTOM);
		menuDlg.show();

		LayoutInflater factory = LayoutInflater.from(DataImportStep1.this);
		View view = factory.inflate(R.layout.import_menu, null);
		// 设置标题
		setTextViewText(view, R.id.menu_title,
				getString(R.string.import_menu_title));
		// 绑定数据

		ListView listView = (ListView) view.findViewById(R.id.menu_list);
		ArrayList<Map<String, String>> listData = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		if (files != null && !files.isEmpty()) {
			for (File file : files) {
				map = new HashMap<String, String>();
				map.put("name", file.getName());
				map.put("path", file.getAbsolutePath());
				listData.add(map);
			}
		}

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				menuDlg.dismiss();
				
				ListView listView = (ListView) parent;
				HashMap<String, String> map = (HashMap<String, String>) listView
						.getItemAtPosition(position);
				//文件绝对路径
				String path = map.get("path");
				//文件名
				String name = map.get("name");
				
				//跳转到导入第二步
				Intent intent = new Intent();
				intent.putExtra("name", name);
				intent.putExtra("path", path);
				goToAnyActivity(DataImportStep2.class, intent, true);
			}
		});

		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
				listData, R.layout.import_menu_item, new String[] { "name" },
				new int[] { R.id.import_menu_item_btn });
		listView.setAdapter(adapter);

		menuDlg.getWindow().setContentView(view);

		// 设置取消按钮的事件
		initCancelButton(view);
	}

	private void initCancelButton(View view) {
		Button btn = (Button) view.findViewById(R.id.import_menu_btn_cancel);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				menuDlg.dismiss();
				goToMenuActivity(true);
			}
		});
	}

	private void initReturnButton() {
		Button btnBack = (Button) this
				.findViewById(R.id.top_v1_ib_left);
		btnBack.setBackgroundResource(R.drawable.top_back);
		btnBack.setText(R.string.btn_title_back);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				goToMenuActivity(true);
			}
		});
	}

	private List<File> getDbFiles() {
		String dir = getDbDir();
		if (dir == null) {
			return null;
		}

		String extension = ".db";
		File[] files = new File(dir).listFiles();
		List<File> result = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (!f.isFile()) {
				continue;
			}
			// 判断扩展名
			if (f.getPath()
					.substring(f.getPath().length() - extension.length())
					.equals(extension)) {
				result.add(f);
			}
		}
		return result;
	}
}