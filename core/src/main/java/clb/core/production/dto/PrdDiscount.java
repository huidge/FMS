package clb.core.production.dto;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.system.dto.BaseDTO;

/**
 * Created by wanjun.feng on 17/4/18.
 */
@Table(name="fms_prd_discount")
public class PrdDiscount extends BaseDTO {
    private static final long serialVersionUID = 1632163800348791069L;

    @Id
    @Column
    @GeneratedValue(generator="IDENTITY")
    private Long discountId;
    
    @Column
    private Long noticeId;
    
    @Column
    @Condition(operator="LIKE")
    @NotEmpty
    private String prdDivision;
    
    @Column
    @Condition(operator="LIKE")
    @NotEmpty
    private String prdClass;
    
    @Column
    @NotNull
    private Long itemId;
    
    @Column
    @Condition(operator="LIKE")
    @NotEmpty
    private String content;
    
    @Column
    @NotNull
    private Date dateFrom;
    
    @Column
    private Date dateTo;
    
    @Transient
    private Date releaseDate;
    
    @Transient
    private String statusCode;
    
    @Transient
    private String itemName;
    
    @Transient
    private String noticeTitle;
    
    @Transient
    private String noticeContent;
    
    @Transient
    private String prdDivisionName;
    
    @Transient
    private String prdClassName;

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getPrdDivision() {
        return prdDivision;
    }

    public void setPrdDivision(String prdDivision) {
        this.prdDivision = prdDivision;
    }

    public String getPrdClass() {
        return prdClass;
    }

    public void setPrdClass(String prdClass) {
        this.prdClass = prdClass;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public String getPrdDivisionName() {
        return prdDivisionName;
    }

    public void setPrdDivisionName(String prdDivisionName) {
        this.prdDivisionName = prdDivisionName;
    }

    public String getPrdClassName() {
        return prdClassName;
    }

    public void setPrdClassName(String prdClassName) {
        this.prdClassName = prdClassName;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}