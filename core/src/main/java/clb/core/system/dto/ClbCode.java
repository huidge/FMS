package clb.core.system.dto;

import com.hand.hap.core.annotation.Children;
import com.hand.hap.core.annotation.MultiLanguage;
import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.system.dto.BaseDTO;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by wanjun.feng on 2017/4/13.
 */
@MultiLanguage
@Table(name = "sys_code_b")
public class ClbCode extends BaseDTO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 快码类型.
     */
    @Condition(operator = LIKE)
    @Column
    @NotNull
    private String code;

    /**
     * 表ID，主键，供其他表做外键.
     */
    @Id
    @Column
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long codeId;

    @Children
    @Transient
    private List<ClbCodeValue> clbCodeValues;

    /**
     * 快码描述.
     */
    @Column
    @com.hand.hap.core.annotation.MultiLanguageField
    @NotEmpty
    @Condition(operator = LIKE)
    private String description;
    
    @Column
    private Long parentId;
    
    @Column
    private String enabledFlag;

    @Transient
    private String type;
    
    @Transient
    private String parentDescription;
    
    public List<ClbCodeValue> getClbCodeValues() {
        return clbCodeValues;
    }

    public void setClbCodeValues(List<ClbCodeValue> clbCodeValues) {
        this.clbCodeValues = clbCodeValues;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public Long getCodeId() {
        return codeId;
    }

    public String getDescription() {
        return description;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getParentDescription() {
        return parentDescription;
    }

    public void setParentDescription(String parentDescription) {
        this.parentDescription = parentDescription;
    }
}