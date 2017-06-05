package org.lc.frameworks.spring;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SpringUtils {
	public static HttpSession session() {
	    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	    return attr.getRequest().getSession(true); // true == allow create
	}
	
	public static Object getSessionAttribute(String a) {
		HttpSession session = session();
		return session!=null ? session.getAttribute(a) : null;
	}
	
	public static void setSessionAttribute(String a, Object v) {
		HttpSession session = session();
		if (session!=null) session.setAttribute(a, v);
	}
}
