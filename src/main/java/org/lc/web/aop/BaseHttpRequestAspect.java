package org.lc.web.aop;

import javax.servlet.http.HttpServletResponse;

import org.lc.web.filters.RequestAndResponseContextHolder;

public class BaseHttpRequestAspect {

	public BaseHttpRequestAspect() {
	}

	public void process() {
		HttpServletResponse response = RequestAndResponseContextHolder.response();
		if (response!=null) {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			response.setDateHeader("Expires", 0); // Proxies.			
		}
	}
}
