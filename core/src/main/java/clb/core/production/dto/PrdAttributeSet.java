package clb.core.production.dto;

/**
 * Created by jiaolong.li on 2017-04-13.
 */

import javax.persistence.*;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import java.util.Date;

import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

@ExtensionAttribute(disable = true)
@Table(name = "fms_prd_attribute_set")
public class PrdAttributeSet extends BaseDTO {

    @Id
    @GeneratedValue
    private Long attSetId;

    @NotEmpty
    private String setName;

    @NotEmpty
    private String statusCode;
    
    private String enable;

    @Column
    private Date lastUpdateDate;

    @Column
    private Long lastUpdatedBy;

    @Transient
    private String lastUpdatedByName;

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public String getLastUpdatedByName() {
        return lastUpdatedByName;
    }

    public void setLastUpdatedByName(String lastUpdatedByName) {
        this.lastUpdatedByName = lastUpdatedByName;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setAttSetId(Long attSetId) {
        this.attSetId = attSetId;
    }

    public Long getAttSetId() {
        return attSetId;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSetName() {
        return setName;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

}
