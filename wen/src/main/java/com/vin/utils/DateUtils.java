package com.vin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期操作类
 * @author Vincent
 *
 */
public class DateUtils {
	
	/** 时间格式(yyyy-MM-dd) */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/** 时间格式(yyyyMMdd) */
	public final static String DATE_PATTERN_TWO = "yyyyMMdd";
	/** 年月格式(yyyy-MM) */
	public final static String DATE_YM_PATTERN = "yyyy-MM";
	
	public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }
    public static Date parse(String dateStr, String pattern) {
        if(dateStr != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            try {
				return df.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
        }
        return null;
    }
    /**
     * 日期增加天数
     * @param date
     * @param num
     * @return
     */
    public static Date addDate(Date date,int num){
    	Calendar calendar=Calendar.getInstance() ;
    	calendar.setTime(date);
    	calendar.add(Calendar.DAY_OF_MONTH, num);
    	return new Date(calendar.getTimeInMillis());
    }
    /**
     * 根据传入的时间获取当月的所有天数
     * @param date
     * @return
     */
    public static List<Date> getMonthDates(Date date){
    	Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		List<Date> list=new ArrayList<Date>();
		int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		list.add(new Date(calendar.getTimeInMillis()));
		for(int j=1;j<actualMaximum;j++){
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date currDate = new Date(calendar.getTimeInMillis());
			list.add(currDate);
		}
		return list;
    }
    
    /*** 
     * 日期月份加减
     *  
     * @param datetime 
     * @return
     */  
    public static String dateMonthCalc(String datetime,int num) {  
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_YM_PATTERN);
        Date date = null;
        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.MONTH, num);
        date = cl.getTime();
        return sdf.format(date);
    }
    
    /**
     * 计算月份差
     * @param starYm  yyyy-MM 或 yyyy-MM-dd
     * @param endYm   yyyy-MM 或 yyyy-MM-dd
     * @return
     */
    public static int calcYmDec(String starYm,String endYm){
    	int rs = 0;
    	String[] sYmArr = starYm.split("-");
    	int sYear = Integer.parseInt(sYmArr[0]);
    	int sMonth = Integer.parseInt(sYmArr[1]);

    	String[] eYmArr = endYm.split("-");
    	int eYear = Integer.parseInt(eYmArr[0]);
    	int eMonth = Integer.parseInt(eYmArr[1]);
    	rs = (eYear-sYear)*12+eMonth-sMonth;
    	return rs;
    }
    
    /**
     * 判断该日期是否是该月的第一天
     * 
     * @param date 需要判断的日期
     * @return
     */
    public static boolean isFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    /**
	 * 判断该日期是否是该月的最后一天
	 * 
	 * @param date 需要判断的日期
	 * @return
	 */
	public static boolean isLastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH) == calendar
				.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 获得该月第一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getFirstDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 获取某月最小天数
		int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		// 设置日历中月份的最小天数
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String firstDayOfMonth = sdf.format(cal.getTime());
		return firstDayOfMonth;
	}

	/**
	 * 获得该月最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// 设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastDayOfMonth = sdf.format(cal.getTime());
		return lastDayOfMonth;
	}
}
