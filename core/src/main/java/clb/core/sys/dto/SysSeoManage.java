package clb.core.sys.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.system.dto.BaseDTO;
/*****
 * @author FengWanJun
 * @Date 2017-09-06
 */
@Table(name="fms_sys_seo_manage")
public class SysSeoManage extends BaseDTO{
    private static final long serialVersionUID = 2243345626201531131L;
    
    @Id
	@GeneratedValue
	private Long seoId;
	
	@NotEmpty
	private String siteTitle;
	
	private String siteDescription;
	
	private String siteKeywords;
	
	@NotNull
    private Long objectVersionNumber;
	
    public Long getSeoId() {
        return seoId;
    }
    public void setSeoId(Long seoId) {
        this.seoId = seoId;
    }
    public String getSiteTitle() {
        return siteTitle;
    }
    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }
    public String getSiteDescription() {
        return siteDescription;
    }
    public void setSiteDescription(String siteDescription) {
        this.siteDescription = siteDescription;
    }
    public String getSiteKeywords() {
        return siteKeywords;
    }
    public void setSiteKeywords(String siteKeywords) {
        this.siteKeywords = siteKeywords;
    }
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}