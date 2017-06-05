package org.lc.js;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.lc.misc.StringUtil;

public class Beautifier {

	public Beautifier() {
		// TODO Auto-generated constructor stub
	}
	
	public static final String JS_EXT = ".js";
	public static final String JS_BEAULTIFIED_EXT = ".b.js";
	public static final String JS_MIN = ".min";
	public static final String TAB = "  ";
	
	
	public static void main(String[] args) {
		if (args==null || args.length==0) throw new RuntimeException("No input/output file specified.");
		String infile = args[0];
		String outfile = null;
		if (args.length>1) {
			outfile = args[1];
		} else {
			outfile = infile.replaceAll(JS_MIN, "");
			if (outfile.endsWith(JS_EXT)) 
				outfile = outfile.substring(0, outfile.length()-JS_EXT.length())+JS_BEAULTIFIED_EXT;
		}
		FileInputStream is = null;
		FileOutputStream os = null;
		char pc = (char)-1;
		char cc = (char)-1;
		boolean insideSingleQuote = false;
		boolean insideDoubleQuote = false;
		try {
			is = new FileInputStream(new File(infile));
			os = new FileOutputStream(new File(outfile));
			int lvl = 0;
			int i = -1;
			while((i=is.read())!=-1) {
				cc = (char)i;
				if (cc=='{') {
					os.write('{'); 
					os.write(System.lineSeparator().getBytes());
					lvl++;
					if (lvl<0) lvl=0;
					os.write(StringUtil.repeat(TAB, lvl).getBytes());
				} else if (cc=='}') {
					os.write('}'); 
					os.write(System.lineSeparator().getBytes());
					lvl--;
					if (lvl<0) lvl=0;
					os.write(StringUtil.repeat(TAB, lvl).getBytes());
				} else if (cc==';') {
					os.write(';');
					if (!insideSingleQuote && !insideDoubleQuote) {
						os.write(System.lineSeparator().getBytes());
						os.write(StringUtil.repeat(TAB, lvl).getBytes());
					}
				} else if (cc=='"') {
					os.write('"');
					if (pc!='\\') {
						if (insideDoubleQuote) {
							insideDoubleQuote = false;
						} else {
							insideDoubleQuote = true;
						}
					}
				} else if (cc=='\'') {
					os.write('\'');
					if (pc!='\\') {
						if (insideSingleQuote) {
							insideSingleQuote = false;
						} else {
							insideSingleQuote = true;
						}
					}
				} else {
					os.write(cc);
				}
				pc = cc;
			} //while
		} catch(Exception e) {
			System.err.println(String.format("Cannot beautify file %1$s", infile));
			e.printStackTrace();
		} finally {
			if (is!=null) 
				try {
					is.close();
				} catch(Exception e) {}
			if (os!=null) 
				try {
					os.flush();
					os.close();
				} catch(Exception e) {}
		}
	}

}
