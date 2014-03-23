/**
 * Copyright (c) 2014. Designed By BaiQiang.
 */
package org.bq.search;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 白强
 * @version 1.0
 */
public class GoogleAjax {
	public static final Logger LOG = LoggerFactory.getLogger(GoogleAjax.class);
	private String responseBody;

	public String search(String keyword, int page) {
		int pageSize = 8;
		// 谷歌搜索结果每页大小为8，start参数代表的是返回结果的开始数
		// 如获取第一页则start=0，第二页则start=10，第三页则start=2，(page-1)*pageSize
		String url = "http://ajax.googleapis.com/ajax/services/search/web?start="
				+ (page - 1) * pageSize + "&rsz=large&v=1.0&q=" + keyword;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			System.out.println(response.getStatusLine());
			System.out.println(response.getEntity());
			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity)
								: null;
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}

			};
			responseBody = httpclient.execute(httpGet, responseHandler);
			System.out.println("----------------------------------------");
			System.out.println(responseBody);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseBody;
	}
}
