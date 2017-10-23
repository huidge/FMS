package clb.core.supplier.exceptions;

import com.hand.hap.core.exception.BaseException;

/**
 * Created by bo.wu@hand-china.com on 2017年4月21日11:21:11.
 * 自定义异常
 */
public class SpeException extends BaseException {
	
	private String code;
	private String descriptionKey;
	

	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getDescriptionKey() {
		return descriptionKey;
	}


	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}


	public SpeException(String code, String descriptionKey, Object[] parameters) {
		super(code, descriptionKey, parameters);
		this.code = code;
		this.descriptionKey = descriptionKey;
	}
}

