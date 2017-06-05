package org.lc.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtil {
	
	public static final int BUF_SIZE = 4096;
	
	public static void writeFile(String filename, InputStream in) throws Exception {
		if (filename==null || filename.trim().equals("") || in==null) return;
		File file = new File(filename);
		FileOutputStream os = new FileOutputStream(file);
		byte[] buf = new byte[BUF_SIZE];
		int read = 0;
		while ((read=in.read(buf, 0, BUF_SIZE))>0) {
			os.write(buf, 0, read);
		}
		os.flush();
		os.close();
	}

}
