package org.lc.model.html;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IElement extends Comparable, Serializable {

	public Map<String, String> getAttributes();
	public List<IElement> getContent();
	public IElement add(IElement e);
	public String getHtml();
	public String getTag();
	public IElement addAttr(String a, String v);
}
