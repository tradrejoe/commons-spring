package org.lc.reflect;

import java.lang.reflect.Method;
import java.util.Comparator;

import org.lc.db.interfaces.IDataTableRecord;

public class GenericObjectComparator implements Comparator {

	public static int ASC = 1;
	public static int DESC = -1;
	
	protected Integer col = 0;
	protected Integer dir = ASC;
	protected String[] props;
	protected Class clz;
	
	public Class getClz() {
		return clz;
	}

	public void setClz(Class clz) {
		this.clz = clz;
	}

	public GenericObjectComparator(Integer col, Integer dir, String[] props, Class... clzz) {
		this.col = col;
		this.dir = dir !=null && dir.intValue()==-1 ? -1 : 1;
		this.props = props;
		if (clzz!=null && clzz.length>0) 
			this.clz = clzz[0];
	}

	public GenericObjectComparator(Integer col, String sdir, String[] props, Class... clzz) {
		this.col = col;
		this.dir = sdir!=null && sdir.equalsIgnoreCase("desc") ? -1 : 1;
		this.props = props;
		if (clzz!=null && clzz.length>0) 
			this.clz = clzz[0];		
	}
	
	public GenericObjectComparator(Integer col, String sdir, IDataTableRecord rec, Class... clzz) {
		this(col, sdir, rec.getSortableColumns(), clzz);
		
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof GenericObjectComparator))
			return false;
		GenericObjectComparator that = (GenericObjectComparator)o;
		if (this.getCol()==null || that.getCol()==null || !this.getCol().equals(that.getCol())) return false;
		if (this.getDir()==null || that.getDir()==null || !this.getDir().equals(that.getDir())) return false;
		String[] thisprops = this.getProps();
		String[] thatprops = that.getProps();
		if (thisprops==null || thatprops==null || thisprops.length!=thatprops.length) return false;
		for (int i=0; i<thisprops.length; i++) {
			if (!thisprops[i].equals(thatprops[i])) return false;
		}
		return true;
	}

	Method mth = null;
	String smth = null;
	
	public int compare(Object o1, Object o2) {
		int out = 0;
		if (this.props==null) return 0;
		if (o1==null) {
			out = -1;
		} else if (o2==null) {
			out = 1;
		} else {
			if (this.col>props.length-1) {
				out = -1;
			} else {
				String prop = props[this.col];
				if (prop==null || prop.trim().length()==0) {
					out = -1;
				} else {
					if (mth == null) {
						mth = ObjectPropertiesUtil.getterMethod(this.clz!=null ? this.clz : this.getClass(), prop);
					}
					if (mth==null) {
						out = -1;
					} else {
						Object prop1 = null;
						Object prop2 = null;
						try {
							prop1 = mth.invoke(o1);
						} catch(Exception ex) {
							out = -1;
						}
						try {
							prop2 = mth.invoke(o2);
						} catch(Exception ex) {
							out = 1;
						}
						if (prop1 == null || !(prop1 instanceof Comparable)) {
							out = -1;
						} else if (!(prop2 instanceof Comparable)) {
							out = 1;
						} else {
							out = ((Comparable)prop1).compareTo((Comparable)prop2);
						}
					}
				}
			}
		}
		return out * dir;
	}
	
	public Integer getCol() {
		return col;
	}

	public void setCol(Integer col) {
		this.col = col;
	}

	public Integer getDir() {
		return dir;
	}

	public void setDir(Integer dir) {
		this.dir = dir;
	}

	public String[] getProps() {
		return props;
	}

	public void setProps(String[] props) {
		this.props = props;
	}

}
