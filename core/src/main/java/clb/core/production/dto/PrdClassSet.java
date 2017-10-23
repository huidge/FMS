package clb.core.production.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.system.dto.BaseDTO;

/**
 * Created by wanjun.feng on 17/4/12.
 */
@Table(name="fms_prd_class_set")
public class PrdClassSet extends BaseDTO {
    private static final long serialVersionUID = -6332582144364951503L;

    @Id
    @Column
    @GeneratedValue(generator="IDENTITY")
    private Long setId;
    
    @Condition(operator="LIKE")
    @Column
    @NotEmpty
    private String setCode;
    
    @Condition(operator="LIKE")
    @Column
    @NotEmpty
    private String setName;
    
    @Column
    private String statusCode;
    
    @Column
    @Condition(operator="LIKE")
    private String comments;
    
    @Column
    private String className1Code;
    
    @Transient
    private String className1Name;
    
    @Column
    private String className2Code;
    
    @Transient
    private String className2Name;
    
    @Column
    private String className3Code;
    
    @Transient
    private String className3Name;
    
    @Column
    private String className4Code;
    
    @Transient
    private String className4Name;
    
    @Column
    private String className5Code;
    
    @Transient
    private String className5Name;
    
    public String getSetName() {
        return setName;
    }
    
    public void setSetName(String setName) {
        this.setName = setName;
    }
    
    public Long getSetId() {
        return setId;
    }
    
    public void setSetId(Long setId) {
        this.setId = setId;
    }
    
    public String getSetCode() {
        return setCode;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getClassName1Code() {
        return className1Code;
    }

    public void setClassName1Code(String className1Code) {
        this.className1Code = className1Code;
    }

    public String getClassName2Code() {
        return className2Code;
    }

    public void setClassName2Code(String className2Code) {
        this.className2Code = className2Code;
    }

    public String getClassName3Code() {
        return className3Code;
    }

    public void setClassName3Code(String className3Code) {
        this.className3Code = className3Code;
    }

    public String getClassName4Code() {
        return className4Code;
    }

    public void setClassName4Code(String className4Code) {
        this.className4Code = className4Code;
    }

    public String getClassName5Code() {
        return className5Code;
    }

    public void setClassName5Code(String className5Code) {
        this.className5Code = className5Code;
    }

    public String getClassName1Name() {
        return className1Name;
    }

    public void setClassName1Name(String className1Name) {
        this.className1Name = className1Name;
    }

    public String getClassName2Name() {
        return className2Name;
    }

    public void setClassName2Name(String className2Name) {
        this.className2Name = className2Name;
    }

    public String getClassName3Name() {
        return className3Name;
    }

    public void setClassName3Name(String className3Name) {
        this.className3Name = className3Name;
    }

    public String getClassName4Name() {
        return className4Name;
    }

    public void setClassName4Name(String className4Name) {
        this.className4Name = className4Name;
    }

    public String getClassName5Name() {
        return className5Name;
    }

    public void setClassName5Name(String className5Name) {
        this.className5Name = className5Name;
    }
}