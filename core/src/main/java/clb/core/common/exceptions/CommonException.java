package clb.core.common.exceptions;

import com.hand.hap.core.exception.BaseException;

/**
 * Created by bo.wu@hand-china.com on 2017年4月21日11:20:31.
 * 自定义异常
 */
public class CommonException extends BaseException {
	
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


	public CommonException(String code, String descriptionKey, Object[] parameters) {
		super(code, descriptionKey, parameters);
		this.code = code;
		this.descriptionKey = descriptionKey;
	}
}

