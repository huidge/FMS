package clb.core.supplier.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

import java.math.BigDecimal;

@ExtensionAttribute(disable=true)
@Table(name = "fms_spe_supplier_settle")
public class SpeSupplierSettle extends BaseDTO {
    @Id
    @GeneratedValue
    private Long settleId;

    @NotNull
    private Long contractId;

    @NotEmpty
    private String costName;

    @NotEmpty
    private String settleTypeCode;

    private BigDecimal amount;

    @NotEmpty
    private String currencyCode;

    private Long programId;

    private Long requestId;


    @Transient
    private Long supplierId;


    public void setSettleId(Long settleId){
        this.settleId = settleId;
    }

    public Long getSettleId(){
        return settleId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public void setCostName(String costName){
        this.costName = costName;
    }

    public String getCostName(){
        return costName;
    }

    public void setSettleTypeCode(String settleTypeCode){
        this.settleTypeCode = settleTypeCode;
    }

    public String getSettleTypeCode(){
        return settleTypeCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrencyCode(String currencyCode){
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode(){
        return currencyCode;
    }

    public void setProgramId(Long programId){
        this.programId = programId;
    }

    public Long getProgramId(){
        return programId;
    }

    public void setRequestId(Long requestId){
        this.requestId = requestId;
    }

    public Long getRequestId(){
        return requestId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
}
