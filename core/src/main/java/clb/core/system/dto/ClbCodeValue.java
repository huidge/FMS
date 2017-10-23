package clb.core.system.dto;

import com.hand.hap.core.annotation.MultiLanguage;
import com.hand.hap.core.annotation.MultiLanguageField;
import com.hand.hap.system.dto.BaseDTO;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by wanjun.feng on 2017/4/13.
 */
@MultiLanguage
@Table(name = "sys_code_value_b")
public class ClbCodeValue extends BaseDTO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 快速编码类型.
     */
    private Long codeId;

    /**
     * ID.
     */
    @Id
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long codeValueId;

    /**
     * 快码编码值描述.
     */
    @MultiLanguageField
    private String description;
    
    @Column
    private String enabledFlag;

    /**
     * 快码编码意义描述.
     */
    @MultiLanguageField
    @NotEmpty
    @Column
    private String meaning;

    /**
     * 快速编码code.
     */
    @NotEmpty
    @Column
    private String value;
    @Column
    private Long orderSeq;
    @Column
    private String parentValue;

    @Transient
    private String functionCode;

    public Long getCodeId() {
        return codeId;
    }

    public Long getCodeValueId() {
        return codeValueId;
    }

    public String getDescription() {
        return description;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getValue() {
        return value;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public void setCodeValueId(Long codeValueId) {
        this.codeValueId = codeValueId;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning == null ? null : meaning.trim();
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public Long getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getParentValue() {
        return parentValue;
    }

    public void setParentValue(String parentValue) {
        this.parentValue = parentValue;
    }
    
    @Transient
    private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }
}