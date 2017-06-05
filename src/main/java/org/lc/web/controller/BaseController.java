package org.lc.web.controller;

import java.beans.PropertyEditorSupport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.internal.preferences.Base64;
import org.lc.db.dto.DataTableDTO;
import org.lc.db.interfaces.IDataTableRecord;
import org.lc.model.CKey;
import org.lc.crypto.DesEncrypter;
import org.lc.reflect.GenericObjectComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class BaseController {

	public static final String TASK = "Task";
	public static final String REQUEST = "Request";
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int DEFAULT_SORT_COL = 0;
	public static final String DEFAULT_SORT_DIR = "desc";
	public static final int DEFAULT_START = 0;
	public static final int DEFAULT_LENGTH = 10;
	public static final int DEFAULT_DRAW = 1;
	public static final String CRYPTO_PASSPHRASE = "Pr0vb0rt8l";
	
	static Logger logger = Logger.getLogger(BaseController.class);
	
	static ObjectMapper mapper = new ObjectMapper();
	
	static DesEncrypter cryptoUtil = new DesEncrypter(CRYPTO_PASSPHRASE); 
	
	public static String serialize(Object o) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, o);
		} catch(Exception e) {
			logger.error("Jackson ObjectMapper exception", e);
		}
		return out.toString();
	}
	
	public static Object deserialize(String src, Class valueType) {
		try {
			return mapper.readValue(src, valueType);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final String ROLE_PROVREQ_READ = "PROVREQ_READ";
	public static final String ROLE_PROVREQ_WRITE = "PROVREQ_WRITE";
	public static final String ROLE_PORTAL_PPG_UPDATE = "PORTAL_PPG_UPDATE";
	
	public static final String PROVIDER_PORTAL_PROXY_USER_ID = "prov.portal.proxyUserId";
	public static final String PROVIDER_PORTAL_USER_ID = "prov.portal.userId";
	public static final String PROVIDER_PORTAL_PPG = "prov.portal.pPG";
	public static final String PROVIDER_PORTAL_PPG_NAME = "prov.portal.pPG.name";
	public static final String PROVIDER_PORTAL_PPG_OBJECT = "prov.portal.pPG.object";
	public static final String PROVIDER_PORTAL_USER_ROLE = "prov.portal.user.role";
	public static final String PPG_LOOKUP = "ppg.lookup";
	
	@ModelAttribute("timestamp")
	public String getTimestamp() {
		return (new Date()).getTime()+"";
	}
	
	public static String getRemoteUser() {
		HttpServletRequest request = getRequest();
		return request != null && request.getRemoteUser()!=null ? request.getRemoteUser() : request.getHeader("OAM_REMOTE_USER");
	}
	
	public static HttpServletRequest getRequest() {
		try {
			return ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		} catch(Exception e) {
			return null;
		}
	}
	
	public static Object getParam(String pname, Object... def) {
		Object d = (def.length>0) ? def[0] : null;
		try {
			Object out = getRequest().getParameter(pname);
			if (out==null) return d;
			if (out instanceof Object[]) {
				Object[] aobj = (Object[])out;
				if (aobj.length>0) return aobj[0];
			}
			return out;
		} catch(Exception e) {
			return d;
		}
	}
	
	public static int getIntParam(String pname, int def) {
		try {
			return Integer.parseInt((String)getParam(pname, def));
		} catch(Exception e) {
			return def;
		}
	}

	public static String getStringParam(String pname, String def) {
		try {
			return (String)getParam(pname, def);
		} catch(Exception e) {
			return def;
		}
	}
	
	public static Object getParams(String pname) {
		try {
			Object out = getRequest().getParameter(pname);
			if (out==null) return new Object[]{};
			if (out instanceof Object[]) return out;
			return new Object[]{out};
		} catch(Exception e) {
			return null;
		}
	}
	
	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter(); 
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
	
	public static String getUrlEncodedStackTrace(Throwable t) {
		String tmp = getStackTrace(t);
		try {
			return URLEncoder.encode(tmp, "UTF-8");
		} catch(Exception e) {
			try {
				return URLEncoder.encode(tmp);
			} catch(Exception e2) {
				return null;
			}
		}
	}
	
	public static boolean isLoggedIn() {
		return getRemoteUser()!=null;
	}
	
	public static Object getSessionAttribute(String aname) {
		try {
			Object out = getRequest().getSession(true).getAttribute(aname);
			if (out==null) return null;
			if (out instanceof Object[]) {
				Object[] aobj = (Object[])out;
				if (aobj.length>0) return aobj[0];
			}
			return out;
		} catch(Exception e) {
			return null;
		}		
	}
	
	public static Object[] getSessionAttributes(String aname) {
		try {
			Object out = getRequest().getSession(true).getAttribute(aname);
			if (out==null) return null;
			if (out instanceof Object[]) return (Object[])out;
			return new Object[]{out};
		} catch(Exception e) {
			return null;
		}
	}
	
	public static void clearSessionAttribute(String name) {
		try {
			getRequest().getSession(true).removeAttribute(name);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setSessionAttribute(String aname, Object val) {
		try {
			getRequest().getSession(true).setAttribute(aname, val);
		} catch(Exception e) {
			e.printStackTrace();		}
	}

	public static synchronized Object getContextAttribute(String aname) {
		try {
			Object out = getRequest().getSession(true).getServletContext().getAttribute(aname);
			if (out==null) return null;
			if (out instanceof Object[]) {
				Object[] aobj = (Object[])out;
				if (aobj.length>0) return aobj[0];
			}
			return out;
		} catch(Exception e) {
			return null;
		}		
	}
	
	public static synchronized Object[] getContextAttributes(String aname) {
		try {
			Object out = getRequest().getSession(true).getServletContext().getAttribute(aname);
			if (out==null) return null;
			if (out instanceof Object[]) return (Object[])out;
			return new Object[]{out};
		} catch(Exception e) {
			return null;
		}
	}
	
	public static synchronized void clearContextAttribute(String name) {
		try {
			getRequest().getSession(true).getServletContext().removeAttribute(name);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void setContextAttribute(String aname, Object val) {
		try {
			getRequest().getSession(true).getServletContext().setAttribute(aname, val);
		} catch(Exception e) {
			e.printStackTrace();		}
	}
		
	public Properties getProperty(String filename) {
		Properties props = new Properties();
		InputStream in =  this.getClass().getResourceAsStream("/WEB-INF/" + filename);
    	if(in == null){
    		in = this.getClass().getResourceAsStream(filename);
    	}
    	
    	if(in == null){
    		in = this.getClass().getClassLoader().getResourceAsStream(filename);
    	}
    	
    	if (in==null) {	//in a jar file
    		String pathPkg = this.getClass().getPackage().getName().replace('.', '/') + "/" + filename;
    		
    		in = this.getClass().getResourceAsStream(pathPkg);
   	
	    	if(in == null){
	    		in = this.getClass().getClassLoader().getResourceAsStream(pathPkg);
	    	}
    	}
    	
    	if(in != null){
	    	try{
	    		props.load(in);
	    	}catch(IOException ioe){
	    		logger.warn("Failed to load property file " + filename);
	    	}catch(Exception e){
	    		logger.warn("Failed to load property file. "  + e.getMessage());
	    	}finally{
	    		try{	in.close();		}catch(IOException ioe){	logger.warn("Failed to close inputstream.");	}
	    	}
    	}else{
    		logger.warn(filename + " is not found.");
    	}
    	return props;
	}
	
	
	public static interface IDataTableSource {
		public Object[] getData();
		public org.lc.reflect.GenericObjectComparator getComparator(int col, String dir);
		public int getFieldCount();
	}
	
	protected DataTableDTO getDataTable(IDataTableSource source) {
		
		if (source==null) return null;
		
		int start = getIntParam("start", DEFAULT_START);
		int length = getIntParam("length", DEFAULT_LENGTH);
		int draw = getIntParam("draw", DEFAULT_DRAW);
		int iordercol = getIntParam("order[0][column]", DEFAULT_SORT_COL);
		
		
		String strorderdir = (String)getParam("order[0][dir]", DEFAULT_SORT_DIR);
		if (strorderdir==null || (!strorderdir.equalsIgnoreCase("asc") && !strorderdir.equalsIgnoreCase("desc"))) {
			strorderdir = DEFAULT_SORT_DIR;
		}
		Object[] tmp = source.getData();
		if (tmp==null || tmp.length==0) return new DataTableDTO(draw, 0, 0, new Object[0][]);	
		Arrays.sort(tmp, source.getComparator(iordercol, strorderdir));
		int _s = start >= 0 && start < tmp.length ? start : 0;
		int _l = DEFAULT_PAGE_SIZE;
		if (length==-1) {
			_s = 0;
			_l = tmp.length;
		} else {
			_l = length <= tmp.length ? length : DEFAULT_PAGE_SIZE;
		}
		Object[][] data = new Object[_l][];
		int i=0;
		for (;i<data.length && (_s+i)<tmp.length; i++) {
			data[i] = ((IDataTableRecord)tmp[_s+i]).toObjectArray();
			for (int c=0; c<data[i].length; c++) {
				if (data[i][c] instanceof String && (data[i][c]==null || ((String)data[i][c]).trim().equals(""))) 
					data[i][c] = "&nbsp;";
			}
		}
		
		for (int j=i; j<data.length; j++) {
			data[j] = toEmptyObjectArray(source.getFieldCount());
		}
		return new DataTableDTO(draw, tmp.length, tmp.length, data);		
	}
	
    public static Object[] toEmptyObjectArray(int len) {
    	Object[] ret = new Object[len];
    	for (int m=0; m<len; m++) {
       		ret[m] = "";
    	}
    	return ret;
    }
    
    public static class NullStringEditor extends PropertyEditorSupport {
    	public NullStringEditor() { super(); }
    	public NullStringEditor(Object source) { super(source); }
        public void setAsText(String value) {
            setValue(value==null || value.trim().equals("") ? null : value);  
        }
        public String getAsText() {
        	Object o = this.getValue();
        	return o==null ? null : o.toString();
        }
    }
    
}
