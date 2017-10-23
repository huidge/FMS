
package clb.core.commission.dto;

/**
 * Created by wanjun.feng on 2017-09-12.
 */

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Table(name = "fms_cmn_channel_Ratio_Detail")
public class CmnChannelRatioDetail extends BaseDTO {
    private static final long serialVersionUID = 5394206675775837436L;

    @Id
    @GeneratedValue
    private Long ratioLineId;

    @NotNull
    private Long ratioId;
    
    private String bigClass;
    @Transient
    private String bigClassName;
    
    private String midClass;
    @Transient
    private String midClassName;
    
    private String minClass;
    @Transient
    private String minClassName;

    private Long itemId;
    
    @Transient
    private String itemName;

    private Long sublineItemId;
    
    @Transient
    private String sublineItemName;
    
    @NotNull
    private BigDecimal theFirstYear;

    private BigDecimal theSecondYear;

    private BigDecimal theThirdYear;

    private BigDecimal theFourthYear;

    private BigDecimal theFifthYear;

    private BigDecimal theSixthYear;

    private BigDecimal theSeventhYear;

    private BigDecimal theEightYear;

    private BigDecimal theNinthYear;

    private BigDecimal theTenthYear;
    
    private String comments;
    
    @NotNull
    private Long objectVersionNumber;

    public Long getRatioLineId() {
        return ratioLineId;
    }

    public void setRatioLineId(Long ratioLineId) {
        this.ratioLineId = ratioLineId;
    }

    public Long getRatioId() {
        return ratioId;
    }

    public void setRatioId(Long ratioId) {
        this.ratioId = ratioId;
    }

    public String getBigClass() {
        return bigClass;
    }

    public void setBigClass(String bigClass) {
        this.bigClass = bigClass;
    }

    public String getMidClass() {
        return midClass;
    }

    public void setMidClass(String midClass) {
        this.midClass = midClass;
    }

    public String getMinClass() {
        return minClass;
    }

    public void setMinClass(String minClass) {
        this.minClass = minClass;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getSublineItemId() {
        return sublineItemId;
    }

    public void setSublineItemId(Long sublineItemId) {
        this.sublineItemId = sublineItemId;
    }

    public String getSublineItemName() {
        return sublineItemName;
    }

    public void setSublineItemName(String sublineItemName) {
        this.sublineItemName = sublineItemName;
    }

    public BigDecimal getTheFirstYear() {
        return theFirstYear;
    }

    public void setTheFirstYear(BigDecimal theFirstYear) {
        this.theFirstYear = theFirstYear;
    }

    public BigDecimal getTheSecondYear() {
        return theSecondYear;
    }

    public void setTheSecondYear(BigDecimal theSecondYear) {
        this.theSecondYear = theSecondYear;
    }

    public BigDecimal getTheThirdYear() {
        return theThirdYear;
    }

    public void setTheThirdYear(BigDecimal theThirdYear) {
        this.theThirdYear = theThirdYear;
    }

    public BigDecimal getTheFourthYear() {
        return theFourthYear;
    }

    public void setTheFourthYear(BigDecimal theFourthYear) {
        this.theFourthYear = theFourthYear;
    }

    public BigDecimal getTheFifthYear() {
        return theFifthYear;
    }

    public void setTheFifthYear(BigDecimal theFifthYear) {
        this.theFifthYear = theFifthYear;
    }

    public BigDecimal getTheSixthYear() {
        return theSixthYear;
    }

    public void setTheSixthYear(BigDecimal theSixthYear) {
        this.theSixthYear = theSixthYear;
    }

    public BigDecimal getTheSeventhYear() {
        return theSeventhYear;
    }

    public void setTheSeventhYear(BigDecimal theSeventhYear) {
        this.theSeventhYear = theSeventhYear;
    }

    public BigDecimal getTheEightYear() {
        return theEightYear;
    }

    public void setTheEightYear(BigDecimal theEightYear) {
        this.theEightYear = theEightYear;
    }

    public BigDecimal getTheNinthYear() {
        return theNinthYear;
    }

    public void setTheNinthYear(BigDecimal theNinthYear) {
        this.theNinthYear = theNinthYear;
    }

    public BigDecimal getTheTenthYear() {
        return theTenthYear;
    }

    public void setTheTenthYear(BigDecimal theTenthYear) {
        this.theTenthYear = theTenthYear;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getBigClassName() {
        return bigClassName;
    }

    public void setBigClassName(String bigClassName) {
        this.bigClassName = bigClassName;
    }

    public String getMidClassName() {
        return midClassName;
    }

    public void setMidClassName(String midClassName) {
        this.midClassName = midClassName;
    }

    public String getMinClassName() {
        return minClassName;
    }

    public void setMinClassName(String minClassName) {
        this.minClassName = minClassName;
    }
}

