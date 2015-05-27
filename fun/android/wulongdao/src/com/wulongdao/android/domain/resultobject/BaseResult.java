/**
 * 
 */
package com.wulongdao.android.domain.resultobject;

import java.io.Serializable;

/**
 * @author yumin
 * 
 */
public class BaseResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1494358147975163091L;

	/**
	 * 
	 */
	private boolean success = false;
	private int code = -1;
	private String msg = "Exception";

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
		this.setSuccess(200 == code ? true : false);
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
