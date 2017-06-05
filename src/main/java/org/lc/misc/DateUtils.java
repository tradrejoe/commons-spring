package org.lc.misc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	public static final SimpleDateFormat sdfmysql = new SimpleDateFormat("yyyy-MM-dd");
	
	public DateUtils() {
	}
	
	public static Date getDateNotime() {
		Date dt = GregorianCalendar.getInstance().getTime();
		String sdt = sdf.format(dt);
		try {		
			return sdf.parse(sdt);
		} catch(Exception e) {
			return dt;
		}
	}
	
	public static String getDateMysql(Date dt) {
		return sdfmysql.format(dt);
	}
	
	public static Integer getDiffDays(Date d0, Date d1) {
		
		return (int)((d1.getTime() - d0.getTime()) / (60 * 60 * 1000 * 24));
		
	}
	
	public static Integer getDiffTradingDays(Date d0, Date d1) {
		if (d0==null || d1==null || d0.after(d1)) return 0;
		Calendar c0 = GregorianCalendar.getInstance();
		c0.setTime(d0);
		Calendar c1 = GregorianCalendar.getInstance();
		c1.setTime(d1);
		int out = 0;
		while(c0.before(c1)) {
			c0.add(Calendar.DATE, 1);
			if (c0.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && c0.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) out++;
		}
		return out;
		
	}	

}
