package clb.core.common.utils;

import org.apache.ibatis.ognl.OgnlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.hap.core.exception.BaseException;

public class ExceptionUtil {
	
	private static Logger log = LoggerFactory.getLogger(ExceptionUtil.class);

	

	
	/**
	 * 校验异常是否为唯一性索引引起的
	 * @param Exception
	 * @return int
	 *             1:是
	 *             0:不是 
	 */
	public static int getExceptionType(Exception exception){
		Throwable thr = getRootCause(exception);
        if (thr instanceof BaseException) {
            BaseException be = (BaseException) thr;
        	String code = be.getCode();
        	code = code.substring(code.indexOf(":")+1,code.length());
        	//数据库错误发生唯一性索引错误时的编码
        	if(code.equals("1") || code.equals("1062") || code.equals("2627") || code.equals("2601")){
        		return 1; 
        	}
        }
		return 0;
	}
	
	/**
	 * 获取根异常 
	 * @param Throwable 异常类
	 * @return Throwable 根异常
	 */
	private static Throwable getRootCause(Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        if (throwable instanceof OgnlException && ((OgnlException) throwable).getReason() != null) {
            return getRootCause(((OgnlException) throwable).getReason());
        }
        return throwable;
    }
	

}
