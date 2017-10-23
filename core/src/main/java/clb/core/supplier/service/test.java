package clb.core.supplier.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.drools.lang.dsl.DSLMapParser.value_chunk_return;

import clb.core.common.utils.DateUtil;
import clb.core.common.utils.RefelctUtil;
import clb.core.common.utils.StringUtil;
import clb.core.forecast.dto.FetSupposeCommon;

public class test {
	
	//保单状态
    private static final String[] INSURANCE_ORDER_STATUS = {"NEW","DATA_APPROVING","NEED_REVIEW","DATA_APPROVED","RESERVING","RESERVE_SUCCESS","ARRIVAL","LEAVE"};

    public static void main(String[] args) throws Exception{
    	/*String data="请查收附件：<br><div class=\"file\" style=\"font-size:12px\"onclick=\"downloadFile(11245,'CLBDATA/FET/2017695be52316-746a-43f4-a704-e06e68c6c61c')\"><img src=\"{root}/resources/images/forecast/download.png\" height=\"40\" width=\"40\"/><br>日历 (1).xlsx</div>";
    	String functionRex = "downloadFile\\(([0-9]+,)([A-Za-z0-9/'-]+)\\)";
    	String paramRex = "\\(.*\\)";
    	String bracketsRex = "(\\(|\\))";
    	Pattern function = Pattern.compile(functionRex);
    	Pattern param = Pattern.compile(paramRex);
    	Matcher func = function.matcher(data);
    	List<SysFile> files = new ArrayList<>();
    	while (func.find()) { 
    		String fun = func.group().replaceAll("'","");  
        	Matcher par = param.matcher(fun);    
    		while (par.find()) { 
    			String fileData = par.group().replaceAll(bracketsRex,"");  
    			Long fileId = Long.parseLong(fileData.split(",")[0]);
    			String filePath = fileData.split(",")[1];
    			SysFile file = new SysFile();
    			file.setFileId(fileId);
    			file.setFilePath(filePath);
    			files.add(file);
        	} 
    	} 
    	System.out.println(files);*/
    	/*Date date = new Date();
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    	String dateString = "2017-06-26 11:10:01";
    	date = simpleDateFormat.parse(dateString);
    	//Date newDate = simpleDateFormat1.parse(simpleDateFormat1.format(date));
    	System.out.println(DateUtil.isMorningOrAfternoon(date));
    	System.out.println(DateUtil.getTimeString(date,false));*/
    	/*String phoneNumber = "sdsd12343434";
    	String phoneCode = StringUtils.substringBefore(phoneNumber,"&");
		String contactNum = StringUtils.substringAfter(phoneNumber,"&");
		System.out.println(phoneCode);
		System.out.println("".equals(contactNum));*/
    	/*Date date1 = new Date();
    	Date date2 = new Date();
    	String d = "2017-07-31 17:25:20";
    	date1 = DateUtil.getDate(d,"yyyy-MM-dd HH:mm:ss");
    	System.out.println(date1);
    	date2 = DateUtil.getDate(d,"yyyy-MM-dd");
    	System.out.println(date2);
    	System.out.println(date1.getTime());
    	System.out.println(date2.getTime());
    	System.out.println(DateUtil.getFormatDate(date1, "yyyy-MM-dd"));*/
    	
    	/*FetSupposeCommon fetSupposeCommon1 = new FetSupposeCommon();
    	FetSupposeCommon fetSupposeCommon2 = new FetSupposeCommon();
    	FetSupposeCommon fetSupposeCommon3 = new FetSupposeCommon();
    	fetSupposeCommon1.setPaymentCompanyType("SUPPLIER");
    	fetSupposeCommon1.setPaymentCompanyId(1L);
    	fetSupposeCommon1.setReceiveCompanyType("SUPPLIER");
    	fetSupposeCommon1.setReceiveCompanyId(2L);
    	
    	fetSupposeCommon2.setPaymentCompanyType("CHANNEL");
    	fetSupposeCommon2.setPaymentCompanyId(1L);
    	fetSupposeCommon2.setReceiveCompanyType("SUPPLIER");
    	fetSupposeCommon2.setReceiveCompanyId(2L);
    	
    	fetSupposeCommon3.setPaymentCompanyType("SUPPLIER");
    	fetSupposeCommon3.setPaymentCompanyId(1L);
    	fetSupposeCommon3.setReceiveCompanyType("CHANNEL");
    	fetSupposeCommon3.setReceiveCompanyId(2L);
    	
    	List<FetSupposeCommon> list = new ArrayList<>();
    	
    	
    	FetSupposeCommon fetSupposeCommon4 = new FetSupposeCommon();
    	fetSupposeCommon4.setPaymentCompanyType("SUPPLIER");
    	fetSupposeCommon4.setPaymentCompanyId(1L);
    	fetSupposeCommon4.setReceiveCompanyType("SUPPLIER");
    	fetSupposeCommon4.setReceiveCompanyId(2L);
    	
    	list.add(fetSupposeCommon1);
    	list.add(fetSupposeCommon2);
    	list.add(fetSupposeCommon3);
    	list.add(fetSupposeCommon4);
    	
    	FetSupposeCommon fetSupposeCommon5 = new FetSupposeCommon();
    	fetSupposeCommon5.setPaymentCompanyType("SUPPLIER");
    	fetSupposeCommon5.setPaymentCompanyId(2L);
    	fetSupposeCommon5.setReceiveCompanyType("SUPPLIER");
    	fetSupposeCommon5.setReceiveCompanyId(2L);
    	
    	String args1[] = {"paymentCompanyType","paymentCompanyId","receiveCompanyType","receiveCompanyId"};
    	
    	List<FetSupposeCommon> d = (List<FetSupposeCommon>)RefelctUtil.getListDataByFields(list, fetSupposeCommon2,args1);
    	
    	System.out.println(d.get(0));
    	
    	String a1 = addZero2Str(9,2);
    	String a2 = addZero2Str(15,2);
    	
    	System.out.println(a1);
    	System.out.println(a2);*/
    	
    	Date date = new Date();
    	System.out.println(DateUtil.getChineseDateString(null));
    	
    	List<String> d =new ArrayList<>();
    	d.add(null);
    	System.out.println(StringUtil.listConvertStr(d));
    }
    
    public static String addZero2Str(Number numObj, int length) {  
        NumberFormat nf = NumberFormat.getInstance();  
        // 设置是否使用分组  
        nf.setGroupingUsed(false);  
        // 设置最大整数位数  
        nf.setMaximumIntegerDigits(length);  
        // 设置最小整数位数  
        nf.setMinimumIntegerDigits(length);  
        return nf.format(numObj);  
    }  
}
