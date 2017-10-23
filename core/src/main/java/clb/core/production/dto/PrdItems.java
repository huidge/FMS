package clb.core.production.dto;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Rex.Hua on 17/4/12.
 */
@Table(name="fms_prd_items")
public class PrdItems extends BaseDTO {
    private static final long serialVersionUID = -2252377040632593943L;

    @Id
    @GeneratedValue(generator="IDENTITY")
    @Column
    private Long itemId;
    
    @Column
    private String itemCode;
    
    @Column
    @Condition(operator="LIKE")
    private String itemName;
    
    @Column
    private String bigClass;
    
    @Column
    private String midClass;
    
    @Column
    private String minClass;

    @Column
    private Long attSetId;

    @Column
    private Long supplierId;

    @Transient
    private String attSetName;

    @Transient
    private String supplierName;
    
    @Column
    private String enabledFlag;

    @Column
    private String comments;

    @Column
    private Long pictureFileId;

    @Column
    private String fullyear;

    @Column
    private String oneyear;

    @Column
    private String halfyear;

    @Column
    private String quarter;

    @Column
    private String onemonth;

    @Column
    private String prepayFlag;

    @Column
    private String attribute1  ;
    @Column
    private String attribute2  ;
    @Column
    private String attribute3  ;
    @Column
    private String attribute4  ;
    @Column
    private String attribute5  ;
    @Column
    private String attribute6  ;
    @Column
    private String attribute7  ;
    @Column
    private String attribute8  ;
    @Column
    private String attribute9  ;
    @Column
    private String attribute10 ;
    @Column
    private String attribute11 ;
    @Column
    private String attribute12 ;
    @Column
    private String attribute13 ;
    @Column
    private String attribute14 ;
    @Column
    private String attribute15 ;
    @Column
    private String attribute16 ;
    @Column
    private String attribute17 ;
    @Column
    private String attribute18 ;
    @Column
    private String attribute19 ;
    @Column
    private String attribute20 ;
    @Column
    private String attribute21 ;
    @Column
    private String attribute22 ;
    @Column
    private String attribute23 ;
    @Column
    private String attribute24 ;
    @Column
    private String attribute25 ;
    @Column
    private String attribute26 ;
    @Column
    private String attribute27 ;
    @Column
    private String attribute28 ;
    @Column
    private String attribute29 ;
    @Column
    private String attribute30 ;
    @Column
    private String attribute31 ;
    @Column
    private String attribute32 ;
    @Column
    private String attribute33 ;
    @Column
    private String attribute34 ;
    @Column
    private String attribute35 ;
    @Column
    private String attribute36 ;
    @Column
    private String attribute37 ;
    @Column
    private String attribute38 ;
    @Column
    private String attribute39 ;
    @Column
    private String attribute40 ;
    @Column
    private String attribute41 ;
    @Column
    private String attribute42 ;
    @Column
    private String attribute43 ;
    @Column
    private String attribute44 ;
    @Column
    private String attribute45 ;
    @Column
    private String attribute46 ;
    @Column
    private String attribute47 ;
    @Column
    private String attribute48 ;
    @Column
    private String attribute49 ;
    @Column
    private String attribute50 ;
    @Column
    private String attribute51 ;
    @Column
    private String attribute52 ;
    @Column
    private String attribute53 ;
    @Column
    private String attribute54 ;
    @Column
    private String attribute55 ;
    @Column
    private String attribute56 ;
    @Column
    private String attribute57 ;
    @Column
    private String attribute58 ;
    @Column
    private String attribute59 ;
    @Column
    private String attribute60 ;
    @Column
    private String attribute61 ;
    @Column
    private String attribute62 ;
    @Column
    private String attribute63 ;
    @Column
    private String attribute64 ;
    @Column
    private String attribute65 ;
    @Column
    private String attribute66 ;
    @Column
    private String attribute67 ;
    @Column
    private String attribute68 ;
    @Column
    private String attribute69 ;
    @Column
    private String attribute70 ;
    @Column
    private String attribute71 ;
    @Column
    private String attribute72 ;
    @Column
    private String attribute73 ;
    @Column
    private String attribute74 ;
    @Column
    private String attribute75 ;
    @Column
    private String attribute76 ;
    @Column
    private String attribute77 ;
    @Column
    private String attribute78 ;
    @Column
    private String attribute79 ;
    @Column
    private String attribute80 ;
    @Column
    private String attribute81 ;
    @Column
    private String attribute82 ;
    @Column
    private String attribute83 ;
    @Column
    private String attribute84 ;
    @Column
    private String attribute85 ;
    @Column
    private String attribute86 ;
    @Column
    private String attribute87 ;
    @Column
    private String attribute88 ;
    @Column
    private String attribute89 ;
    @Column
    private String attribute90 ;
    @Column
    private String attribute91 ;
    @Column
    private String attribute92 ;
    @Column
    private String attribute93 ;
    @Column
    private String attribute94 ;
    @Column
    private String attribute95 ;
    @Column
    private String attribute96 ;
    @Column
    private String attribute97 ;
    @Column
    private String attribute98 ;
    @Column
    private String attribute99 ;
    @Column
    private String attribute100;
    @Column
    private String attribute101;
    @Column
    private String attribute102;
    @Column
    private String attribute103;
    @Column
    private String attribute104;
    @Column
    private String attribute105;
    @Column
    private String attribute106;
    @Column
    private String attribute107;
    @Column
    private String attribute108;
    @Column
    private String attribute109;
    @Column
    private String attribute110;
    @Column
    private String attribute111;
    @Column
    private String attribute112;
    @Column
    private String attribute113;
    @Column
    private String attribute114;
    @Column
    private String attribute115;
    @Column
    private String attribute116;
    @Column
    private String attribute117;
    @Column
    private String attribute118;
    @Column
    private String attribute119;
    @Column
    private String attribute120;
    @Column
    private Double oneyearRate;
    @Column
    private Double halfyearRate;
    @Column
    private Double quarterRate;
    @Column
    private Double onemonthRate;
    //前端传递中文名
    @Transient
    private String className;
    @Transient
    private String bigClassName;
    @Transient
    private String midClassName;
    @Transient
    private String minClassName;
    // 年利息
    @Transient
    private String annualInterestFrom;
    @Transient
    private String annualInterestTo;
    // 市值（港元）
    @Transient
    private String marketValueFrom;
    @Transient
    private String marketValueTo;
    // 投资金额
    @Transient
    private String investmentAmountFrom;
    @Transient
    private String investmentAmountTo;
    // 办理周期
    @Transient
    private String transactionDataFrom;
    @Transient
    private String transactionDataTo;

    @Transient
    private String ageFrom;
    @Transient
    private String ageTo;
    @Transient
    private String pictureFilePath;
    @Transient
    private String sublineItemName;
    @Transient
    private String attributeCondition;
    @Transient
    private String itemLabel;
    @Transient
    private String hotRecommendFlag;
    @Transient
    private String discountFlag;
    @Transient
    private String currencyCode;
    //产品优惠信息
    @Transient
    private List<PrdDiscount> prdDiscountList;

    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    @Transient
    private List<PrdAttributeSetLine> prdAttributeSetLine;

    @Transient
    private List<PrdItemPaymode> prdItemPaymode;

    @Transient
    private List<PrdItemLabelLines> prdItemLabelLinesList;

    @Transient
    private List<PrdItemSubline> prdItemSublineList;

    @Transient
    private List<PrdItemSelfpay> prdItemSelfpayList;

    @Transient
    private List<PrdItemSecurityPlan> prdItemSecurityPlan;

    @Transient
    private List<PrdImageText> prdImageText;

    @Transient
    private Long channelId;

    @Transient
    private Long customerId;

    @Transient
    private BigDecimal orderAmount;

    @Transient
    private String unAdditionalRiskFlag;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public Long getAttSetId() {
        return attSetId;
    }

    public void setAttSetId(Long attSetId) {
        this.attSetId = attSetId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getAttSetName() {
        return attSetName;
    }

    public void setAttSetName(String attSetName) {
        this.attSetName = attSetName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getPictureFileId() {
        return pictureFileId;
    }

    public void setPictureFileId(Long pictureFileId) {
        this.pictureFileId = pictureFileId;
    }

    public String getFullyear() {
        return fullyear;
    }

    public void setFullyear(String fullyear) {
        this.fullyear = fullyear;
    }

    public String getOneyear() {
        return oneyear;
    }

    public void setOneyear(String oneyear) {
        this.oneyear = oneyear;
    }

    public String getHalfyear() {
        return halfyear;
    }

    public void setHalfyear(String halfyear) {
        this.halfyear = halfyear;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getOnemonth() {
        return onemonth;
    }

    public void setOnemonth(String onemonth) {
        this.onemonth = onemonth;
    }

    public String getPrepayFlag() {
//        if(prepayFlag==null){
//            return "N";
//        }
        return prepayFlag;
    }

    public void setPrepayFlag(String prepayFlag) {
        this.prepayFlag = prepayFlag;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String getAttribute1() {
        return attribute1;
    }

    @Override
    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
    }

    @Override
    public String getAttribute2() {
        return attribute2;
    }

    @Override
    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
    }

    @Override
    public String getAttribute3() {
        return attribute3;
    }

    @Override
    public void setAttribute3(String attribute3) {
        this.attribute3 = attribute3;
    }

    @Override
    public String getAttribute4() {
        return attribute4;
    }

    @Override
    public void setAttribute4(String attribute4) {
        this.attribute4 = attribute4;
    }

    @Override
    public String getAttribute5() {
        return attribute5;
    }

    @Override
    public void setAttribute5(String attribute5) {
        this.attribute5 = attribute5;
    }

    @Override
    public String getAttribute6() {
        return attribute6;
    }

    @Override
    public void setAttribute6(String attribute6) {
        this.attribute6 = attribute6;
    }

    @Override
    public String getAttribute7() {
        return attribute7;
    }

    @Override
    public void setAttribute7(String attribute7) {
        this.attribute7 = attribute7;
    }

    @Override
    public String getAttribute8() {
        return attribute8;
    }

    @Override
    public void setAttribute8(String attribute8) {
        this.attribute8 = attribute8;
    }

    @Override
    public String getAttribute9() {
        return attribute9;
    }

    @Override
    public void setAttribute9(String attribute9) {
        this.attribute9 = attribute9;
    }

    @Override
    public String getAttribute10() {
        return attribute10;
    }

    @Override
    public void setAttribute10(String attribute10) {
        this.attribute10 = attribute10;
    }

    @Override
    public String getAttribute11() {
        return attribute11;
    }

    @Override
    public void setAttribute11(String attribute11) {
        this.attribute11 = attribute11;
    }

    @Override
    public String getAttribute12() {
        return attribute12;
    }

    @Override
    public void setAttribute12(String attribute12) {
        this.attribute12 = attribute12;
    }

    @Override
    public String getAttribute13() {
        return attribute13;
    }

    @Override
    public void setAttribute13(String attribute13) {
        this.attribute13 = attribute13;
    }

    @Override
    public String getAttribute14() {
        return attribute14;
    }

    @Override
    public void setAttribute14(String attribute14) {
        this.attribute14 = attribute14;
    }

    @Override
    public String getAttribute15() {
        return attribute15;
    }

    @Override
    public void setAttribute15(String attribute15) {
        this.attribute15 = attribute15;
    }

    public String getAttribute16() {
        return attribute16;
    }

    public void setAttribute16(String attribute16) {
        this.attribute16 = attribute16;
    }

    public String getAttribute17() {
        return attribute17;
    }

    public void setAttribute17(String attribute17) {
        this.attribute17 = attribute17;
    }

    public String getAttribute18() {
        return attribute18;
    }

    public void setAttribute18(String attribute18) {
        this.attribute18 = attribute18;
    }

    public String getAttribute19() {
        return attribute19;
    }

    public void setAttribute19(String attribute19) {
        this.attribute19 = attribute19;
    }

    public String getAttribute20() {
        return attribute20;
    }

    public void setAttribute20(String attribute20) {
        this.attribute20 = attribute20;
    }

    public String getAttribute21() {
        return attribute21;
    }

    public void setAttribute21(String attribute21) {
        this.attribute21 = attribute21;
    }

    public String getAttribute22() {
        return attribute22;
    }

    public void setAttribute22(String attribute22) {
        this.attribute22 = attribute22;
    }

    public String getAttribute23() {
        return attribute23;
    }

    public void setAttribute23(String attribute23) {
        this.attribute23 = attribute23;
    }

    public String getAttribute24() {
        return attribute24;
    }

    public void setAttribute24(String attribute24) {
        this.attribute24 = attribute24;
    }

    public String getAttribute25() {
        return attribute25;
    }

    public void setAttribute25(String attribute25) {
        this.attribute25 = attribute25;
    }

    public String getAttribute26() {
        return attribute26;
    }

    public void setAttribute26(String attribute26) {
        this.attribute26 = attribute26;
    }

    public String getAttribute27() {
        return attribute27;
    }

    public void setAttribute27(String attribute27) {
        this.attribute27 = attribute27;
    }

    public String getAttribute28() {
        return attribute28;
    }

    public void setAttribute28(String attribute28) {
        this.attribute28 = attribute28;
    }

    public String getAttribute29() {
        return attribute29;
    }

    public void setAttribute29(String attribute29) {
        this.attribute29 = attribute29;
    }

    public String getAttribute30() {
        return attribute30;
    }

    public void setAttribute30(String attribute30) {
        this.attribute30 = attribute30;
    }

    public String getAttribute31() {
        return attribute31;
    }

    public void setAttribute31(String attribute31) {
        this.attribute31 = attribute31;
    }

    public String getAttribute32() {
        return attribute32;
    }

    public void setAttribute32(String attribute32) {
        this.attribute32 = attribute32;
    }

    public String getAttribute33() {
        return attribute33;
    }

    public void setAttribute33(String attribute33) {
        this.attribute33 = attribute33;
    }

    public String getAttribute34() {
        return attribute34;
    }

    public void setAttribute34(String attribute34) {
        this.attribute34 = attribute34;
    }

    public String getAttribute35() {
        return attribute35;
    }

    public void setAttribute35(String attribute35) {
        this.attribute35 = attribute35;
    }

    public String getAttribute36() {
        return attribute36;
    }

    public void setAttribute36(String attribute36) {
        this.attribute36 = attribute36;
    }

    public String getAttribute37() {
        return attribute37;
    }

    public void setAttribute37(String attribute37) {
        this.attribute37 = attribute37;
    }

    public String getAttribute38() {
        return attribute38;
    }

    public void setAttribute38(String attribute38) {
        this.attribute38 = attribute38;
    }

    public String getAttribute39() {
        return attribute39;
    }

    public void setAttribute39(String attribute39) {
        this.attribute39 = attribute39;
    }

    public String getAttribute40() {
        return attribute40;
    }

    public void setAttribute40(String attribute40) {
        this.attribute40 = attribute40;
    }

    public String getAttribute41() {
        return attribute41;
    }

    public void setAttribute41(String attribute41) {
        this.attribute41 = attribute41;
    }

    public String getAttribute42() {
        return attribute42;
    }

    public void setAttribute42(String attribute42) {
        this.attribute42 = attribute42;
    }

    public String getAttribute43() {
        return attribute43;
    }

    public void setAttribute43(String attribute43) {
        this.attribute43 = attribute43;
    }

    public String getAttribute44() {
        return attribute44;
    }

    public void setAttribute44(String attribute44) {
        this.attribute44 = attribute44;
    }

    public String getAttribute45() {
        return attribute45;
    }

    public void setAttribute45(String attribute45) {
        this.attribute45 = attribute45;
    }

    public String getAttribute46() {
        return attribute46;
    }

    public void setAttribute46(String attribute46) {
        this.attribute46 = attribute46;
    }

    public String getAttribute47() {
        return attribute47;
    }

    public void setAttribute47(String attribute47) {
        this.attribute47 = attribute47;
    }

    public String getAttribute48() {
        return attribute48;
    }

    public void setAttribute48(String attribute48) {
        this.attribute48 = attribute48;
    }

    public String getAttribute49() {
        return attribute49;
    }

    public void setAttribute49(String attribute49) {
        this.attribute49 = attribute49;
    }

    public String getAttribute50() {
        return attribute50;
    }

    public void setAttribute50(String attribute50) {
        this.attribute50 = attribute50;
    }

    public String getAttribute51() {
        return attribute51;
    }

    public void setAttribute51(String attribute51) {
        this.attribute51 = attribute51;
    }

    public String getAttribute52() {
        return attribute52;
    }

    public void setAttribute52(String attribute52) {
        this.attribute52 = attribute52;
    }

    public String getAttribute53() {
        return attribute53;
    }

    public void setAttribute53(String attribute53) {
        this.attribute53 = attribute53;
    }

    public String getAttribute54() {
        return attribute54;
    }

    public void setAttribute54(String attribute54) {
        this.attribute54 = attribute54;
    }

    public String getAttribute55() {
        return attribute55;
    }

    public void setAttribute55(String attribute55) {
        this.attribute55 = attribute55;
    }

    public String getAttribute56() {
        return attribute56;
    }

    public void setAttribute56(String attribute56) {
        this.attribute56 = attribute56;
    }

    public String getAttribute57() {
        return attribute57;
    }

    public void setAttribute57(String attribute57) {
        this.attribute57 = attribute57;
    }

    public String getAttribute58() {
        return attribute58;
    }

    public void setAttribute58(String attribute58) {
        this.attribute58 = attribute58;
    }

    public String getAttribute59() {
        return attribute59;
    }

    public void setAttribute59(String attribute59) {
        this.attribute59 = attribute59;
    }

    public String getAttribute60() {
        return attribute60;
    }

    public void setAttribute60(String attribute60) {
        this.attribute60 = attribute60;
    }

    public String getAttribute61() {
        return attribute61;
    }

    public void setAttribute61(String attribute61) {
        this.attribute61 = attribute61;
    }

    public String getAttribute62() {
        return attribute62;
    }

    public void setAttribute62(String attribute62) {
        this.attribute62 = attribute62;
    }

    public String getAttribute63() {
        return attribute63;
    }

    public void setAttribute63(String attribute63) {
        this.attribute63 = attribute63;
    }

    public String getAttribute64() {
        return attribute64;
    }

    public void setAttribute64(String attribute64) {
        this.attribute64 = attribute64;
    }

    public String getAttribute65() {
        return attribute65;
    }

    public void setAttribute65(String attribute65) {
        this.attribute65 = attribute65;
    }

    public String getAttribute66() {
        return attribute66;
    }

    public void setAttribute66(String attribute66) {
        this.attribute66 = attribute66;
    }

    public String getAttribute67() {
        return attribute67;
    }

    public void setAttribute67(String attribute67) {
        this.attribute67 = attribute67;
    }

    public String getAttribute68() {
        return attribute68;
    }

    public void setAttribute68(String attribute68) {
        this.attribute68 = attribute68;
    }

    public String getAttribute69() {
        return attribute69;
    }

    public void setAttribute69(String attribute69) {
        this.attribute69 = attribute69;
    }

    public String getAttribute70() {
        return attribute70;
    }

    public void setAttribute70(String attribute70) {
        this.attribute70 = attribute70;
    }

    public String getAttribute71() {
        return attribute71;
    }

    public void setAttribute71(String attribute71) {
        this.attribute71 = attribute71;
    }

    public String getAttribute72() {
        return attribute72;
    }

    public void setAttribute72(String attribute72) {
        this.attribute72 = attribute72;
    }

    public String getAttribute73() {
        return attribute73;
    }

    public void setAttribute73(String attribute73) {
        this.attribute73 = attribute73;
    }

    public String getAttribute74() {
        return attribute74;
    }

    public void setAttribute74(String attribute74) {
        this.attribute74 = attribute74;
    }

    public String getAttribute75() {
        return attribute75;
    }

    public void setAttribute75(String attribute75) {
        this.attribute75 = attribute75;
    }

    public String getAttribute76() {
        return attribute76;
    }

    public void setAttribute76(String attribute76) {
        this.attribute76 = attribute76;
    }

    public String getAttribute77() {
        return attribute77;
    }

    public void setAttribute77(String attribute77) {
        this.attribute77 = attribute77;
    }

    public String getAttribute78() {
        return attribute78;
    }

    public void setAttribute78(String attribute78) {
        this.attribute78 = attribute78;
    }

    public String getAttribute79() {
        return attribute79;
    }

    public void setAttribute79(String attribute79) {
        this.attribute79 = attribute79;
    }

    public String getAttribute80() {
        return attribute80;
    }

    public void setAttribute80(String attribute80) {
        this.attribute80 = attribute80;
    }

    public String getAttribute81() {
        return attribute81;
    }

    public void setAttribute81(String attribute81) {
        this.attribute81 = attribute81;
    }

    public String getAttribute82() {
        return attribute82;
    }

    public void setAttribute82(String attribute82) {
        this.attribute82 = attribute82;
    }

    public String getAttribute83() {
        return attribute83;
    }

    public void setAttribute83(String attribute83) {
        this.attribute83 = attribute83;
    }

    public String getAttribute84() {
        return attribute84;
    }

    public void setAttribute84(String attribute84) {
        this.attribute84 = attribute84;
    }

    public String getAttribute85() {
        return attribute85;
    }

    public void setAttribute85(String attribute85) {
        this.attribute85 = attribute85;
    }

    public String getAttribute86() {
        return attribute86;
    }

    public void setAttribute86(String attribute86) {
        this.attribute86 = attribute86;
    }

    public String getAttribute87() {
        return attribute87;
    }

    public void setAttribute87(String attribute87) {
        this.attribute87 = attribute87;
    }

    public String getAttribute88() {
        return attribute88;
    }

    public void setAttribute88(String attribute88) {
        this.attribute88 = attribute88;
    }

    public String getAttribute89() {
        return attribute89;
    }

    public void setAttribute89(String attribute89) {
        this.attribute89 = attribute89;
    }

    public String getAttribute90() {
        return attribute90;
    }

    public void setAttribute90(String attribute90) {
        this.attribute90 = attribute90;
    }

    public String getAttribute91() {
        return attribute91;
    }

    public void setAttribute91(String attribute91) {
        this.attribute91 = attribute91;
    }

    public String getAttribute92() {
        return attribute92;
    }

    public void setAttribute92(String attribute92) {
        this.attribute92 = attribute92;
    }

    public String getAttribute93() {
        return attribute93;
    }

    public void setAttribute93(String attribute93) {
        this.attribute93 = attribute93;
    }

    public String getAttribute94() {
        return attribute94;
    }

    public void setAttribute94(String attribute94) {
        this.attribute94 = attribute94;
    }

    public String getAttribute95() {
        return attribute95;
    }

    public void setAttribute95(String attribute95) {
        this.attribute95 = attribute95;
    }

    public String getAttribute96() {
        return attribute96;
    }

    public void setAttribute96(String attribute96) {
        this.attribute96 = attribute96;
    }

    public String getAttribute97() {
        return attribute97;
    }

    public void setAttribute97(String attribute97) {
        this.attribute97 = attribute97;
    }

    public String getAttribute98() {
        return attribute98;
    }

    public void setAttribute98(String attribute98) {
        this.attribute98 = attribute98;
    }

    public String getAttribute99() {
        return attribute99;
    }

    public void setAttribute99(String attribute99) {
        this.attribute99 = attribute99;
    }

    public String getAttribute100() {
        return attribute100;
    }

    public void setAttribute100(String attribute100) {
        this.attribute100 = attribute100;
    }

    public String getAttribute101() {
        return attribute101;
    }

    public void setAttribute101(String attribute101) {
        this.attribute101 = attribute101;
    }

    public String getAttribute102() {
        return attribute102;
    }

    public void setAttribute102(String attribute102) {
        this.attribute102 = attribute102;
    }

    public String getAttribute103() {
        return attribute103;
    }

    public void setAttribute103(String attribute103) {
        this.attribute103 = attribute103;
    }

    public String getAttribute104() {
        return attribute104;
    }

    public void setAttribute104(String attribute104) {
        this.attribute104 = attribute104;
    }

    public String getAttribute105() {
        return attribute105;
    }

    public void setAttribute105(String attribute105) {
        this.attribute105 = attribute105;
    }

    public String getAttribute106() {
        return attribute106;
    }

    public void setAttribute106(String attribute106) {
        this.attribute106 = attribute106;
    }

    public String getAttribute107() {
        return attribute107;
    }

    public void setAttribute107(String attribute107) {
        this.attribute107 = attribute107;
    }

    public String getAttribute108() {
        return attribute108;
    }

    public void setAttribute108(String attribute108) {
        this.attribute108 = attribute108;
    }

    public String getAttribute109() {
        return attribute109;
    }

    public void setAttribute109(String attribute109) {
        this.attribute109 = attribute109;
    }

    public String getAttribute110() {
        return attribute110;
    }

    public void setAttribute110(String attribute110) {
        this.attribute110 = attribute110;
    }

    public String getAttribute111() {
        return attribute111;
    }

    public void setAttribute111(String attribute111) {
        this.attribute111 = attribute111;
    }

    public String getAttribute112() {
        return attribute112;
    }

    public void setAttribute112(String attribute112) {
        this.attribute112 = attribute112;
    }

    public String getAttribute113() {
        return attribute113;
    }

    public void setAttribute113(String attribute113) {
        this.attribute113 = attribute113;
    }

    public String getAttribute114() {
        return attribute114;
    }

    public void setAttribute114(String attribute114) {
        this.attribute114 = attribute114;
    }

    public String getAttribute115() {
        return attribute115;
    }

    public void setAttribute115(String attribute115) {
        this.attribute115 = attribute115;
    }

    public String getAttribute116() {
        return attribute116;
    }

    public void setAttribute116(String attribute116) {
        this.attribute116 = attribute116;
    }

    public String getAttribute117() {
        return attribute117;
    }

    public void setAttribute117(String attribute117) {
        this.attribute117 = attribute117;
    }

    public String getAttribute118() {
        return attribute118;
    }

    public void setAttribute118(String attribute118) {
        this.attribute118 = attribute118;
    }

    public String getAttribute119() {
        return attribute119;
    }

    public void setAttribute119(String attribute119) {
        this.attribute119 = attribute119;
    }

    public String getAttribute120() {
        return attribute120;
    }

    public void setAttribute120(String attribute120) {
        this.attribute120 = attribute120;
    }

    public List<PrdItemLabelLines> getPrdItemLabelLinesList() {
        return prdItemLabelLinesList;
    }

    public void setPrdItemLabelLinesList(List<PrdItemLabelLines> prdItemLabelLinesList) {
        this.prdItemLabelLinesList = prdItemLabelLinesList;
    }

    public List<PrdAttributeSetLine> getPrdAttributeSetLine() {
        return prdAttributeSetLine;
    }

    public void setPrdAttributeSetLine(List<PrdAttributeSetLine> prdAttributeSetLine) {
        this.prdAttributeSetLine = prdAttributeSetLine;
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

    public String getAgeFrom() {
        return ageFrom;
    }

    public void setAgeFrom(String ageFrom) {
        this.ageFrom = ageFrom;
    }

    public String getAgeTo() {
        return ageTo;
    }

    public void setAgeTo(String ageTo) {
        this.ageTo = ageTo;
    }

    public String getPictureFilePath() {
        return pictureFilePath;
    }

    public void setPictureFilePath(String pictureFilePath) {
        this.pictureFilePath = pictureFilePath;
    }

    public List<PrdItemSubline> getPrdItemSublineList() {
        return prdItemSublineList;
    }

    public void setPrdItemSublineList(List<PrdItemSubline> prdItemSublineList) {
        this.prdItemSublineList = prdItemSublineList;
    }

	public Double getOneyearRate() {
		return oneyearRate;
	}

	public void setOneyearRate(Double oneyearRate) {
		this.oneyearRate = oneyearRate;
	}

	public Double getHalfyearRate() {
		return halfyearRate;
	}

	public void setHalfyearRate(Double halfyearRate) {
		this.halfyearRate = halfyearRate;
	}

	public Double getQuarterRate() {
		return quarterRate;
	}

	public void setQuarterRate(Double quarterRate) {
		this.quarterRate = quarterRate;
	}

	public Double getOnemonthRate() {
		return onemonthRate;
	}

	public void setOnemonthRate(Double onemonthRate) {
		this.onemonthRate = onemonthRate;
	}

    public List<PrdItemSecurityPlan> getPrdItemSecurityPlan() {
        return prdItemSecurityPlan;
    }

    public void setPrdItemSecurityPlan(List<PrdItemSecurityPlan> prdItemSecurityPlan) {
        this.prdItemSecurityPlan = prdItemSecurityPlan;
    }

    public List<PrdItemSelfpay> getPrdItemSelfpayList() {
        return prdItemSelfpayList;
    }

    public void setPrdItemSelfpayList(List<PrdItemSelfpay> prdItemSelfpayList) {
        this.prdItemSelfpayList = prdItemSelfpayList;
    }

    public List<PrdItemPaymode> getPrdItemPaymode() {
        return prdItemPaymode;
    }

    public void setPrdItemPaymode(List<PrdItemPaymode> prdItemPaymode) {
        this.prdItemPaymode = prdItemPaymode;
    }

    public List<PrdImageText> getPrdImageText() {
        return prdImageText;
    }

    public void setPrdImageText(List<PrdImageText> prdImageText) {
        this.prdImageText = prdImageText;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    @Transient
    private Double yearPermium;

	public Double getYearPermium() {
		return yearPermium;
	}

	public void setYearPermium(Double yearPermium) {
		this.yearPermium = yearPermium;
	}
	
	@Transient
	private Double surrenderCash30;
	@Transient
	private Double surrenderCash40;
	@Transient
	private Double surrenderCash50;
	@Transient
	private Double surrenderCash60;
	@Transient
	private Double dieCash30;
	@Transient
	private Double dieCash40;
	@Transient
	private Double dieCash50;
	@Transient
	private Double dieCash60;

	public Double getSurrenderCash30() {
		return surrenderCash30;
	}

	public void setSurrenderCash30(Double surrenderCash30) {
		this.surrenderCash30 = surrenderCash30;
	}

	public Double getSurrenderCash40() {
		return surrenderCash40;
	}

	public void setSurrenderCash40(Double surrenderCash40) {
		this.surrenderCash40 = surrenderCash40;
	}

	public Double getSurrenderCash50() {
		return surrenderCash50;
	}

	public void setSurrenderCash50(Double surrenderCash50) {
		this.surrenderCash50 = surrenderCash50;
	}

	public Double getSurrenderCash60() {
		return surrenderCash60;
	}

	public void setSurrenderCash60(Double surrenderCash60) {
		this.surrenderCash60 = surrenderCash60;
	}

	public Double getDieCash30() {
		return dieCash30;
	}

	public void setDieCash30(Double dieCash30) {
		this.dieCash30 = dieCash30;
	}

	public Double getDieCash40() {
		return dieCash40;
	}

	public void setDieCash40(Double dieCash40) {
		this.dieCash40 = dieCash40;
	}

	public Double getDieCash50() {
		return dieCash50;
	}

	public void setDieCash50(Double dieCash50) {
		this.dieCash50 = dieCash50;
	}

	public Double getDieCash60() {
		return dieCash60;
	}

	public void setDieCash60(Double dieCash60) {
		this.dieCash60 = dieCash60;
	}

    public String getSublineItemName() {
        return sublineItemName;
    }

    public void setSublineItemName(String sublineItemName) {
        this.sublineItemName = sublineItemName;
    }

    public String getAttributeCondition() {
        return attributeCondition;
    }

    public void setAttributeCondition(String attributeCondition) {
        this.attributeCondition = attributeCondition;
    }

    public String getAnnualInterestFrom() {
        return annualInterestFrom;
    }

    public void setAnnualInterestFrom(String annualInterestFrom) {
        this.annualInterestFrom = annualInterestFrom;
    }

    public String getAnnualInterestTo() {
        return annualInterestTo;
    }

    public void setAnnualInterestTo(String annualInterestTo) {
        this.annualInterestTo = annualInterestTo;
    }

    public String getMarketValueFrom() {
        return marketValueFrom;
    }

    public void setMarketValueFrom(String marketValueFrom) {
        this.marketValueFrom = marketValueFrom;
    }

    public String getMarketValueTo() {
        return marketValueTo;
    }

    public void setMarketValueTo(String marketValueTo) {
        this.marketValueTo = marketValueTo;
    }

    public String getInvestmentAmountFrom() {
        return investmentAmountFrom;
    }

    public void setInvestmentAmountFrom(String investmentAmountFrom) {
        this.investmentAmountFrom = investmentAmountFrom;
    }

    public String getInvestmentAmountTo() {
        return investmentAmountTo;
    }

    public void setInvestmentAmountTo(String investmentAmountTo) {
        this.investmentAmountTo = investmentAmountTo;
    }

    public String getTransactionDataFrom() {
        return transactionDataFrom;
    }

    public void setTransactionDataFrom(String transactionDataFrom) {
        this.transactionDataFrom = transactionDataFrom;
    }

    public String getTransactionDataTo() {
        return transactionDataTo;
    }

    public void setTransactionDataTo(String transactionDataTo) {
        this.transactionDataTo = transactionDataTo;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<PrdDiscount> getPrdDiscountList() {
        return prdDiscountList;
    }

    public void setPrdDiscountList(List<PrdDiscount> prdDiscountList) {
        this.prdDiscountList = prdDiscountList;
    }

    public String getHotRecommendFlag() {
        return hotRecommendFlag;
    }

    public void setHotRecommendFlag(String hotRecommendFlag) {
        this.hotRecommendFlag = hotRecommendFlag;
    }

    public String getDiscountFlag() {
        return discountFlag;
    }

    public void setDiscountFlag(String discountFlag) {
        this.discountFlag = discountFlag;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getUnAdditionalRiskFlag() {
        return unAdditionalRiskFlag;
    }

    public void setUnAdditionalRiskFlag(String unAdditionalRiskFlag) {
        this.unAdditionalRiskFlag = unAdditionalRiskFlag;
    }
}
