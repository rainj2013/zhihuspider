package top.rainj2013.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Strings;
import org.nutz.log.Logs;

public class DateUtil {
	public static final int DAY = 1000 * 60 * 60 * 24;
	public static final double DAY2 = 1000 * 60 * 60 * 24;
	private static Map<String, DateFormat> formats = new HashMap<String, DateFormat>();
	private static final String DEFAULT = "yyyy/MM/dd HH:mm";
	private static final String DEFAULT2 = "yyyy-MM-dd HH:mm";

	private static final String DAYFORMAT = "yyyy-MM-dd";

	public static Date parse(String date, String pattern) {
		if (Strings.isBlank(date))
			return null;
		try {
			return getFormat(pattern).parse(date);// getFormat(pattern).parse(date);
		} catch (ParseException e) {
			// e.printStackTrace();
			Logs.get().errorf("date parse error: %s-->%s", date, pattern);
			return null;
		}
	}

	public static String format(Date date) {
		return format(date, DEFAULT);
	}

	public static Date parse(String date) {
		if (Strings.isBlank(date))
			return null;
		try {
			return getFormat(DEFAULT).parse(date);
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				return getFormat(DEFAULT2).parse(date);
			} catch (ParseException e1) {
				// e1.printStackTrace();
				Logs.get().errorf("date parse error: %s-->(%s,%s)", date,
						DEFAULT, DEFAULT2);
				return null;
			}
		}
	}

	public static String format(Date date, String pattern) {
		return getFormat(pattern).format(date);
	}

	protected static DateFormat getFormat(String pattern) {
		DateFormat df = formats.get(pattern);
		if (df == null) {
			// 还是支持一下并发比较安全……
			synchronized (formats) {
				if ((df = formats.get(pattern)) == null) {
					df = new SimpleDateFormat(pattern);
					// df.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
					formats.put(pattern, df);
				}
			}
		}
		return (DateFormat) df.clone();
	}

	public static Date parseAll(String date, String... patterns) {
		for (String p : patterns) {
			Date d = parse(date, p);
			if (d != null)
				return d;
		}
		return null;
	}

	public static double dayDiff(Date d1, Date d2) {
		long timeStamp = d1.getTime() - d2.getTime();
		return Math.abs(timeStamp / DAY2);
	}

	public static boolean isSame(Date d1, Date d2) {
		if (d1 == null || d2 == null)
			return false;
		String date1 = getFormat("yyyy-MM-dd").format(d1);
		String date2 = getFormat("yyyy-MM-dd").format(d2);
		return Strings.equals(date1, date2);
	}

	/**
	 * 得到几天前的时间
	 */

	public static Date getDateBefore(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		return now.getTime();
	}

	/**
	 * 得到几天后的时间
	 */

	public static Date getDateAfter(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}

	public static Date toDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}

	public static Date tomorrow(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}

	public static Date yesterday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	public static List<Date> rangeDay(Date start, Date end) {
		if (start.after(end))
			throw new IllegalArgumentException("start after end");
		start = DateUtil.toDay(start);
		end = DateUtil.toDay(end);
		List<Date> dates = new ArrayList<Date>();
		Date d = start;
		while (!d.after(end)) {
			dates.add(d);
			d = DateUtil.tomorrow(d);
		}
		return dates;
	}

	public static boolean checkBetween(Date checkDate, Date start, Date end) {
		if (start != null) {
			if (checkDate.before(start))
				return false;
		}
		if (end != null) {
			if (checkDate.after(end))
				return false;
		}
		return true;
	}

	public static ArrayList<String> parseDates(String date, String dateType) {
		
		Calendar calendar = Calendar.getInstance();
		ArrayList<String> result = new ArrayList<String>();
		Date day = parse(date,DAYFORMAT);
		String year = date.split("-")[0];
		String _day;
		switch (dateType) {
		
		case "day":
			result.add(format(day, DAYFORMAT));
			for (int i = 1; i <= 7; i++) {
				day = tomorrow(day);
				result.add(format(day, DAYFORMAT));
			}
			break;
			
		case "week":
			result.add(format(day, DAYFORMAT));
			for(int i = 1 ; i<=4; i++){
				day = getDateAfter(day,7);
				result.add(format(day, DAYFORMAT));
			}
			break;
			
			
		case "month":
			for(int i=1; i<=12; i++){
				_day = year+"-"+i+"-01";
				result.add(_day);
			}
			result.add((1+Integer.parseInt(year))+"-01-01");
			break;
			
		case "quarter":
			for(int i=1; i<=12; i+=3){
				_day = year+"-"+i+"-01";
				result.add(_day);
			}
			result.add((1+Integer.parseInt(year))+"-01-01");
			break;
		
		case "year":
			for(int i = Integer.parseInt(year); i<= calendar.get(1)+1; i++){
				result.add(i+"-01-01");
			}
			break;
		}
		return result;
	}
	
	
}
