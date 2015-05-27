/**
 * 
 */
package com.wulongdao.android.adapter;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author yumin
 * 
 */
public class CommentListItem {

	/**
	 * 
	 */
	public ImageView avatar;
	public TextView nickname;
	public TextView message;
	public TextView loadTime;

	/**
	 * 
	 */
	public ImageView getAvatar() {
		return avatar;
	}

	public TextView getNickname() {
		return nickname;
	}

	public TextView getMessage() {
		return message;
	}

	public TextView getLoadTime() {
		return loadTime;
	}

	public void setAvatar(ImageView avatar) {
		this.avatar = avatar;
	}

	public void setNickname(TextView nickname) {
		this.nickname = nickname;
	}

	public void setMessage(TextView message) {
		this.message = message;
	}

	public void setLoadTime(TextView loadTime) {
		this.loadTime = loadTime;
	}

}
