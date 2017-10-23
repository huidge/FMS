package clb.core.common.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.hap.core.exception.BaseException;
import com.steadystate.css.parser.ParseException;

import clb.core.order.service.impl.OrdAppointmentScheduleServiceImpl;

public class DateUtil {
	
	private static Logger log = LoggerFactory.getLogger(DateUtil.class);

	
	// 1900-1-1为周一
	private final static Long ORIGIN_DATE_NUMBER = -2209017600000L;
	// 一天的毫秒数
	private final static Long DAY_MIMUS = (long) (24 * 60 * 60 * 1000);
	// 取余后周六为5
	private final static int STATURDAY_NUMBER = 5;
	// 取余后周日为6
	private final static int SUNDAY_NUMBER = 6;
	// 一周的天数
	private final static int WEEK_NUMBER = 7;

	// 判断日期是否为周末
	public static boolean isholiday(Long minus) {
		Long days = (minus - ORIGIN_DATE_NUMBER) / DAY_MIMUS;
		int week = (int) (days % WEEK_NUMBER);
		// 取余为5和6，则分别为周六和周日
		if (week == STATURDAY_NUMBER || week == SUNDAY_NUMBER) {
			return true;
		}
		return false;
	}

	// 日期毫秒数转换成日期字符串
	public static String transFormToDateString(Long minus) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date originDate = new Date(minus);
		return dateFormat.format(originDate);

	}

	// 日期毫秒数转换成日期
	public static Date transFormToDate(Long minus) {
		Date originDate = new Date(minus);
		return originDate;
	}
	
	//获取当前年月日
	public static Date getNow(){
		Date date = new Date();
		int year = getYear(date);
		int month = getMonth(date);
		int day = getDay(date);
		return setDate(year,month,day);
	}

	// 获取年份
	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	// 获取月份
	public static int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH) + 1;
	}

	// 获取日
	public static int getDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	// 根据年月获取日期
	public static List<Date> getDaysByYearAndMonth(Date date) {
		List<Date> dates = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.getActualMaximum(Calendar.DATE);
		int year = getYear(date);
		int month = getMonth(date);
		for (int i = 1; i <= day; i++) {
			dates.add(setDate(year, month, i));
		}
		return dates;
	}

	// 设置日期
	public static Date setDate(int year, int month, int day) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.YEAR, year);// 设置年
		gc.set(Calendar.MONTH, month - 1);// 设置年
		gc.set(Calendar.DAY_OF_MONTH, day);// 设置年
		Date date = gc.getTime();
		return date;
	}

	// 判断两个日期年月日是否相等
	public static boolean isSameYearMonthDay(Date date1, Date date2) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dString1 = format.format(date1);
		String dString2 = format.format(date2);
		if (dString1.equals(dString2))
			return true;
		return false;
	}

	// 判断一个日期时间是上午还是下午
	public static int isMorningOrAfternoon(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if (hour < 12)
			return 1;
		return 2;
	}

	// 获取时间字符串
	public static String getTimeString(Date date, Boolean hasMinus) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		int seconds = c.get(Calendar.SECOND);
		StringBuffer stringBuffer = new StringBuffer();
		if (hour < 10) {
			stringBuffer.append("0" + hour + ":");
		} else {
			stringBuffer.append(hour + ":");
		}
		if (minutes < 10) {
			stringBuffer.append("0" + minutes);
		} else {
			stringBuffer.append(minutes);
		}
		if (hasMinus) {
			if (seconds < 10) {
				stringBuffer.append(":0" + seconds);
			} else {
				stringBuffer.append(":" + seconds);
			}
		}

		return stringBuffer.toString();
	}

	/**
	 * 获取指定时间的那天 00:00:00 的时间
	 *
	 * @param date
	 * @return
	 */
	public static Date dayStart(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 00);
		c.set(Calendar.MINUTE, 00);
		c.set(Calendar.SECOND, 00);
		return c.getTime();
	}

	/**
	 * 获取指定时间的那天 23:59:59 的时间
	 *
	 * @param date
	 * @return
	 */
	public static Date dayEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTime();
	}

	// 日期加N秒
	public static Date addSecond(Date date, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, second);
		return calendar.getTime();
	}

	/**
	 * 根据出生年月获取年龄
	 *
	 * @param dateOfBirth
	 * @return
	 */
	public static int getAgeByDate(Date dateOfBirth) {
		int age = 0;
		Calendar born = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		if (dateOfBirth != null) {
			now.setTime(new Date());
			born.setTime(dateOfBirth);
			if (born.after(now)) {
				throw new IllegalArgumentException("年龄不能超过当前日期");
			}
			age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
			int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
			int bornDayOfYear = born.get(Calendar.DAY_OF_YEAR);
			System.out.println("nowDayOfYear:" + nowDayOfYear + " bornDayOfYear:" + bornDayOfYear);
			if (nowDayOfYear < bornDayOfYear) {
				age -= 1;
			}
		}
		return age;
	}

	/**
	 * 对时间的年月日进行加减 
	 * @param date
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date getDate(Date date, int year, int month, int day) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.YEAR, year);
		gc.add(Calendar.MONTH, month);
		gc.add(Calendar.DATE, day);
		return gc.getTime();
	}

	/**
	 * 计算两个日期之间相差的天数
	 * @param smdate 较小的时间
	 * @param bdate  较大的时间
	 *
	 * @return 相差天数
	 */
	public static int daysBetween(Date smdate,Date bdate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		if (between_days < 0) {
			between_days = (time1 - time2) / (1000 * 3600 * 24) + 1;
		}

		return (int)between_days;
	}

	/**
	 * 日期加一年 
	 */
	public static Date addYear(Date date) {
		int year = getYear(date)+1;
		int month = getMonth(date);
		int day = getDay(date);
		return setDate(year, month, day);
	}

	/**
	 * 根据年月周返回时间区间
	 *
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weeks
	 *            第几周
	 * @return 返回第几周的开始与结束日期
	 */
	public static Map<String, Object> getScopeForweeks(int year, int month, int weeks) {
		Map<String, Object> map = new HashMap<String, Object>();
		String time = year + "-" + getMonthToStr(month);
		Map<String, Object> result = getDateScope(time);
		// 获取天数和周数
		int resultDays = Integer.parseInt(result.get("days").toString());
		int resultWeeks = Integer.parseInt(result.get("weeks").toString());
		/**
		 * 如果参数周数大于实际周数 则返回一个不存在的日期 默认设置为当前 天数+1
		 */
		if (weeks > resultWeeks) {
			int days = resultDays + 1;
			String beginDate = year + "-" + getMonthToStr(month) + "-" + days;
			map.put("beginDate", beginDate);
			map.put("endDate", beginDate);
		} else {
			// 获取当月第一天属于周几
			Map<Integer, Object> scopes = getScope(time, resultDays, resultWeeks);
			map = (Map<String, Object>) scopes.get(weeks);
		}
		return map;
	}

	/**
	 * 获取某年某月的天数以及周数
	 *
	 * @param(格式：yyyy-MM) @return
	 */
	private static Map<String, Object> getDateScope(String time) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 保证日期位数
			if (time.length() <= 7) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(time + "-01");
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				// 获取天数
				int days = c.getActualMaximum(Calendar.DAY_OF_MONTH);
				// 获取周数
				int weeks = c.getActualMaximum(Calendar.WEEK_OF_MONTH);
				map.put("days", days);
				map.put("weeks", weeks);
			} else {
				throw new RuntimeException("日期格式不正确");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获取本月周区间
	 *
	 * @param date(yyyy-mm)日期
	 * @param days
	 *            天数
	 * @param weeks
	 *            周数
	 * @return
	 */
	private static Map<Integer, Object> getScope(String date, int days, int weeks) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		// 遍历周数
		int midNum = 0;
		for (int i = 1; i <= weeks; i++) {
			// 第一周
			if (i == 1) {
				String time = date + "-01";
				String week = getWeekOfDate(time);
				// 获取第一周还有多少天
				int firstDays = getSurplusDays(week);
				// 获取第一周结束日期
				int endDays = 1 + firstDays;
				String time2 = date + "-0" + endDays;
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("beginDate", time);
				data.put("endDate", time2);
				map.put(i, data);
				//
				midNum = endDays;
			} else {
				// 当前日期数+7 比较 当月天数
				if ((midNum + 7) <= days) {
					int beginNum = midNum + 1;
					System.out.println("begin:" + beginNum);

					int endNum = midNum + 7;
					System.out.println("end:" + endNum);

					String time1 = date + "-" + getNum(beginNum);
					String time2 = date + "-" + getNum(endNum);
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("beginDate", time1);
					data.put("endDate", time2);
					map.put(i, data);
					midNum = endNum;
				} else {
					int beginNum = midNum + 1;
					int endNum = days;
					String time1 = date + "-" + getNum(beginNum);
					String time2 = date + "-" + getNum(endNum);
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("beginDate", time1);
					data.put("endDate", time2);
					map.put(i, data);
					midNum = endNum;
				}
			}
		}

		return map;
	}

	/**
	 * 获取日期属于周几
	 *
	 * @param time(格式：yyyy-MM-dd)
	 * @return
	 * @throws Exception
	 */
	private static String getWeekOfDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String week = "";
		try {
			Date date = sdf.parse(time);
			String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (w < 0)
				w = 0;
			week = weekDays[w];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return week;
	}

	/**
	 * 获取数字
	 *
	 * @return
	 */
	private static String getNum(int num) {
		int result = num / 10;
		if (result == 0) {
			return "0" + num;
		} else {
			return num + "";
		}
	}

	/**
	 * 返回月份的字符串
	 *
	 * @param month
	 * @return
	 */
	private static String getMonthToStr(int month) {
		String str = "";
		switch (month) {
			case 1:
				str = "01";
				break;
			case 2:
				str = "02";
				break;
			case 3:
				str = "03";
				break;
			case 4:
				str = "04";
				break;
			case 5:
				str = "05";
				break;
			case 6:
				str = "06";
				break;
			case 7:
				str = "07";
				break;
			case 8:
				str = "08";
				break;
			case 9:
				str = "09";
				break;
			case 10:
				str = "10";
				break;
			case 11:
				str = "11";
				break;
			case 12:
				str = "12";
				break;
		}
		return str;
	}

	/**
	 * 根据当前周几判断当前周还有几天
	 *
	 * @param week{"星期日",
	 *            "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"}
	 * @return
	 */
	private static int getSurplusDays(String week) {
		int num = 0;
		if ("星期日".equals(week)) {
			num = 0;
		} else if ("星期一".equals(week)) {
			num = 6;
		} else if ("星期二".equals(week)) {
			num = 5;
		} else if ("星期三".equals(week)) {
			num = 4;
		} else if ("星期四".equals(week)) {
			num = 3;
		} else if ("星期五".equals(week)) {
			num = 2;
		} else if ("星期六".equals(week)) {
			num = 1;
		}
		return num;
	}
	
	public static List<Date> getDateList(Date date,String type){
		List<Date> res = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		Date begin = null;
		Date end = null;
		if("day".equals(type)){
			c.set(Calendar.DAY_OF_WEEK, c.getActualMinimum(Calendar.DAY_OF_WEEK));
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.SECOND,0);
			c.set(Calendar.MINUTE,0);
			begin = transFormToDate(c.getTimeInMillis()+DAY_MIMUS);
			c.set(Calendar.DAY_OF_WEEK, c.getActualMaximum(Calendar.DAY_OF_WEEK));
			c.set(Calendar.HOUR_OF_DAY,23);
			c.set(Calendar.SECOND,59);
			c.set(Calendar.MINUTE,59);
			end = transFormToDate(c.getTimeInMillis()+DAY_MIMUS);
		}else if("month".equals(type)){
			c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.SECOND,0);
			c.set(Calendar.MINUTE,0);
			begin = c.getTime();
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			c.set(Calendar.HOUR_OF_DAY,23);
			c.set(Calendar.SECOND,59);
			c.set(Calendar.MINUTE,59);
			end = c.getTime();
		}else if("year".equals(type)){
			c.set(Calendar.DAY_OF_YEAR, c.getActualMinimum(Calendar.DAY_OF_YEAR));
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.SECOND,0);
			c.set(Calendar.MINUTE,0);
			begin = c.getTime();
			c.set(Calendar.DAY_OF_YEAR, c.getActualMaximum(Calendar.DAY_OF_YEAR));
			c.set(Calendar.HOUR_OF_DAY,23);
			c.set(Calendar.SECOND,59);
			c.set(Calendar.MINUTE,59);
			end = c.getTime();
		}
		res.add(begin);
		res.add(end);
		return res;
	}
	
	
	/**
	 * 获取当前时间的周一和周末 
	 */
	public static List<Date> getCurrentMondayAndSunday(){
		Date now = getNow();
		return getDateList(now,"day");
	}
	
	/**
	 * 获取当前时间月初和月末 
	 */
	public static List<Date> getCurrentMonthBeginAndEnd(){
		Date now = getNow();
		return getDateList(now,"month");
	}
	
	/**
	 * 获取当前时间年初和年末 
	 */
	public static List<Date> getCurrentYearBeginAndEnd(){
		Date now = getNow();
		return getDateList(now,"year");
	}
	
	/**
	 * 获取当前时间的周一和周末 
	 */
	public static List<Date> getLastMondayAndSunday(){
		Date last = transFormToDate(getCurrentMondayAndSunday().get(0).getTime()-2*DAY_MIMUS);
		return getDateList(last,"day");
	}
	
	/**
	 * 获取当前时间月初和月末 
	 */
	public static List<Date> getLastMonthBeginAndEnd(){
		Date last = transFormToDate(getCurrentMonthBeginAndEnd().get(0).getTime()-DAY_MIMUS);
		return getDateList(last,"month");
	}
	
	/**
	 * 获取当前时间年初和年末 
	 */
	public static List<Date> getLastYearBeginAndEnd(){
		Date last = transFormToDate(getCurrentYearBeginAndEnd().get(0).getTime()-DAY_MIMUS);
		return getDateList(last,"year");
	}
	
	/**
     * 将指定格式的字符串转换成时间对象
     * @param str  时间字符串
     * @param sfgs 指定格式
	 * @throws java.text.ParseException 
     * */
    public static Date getDate(String str, String sfgs){
        SimpleDateFormat sf = new SimpleDateFormat(sfgs);
        Date res = new Date();
        try {
			res = sf.parse(str);
		} catch (java.text.ParseException e) {
			log.error("日期转换失败:",e);
		}
        return res;
    }

	/**
	 * 将指定格式的字符串转换成时间对象
	 *
	 * @throws java.text.ParseException
	 * */
	public static String getDateStr(Date date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		String dateString = formatter.format(date);
		return dateString;
	}
	
	/**
	 * 将日期转换成时间对象
	 * @throws java.text.ParseException 
	 *
	 * @throws java.text.ParseException
	 * */
	public static Date getFormatDate(Date date, String pattern){
		return getDate(getDateStr(date,pattern),pattern);
	}
	
	/**
	 * 将日期转换成中文年月日
	 * */
	public static String getChineseDateString(Date date){
		if(date == null)return "";
		int year = getYear(date);
		int month = getMonth(date);
		int day = getDay(date);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(year);
		stringBuffer.append("年");
		stringBuffer.append(month);
		stringBuffer.append("月");
		stringBuffer.append(day);
		stringBuffer.append("日");
		return stringBuffer.toString();
	}
	
	/**
	 *  获取两个日期之差 
	 **/
	public static int getDateDiff(Date date1,Date date2){
		date1 =  getFormatDate(date1,"yyyy-MM-dd");
		date2 =  getFormatDate(date2,"yyyy-MM-dd");
		
		Long dateTime1 = date1.getTime();
		Long dateTime2 = date2.getTime();
		int result = (int)((dateTime1 - dateTime2)/DAY_MIMUS);
		if(result < 0 )result = 0-result;
		return result;
	}
	
	/**
	 *  获取两个日期之差  可以为负数
	 **/
	public static Long getDateDiff2(Date date1,Date date2){
		date1 =  getFormatDate(date1,"yyyy-MM-dd");
		date2 =  getFormatDate(date2,"yyyy-MM-dd");
		Long dateTime1 = date1.getTime();
		Long dateTime2 = date2.getTime();
		return (dateTime1 - dateTime2)/DAY_MIMUS;
	}

	/**
	 *  获取日期开始或者结束
	 **/
	public static Date getDateBeginEnd(int year, int month, int day, String type){

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		if (month != 0) {
			cal.set(Calendar.MONTH, month - 1);
		} else {
			cal.set(Calendar.MONTH, 11);
		}

		if (day != 0) {
			cal.set(Calendar.DAY_OF_MONTH, day);
		} else {
			// 取最后一天
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}

		if ("Begin".equals(type)) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else if ("End".equals(type)) {
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
		}
		return cal.getTime();
	}

}
