package clb.core.commission.dto;

import java.util.Date;

/**
 * 供应商佣金表计算存储
 *
 * @jiaolong.li@hand-china.com
 * @create 2017-05-02 15:47
 **/
public class CmnSpeCmnCalRec {

    private Long basicId;

    private Long overrideId;

    private Long extraId;

    private Long policyholdersMinAge;

    private Long policyholdersMaxAge;

    private Date effectiveStartDate;

    private Date effectiveEndDate;

    public Long getBasicId() {
        return basicId;
    }

    public void setBasicId(Long basicId) {
        this.basicId = basicId;
    }

    public Long getOverrideId() {
        return overrideId;
    }

    public void setOverrideId(Long overrideId) {
        this.overrideId = overrideId;
    }

    public Long getExtraId() {
        return extraId;
    }

    public void setExtraId(Long extraId) {
        this.extraId = extraId;
    }

    public Long getPolicyholdersMinAge() {
        return policyholdersMinAge;
    }

    public void setPolicyholdersMinAge(Long policyholdersMinAge) {
        this.policyholdersMinAge = policyholdersMinAge;
    }

    public Long getPolicyholdersMaxAge() {
        return policyholdersMaxAge;
    }

    public void setPolicyholdersMaxAge(Long policyholdersMaxAge) {
        this.policyholdersMaxAge = policyholdersMaxAge;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public void setAtt(CmnSpeCmnCalRec rec){
        this.basicId = rec.getBasicId();
        this.overrideId = rec.getOverrideId();
        this.effectiveStartDate = rec.getEffectiveStartDate();
        this.effectiveEndDate = rec.getEffectiveEndDate();
        this.extraId = rec.getExtraId();
        this.policyholdersMinAge = rec.getPolicyholdersMinAge();
        this.policyholdersMaxAge = rec.getPolicyholdersMaxAge();
    }


}
