package org.lc.db.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.lc.db.interfaces.IDataTableRecord;
import org.lc.reflect.ObjectPropertiesUtil;

public abstract class DataTableRecordImpl implements IDataTableRecord, Serializable {

	public static final long serialVersionUID = 0L;
	
	protected static Map<Class, Method[]> propMetaData = new HashMap<Class, Method[]>(); 
	
	public DataTableRecordImpl() {
		Method[] pmths = propMetaData.get(this.getClass());
		if (pmths==null) {
			synchronized(propMetaData) {
		    	String[] uicols = getUIColumns();
		    	pmths = new Method[uicols.length];
		    	for (int m=0; m<pmths.length; m++) {
		    		try {
		        		String prop = uicols[m];
		        		pmths[m] = ObjectPropertiesUtil.getterMethod(this.getClass(), prop);
		        	} catch(Exception e) {
		        		e.printStackTrace();
		        		pmths[m] = null;
		        	}	
		    	}
		    	propMetaData.put(this.getClass(), pmths);
			}
		}
	}

    public abstract String[] getUIColumns();
    public abstract String[] getSortableColumns();
    
    public Object[] toObjectArray() {
    	Method[] pmths = propMetaData.get(this.getClass());
    	if (pmths==null || pmths.length<1) {
    		return new Object[getUIColumns().length];
    	}
    	Object[] ret = new Object[pmths.length];
    	for (int m=0; m<pmths.length; m++) {
    		try {
    			Method tm = pmths[m];
        		if (tm!=null) ret[m] = tm.invoke(this);
        		if (ret[m]==null) ret[m] = "";
        	} catch(Exception ignore) {
        		ret[m] = "";
        	}
    	}
    	return ret;
    }	
}
