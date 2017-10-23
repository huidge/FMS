package clb.core.production.dto;

/**
 * Created by jiaolong.li on 2017-04-13.
 */

import javax.persistence.*;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(disable = true)
@Table(name = "fms_prd_attribute")
public class PrdAttribute extends BaseDTO {
    @Id
    @GeneratedValue
    private Long attId;

    @NotEmpty
    private String attName;

    private String fieldCode;

    private String fieldType;

    @Column
    private Long valueSetId;

    private String defaultValue;

    private String fieldComments;

    private Long fieldDisplayLength;

    private String statusCode;

    @Transient
    private String valueSetName;

    public String getValueSetName() {
        return valueSetName;
    }

    public void setValueSetName(String valueSetName) {
        this.valueSetName = valueSetName;
    }

    public void setAttId(Long attrId) {
        this.attId = attrId;
    }

    public Long getAttId() {
        return attId;
    }

    public void setAttName(String attrName) {
        this.attName = attrName;
    }

    public String getAttName() {
        return attName;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public Long getValueSetId() {
        return valueSetId;
    }

    public void setValueSetId(Long valueSetId) {
        this.valueSetId = valueSetId;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setFieldComments(String fieldComments) {
        this.fieldComments = fieldComments;
    }

    public String getFieldComments() {
        return fieldComments;
    }

    public void setFieldDisplayLength(Long fieldDisplayLength) {
        this.fieldDisplayLength = fieldDisplayLength;
    }

    public Long getFieldDisplayLength() {
        return fieldDisplayLength;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

}
