package org.lc.misc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionUtil {

	public ExceptionUtil() {
	}

	public static String getStack(Throwable e) {
		if (e==null) return "";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(out));
		return out.toString();
	}
}
