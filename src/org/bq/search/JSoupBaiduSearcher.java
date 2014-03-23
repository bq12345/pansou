/**
 * Copyright (c) 2014. Designed By BaiQiang.
 */
package org.bq.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bq.model.Item;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 白强
 * @version 1.0
 * Date:2014年3月23日
 */
public class JSoupBaiduSearcher {
	private static final Logger LOG = LoggerFactory
			.getLogger(JSoupBaiduSearcher.class);
	List<Item> items = new ArrayList<Item>(10);
	private String responseBody;

	public String search(String keyword, int page) {
		items.clear();
		int pageSize = 10;
		String html = getHtml(keyword, page);
		Document document = Jsoup.parse(html);
		// 获取搜索结果数目
		int total = getBaiduSearchResultCount(document);
		int len = 10;
		if (total < 1) {
			return null;
		}
		// 如果搜索到的结果不足一页
		if (total < 10) {
			len = total;
		}
		for (int i = 0; i < len; i++) {
			Item item = new Item();
			String titleCssQuery = "html body div#wrapper div#container div#content_left div#"
					+ (i + 1 + (page - 1) * pageSize) + " h3 a";
			String contentCssQuery = "html body div#wrapper div#container div#content_left div#"
					+ (i + 1 + (page - 1) * pageSize);
			LOG.debug("titleCssQuery:" + titleCssQuery);
			LOG.debug("contentCssQuery:" + contentCssQuery);
			Element titleElement = document.select(titleCssQuery).first();
			Element contentElement = document.select(contentCssQuery)
					.select("div").first();
			String href = "";
			String titleText = "";
			String content = "";
			if (titleElement != null) {
				titleText = titleElement.text();
				href = titleElement.attr("href");
				item.setTitle(titleText);
				item.setUrl(href);
			}
			if (contentElement != null) {
				content = contentElement.text();
				item.setContent(content);
			}
			LOG.debug("Title:" + titleText);
			LOG.debug("Href:" + href);
			LOG.debug("Content:" + content);
			items.add(item);
		}
		JSONArray array = JSONArray.fromObject(items);
		return array.toString();
	}

	public String getHtml(String keyword, int page) {
		int pageSize = 10;
		String url = "http://www.baidu.com/s?pn=" + (page - 1) * pageSize
				+ "&wd=" + keyword + "%20" + "site:pan.baidu.com" + "&ie=utf-8";
		url = url.replace(" ", "%20");
		LOG.debug("-----------------------------------------" + url);
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

	/**
	 * 获取百度搜索结果数
	 * 
	 * @param document
	 * @return 结果数
	 */
	private int getBaiduSearchResultCount(Document document) {
		String cssQuery = "html body div#wrapper div#container p#page span.nums";
		LOG.debug("total cssQuery: " + cssQuery);
		Element totalElement = document.select(cssQuery).first();
		String totalText = totalElement.text();
		LOG.info("搜索结果文本：" + totalText);
		String regEx = "[^0-9]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(totalText);
		totalText = matcher.replaceAll("");
		int total = Integer.parseInt(totalText);
		LOG.info("搜索结果数：" + total);
		return total;
	}
}