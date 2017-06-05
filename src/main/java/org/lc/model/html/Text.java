package org.lc.model.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Text extends ArrayList implements IElement {

	public static final long serialVersionUID = 0L;
	
	String contents = null;
	
	public Text() {
	}
	
	public Text(String a) {
		super();
		contents = a;
	}

	public int compareTo(Object o) {
		if (o==null) return -1;
		Class thatClass = o.getClass();
		if (!(IElement.class.isAssignableFrom(thatClass))) return -1;
		return this.getHtml().compareTo(((IElement)o).getHtml());
	}

	public Map<String, String> getAttributes() {
		Map<String, String> out = new HashMap<String, String>();
		return out;
	}

	public List<IElement> getContent() {
		ArrayList<IElement> out = new ArrayList<IElement>();
		out.add(this);
		return out;
	}

	public IElement add(IElement e) {
		contents += (e==null ? "" : e.toString());
		return this;
	}
	
	public String getHtml() {
		return contents==null ? "" : contents;
	}

	public String getTag() {
		return "";
	}
	
	public IElement addAttr(String a, String v) {
		return this;
	}
}
