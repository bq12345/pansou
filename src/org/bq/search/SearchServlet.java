/**
 * Copyright (c) 2014. Designed By BaiQiang.
 */
package org.bq.search;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * Date:2014年3月23日
 */
public class SearchServlet extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	
	public static final Logger LOG = LoggerFactory.getLogger(SearchServlet.class);
	private String responseBody;

	public String search(String keyword, int page) {
		int pageSize = 8;
		// 谷歌搜索结果每页大小为8，start参数代表的是返回结果的开始数
		// 如获取第一页则start=0，第二页则start=10，第三页则start=20，以此类推，抽象出模式：(page-1)*pageSize
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return responseBody;
	}
	public SearchServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String s=request.getParameter("q");
		String keyword=new String(s.getBytes("ISO-8859-1"), "utf-8");
		int page=Integer.valueOf(request.getParameter("page"));
		LOG.debug(keyword+"::"+page);
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.print(this.search(keyword+"site:pan.baidu.com", page));
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		LOG.debug("init");
	}

}
