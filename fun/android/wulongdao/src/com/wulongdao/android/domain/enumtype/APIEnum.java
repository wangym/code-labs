/**
 * 
 */
package com.wulongdao.android.domain.enumtype;

/**
 * @author yumin
 * 
 */
public enum APIEnum {

	COMMENT_LIST("http://106.187.53.233/api/comment/list"),
	COMMENT_NEW("http://106.187.53.233/api/comment/new"),
	QUESTION_LIST("http://106.187.53.233/api/question/list"),
	QUESTION_NEW("http://106.187.53.233/api/question/new"),
	UPLOAD_AUDIO("http://106.187.53.233/api/upload/audio"),
	UPLOAD_IMAGE("http://106.187.53.233/api/upload/image"),
	USER_LOGIN("http://106.187.53.233/api/user/login"),
	USER_MODIFY_PASSWORD("http://106.187.53.233/api/user/modifyPass"),
	USER_REGISTER("http://106.187.53.233/api/user/register"),
	USER_UPLOAD_AVATAR("http://106.187.53.233/api/user/avatar");

	/**
	 * 
	 */
	private String url;

	/**
	 * 
	 * @param url
	 */
	APIEnum(String url) {
		this.url = url;
	}

	/**
	 * 
	 * @return
	 */
	public String URL() {
		return url;
	}

}
