package clb.core.common.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.ListUtils;

public class SortUtils {
	
	//sort 1正序 0 倒序  filed 多字段排序
    public static <t> ImComparator createComparator(int sort, List<String> filed) {
        return new ImComparator(sort, filed);
    }
    
    /** 
     * 给数字对象按照指定长度在左侧补0. 
     *  
     * 使用案例: addZero2Str(11,4) 返回 "0011", addZero2Str(-18,6)返回 "-000018" 
     *  
     * @param numObj 
     *            数字对象 
     * @param length 
     *            指定的长度 
     * @return 
     */  
    public static String addZero2Str(Number numObj, int length) {  
        NumberFormat nf = NumberFormat.getInstance();  
        // 设置是否使用分组  
        nf.setGroupingUsed(false);  
        // 设置最大整数位数  
        nf.setMaximumIntegerDigits(length);  
        // 设置最小整数位数  
        nf.setMinimumIntegerDigits(length);  
        return nf.format(numObj);  
    }  
 
    public static class ImComparator implements Comparator {
        int sort = 1;
        List<String> fileds;
 
        public ImComparator(int sort, List<String> fileds) {
            this.sort = sort == 0 ? 0 : 1;
            this.fileds = fileds;
        }
 
        @Override
        public int compare(Object o1, Object o2) {
            int result = 0;
            for (String field : fileds) {
                Object value1 = RefelctUtil.getFieldValueIgnoreCheck(field,o1);
                Object value2 = RefelctUtil.getFieldValueIgnoreCheck(field,o2);
                if (value1 == null || value2 == null) {
                    continue;
                }
                String str1 = value1.toString();  
                String str2 = value2.toString();  
                System.out.println(value1.getClass());
                //基本类型
                if (value1 instanceof Number && value2 instanceof Number) {  
                    int maxlen = Math.max(str1.length(), str2.length());  
                    str1 = addZero2Str((Number) value1, maxlen);  
                    str2 = addZero2Str((Number) value2, maxlen);  
                } 
                //日期类型
                else if (value1 instanceof Date && value2 instanceof Date) {  
                    long time1 = ((Date) value1).getTime();  
                    long time2 = ((Date) value2).getTime();  
                    int maxlen = Long.toString(Math.max(time1, time2)).length();  
                    str1 = addZero2Str(time1, maxlen);  
                    str2 = addZero2Str(time2, maxlen);  
                }
                //BigDecimal
                else if (value1 instanceof BigDecimal && value2 instanceof BigDecimal) {  
                	BigDecimal b1 = new BigDecimal(str1);  
                	BigDecimal b2 = new BigDecimal(str2);  
                    int maxlen = Long.toString(Math.max(str1.length(), str2.length())).length();  
                    str1 = addZero2Str(b1.doubleValue(),maxlen);  
                    str2 = addZero2Str(b2.doubleValue(),maxlen);  
                }
                if(str1.equals(str2))continue;
                if (sort == 1) {
                    return str1.compareTo(str2);
                } else if (sort == 0) {
                    return str2.compareTo(str1);
                } else {
                    continue;
                }
            }
            return result;
        }
    }
}
