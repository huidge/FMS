package clb.core.fnd.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.system.dto.BaseDTO;

/**
 * @name ImportTemp
 * @author junqiang.xiao@hand-china.com 2016年12月20日下午1:20:19
 * @description 通用导入临时表
 * @version
 */
@Table(name = "FND_IMPORT_TEMP")
public class ImportTemp extends BaseDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String ERROR_STATUS = "E";
	public static final String SUCCESS_STATUS = "S";
	public static final String WARNING_STAUTS = "W";
	public static final String NEW_STAUTS = "N";

	@Id
	@Column
	@GeneratedValue(generator = GENERATOR_TYPE)
	private Long importTempId;
	@Column
	@NotNull
	private Long importBatchId;
	@Column
	@NotEmpty
	private String importCode = "FHI";
	@Column
	private Long lineNumber;
	@Column
	private String origFileName;
	@Column
	private String importStatus;
	@Column
	private String importMessage;
	@Column
	private String sheet;
	@Column
	private String attributeCategory;

	private String attribute1;

	private String attribute2;

	private String attribute3;

	private String attribute4;

	private String attribute5;

	private String attribute6;

	private String attribute7;

	private String attribute8;

	private String attribute9;

	private String attribute10;

	private String attribute11;

	private String attribute12;

	private String attribute13;

	private String attribute14;

	private String attribute15;

	private String attribute16;

	private String attribute17;

	private String attribute18;

	private String attribute19;

	private String attribute20;

	private String attribute21;

	private String attribute22;

	private String attribute23;

	private String attribute24;

	private String attribute25;

	private String attribute26;

	private String attribute27;

	private String attribute28;

	private String attribute29;

	private String attribute30;

	//add by zhaojun 20170725
	private String attribute31;
	private String attribute32;
	private String attribute33;
	private String attribute34;
	private String attribute35;
	private String attribute36;
	private String attribute37;
	private String attribute38;
	private String attribute39;
	private String attribute40;
	private String attribute41;
	private String attribute42;
	private String attribute43;
	private String attribute44;
	private String attribute45;
	private String attribute46;
	private String attribute47;
	private String attribute48;
	private String attribute49;
	private String attribute50;
	private String attribute51;
	private String attribute52;
	private String attribute53;
	private String attribute54;
	private String attribute55;
	private String attribute56;
	private String attribute57;
	private String attribute58;
	private String attribute59;
	private String attribute60;
	private String attribute61;
	private String attribute62;
	private String attribute63;
	private String attribute64;
	private String attribute65;
	private String attribute66;
	private String attribute67;
	private String attribute68;
	private String attribute69;
	private String attribute70;
	private String attribute71;
	private String attribute72;
	private String attribute73;
	private String attribute74;
	private String attribute75;
	private String attribute76;
	private String attribute77;
	private String attribute78;
	private String attribute79;
	private String attribute80;
	private String attribute81;
	private String attribute82;
	private String attribute83;
	private String attribute84;
	private String attribute85;
	private String attribute86;
	private String attribute87;
	private String attribute88;
	private String attribute89;
	private String attribute90;
	private String attribute91;
	private String attribute92;
	private String attribute93;
	private String attribute94;
	private String attribute95;
	private String attribute96;
	private String attribute97;
	private String attribute98;
	private String attribute99;
	private String attribute100;
	private String attribute101;
	private String attribute102;
	private String attribute103;
	private String attribute104;
	private String attribute105;
	private String attribute106;
	private String attribute107;
	private String attribute108;
	private String attribute109;
	private String attribute110;
	private String attribute111;
	private String attribute112;
	private String attribute113;
	private String attribute114;
	private String attribute115;
	private String attribute116;
	private String attribute117;
	private String attribute118;
	private String attribute119;
	private String attribute120;
	private String attribute121;
	private String attribute122;
	private String attribute123;
	private String attribute124;
	private String attribute125;
	private String attribute126;
	private String attribute127;
	private String attribute128;
	private String attribute129;
	private String attribute130;
	private String attribute131;
	private String attribute132;
	private String attribute133;
	private String attribute134;
	private String attribute135;
	private String attribute136;
	private String attribute137;
	private String attribute138;
	private String attribute139;
	private String attribute140;
	private String attribute141;
	private String attribute142;
	private String attribute143;
	private String attribute144;
	private String attribute145;
	private String attribute146;
	private String attribute147;
	private String attribute148;
	private String attribute149;
	private String attribute150;

	private String convertedAttribute1;

	private String convertedAttribute2;

	private String convertedAttribute3;

	private String convertedAttribute4;

	private String convertedAttribute5;

	private String convertedAttribute6;

	private String convertedAttribute7;

	private String convertedAttribute8;

	private String convertedAttribute9;

	private String convertedAttribute10;

	private String convertedAttribute11;

	private String convertedAttribute12;

	private String convertedAttribute13;

	private String convertedAttribute14;

	private String convertedAttribute15;

	private String convertedAttribute16;

	private String convertedAttribute17;

	private String convertedAttribute18;

	private String convertedAttribute19;

	private String convertedAttribute20;

	private String convertedAttribute21;

	private String convertedAttribute22;

	private String convertedAttribute23;

	private String convertedAttribute24;

	private String convertedAttribute25;

	private String convertedAttribute26;

	private String convertedAttribute27;

	private String convertedAttribute28;

	private String convertedAttribute29;

	private String convertedAttribute30;

	private Long objectVersionNumber;

	private String lineContent;


	public Long getImportTempId() {
		return importTempId;
	}

	public void setImportTempId(Long importTempId) {
		this.importTempId = importTempId;
	}

	public Long getImportBatchId() {
		return importBatchId;
	}

	public void setImportBatchId(Long importBatchId) {
		this.importBatchId = importBatchId;
	}

	public String getImportCode() {
		return importCode;
	}

	public void setImportCode(String importCode) {
		this.importCode = importCode == null ? null : importCode.trim();
	}

	public Long getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Long lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getOrigFileName() {
		return origFileName;
	}

	public void setOrigFileName(String origFileName) {
		this.origFileName = origFileName == null ? null : origFileName.trim();
	}

	public String getImportStatus() {
		return importStatus;
	}

	public void setImportStatus(String importStatus) {
		this.importStatus = importStatus == null ? null : importStatus.trim();
	}

	public String getImportMessage() {
		return importMessage;
	}

	public void setImportMessage(String importMessage) {
		this.importMessage = importMessage == null ? null : importMessage.trim();
	}

	public String getSheet() {
		return sheet;
	}

	public void setSheet(String sheet) {
		this.sheet = sheet == null ? null : sheet.trim();
	}

	public String getAttributeCategory() {
		return attributeCategory;
	}

	public void setAttributeCategory(String attributeCategory) {
		this.attributeCategory = attributeCategory == null ? null : attributeCategory.trim();
	}

	public String getAttribute1() {
		return attribute1;
	}

	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1 == null ? null : attribute1.trim();
	}

	public String getAttribute2() {
		return attribute2;
	}

	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2 == null ? null : attribute2.trim();
	}

	public String getAttribute3() {
		return attribute3;
	}

	public void setAttribute3(String attribute3) {
		this.attribute3 = attribute3 == null ? null : attribute3.trim();
	}

	public String getAttribute4() {
		return attribute4;
	}

	public void setAttribute4(String attribute4) {
		this.attribute4 = attribute4 == null ? null : attribute4.trim();
	}

	public String getAttribute5() {
		return attribute5;
	}

	public void setAttribute5(String attribute5) {
		this.attribute5 = attribute5 == null ? null : attribute5.trim();
	}

	public String getAttribute6() {
		return attribute6;
	}

	public void setAttribute6(String attribute6) {
		this.attribute6 = attribute6 == null ? null : attribute6.trim();
	}

	public String getAttribute7() {
		return attribute7;
	}

	public void setAttribute7(String attribute7) {
		this.attribute7 = attribute7 == null ? null : attribute7.trim();
	}

	public String getAttribute8() {
		return attribute8;
	}

	public void setAttribute8(String attribute8) {
		this.attribute8 = attribute8 == null ? null : attribute8.trim();
	}

	public String getAttribute9() {
		return attribute9;
	}

	public void setAttribute9(String attribute9) {
		this.attribute9 = attribute9 == null ? null : attribute9.trim();
	}

	public String getAttribute10() {
		return attribute10;
	}

	public void setAttribute10(String attribute10) {
		this.attribute10 = attribute10 == null ? null : attribute10.trim();
	}

	public String getAttribute11() {
		return attribute11;
	}

	public void setAttribute11(String attribute11) {
		this.attribute11 = attribute11 == null ? null : attribute11.trim();
	}

	public String getAttribute12() {
		return attribute12;
	}

	public void setAttribute12(String attribute12) {
		this.attribute12 = attribute12 == null ? null : attribute12.trim();
	}

	public String getAttribute13() {
		return attribute13;
	}

	public void setAttribute13(String attribute13) {
		this.attribute13 = attribute13 == null ? null : attribute13.trim();
	}

	public String getAttribute14() {
		return attribute14;
	}

	public void setAttribute14(String attribute14) {
		this.attribute14 = attribute14 == null ? null : attribute14.trim();
	}

	public String getAttribute15() {
		return attribute15;
	}

	public void setAttribute15(String attribute15) {
		this.attribute15 = attribute15 == null ? null : attribute15.trim();
	}

	public String getAttribute16() {
		return attribute16;
	}

	public void setAttribute16(String attribute16) {
		this.attribute16 = attribute16 == null ? null : attribute16.trim();
	}

	public String getAttribute17() {
		return attribute17;
	}

	public void setAttribute17(String attribute17) {
		this.attribute17 = attribute17 == null ? null : attribute17.trim();
	}

	public String getAttribute18() {
		return attribute18;
	}

	public void setAttribute18(String attribute18) {
		this.attribute18 = attribute18 == null ? null : attribute18.trim();
	}

	public String getAttribute19() {
		return attribute19;
	}

	public void setAttribute19(String attribute19) {
		this.attribute19 = attribute19 == null ? null : attribute19.trim();
	}

	public String getAttribute20() {
		return attribute20;
	}

	public void setAttribute20(String attribute20) {
		this.attribute20 = attribute20 == null ? null : attribute20.trim();
	}

	public String getAttribute21() {
		return attribute21;
	}

	public void setAttribute21(String attribute21) {
		this.attribute21 = attribute21 == null ? null : attribute21.trim();
	}

	public String getAttribute22() {
		return attribute22;
	}

	public void setAttribute22(String attribute22) {
		this.attribute22 = attribute22 == null ? null : attribute22.trim();
	}

	public String getAttribute23() {
		return attribute23;
	}

	public void setAttribute23(String attribute23) {
		this.attribute23 = attribute23 == null ? null : attribute23.trim();
	}

	public String getAttribute24() {
		return attribute24;
	}

	public void setAttribute24(String attribute24) {
		this.attribute24 = attribute24 == null ? null : attribute24.trim();
	}

	public String getAttribute25() {
		return attribute25;
	}

	public void setAttribute25(String attribute25) {
		this.attribute25 = attribute25 == null ? null : attribute25.trim();
	}

	public String getAttribute26() {
		return attribute26;
	}

	public void setAttribute26(String attribute26) {
		this.attribute26 = attribute26 == null ? null : attribute26.trim();
	}

	public String getAttribute27() {
		return attribute27;
	}

	public void setAttribute27(String attribute27) {
		this.attribute27 = attribute27 == null ? null : attribute27.trim();
	}

	public String getAttribute28() {
		return attribute28;
	}

	public void setAttribute28(String attribute28) {
		this.attribute28 = attribute28 == null ? null : attribute28.trim();
	}

	public String getAttribute29() {
		return attribute29;
	}

	public void setAttribute29(String attribute29) {
		this.attribute29 = attribute29 == null ? null : attribute29.trim();
	}

	public String getAttribute30() {
		return attribute30;
	}

	public void setAttribute30(String attribute30) {
		this.attribute30 = attribute30 == null ? null : attribute30.trim();
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

	public String getAttribute121() {
		return attribute121;
	}

	public void setAttribute121(String attribute121) {
		this.attribute121 = attribute121;
	}

	public String getAttribute122() {
		return attribute122;
	}

	public void setAttribute122(String attribute122) {
		this.attribute122 = attribute122;
	}

	public String getAttribute123() {
		return attribute123;
	}

	public void setAttribute123(String attribute123) {
		this.attribute123 = attribute123;
	}

	public String getAttribute124() {
		return attribute124;
	}

	public void setAttribute124(String attribute124) {
		this.attribute124 = attribute124;
	}

	public String getAttribute125() {
		return attribute125;
	}

	public void setAttribute125(String attribute125) {
		this.attribute125 = attribute125;
	}

	public String getAttribute126() {
		return attribute126;
	}

	public void setAttribute126(String attribute126) {
		this.attribute126 = attribute126;
	}

	public String getAttribute127() {
		return attribute127;
	}

	public void setAttribute127(String attribute127) {
		this.attribute127 = attribute127;
	}

	public String getAttribute128() {
		return attribute128;
	}

	public void setAttribute128(String attribute128) {
		this.attribute128 = attribute128;
	}

	public String getAttribute129() {
		return attribute129;
	}

	public void setAttribute129(String attribute129) {
		this.attribute129 = attribute129;
	}

	public String getAttribute130() {
		return attribute130;
	}

	public void setAttribute130(String attribute130) {
		this.attribute130 = attribute130;
	}

	public String getAttribute131() {
		return attribute131;
	}

	public void setAttribute131(String attribute131) {
		this.attribute131 = attribute131;
	}

	public String getAttribute132() {
		return attribute132;
	}

	public void setAttribute132(String attribute132) {
		this.attribute132 = attribute132;
	}

	public String getAttribute133() {
		return attribute133;
	}

	public void setAttribute133(String attribute133) {
		this.attribute133 = attribute133;
	}

	public String getAttribute134() {
		return attribute134;
	}

	public void setAttribute134(String attribute134) {
		this.attribute134 = attribute134;
	}

	public String getAttribute135() {
		return attribute135;
	}

	public void setAttribute135(String attribute135) {
		this.attribute135 = attribute135;
	}

	public String getAttribute136() {
		return attribute136;
	}

	public void setAttribute136(String attribute136) {
		this.attribute136 = attribute136;
	}

	public String getAttribute137() {
		return attribute137;
	}

	public void setAttribute137(String attribute137) {
		this.attribute137 = attribute137;
	}

	public String getAttribute138() {
		return attribute138;
	}

	public void setAttribute138(String attribute138) {
		this.attribute138 = attribute138;
	}

	public String getAttribute139() {
		return attribute139;
	}

	public void setAttribute139(String attribute139) {
		this.attribute139 = attribute139;
	}

	public String getAttribute140() {
		return attribute140;
	}

	public void setAttribute140(String attribute140) {
		this.attribute140 = attribute140;
	}

	public String getAttribute141() {
		return attribute141;
	}

	public void setAttribute141(String attribute141) {
		this.attribute141 = attribute141;
	}

	public String getAttribute142() {
		return attribute142;
	}

	public void setAttribute142(String attribute142) {
		this.attribute142 = attribute142;
	}

	public String getAttribute143() {
		return attribute143;
	}

	public void setAttribute143(String attribute143) {
		this.attribute143 = attribute143;
	}

	public String getAttribute144() {
		return attribute144;
	}

	public void setAttribute144(String attribute144) {
		this.attribute144 = attribute144;
	}

	public String getAttribute145() {
		return attribute145;
	}

	public void setAttribute145(String attribute145) {
		this.attribute145 = attribute145;
	}

	public String getAttribute146() {
		return attribute146;
	}

	public void setAttribute146(String attribute146) {
		this.attribute146 = attribute146;
	}

	public String getAttribute147() {
		return attribute147;
	}

	public void setAttribute147(String attribute147) {
		this.attribute147 = attribute147;
	}

	public String getAttribute148() {
		return attribute148;
	}

	public void setAttribute148(String attribute148) {
		this.attribute148 = attribute148;
	}

	public String getAttribute149() {
		return attribute149;
	}

	public void setAttribute149(String attribute149) {
		this.attribute149 = attribute149;
	}

	public String getAttribute150() {
		return attribute150;
	}

	public void setAttribute150(String attribute150) {
		this.attribute150 = attribute150;
	}

	public String getConvertedAttribute1() {
		return convertedAttribute1;
	}

	public void setConvertedAttribute1(String convertedAttribute1) {
		this.convertedAttribute1 = convertedAttribute1 == null ? null : convertedAttribute1.trim();
	}

	public String getConvertedAttribute2() {
		return convertedAttribute2;
	}

	public void setConvertedAttribute2(String convertedAttribute2) {
		this.convertedAttribute2 = convertedAttribute2 == null ? null : convertedAttribute2.trim();
	}

	public String getConvertedAttribute3() {
		return convertedAttribute3;
	}

	public void setConvertedAttribute3(String convertedAttribute3) {
		this.convertedAttribute3 = convertedAttribute3 == null ? null : convertedAttribute3.trim();
	}

	public String getConvertedAttribute4() {
		return convertedAttribute4;
	}

	public void setConvertedAttribute4(String convertedAttribute4) {
		this.convertedAttribute4 = convertedAttribute4 == null ? null : convertedAttribute4.trim();
	}

	public String getConvertedAttribute5() {
		return convertedAttribute5;
	}

	public void setConvertedAttribute5(String convertedAttribute5) {
		this.convertedAttribute5 = convertedAttribute5 == null ? null : convertedAttribute5.trim();
	}

	public String getConvertedAttribute6() {
		return convertedAttribute6;
	}

	public void setConvertedAttribute6(String convertedAttribute6) {
		this.convertedAttribute6 = convertedAttribute6 == null ? null : convertedAttribute6.trim();
	}

	public String getConvertedAttribute7() {
		return convertedAttribute7;
	}

	public void setConvertedAttribute7(String convertedAttribute7) {
		this.convertedAttribute7 = convertedAttribute7 == null ? null : convertedAttribute7.trim();
	}

	public String getConvertedAttribute8() {
		return convertedAttribute8;
	}

	public void setConvertedAttribute8(String convertedAttribute8) {
		this.convertedAttribute8 = convertedAttribute8 == null ? null : convertedAttribute8.trim();
	}

	public String getConvertedAttribute9() {
		return convertedAttribute9;
	}

	public void setConvertedAttribute9(String convertedAttribute9) {
		this.convertedAttribute9 = convertedAttribute9 == null ? null : convertedAttribute9.trim();
	}

	public String getConvertedAttribute10() {
		return convertedAttribute10;
	}

	public void setConvertedAttribute10(String convertedAttribute10) {
		this.convertedAttribute10 = convertedAttribute10 == null ? null : convertedAttribute10.trim();
	}

	public String getConvertedAttribute11() {
		return convertedAttribute11;
	}

	public void setConvertedAttribute11(String convertedAttribute11) {
		this.convertedAttribute11 = convertedAttribute11 == null ? null : convertedAttribute11.trim();
	}

	public String getConvertedAttribute12() {
		return convertedAttribute12;
	}

	public void setConvertedAttribute12(String convertedAttribute12) {
		this.convertedAttribute12 = convertedAttribute12 == null ? null : convertedAttribute12.trim();
	}

	public String getConvertedAttribute13() {
		return convertedAttribute13;
	}

	public void setConvertedAttribute13(String convertedAttribute13) {
		this.convertedAttribute13 = convertedAttribute13 == null ? null : convertedAttribute13.trim();
	}

	public String getConvertedAttribute14() {
		return convertedAttribute14;
	}

	public void setConvertedAttribute14(String convertedAttribute14) {
		this.convertedAttribute14 = convertedAttribute14 == null ? null : convertedAttribute14.trim();
	}

	public String getConvertedAttribute15() {
		return convertedAttribute15;
	}

	public void setConvertedAttribute15(String convertedAttribute15) {
		this.convertedAttribute15 = convertedAttribute15 == null ? null : convertedAttribute15.trim();
	}

	public String getConvertedAttribute16() {
		return convertedAttribute16;
	}

	public void setConvertedAttribute16(String convertedAttribute16) {
		this.convertedAttribute16 = convertedAttribute16 == null ? null : convertedAttribute16.trim();
	}

	public String getConvertedAttribute17() {
		return convertedAttribute17;
	}

	public void setConvertedAttribute17(String convertedAttribute17) {
		this.convertedAttribute17 = convertedAttribute17 == null ? null : convertedAttribute17.trim();
	}

	public String getConvertedAttribute18() {
		return convertedAttribute18;
	}

	public void setConvertedAttribute18(String convertedAttribute18) {
		this.convertedAttribute18 = convertedAttribute18 == null ? null : convertedAttribute18.trim();
	}

	public String getConvertedAttribute19() {
		return convertedAttribute19;
	}

	public void setConvertedAttribute19(String convertedAttribute19) {
		this.convertedAttribute19 = convertedAttribute19 == null ? null : convertedAttribute19.trim();
	}

	public String getConvertedAttribute20() {
		return convertedAttribute20;
	}

	public void setConvertedAttribute20(String convertedAttribute20) {
		this.convertedAttribute20 = convertedAttribute20 == null ? null : convertedAttribute20.trim();
	}

	public String getConvertedAttribute21() {
		return convertedAttribute21;
	}

	public void setConvertedAttribute21(String convertedAttribute21) {
		this.convertedAttribute21 = convertedAttribute21 == null ? null : convertedAttribute21.trim();
	}

	public String getConvertedAttribute22() {
		return convertedAttribute22;
	}

	public void setConvertedAttribute22(String convertedAttribute22) {
		this.convertedAttribute22 = convertedAttribute22 == null ? null : convertedAttribute22.trim();
	}

	public String getConvertedAttribute23() {
		return convertedAttribute23;
	}

	public void setConvertedAttribute23(String convertedAttribute23) {
		this.convertedAttribute23 = convertedAttribute23 == null ? null : convertedAttribute23.trim();
	}

	public String getConvertedAttribute24() {
		return convertedAttribute24;
	}

	public void setConvertedAttribute24(String convertedAttribute24) {
		this.convertedAttribute24 = convertedAttribute24 == null ? null : convertedAttribute24.trim();
	}

	public String getConvertedAttribute25() {
		return convertedAttribute25;
	}

	public void setConvertedAttribute25(String convertedAttribute25) {
		this.convertedAttribute25 = convertedAttribute25 == null ? null : convertedAttribute25.trim();
	}

	public String getConvertedAttribute26() {
		return convertedAttribute26;
	}

	public void setConvertedAttribute26(String convertedAttribute26) {
		this.convertedAttribute26 = convertedAttribute26 == null ? null : convertedAttribute26.trim();
	}

	public String getConvertedAttribute27() {
		return convertedAttribute27;
	}

	public void setConvertedAttribute27(String convertedAttribute27) {
		this.convertedAttribute27 = convertedAttribute27 == null ? null : convertedAttribute27.trim();
	}

	public String getConvertedAttribute28() {
		return convertedAttribute28;
	}

	public void setConvertedAttribute28(String convertedAttribute28) {
		this.convertedAttribute28 = convertedAttribute28 == null ? null : convertedAttribute28.trim();
	}

	public String getConvertedAttribute29() {
		return convertedAttribute29;
	}

	public void setConvertedAttribute29(String convertedAttribute29) {
		this.convertedAttribute29 = convertedAttribute29 == null ? null : convertedAttribute29.trim();
	}

	public String getConvertedAttribute30() {
		return convertedAttribute30;
	}

	public void setConvertedAttribute30(String convertedAttribute30) {
		this.convertedAttribute30 = convertedAttribute30 == null ? null : convertedAttribute30.trim();
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
	}

	public String getLineContent() {
		return lineContent;
	}

	public void setLineContent(String lineContent) {
		this.lineContent = lineContent == null ? null : lineContent.trim();
	}

	
}