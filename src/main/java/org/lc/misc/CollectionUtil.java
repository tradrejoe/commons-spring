package org.lc.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtil {

	public static <T> List<T> subset(Collection<T> col, int start, int end) {
		List<T> out = new ArrayList<T>();
		int idx = 0;
		for (T t : col) {
			if (idx>=start && idx < end) {
				out.add(t);
			}
			idx++;
		}
		return out;
	}
}
