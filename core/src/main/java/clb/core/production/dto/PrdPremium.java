package clb.core.production.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.system.dto.BaseDTO;

/*****
 * @author tiansheng.ye
 * @Date 2017/05/15
 * @Desc 保费表
 */
@Table(name = "FMS_PRD_PREMIUM")
public class PrdPremium extends BaseDTO {
	private static final long serialVersionUID = -2252377040632593945L;

	@Id
	@GeneratedValue(generator = "IDENTITY")
	@Column
	private Long premiumId;

	@Column
	@NotNull
	private Long itemId;

	@Column
	@NotEmpty
	private String currency;

	@Column
	private String smokeFlag;

	@Column
	private String gender;

	private String nationalityClass;
	
	@Column
	@NotNull
	private Long sublineId;

	@Column
	private Long planId;
	
	@Transient
	private String securityLevel;
	@Transient
	private String securityRegion;
	
	@Column
	private Long selfpayId;
	@Transient
	private String selfpay;
	
	@Column
	private Double premium0;
	@Column
	private Double premium1;
	@Column
	private Double premium2;
	@Column
	private Double premium3;
	@Column
	private Double premium4;
	@Column
	private Double premium5;
	@Column
	private Double premium6;
	@Column
	private Double premium7;
	@Column
	private Double premium8;
	@Column
	private Double premium9;
	@Column
	private Double premium10;
	@Column
	private Double premium11;
	@Column
	private Double premium12;
	@Column
	private Double premium13;
	@Column
	private Double premium14;
	@Column
	private Double premium15;
	@Column
	private Double premium16;
	@Column
	private Double premium17;
	@Column
	private Double premium18;
	@Column
	private Double premium19;
	@Column
	private Double premium20;
	@Column
	private Double premium21;
	@Column
	private Double premium22;
	@Column
	private Double premium23;
	@Column
	private Double premium24;
	@Column
	private Double premium25;
	@Column
	private Double premium26;
	@Column
	private Double premium27;
	@Column
	private Double premium28;
	@Column
	private Double premium29;
	@Column
	private Double premium30;
	@Column
	private Double premium31;
	@Column
	private Double premium32;
	@Column
	private Double premium33;
	@Column
	private Double premium34;
	@Column
	private Double premium35;
	@Column
	private Double premium36;
	@Column
	private Double premium37;
	@Column
	private Double premium38;
	@Column
	private Double premium39;
	@Column
	private Double premium40;
	@Column
	private Double premium41;
	@Column
	private Double premium42;
	@Column
	private Double premium43;
	@Column
	private Double premium44;
	@Column
	private Double premium45;
	@Column
	private Double premium46;
	@Column
	private Double premium47;
	@Column
	private Double premium48;
	@Column
	private Double premium49;
	@Column
	private Double premium50;
	@Column
	private Double premium51;
	@Column
	private Double premium52;
	@Column
	private Double premium53;
	@Column
	private Double premium54;
	@Column
	private Double premium55;
	@Column
	private Double premium56;
	@Column
	private Double premium57;
	@Column
	private Double premium58;
	@Column
	private Double premium59;
	@Column
	private Double premium60;
	@Column
	private Double premium61;
	@Column
	private Double premium62;
	@Column
	private Double premium63;
	@Column
	private Double premium64;
	@Column
	private Double premium65;
	@Column
	private Double premium66;
	@Column
	private Double premium67;
	@Column
	private Double premium68;
	@Column
	private Double premium69;
	@Column
	private Double premium70;
	@Column
	private Double premium71;
	@Column
	private Double premium72;
	@Column
	private Double premium73;
	@Column
	private Double premium74;
	@Column
	private Double premium75;
	@Column
	private Double premium76;
	@Column
	private Double premium77;
	@Column
	private Double premium78;
	@Column
	private Double premium79;
	@Column
	private Double premium80;
	@Column
	private Double premium81;
	@Column
	private Double premium82;
	@Column
	private Double premium83;
	@Column
	private Double premium84;
	@Column
	private Double premium85;
	@Column
	private Double premium86;
	@Column
	private Double premium87;
	@Column
	private Double premium88;
	@Column
	private Double premium89;
	@Column
	private Double premium90;
	@Column
	private Double premium91;
	@Column
	private Double premium92;
	@Column
	private Double premium93;
	@Column
	private Double premium94;
	@Column
	private Double premium95;
	@Column
	private Double premium96;
	@Column
	private Double premium97;
	@Column
	private Double premium98;
	@Column
	private Double premium99;
	@Column
	private Double premium100;
	
	@Transient
	private String premiumColumn;//保费的查询字段
	
	@Transient
	private Integer age;//年龄
	
	@Transient
	private Double amount;//保额
	
	@Transient
	private String amountType;//保额种类
	
	@Transient
	private String payMethod;//缴费方式

	public Long getPremiumId() {
		return premiumId;
	}

	public void setPremiumId(Long premiumId) {
		this.premiumId = premiumId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSmokeFlag() {
		return smokeFlag;
	}

	public void setSmokeFlag(String smokeFlag) {
		this.smokeFlag = smokeFlag;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationalityClass() {
		return nationalityClass;
	}

	public void setNationalityClass(String nationalityClass) {
		this.nationalityClass = nationalityClass;
	}

	public Long getSublineId() {
		return sublineId;
	}

	public void setSublineId(Long sublineId) {
		this.sublineId = sublineId;
	}

	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public String getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(String securityLevel) {
		this.securityLevel = securityLevel;
	}

	public String getSecurityRegion() {
		return securityRegion;
	}

	public void setSecurityRegion(String securityRegion) {
		this.securityRegion = securityRegion;
	}

	public String getSelfpay() {
		return selfpay;
	}

	public void setSelfpay(String selfpay) {
		this.selfpay = selfpay;
	}

	public Long getSelfpayId() {
		return selfpayId;
	}

	public void setSelfpayId(Long selfpayId) {
		this.selfpayId = selfpayId;
	}

	public Double getPremium0() {
		return premium0;
	}

	public void setPremium0(Double premium0) {
		this.premium0 = premium0;
	}

	public Double getPremium1() {
		return premium1;
	}

	public void setPremium1(Double premium1) {
		this.premium1 = premium1;
	}

	public Double getPremium2() {
		return premium2;
	}

	public void setPremium2(Double premium2) {
		this.premium2 = premium2;
	}

	public Double getPremium3() {
		return premium3;
	}

	public void setPremium3(Double premium3) {
		this.premium3 = premium3;
	}

	public Double getPremium4() {
		return premium4;
	}

	public void setPremium4(Double premium4) {
		this.premium4 = premium4;
	}

	public Double getPremium5() {
		return premium5;
	}

	public void setPremium5(Double premium5) {
		this.premium5 = premium5;
	}

	public Double getPremium6() {
		return premium6;
	}

	public void setPremium6(Double premium6) {
		this.premium6 = premium6;
	}

	public Double getPremium7() {
		return premium7;
	}

	public void setPremium7(Double premium7) {
		this.premium7 = premium7;
	}

	public Double getPremium8() {
		return premium8;
	}

	public void setPremium8(Double premium8) {
		this.premium8 = premium8;
	}

	public Double getPremium9() {
		return premium9;
	}

	public void setPremium9(Double premium9) {
		this.premium9 = premium9;
	}

	public Double getPremium10() {
		return premium10;
	}

	public void setPremium10(Double premium10) {
		this.premium10 = premium10;
	}

	public Double getPremium11() {
		return premium11;
	}

	public void setPremium11(Double premium11) {
		this.premium11 = premium11;
	}

	public Double getPremium12() {
		return premium12;
	}

	public void setPremium12(Double premium12) {
		this.premium12 = premium12;
	}

	public Double getPremium13() {
		return premium13;
	}

	public void setPremium13(Double premium13) {
		this.premium13 = premium13;
	}

	public Double getPremium14() {
		return premium14;
	}

	public void setPremium14(Double premium14) {
		this.premium14 = premium14;
	}

	public Double getPremium15() {
		return premium15;
	}

	public void setPremium15(Double premium15) {
		this.premium15 = premium15;
	}

	public Double getPremium16() {
		return premium16;
	}

	public void setPremium16(Double premium16) {
		this.premium16 = premium16;
	}

	public Double getPremium17() {
		return premium17;
	}

	public void setPremium17(Double premium17) {
		this.premium17 = premium17;
	}

	public Double getPremium18() {
		return premium18;
	}

	public void setPremium18(Double premium18) {
		this.premium18 = premium18;
	}

	public Double getPremium19() {
		return premium19;
	}

	public void setPremium19(Double premium19) {
		this.premium19 = premium19;
	}

	public Double getPremium20() {
		return premium20;
	}

	public void setPremium20(Double premium20) {
		this.premium20 = premium20;
	}

	public Double getPremium21() {
		return premium21;
	}

	public void setPremium21(Double premium21) {
		this.premium21 = premium21;
	}

	public Double getPremium22() {
		return premium22;
	}

	public void setPremium22(Double premium22) {
		this.premium22 = premium22;
	}

	public Double getPremium23() {
		return premium23;
	}

	public void setPremium23(Double premium23) {
		this.premium23 = premium23;
	}

	public Double getPremium24() {
		return premium24;
	}

	public void setPremium24(Double premium24) {
		this.premium24 = premium24;
	}

	public Double getPremium25() {
		return premium25;
	}

	public void setPremium25(Double premium25) {
		this.premium25 = premium25;
	}

	public Double getPremium26() {
		return premium26;
	}

	public void setPremium26(Double premium26) {
		this.premium26 = premium26;
	}

	public Double getPremium27() {
		return premium27;
	}

	public void setPremium27(Double premium27) {
		this.premium27 = premium27;
	}

	public Double getPremium28() {
		return premium28;
	}

	public void setPremium28(Double premium28) {
		this.premium28 = premium28;
	}

	public Double getPremium29() {
		return premium29;
	}

	public void setPremium29(Double premium29) {
		this.premium29 = premium29;
	}

	public Double getPremium30() {
		return premium30;
	}

	public void setPremium30(Double premium30) {
		this.premium30 = premium30;
	}

	public Double getPremium31() {
		return premium31;
	}

	public void setPremium31(Double premium31) {
		this.premium31 = premium31;
	}

	public Double getPremium32() {
		return premium32;
	}

	public void setPremium32(Double premium32) {
		this.premium32 = premium32;
	}

	public Double getPremium33() {
		return premium33;
	}

	public void setPremium33(Double premium33) {
		this.premium33 = premium33;
	}

	public Double getPremium34() {
		return premium34;
	}

	public void setPremium34(Double premium34) {
		this.premium34 = premium34;
	}

	public Double getPremium35() {
		return premium35;
	}

	public void setPremium35(Double premium35) {
		this.premium35 = premium35;
	}

	public Double getPremium36() {
		return premium36;
	}

	public void setPremium36(Double premium36) {
		this.premium36 = premium36;
	}

	public Double getPremium37() {
		return premium37;
	}

	public void setPremium37(Double premium37) {
		this.premium37 = premium37;
	}

	public Double getPremium38() {
		return premium38;
	}

	public void setPremium38(Double premium38) {
		this.premium38 = premium38;
	}

	public Double getPremium39() {
		return premium39;
	}

	public void setPremium39(Double premium39) {
		this.premium39 = premium39;
	}

	public Double getPremium40() {
		return premium40;
	}

	public void setPremium40(Double premium40) {
		this.premium40 = premium40;
	}

	public Double getPremium41() {
		return premium41;
	}

	public void setPremium41(Double premium41) {
		this.premium41 = premium41;
	}

	public Double getPremium42() {
		return premium42;
	}

	public void setPremium42(Double premium42) {
		this.premium42 = premium42;
	}

	public Double getPremium43() {
		return premium43;
	}

	public void setPremium43(Double premium43) {
		this.premium43 = premium43;
	}

	public Double getPremium44() {
		return premium44;
	}

	public void setPremium44(Double premium44) {
		this.premium44 = premium44;
	}

	public Double getPremium45() {
		return premium45;
	}

	public void setPremium45(Double premium45) {
		this.premium45 = premium45;
	}

	public Double getPremium46() {
		return premium46;
	}

	public void setPremium46(Double premium46) {
		this.premium46 = premium46;
	}

	public Double getPremium47() {
		return premium47;
	}

	public void setPremium47(Double premium47) {
		this.premium47 = premium47;
	}

	public Double getPremium48() {
		return premium48;
	}

	public void setPremium48(Double premium48) {
		this.premium48 = premium48;
	}

	public Double getPremium49() {
		return premium49;
	}

	public void setPremium49(Double premium49) {
		this.premium49 = premium49;
	}

	public Double getPremium50() {
		return premium50;
	}

	public void setPremium50(Double premium50) {
		this.premium50 = premium50;
	}

	public Double getPremium51() {
		return premium51;
	}

	public void setPremium51(Double premium51) {
		this.premium51 = premium51;
	}

	public Double getPremium52() {
		return premium52;
	}

	public void setPremium52(Double premium52) {
		this.premium52 = premium52;
	}

	public Double getPremium53() {
		return premium53;
	}

	public void setPremium53(Double premium53) {
		this.premium53 = premium53;
	}

	public Double getPremium54() {
		return premium54;
	}

	public void setPremium54(Double premium54) {
		this.premium54 = premium54;
	}

	public Double getPremium55() {
		return premium55;
	}

	public void setPremium55(Double premium55) {
		this.premium55 = premium55;
	}

	public Double getPremium56() {
		return premium56;
	}

	public void setPremium56(Double premium56) {
		this.premium56 = premium56;
	}

	public Double getPremium57() {
		return premium57;
	}

	public void setPremium57(Double premium57) {
		this.premium57 = premium57;
	}

	public Double getPremium58() {
		return premium58;
	}

	public void setPremium58(Double premium58) {
		this.premium58 = premium58;
	}

	public Double getPremium59() {
		return premium59;
	}

	public void setPremium59(Double premium59) {
		this.premium59 = premium59;
	}

	public Double getPremium60() {
		return premium60;
	}

	public void setPremium60(Double premium60) {
		this.premium60 = premium60;
	}

	public Double getPremium61() {
		return premium61;
	}

	public void setPremium61(Double premium61) {
		this.premium61 = premium61;
	}

	public Double getPremium62() {
		return premium62;
	}

	public void setPremium62(Double premium62) {
		this.premium62 = premium62;
	}

	public Double getPremium63() {
		return premium63;
	}

	public void setPremium63(Double premium63) {
		this.premium63 = premium63;
	}

	public Double getPremium64() {
		return premium64;
	}

	public void setPremium64(Double premium64) {
		this.premium64 = premium64;
	}

	public Double getPremium65() {
		return premium65;
	}

	public void setPremium65(Double premium65) {
		this.premium65 = premium65;
	}

	public Double getPremium66() {
		return premium66;
	}

	public void setPremium66(Double premium66) {
		this.premium66 = premium66;
	}

	public Double getPremium67() {
		return premium67;
	}

	public void setPremium67(Double premium67) {
		this.premium67 = premium67;
	}

	public Double getPremium68() {
		return premium68;
	}

	public void setPremium68(Double premium68) {
		this.premium68 = premium68;
	}

	public Double getPremium69() {
		return premium69;
	}

	public void setPremium69(Double premium69) {
		this.premium69 = premium69;
	}

	public Double getPremium70() {
		return premium70;
	}

	public void setPremium70(Double premium70) {
		this.premium70 = premium70;
	}

	public Double getPremium71() {
		return premium71;
	}

	public void setPremium71(Double premium71) {
		this.premium71 = premium71;
	}

	public Double getPremium72() {
		return premium72;
	}

	public void setPremium72(Double premium72) {
		this.premium72 = premium72;
	}

	public Double getPremium73() {
		return premium73;
	}

	public void setPremium73(Double premium73) {
		this.premium73 = premium73;
	}

	public Double getPremium74() {
		return premium74;
	}

	public void setPremium74(Double premium74) {
		this.premium74 = premium74;
	}

	public Double getPremium75() {
		return premium75;
	}

	public void setPremium75(Double premium75) {
		this.premium75 = premium75;
	}

	public Double getPremium76() {
		return premium76;
	}

	public void setPremium76(Double premium76) {
		this.premium76 = premium76;
	}

	public Double getPremium77() {
		return premium77;
	}

	public void setPremium77(Double premium77) {
		this.premium77 = premium77;
	}

	public Double getPremium78() {
		return premium78;
	}

	public void setPremium78(Double premium78) {
		this.premium78 = premium78;
	}

	public Double getPremium79() {
		return premium79;
	}

	public void setPremium79(Double premium79) {
		this.premium79 = premium79;
	}

	public Double getPremium80() {
		return premium80;
	}

	public void setPremium80(Double premium80) {
		this.premium80 = premium80;
	}

	public Double getPremium81() {
		return premium81;
	}

	public void setPremium81(Double premium81) {
		this.premium81 = premium81;
	}

	public Double getPremium82() {
		return premium82;
	}

	public void setPremium82(Double premium82) {
		this.premium82 = premium82;
	}

	public Double getPremium83() {
		return premium83;
	}

	public void setPremium83(Double premium83) {
		this.premium83 = premium83;
	}

	public Double getPremium84() {
		return premium84;
	}

	public void setPremium84(Double premium84) {
		this.premium84 = premium84;
	}

	public Double getPremium85() {
		return premium85;
	}

	public void setPremium85(Double premium85) {
		this.premium85 = premium85;
	}

	public Double getPremium86() {
		return premium86;
	}

	public void setPremium86(Double premium86) {
		this.premium86 = premium86;
	}

	public Double getPremium87() {
		return premium87;
	}

	public void setPremium87(Double premium87) {
		this.premium87 = premium87;
	}

	public Double getPremium88() {
		return premium88;
	}

	public void setPremium88(Double premium88) {
		this.premium88 = premium88;
	}

	public Double getPremium89() {
		return premium89;
	}

	public void setPremium89(Double premium89) {
		this.premium89 = premium89;
	}

	public Double getPremium90() {
		return premium90;
	}

	public void setPremium90(Double premium90) {
		this.premium90 = premium90;
	}

	public Double getPremium91() {
		return premium91;
	}

	public void setPremium91(Double premium91) {
		this.premium91 = premium91;
	}

	public Double getPremium92() {
		return premium92;
	}

	public void setPremium92(Double premium92) {
		this.premium92 = premium92;
	}

	public Double getPremium93() {
		return premium93;
	}

	public void setPremium93(Double premium93) {
		this.premium93 = premium93;
	}

	public Double getPremium94() {
		return premium94;
	}

	public void setPremium94(Double premium94) {
		this.premium94 = premium94;
	}

	public Double getPremium95() {
		return premium95;
	}

	public void setPremium95(Double premium95) {
		this.premium95 = premium95;
	}

	public Double getPremium96() {
		return premium96;
	}

	public void setPremium96(Double premium96) {
		this.premium96 = premium96;
	}

	public Double getPremium97() {
		return premium97;
	}

	public void setPremium97(Double premium97) {
		this.premium97 = premium97;
	}

	public Double getPremium98() {
		return premium98;
	}

	public void setPremium98(Double premium98) {
		this.premium98 = premium98;
	}

	public Double getPremium99() {
		return premium99;
	}

	public void setPremium99(Double premium99) {
		this.premium99 = premium99;
	}

	public Double getPremium100() {
		return premium100;
	}

	public void setPremium100(Double premium100) {
		this.premium100 = premium100;
	}

	// 产品名称
	@Transient
	private String itemName;
	@Transient
	String sublineItemName;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getSublineItemName() {
		return sublineItemName;
	}

	public void setSublineItemName(String sublineItemName) {
		this.sublineItemName = sublineItemName;
	}

	public String getPremiumColumn() {
		return premiumColumn;
	}

	public void setPremiumColumn(String premiumColumn) {
		this.premiumColumn = premiumColumn;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}
}
