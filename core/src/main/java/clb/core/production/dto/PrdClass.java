package clb.core.production.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.hand.hap.system.dto.BaseDTO;

/**
 * Created by wanjun.feng on 17/4/12.
 */
@Table(name="fms_prd_class")
public class PrdClass extends BaseDTO {
    private static final long serialVersionUID = -2252377040632593943L;

    @Id
    @GeneratedValue(generator="IDENTITY")
    @Column
    private Long classId;
    
    @Column
    @NotNull
    private Long setId;
    
    @Column
    private String className1;
    
    @Column
    private String className2;
    
    @Column
    private String className3;
    
    @Column
    private String className4;
    
    @Column
    private String className5;
    
    @Column
    private String statusCode;
    
    @Column
    private String queryFlag;
    
    @Column
    private String updateFlag;
 

    public String getQueryFlag() {
		return queryFlag;
	}

	public void setQueryFlag(String queryFlag) {
		this.queryFlag = queryFlag;
	}

	public String getUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getSetId() {
        return setId;
    }

    public void setSetId(Long setId) {
        this.setId = setId;
    }

    public String getClassName1() {
        return className1;
    }

    public void setClassName1(String className1) {
        this.className1 = className1;
    }

    public String getClassName2() {
        return className2;
    }

    public void setClassName2(String className2) {
        this.className2 = className2;
    }

    public String getClassName3() {
        return className3;
    }

    public void setClassName3(String className3) {
        this.className3 = className3;
    }

    public String getClassName4() {
        return className4;
    }

    public void setClassName4(String className4) {
        this.className4 = className4;
    }

    public String getClassName5() {
        return className5;
    }

    public void setClassName5(String className5) {
        this.className5 = className5;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}