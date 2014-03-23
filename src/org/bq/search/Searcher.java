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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 白强
 * @version 1.0
 * Date:2014年3月23日
 */
public class Searcher extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final Logger LOG = LoggerFactory.getLogger(Searcher.class);

	public String search(String keyword, int page) {
		JSoupBaiduSearcher jSoupBaiduSearcher = new JSoupBaiduSearcher();
		return jSoupBaiduSearcher.search(keyword, page);

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
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String s = request.getParameter("q");
		String keyword = new String(s.getBytes("ISO-8859-1"), "utf-8");
		int page = Integer.valueOf(request.getParameter("page"));
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		String str=this.search(keyword, page);
		out.print(str);
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		LOG.debug("init");
	}

}
