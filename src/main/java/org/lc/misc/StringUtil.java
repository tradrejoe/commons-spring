package org.lc.misc;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.lc.model.CKey;

public class StringUtil {

	public StringUtil() {
	}
	public static String urlEncode(String arg) {
		if (arg==null) return null;
		String tmp = arg;
		try {
			tmp = URLEncoder.encode(arg, "UTF-8");
		} catch(Exception e) {
			tmp = URLEncoder.encode(arg);
		}
		return tmp.replace("+", "%20");
	}
	public static String urlDecode(String arg) {
		if (arg==null) return null;
		String tmp = arg;
		tmp.replace("%20", "+");
		try {
			tmp = URLDecoder.decode(arg, "UTF-8");
		} catch(Exception e) {
			tmp = URLDecoder.decode(arg);
		}
		return tmp;
	}
	
	public static <T,U> String mapToCsv(Map<T, U> m) {
		String buf = "";
		for (Object k : m.keySet()) {
			buf += k + m.get(k).toString() + System.lineSeparator();
		}
		return buf;
	}
	
	public static String repeat(String in, int count) {
		if (in==null || in.equals("") || count < 1) return in;
		String buf = "";
		for (int c=0; c<count; c++) {
			buf += in;
		}
		return buf;
	}
	public static void main(String[] args) {
		Map<CKey, Double> tmp = new HashMap<CKey, Double>();
		CKey k = new CKey("GOOG", 5, 2, "^GSPC");
		tmp.put(k, 0.0123);
		k = new CKey("FB", 5, 2, "^GSPC");
		tmp.put(k, 0.0023);
		k = new CKey("MSFT", 5, 2, "^GSPC");
		tmp.put(k, 0.025);
		System.out.println(mapToCsv(tmp));
	}
}
