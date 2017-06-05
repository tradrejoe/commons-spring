package org.lc.misc;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.lc.model.CKey;


public class JsonUtil {
	static Logger logger = Logger.getLogger(JsonUtil.class);
	
		//TODO: consolidate similar code, perhaps using generics.
	public static String toJsonObject(Object[][] a) {
		String buf = "";
		try {
			if (a==null) return "''";
			buf += "[";
			for (int r = 0; r<a.length-1; r++) {
				buf += "[";
				for (int c=0; c<a[r].length-1; c++) {
					buf += a[r][c] + ",";
				}
				buf += a[r][a[r].length-1] + "],";
			}
			for (int c=0; c<a[a.length-1].length-1; c++) {
				buf += a[a.length-1][c] + ",";
			}
			buf += a[a.length-1][a[a.length-1].length-1] + "]]";
		} catch(Exception e) {
			//e.printStackTrace();
			return "null";
		}
		return buf;		
	}
	public static String toJsonDouble(double [][] a) {
		String buf = "";
		try {
			if (a==null) return "''";
			buf += "[";
			for (int r = 0; r<a.length-1; r++) {
				buf += "[";
				for (int c=0; c<a[r].length-1; c++) {
					buf += a[r][c] + ",";
				}
				buf += a[r][a[r].length-1] + "],";
			}
			for (int c=0; c<a[a.length-1].length-1; c++) {
				buf += a[a.length-1][c] + ",";
			}
			buf += a[a.length-1][a[a.length-1].length-1] + "]]";
		} catch(Exception e) {
			//e.printStackTrace();
			return "null";
			
		}		
		return buf;		
	}	
	
	public static String toJsonArrayObject(Object[] a) {
		String buf = "";
		try {
			if (a==null) return "''";
			buf += "[";
			for (int c=0; c<a.length-1; c++) {
				buf += a[c] + ",";
			}
			buf += a[a.length-1] + "]";
		} catch(Exception e) {
			//e.printStackTrace();
			return "null";
		}		
		return buf;		
	}
	public static String toJsonArrayDouble(double[] a) {
		String buf = "";
		try {
			if (a==null) return "''";
			buf += "[";
			for (int c=0; c<a.length-1; c++) {
				buf += a[c] + ",";
			}
			buf += a[a.length-1] + "]";
		} catch(Exception e) {
			//e.printStackTrace();
			return "null";
		}		
		return buf;		
	}
	public static String toJsonArrayInt(int[] a) {
		String buf = "";
		try {
			if (a==null) return "''";
			buf += "[";
			for (int c=0; c<a.length-1; c++) {
				buf += a[c] + ",";
			}
			buf += a[a.length-1] + "]";
		} catch(Exception e) {
			//e.printStackTrace();
			return "null";
		}		
		return buf;		
	}
	
	static ObjectMapper mapper = new ObjectMapper();
	
	public static String serialize(Object o) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, o);
		} catch(Exception e) {
			logger.error("Jackson ObjectMapper exception", e);
		}
		return out.toString();
	}
	
	public static String toString(CKey key) {
		String buf = new String();
		for (Object k : key.getKeys()) {
			buf += k + ",";
		}
		return buf;
	}
	 
}
