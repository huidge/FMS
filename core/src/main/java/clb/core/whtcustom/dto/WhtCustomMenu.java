package clb.core.whtcustom.dto;

import java.util.List;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;
@ExtensionAttribute(disable=true)
@Table(name = "fms_wht_custom_menu")
public class WhtCustomMenu extends BaseDTO {
     @Id
     @GeneratedValue
      private Long menuId;

     @NotEmpty
      private String menuName;

     @NotEmpty
      private String menuType;

      private Long parentMenuId;
      
      private Long rowNumber;

     @NotEmpty
      private String originalId;
     
      private String accountNum;

      private String menuContentType;

      private String menuContent;
      
      private String menuPreviewContent;
      
      private String menuKey;
      
      private String text;

      private String pictureTextId;
      
      private String pictureId;
      
      private String voiceId;
      
      private String videoId;
      
      private String pageAddress;
      
      private String smallProGram;
      
      private String menuOperation;
      
      private String customerServiceFlag;

      private Long programId;

      private Long requestId;
      
      @Transient
  	  private List<WhtCustomMenu> menuConTentList;
      
      /*@Transient
	  private String pictureTextIdPath;*/
      
      @Transient
      private String whtType;
      
      /*@Transient
      private String pictureIdPath;*/
      
	public String getMenuPreviewContent() {
		return menuPreviewContent;
	}

	public void setMenuPreviewContent(String menuPreviewContent) {
		this.menuPreviewContent = menuPreviewContent;
	}

	public String getCustomerServiceFlag() {
		return customerServiceFlag;
	}

	public void setCustomerServiceFlag(String customerServiceFlag) {
		this.customerServiceFlag = customerServiceFlag;
	}

	public String getMenuOperation() {
		return menuOperation;
	}

	public void setMenuOperation(String menuOperation) {
		this.menuOperation = menuOperation;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMenuKey() {
		return menuKey;
	}

	public void setMenuKey(String menuKey) {
		this.menuKey = menuKey;
	}

	public String getOriginalId() {
		return originalId;
	}

	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}

	public String getWhtType() {
		return whtType;
	}

	public void setWhtType(String whtType) {
		this.whtType = whtType;
	}

	public Long getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(Long rowNumber) {
		this.rowNumber = rowNumber;
	}

	public List<WhtCustomMenu> getMenuConTentList() {
		return menuConTentList;
	}

	public void setMenuConTentList(List<WhtCustomMenu> menuConTentList) {
		this.menuConTentList = menuConTentList;
	}

	public String getPictureTextId() {
		return pictureTextId;
	}

	public void setPictureTextId(String pictureTextId) {
		this.pictureTextId = pictureTextId;
	}

	public String getPictureId() {
		return pictureId;
	}

	public void setPictureId(String pictureId) {
		this.pictureId = pictureId;
	}

	public String getVoiceId() {
		return voiceId;
	}

	public void setVoiceId(String voiceId) {
		this.voiceId = voiceId;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getSmallProGram() {
		return smallProGram;
	}

	public void setSmallProGram(String smallProGram) {
		this.smallProGram = smallProGram;
	}

	public void setMenuId(Long menuId){
         this.menuId = menuId;
     }

     public Long getMenuId(){
         return menuId;
     }

     public void setMenuName(String menuName){
         this.menuName = menuName;
     }

     public String getMenuName(){
         return menuName;
     }

     public void setMenuType(String menuType){
         this.menuType = menuType;
     }

     public String getMenuType(){
         return menuType;
     }

     public void setParentMenuId(Long parentMenuId){
         this.parentMenuId = parentMenuId;
     }

     public Long getParentMenuId(){
         return parentMenuId;
     }

     public void setAccountNum(String accountNum){
         this.accountNum = accountNum;
     }

     public String getAccountNum(){
         return accountNum;
     }

     public void setMenuContentType(String menuContentType){
         this.menuContentType = menuContentType;
     }

     public String getMenuContentType(){
         return menuContentType;
     }

     public void setMenuContent(String menuContent){
         this.menuContent = menuContent;
     }

     public String getMenuContent(){
         return menuContent;
     }

     public void setPageAddress(String pageAddress){
         this.pageAddress = pageAddress;
     }

     public String getPageAddress(){
         return pageAddress;
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

     }
