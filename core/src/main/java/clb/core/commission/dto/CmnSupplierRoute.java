package clb.core.commission.dto;

/**
 * Auto Generated By Hap Code Generator
 **/

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@ExtensionAttribute(disable = true)
@Table(name = "fms_cmn_supplier_route")
public class CmnSupplierRoute extends BaseDTO {
    @Id
    @GeneratedValue
    private Long routeId;

    @NotNull
    private Long supplierCommissionLineId;

    @NotNull
    private Long seqNum;

    @NotNull
    private Long supplierId;

    @Transient
    private String supplierName;

    private Long levelCommissionLineId;

    private Long parentRouteId;

    private Long programId;

    private Long requestId;

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setSupplierCommissionLineId(Long supplierCommissionLineId) {
        this.supplierCommissionLineId = supplierCommissionLineId;
    }

    public Long getSupplierCommissionLineId() {
        return supplierCommissionLineId;
    }

    public void setSeqNum(Long seqNum) {
        this.seqNum = seqNum;
    }

    public Long getSeqNum() {
        return seqNum;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setLevelCommissionLineId(Long levelCommissionLineId) {
        this.levelCommissionLineId = levelCommissionLineId;
    }

    public Long getLevelCommissionLineId() {
        return levelCommissionLineId;
    }

    public void setParentRouteId(Long parentRouteId) {
        this.parentRouteId = parentRouteId;
    }

    public Long getParentRouteId() {
        return parentRouteId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getRequestId() {
        return requestId;
    }

}
