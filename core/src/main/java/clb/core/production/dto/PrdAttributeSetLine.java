package clb.core.production.dto;

/**
 * Created by jiaolong.li on 2017-04-13.
 */

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(disable = true)
@Table(name = "fms_prd_attribute_set_line")
public class PrdAttributeSetLine extends BaseDTO {
    @Id
    @GeneratedValue
    private Long lineId;

    @NotNull
    private Long attSetId;

    @NotEmpty
    private String attGroup;

    @NotNull
    private Long seqNum;

    @NotNull
    private Long attId;

    private String requiredFlag;

    @Transient
    private String attGroupName;

    public String getRequiredFlag() {
        return requiredFlag;
    }

    public void setRequiredFlag(String requiredFlag) {
        this.requiredFlag = requiredFlag;
    }

    public String getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(String displayFlag) {
        this.displayFlag = displayFlag;
    }

    public String getCompareFlag() {
        return compareFlag;
    }

    public void setCompareFlag(String compareFlag) {
        this.compareFlag = compareFlag;
    }

    private String displayFlag;

    private String compareFlag;

    private String compareRule;

    @NotEmpty
    private String statusCode;

    @Transient
    private String attName;

    @Transient
    private String fieldType;
    @Transient
    private Long valueSetId;
    @Transient
    private String defaultValue;
    @Transient
    private String fieldComments;
    @Transient
    private String fieldDisplayLength;
    @Transient
    private String fieldCode;


    public String getAttName() {
        return attName;
    }

    public void setAttName(String attName) {
        this.attName = attName;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setAttSetId(Long attSetId) {
        this.attSetId = attSetId;
    }

    public Long getAttSetId() {
        return attSetId;
    }

    public void setAttGroup(String attGroup) {
        this.attGroup = attGroup;
    }

    public String getAttGroup() {
        return attGroup;
    }

    public void setSeqNum(Long seqNum) {
        this.seqNum = seqNum;
    }

    public Long getSeqNum() {
        return seqNum;
    }

    public void setAttId(Long attId) {
        this.attId = attId;
    }

    public Long getAttId() {
        return attId;
    }

    public void setCompareRule(String compareRule) {
        this.compareRule = compareRule;
    }

    public String getCompareRule() {
        return compareRule;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Long getValueSetId() {
        return valueSetId;
    }

    public void setValueSetId(Long valueSetId) {
        this.valueSetId = valueSetId;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFieldComments() {
        return fieldComments;
    }

    public void setFieldComments(String fieldComments) {
        this.fieldComments = fieldComments;
    }

    public String getFieldDisplayLength() {
        return fieldDisplayLength;
    }

    public void setFieldDisplayLength(String fieldDisplayLength) {
        this.fieldDisplayLength = fieldDisplayLength;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getAttGroupName() {
        return attGroupName;
    }

    public void setAttGroupName(String attGroupName) {
        this.attGroupName = attGroupName;
    }
}
