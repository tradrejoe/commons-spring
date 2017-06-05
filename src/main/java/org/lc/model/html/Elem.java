package org.lc.model.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Elem implements IElement {

	public static final long serialVersionUID = 0L;
	
	static Set<String> allelm = new TreeSet<String>();
	
	List<IElement> children = null;
	Map<String, String> attr = null;
	String tag = null;
	
	public Elem(String tag) {
		if (tag==null || tag.trim().equals("")) throw new RuntimeException("No tag.");
		this.tag = tag.trim().toLowerCase();
		children = new ArrayList<IElement>();
		attr = new TreeMap<String, String>();
	}

	public int compareTo(Object o) {
		if (o==null) return -1;
		if (!IElement.class.isAssignableFrom(o.getClass())) return -1;
		IElement that = (IElement)o;
		//attributes with sorted keys
		Map<String, String> myatt = this.getAttributes();
		if (myatt==null || myatt.size()==0) return 1;
		Map<String, String> youratt = that.getAttributes();
		for (String key : myatt.keySet()) {
			String ya = youratt.get(key);
			int tmp = ya.compareTo(ya);
			if (tmp!=0) return tmp;
		}
		//contents
		List<IElement> mychld = this.getContent();
		List<IElement> yourchld = that.getContent();
		if (mychld==null || mychld.size()==0 || mychld.size() < yourchld.size()) return 1;
		if (yourchld==null || yourchld.size()==0 || mychld.size() > yourchld.size()) return -1;
		for (int e=0; e<mychld.size(); e++) {
			if (yourchld.size()<e) return -1;
			IElement myelm = mychld.get(e);
			IElement yourelm = yourchld.get(e);
			int tmp = myelm.compareTo(yourelm);
			if (tmp!=0) return tmp;
		}
		return 0;
	}

	public Map<String, String> getAttributes() {
		return attr;
	}

	public List<IElement> getContent() {
		return children;
	}

	public IElement addAttr(String a, String v) {
		if (a==null || a.trim().equals("") || v==null || v.trim().equals("")) return this;
		attr.put(a.trim().toLowerCase(), v);
		return this;
	}
	
	public IElement add(IElement e) {
		if (e==null) return this;
		synchronized(allelm) {
			String addr = e.toString();
			if (!(e instanceof Text) && allelm.contains(addr)) return this;
			children.add(e);
			allelm.add(addr);
		}
		return this;
	}

	public String getHtml() {
		String buf = "<" + tag;
		for (String a : attr.keySet()) {
			buf += " " + a + "='" + attr.get(a) + "'"; 
		}
		if (children.size()==0) return buf + "/>\n";
		buf += ">";
		for (IElement elm : children) {
			buf += elm.getHtml() + "\n";
		}
		return buf + "<" + tag + "/>";
	}
	
	public String getTag() {
		return tag;
	}
}
