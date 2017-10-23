package clb.core.fnd.utils;

/**
 * @name ValidationTableException
 * @description ValidationTable 异常
 * @author dezhi.shen@hand-china.com 2016年8月30日
 * @version 1.0
 */
public class ValidationTableException extends Exception {
	/**
	 * @author dezhi.shen@hand-china.com 2016年8月30日
	 *         ValidationTableException.java
	 */
	private static final long serialVersionUID = -5965533823913003472L;
	/**
	 * 错误消息Code
	 * 
	 * @author dezhi.shen@hand-china.com 2016年8月30日
	 *         ValidationTableException.java
	 */
	private String code;
	/**
	 * 错误消息参数
	 * 
	 * @author dezhi.shen@hand-china.com 2016年8月30日
	 *         ValidationTableException.java
	 */
	private Object args[];

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public ValidationTableException(String code, Object[] args) {
		super();
		this.code = code;
		this.args = args;
	}

	public ValidationTableException() {
	}
}
