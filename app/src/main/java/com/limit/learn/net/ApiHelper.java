package com.limit.learn.net;

import com.limit.learn.net.api.AccountApi;
import com.limit.learn.net.http.HttpService;

/**
 * 网络请求 API 帮助类
 */
public class ApiHelper {

	private static AccountApi mAccountApiService; // 账户 user

	/**
	 * 获取用户 账号相关 API 接口
	 *
	 * @return 账户 API
	 */
	public static AccountApi getAccountApiService() {
		if (null == mAccountApiService) {
			mAccountApiService = HttpService.instance().getService(AccountApi.class);
		}
		return mAccountApiService;
	}


}
