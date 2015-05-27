/**
 * 
 */
package com.wulongdao.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import com.wulongdao.android.activity.R;
import com.wulongdao.android.etc.Constant;
import com.wulongdao.android.etc.Util;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.wulongdao.etc.BitmapUtil;
import android.wulongdao.etc.CommonUtil;

/**
 * @author yumin
 * 
 */
public class QuestionListAdapter extends BaseAdapter {

	/**
	 * 
	 */
	private LayoutInflater inflater;
	private ArrayList<HashMap<String, Object>> data;

	/**
	 * 
	 * @param context
	 * @param list
	 */
	public QuestionListAdapter(Context context, ArrayList<HashMap<String, Object>> list) {

		inflater = LayoutInflater.from(context);
		data = list;
	}

	@Override
	public int getCount() {

		int count = 0;

		if (null != data) {
			count = data.size();
		}

		return count;
	}

	@Override
	public Object getItem(int position) {

		Object item = null;

		if (null != data) {
			item = data.get(position);
		}

		return item;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = inflater.inflate(R.layout.question_list_item, null);
		QuestionListItem item = new QuestionListItem();
		item.setAvatar((ImageView) convertView.findViewById(R.id.question_list_item_iv_avatar));
		item.setNickname((TextView) convertView.findViewById(R.id.question_list_item_tv_nickname));
		item.setMessage((TextView) convertView.findViewById(R.id.question_list_item_tv_message));
		item.setLoadTime((TextView) convertView.findViewById(R.id.question_list_item_tv_load_time));
		item.setImage((ImageView) convertView.findViewById(R.id.question_list_item_iv_image));
		item.setAudio((ImageView) convertView.findViewById(R.id.question_list_item_iv_audio));
		convertView.setTag(item);

		if (null != data && null != item) {
			String avatar = (String) data.get(position).get(Constant.K_AVATAR);
			String nickname = (String) data.get(position).get(Constant.K_NICKNAME);
			String message = (String) data.get(position).get(Constant.K_MESSAGE);
			String loadTime = Util.getLoadTime((Long) data.get(position).get(Constant.K_LOAD_TIME));
			String image = (String) CommonUtil.getMapValue(data.get(position), Constant.K_IMAGE);
			String audio = (String) CommonUtil.getMapValue(data.get(position), Constant.K_AUDIO);
			if (CommonUtil.isNotEmpty(avatar)) {
				item.getAvatar().setVisibility(View.VISIBLE);
				item.getAvatar().setImageBitmap(BitmapUtil.getBitmap(avatar + Constant.IMAGE_SIZE_LIST));
			}
			item.getNickname().setText(nickname);
			item.getMessage().setText(message);
			item.getLoadTime().setText(loadTime);
			if (CommonUtil.isNotEmpty(image)) {
				item.getImage().setVisibility(View.VISIBLE);
				item.getImage().setImageBitmap(BitmapUtil.getBitmap(image + Constant.IMAGE_SIZE_LIST));
			}
			if (CommonUtil.isNotEmpty(audio)) {
				item.getAudio().setVisibility(View.VISIBLE);
				item.getAudio().setBackgroundResource(R.drawable.ic_sound);
			}
		}

		return convertView;
	}

	/**
	 * 
	 * @param item
	 */
	public void addItem(HashMap<String, Object> item) {

		data.add(item);
	}

}
