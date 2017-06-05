package org.lc.model;

import java.util.ArrayList;
import java.util.List;

public class CKey implements Comparable {

	List keys= new ArrayList();
	
	public List getKeys() {
		return this.keys;
	}

	public void setKeys(List keys) {
		this.keys = keys;
	}

	public CKey() {
		
	}
	
	public CKey(List keys) {
		for (Object key : keys) {
			this.keys.add(key);
		}
	}
	
	public CKey(Object... args) {
		for(int i=0; i<args.length;i++) {
			this.keys.add(args[i]);
		}
	}

	public int hashCode() {
		int result = 0;
		for (int i=0; i<this.keys.size(); i++) {
			result = result * 31 + this.keys.get(i).hashCode();
		}
		return result;
	}
	
	public int compareTo(Object obj) {
		if (obj==null || !(obj instanceof CKey)) return -1;
		CKey that = (CKey)obj;
		List mykeys = this.getKeys();
		List yourkeys = that.getKeys();
		for (int k=0; k<mykeys.size() && k<yourkeys.size(); k++) {
			if (mykeys.get(k)==null || yourkeys.get(k)==null || !(mykeys.get(k) instanceof Comparable) || !(yourkeys.get(k) instanceof Comparable)) return 0;
			Comparable me = (Comparable)mykeys.get(k);
			Comparable you = (Comparable)yourkeys.get(k);
			int cmp = me.compareTo(you);
			if (cmp!=0) return cmp;
		}
		return 0;
	}
	
	public boolean equals(Object k) {
		if (k==null || !(k instanceof CKey)) return false;
		return this.compareTo(((CKey)k))==0;
	}
	
	public String toString() {
		if (this.getKeys()==null) return "";
		String buf = "";
		for (Object k : this.getKeys()) {
			buf += k + ",";
		}
		return buf;
		
	}
}
