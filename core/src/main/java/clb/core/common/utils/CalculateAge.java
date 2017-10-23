package clb.core.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalculateAge {
	
	public static void main(String[] args) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
		Date date=null;
		try {
			date = sdf.parse("2017-03-14");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int s1 =  accessAge(date, "ASCSR");
		int s2 =  accessAge(date, "AXCSR");
		int s3 =  accessAge(date, "BC");
		int s4 =  accessAge(date, "HL");
		
		System.out.println(s1);
		System.out.println(s2);
		System.out.println(s3);
		System.out.println(s4);
	}
	
	 public static int accessAge(Date date, String type){
		 if(("ASCSR").equals(type)){
			 return getAgeByLastBirth(date);
		 }
		 if(("AXCSR").equals(type)){
			 return getAgeByNextBirth(date);
		 }
		 if(("BC").equals(type)){
			 return getAgeByBC(date);
		 }
		 if(("HL").equals(type)){
			 return getAgeByHL(date);
		 }
		 return 0;
	 }

	 /**
	  * 按上次生日
	  * @param birthday
	  * @return
	  */
	 public static int getAgeByLastBirth(Date birthDay) {
	        int age = 0;
	        try {
	            Calendar now = Calendar.getInstance();
	            now.setTime(new Date());// 当前时间

	            Calendar cal = Calendar.getInstance();
	            if (cal.before(birthDay)) {  
		            return 0; 
		        }  
		        int yearNow = cal.get(Calendar.YEAR);  
		        int monthNow = cal.get(Calendar.MONTH);  
		        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
		        cal.setTime(birthDay);  
		  
		        int yearBirth = cal.get(Calendar.YEAR);  
		        int monthBirth = cal.get(Calendar.MONTH);  
		        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
		  
		        age = yearNow - yearBirth;  
		  
		        if (monthNow <= monthBirth) {  
		            if (monthNow == monthBirth) {  
		                if (dayOfMonthNow < dayOfMonthBirth) age--;  
		            }else{  
		                age--;  
		            }  
		        }  
		        return age;  
	        } catch (Exception e) {//兼容性更强,异常后返回数据
	           return 0;
	        }
	    }
	 
	   /**
	    * 按下次生日  
	    * @param birthday
	    * @return
	    */
	   public static int getAgeByNextBirth(Date birthDay) {
	        int age = 0;
	        try {
	            Calendar now = Calendar.getInstance();
	            now.setTime(new Date());// 当前时间

	            Calendar birth = Calendar.getInstance();
	            birth.setTime(birthDay);

	            Calendar cal = Calendar.getInstance();
	            if (cal.before(birthDay)) {  
		            return 0; 
		        }  
		        int yearNow = cal.get(Calendar.YEAR);  
		        int monthNow = cal.get(Calendar.MONTH);  
		        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
		        cal.setTime(birthDay);  
		  
		        int yearBirth = cal.get(Calendar.YEAR);  
		        int monthBirth = cal.get(Calendar.MONTH);  
		        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
		  
		        age = yearNow - yearBirth;  
		  
		        if (monthNow <= monthBirth) {  
		            if (monthNow == monthBirth) {  
		                if (dayOfMonthNow < dayOfMonthBirth) age--;  
		            }else{  
		                age--;  
		            }  
		        }  
	            return age+1;
	        } catch (Exception e) {//兼容性更强,异常后返回数据
	           return 0;
	        }
	    }
	   
	   /**
	    * 按保成计算年龄
	    * @param birthday
	    * @return
	    */
	   public static int getAgeByBC(Date birthday) {
	        int age = 0;
	        try {
	            Calendar now = Calendar.getInstance();
	            now.setTime(new Date());// 当前时间

	            Calendar birth = Calendar.getInstance();
	            birth.setTime(birthday);

	            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
	                age = 0;
	            } else {
	                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
	                if (now.get(Calendar.MONTH) > birth.get(Calendar.MONTH)) {
	                    age += 1;
	                }
	            }
	            return age;
	        } catch (Exception e) {//兼容性更强,异常后返回数据
	           return 0;
	        }
	    }
	   
	   /**
	    * 宏利计算年龄方法
	    * @param birthday
	    * @return
	    */
	   public static int getAgeByHL(Date birthday) {
	        int age = 0;
	        try {
	            Date birth = DateUtil.getDate(birthday, 0, -6, 0);
	            age = DateUtil.getYear(new Date()) - DateUtil.getYear(birth);
	            if(CalculateAge.compareDate(new Date(), birth) == -1) {
	            	return age -1;
	            }else {
	            	return age;
	            }
	        } catch (Exception e) {//兼容性更强,异常后返回数据
	           return 0;
	        }
	    }
	   /**
	    * 比较2个时间的月份和具体的日期
	    * @param newDate
	    * @param oldDate
	    * @return   1  表示大于等于  -1 表示小于
	    */
	   public static int compareDate(Date newDate,Date oldDate) {
		   int oldMonth = DateUtil.getMonth(oldDate);
		   int newMonth = DateUtil.getMonth(newDate);
		   int oldDays = DateUtil.getDay(oldDate);
		   int newDays = DateUtil.getDay(newDate);
		   if(newMonth > oldMonth) {
			   return 1;
		   }else if(newMonth == oldMonth){
			   if(newDays >= oldDays) {
				   return 1;
			   }else {
				   return -1;
			   }
		   }else {
			   return -1;
		   }
	   }
}
