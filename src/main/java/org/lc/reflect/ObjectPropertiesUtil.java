package org.lc.reflect;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.lc.model.CKey;

public class ObjectPropertiesUtil {

	public static String METHOD_TYPE_GETTER = "getter";
	public static String METHOD_TYPE_SETTER = "setter";
	
	public ObjectPropertiesUtil() {
	}

	static HashMap<CKey, Method> methodLookup = new HashMap<CKey, Method>();
	
	public static Method getterMethod(Class c, String prop) {
		String cname = c==null ? "null" : c.getName()+"";
		String proper = properCase(prop);
		CKey ckey = new CKey(cname, METHOD_TYPE_GETTER, proper);
		Method mth = methodLookup.get(ckey);
		if (mth==null) {
			String smth = "get" + proper;
			try {
				mth = c.getMethod(smth);
			} catch(Exception e) {
				smth = "is" + proper;
				try {
					mth = c.getMethod(smth);
				} catch(Exception ex) {
					mth = null;
				}
			}
			if (mth!=null) {
				synchronized(methodLookup) {
					methodLookup.put(ckey, mth);
				}
			}
		}
		return mth;
	}
	
	public static Method setterMethod(Class c, String prop, Class argcls) {
		String cname = c==null ? "null" : c.getName()+"";
		String proper = properCase(prop);
		CKey ckey = new CKey(cname, METHOD_TYPE_SETTER, proper);
		Method mth = methodLookup.get(ckey);
		if (mth==null) {
			String smth = "set" + proper;
			try {
				mth = c.getMethod(smth, argcls);
			} catch(Exception e) {
				e.printStackTrace();
			}
			if (mth!=null) {
				synchronized(methodLookup) {
					methodLookup.put(ckey, mth);
				}
			}
		}
		return mth;
	}
	
	public static String properCase(String prop) {
		return prop.substring(0,1).toUpperCase() + prop.substring(1, prop.length());
	}
	

    public static String arrayToString(String[] a, String separator) {
        String result = "";
        if (a!=null&&a.length > 0) {
            for (int i = 0; i < a.length; i++) {
            	if (a[i]!=null)
            		result = result + separator + a[i];
            }
            if (result.startsWith(",")) result = result.substring(1, result.length());
        }
        return result;
    }

    public static String[] parsString(String[] a, String s) {
        
        if (a != null && s != null)
            a = s.split(",");
        return a;

    }
    
    public static void main(String[] args) {
    	System.out.println(ObjectPropertiesUtil.arrayToString(new String[]{null,null,null,null,null,null,null,null,null,null}, ","));
    	System.out.println(ObjectPropertiesUtil.arrayToString(new String[]{"1",null,null,"4",null,null,null,null,null,"10"}, ","));
    	System.out.println(ObjectPropertiesUtil.arrayToString(new String[]{"1","2",null}, ","));
    	System.out.println(ObjectPropertiesUtil.arrayToString(new String[]{null,null,"3","4",null,null,null,null,"9",null}, ","));
    }
}
