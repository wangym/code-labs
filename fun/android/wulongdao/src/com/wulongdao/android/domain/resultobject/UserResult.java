/**
 * 
 */
package com.wulongdao.android.domain.resultobject;

import com.wulongdao.android.domain.dataobject.UserDO;

/**
 * @author yumin
 * 
 */
public class UserResult extends BaseResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6535623221573570391L;

	/**
	 * 
	 */
	private UserDO userDO;

	/**
	 * @return the userDO
	 */
	public UserDO getUserDO() {
		return userDO;
	}

	/**
	 * @param userDO the userDO to set
	 */
	public void setUserDO(UserDO userDO) {
		this.userDO = userDO;
	}

}
