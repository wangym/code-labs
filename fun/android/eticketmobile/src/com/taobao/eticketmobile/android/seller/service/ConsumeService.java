/**
 * 
 */
package com.taobao.eticketmobile.android.seller.service;

import me.yumin.android.common.etc.DESede;
import com.taobao.eticketmobile.android.common.api.domain.api.result.BeforeConsumeApiResult;
import com.taobao.eticketmobile.android.common.api.domain.api.result.ConsumeApiResult;
import com.taobao.eticketmobile.android.common.api.domain.param.BeforeConsumeApiParam;
import com.taobao.eticketmobile.android.common.api.domain.param.ConsumeApiParam;
import com.taobao.eticketmobile.android.common.api.top.BeforeConsumeApi;
import com.taobao.eticketmobile.android.common.api.top.ConsumeApi;
import com.taobao.eticketmobile.android.seller.domain.inputobject.ConsumeExecuteInput;
import com.taobao.eticketmobile.android.seller.domain.inputobject.ConsumeQueryInput;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import com.taobao.eticketmobile.android.seller.etc.GlobalVar;

/**
 * @author yumin
 * 
 */
public class ConsumeService {

	/**
	 * 核销查询
	 * 
	 * @param input
	 * @return
	 */
	public static BeforeConsumeApiResult query(ConsumeQueryInput input) {

		BeforeConsumeApiResult apiResult = null;

		if (null != input) {
			try {
				// 参数定义
				String code = input.getCode();
				String mobile = input.getMobile();
				// 接口请求
				BeforeConsumeApiParam apiParam = new BeforeConsumeApiParam();
				apiParam.setAppKey(Constant.APP_KEY);
				apiParam.setEticketToken(getQueryETicketToken(code, mobile));
				apiParam.setSid(GlobalVar.sid);
				apiParam.setTtid(Constant.TTID);
				apiResult = BeforeConsumeApi.request(apiParam);
				// 字段补全
				apiResult.setCode(code);
				apiResult.setMobile(mobile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return apiResult;
	}

	/**
	 * 核销执行
	 * 
	 * @param input
	 * @return
	 */
	public static ConsumeApiResult consume(ConsumeExecuteInput input) {

		ConsumeApiResult apiResult = null;

		if (null != input) {
			try {
				// 参数定义
				String code = input.getCode();
				String mobile = input.getMobile();
				String consumeNum = input.getConsumeNum();
				String eticketToken = getConsumeETicketToken(code, mobile, consumeNum);
				// 接口请求
				ConsumeApiParam apiParam = new ConsumeApiParam();
				apiParam.setAppKey(Constant.APP_KEY);
				apiParam.setEticketToken(eticketToken);
				apiParam.setSid(GlobalVar.sid);
				apiParam.setTtid(Constant.TTID);
				apiResult = ConsumeApi.request(apiParam);
				// 字段补全
				apiResult.setConsumeNum(consumeNum);
				apiResult.setEticketToken(eticketToken);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return apiResult;
	}

	/**
	 * 
	 * @param code
	 * @param mobile
	 * @return
	 */
	public static String getQueryETicketToken(String code, String mobile) {

		String token = null;

		token = String.format(
				"userId:%s,operatorId:%s,code:%s,buyerMobile:%s,bizCode:%s",
				Constant.ET_USER_ID, GlobalVar.userId, code, mobile, Constant.ET_BIZ_CODE);
		token = new String(DESede.encrypt(token, Constant.ET_DES_SECRET));

		return token;
	}

	/**
	 * 
	 * @param code
	 * @param mobile
	 * @param consumeNum
	 * @return
	 */
	public static String getConsumeETicketToken(String code, String mobile, String consumeNum) {

		String token = null;

		token = String.format(
				"userId:%s,operatorId:%s,code:%s,buyerMobile:%s,bizCode:%s,consumeNum:%s,seed:%s",
				Constant.ET_USER_ID, GlobalVar.userId, code, mobile, Constant.ET_BIZ_CODE, consumeNum, getSeed());
		token = new String(DESede.encrypt(token, Constant.ET_DES_SECRET));

		return token;
	}

	/**
	 * 
	 * @return
	 */
	public static String getSeed() {

		String seed = new StringBuffer().append(GlobalVar.imei)
				.append(GlobalVar.imsi).append(System.currentTimeMillis())
				.toString();

		return seed;
	}
}
