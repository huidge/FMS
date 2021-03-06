package clb.core.supplier.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;

import clb.core.common.annotations.Title;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
@ExtensionAttribute(disable=true)
@Table(name = "fms_spe_calendar_line")
public class SpeCalendarLine extends BaseDTO {
     @Id
     @GeneratedValue
      private Long lineId;

     @NotNull
      private Long calendarId;

     @NotNull
     @Title(title = "spe.calendar.year",index=0)
      private Long theYear;

     @NotNull
     @Title(title = "spe.calendar.month",index=1)
      private Long theMonth;

     @NotNull
     @Title(title = "spe.calendar.day",index=2)
      private Long theDay;

     @NotEmpty
     @Title(title = "spe.calendar.datetype",index=3)
      private String dayType;
     
     @Transient
     private Long dateNumber;


	public Long getDateNumber() {
		return dateNumber;
	}

	public void setDateNumber(Long dateNumber) {
		this.dateNumber = dateNumber;
	}

	public void setLineId(Long lineId){
         this.lineId = lineId;
     }

     public Long getLineId(){
         return lineId;
     }

     public void setCalendarId(Long calendarId){
         this.calendarId = calendarId;
     }

     public Long getCalendarId(){
         return calendarId;
     }

     public void setTheYear(Long theYear){
         this.theYear = theYear;
     }

     public Long getTheYear(){
         return theYear;
     }

     public void setTheMonth(Long theMonth){
         this.theMonth = theMonth;
     }

     public Long getTheMonth(){
         return theMonth;
     }

     public void setTheDay(Long theDay){
         this.theDay = theDay;
     }

     public Long getTheDay(){
         return theDay;
     }

     public void setDayType(String dayType){
         this.dayType = dayType;
     }

     public String getDayType(){
         return dayType;
     }

     @Transient
     private Long supplierId;
	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}
     
    
}
