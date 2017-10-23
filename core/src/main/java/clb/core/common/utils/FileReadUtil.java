package clb.core.common.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

/*****
 * 
 * @author tiansheng.ye
 * @Date 2017-05-17
 */
public class FileReadUtil {

	/***
	 * 得到excel里面所有的内容的集合
	 */
	public static List<List<String>> fileRead(MultipartFile file,int sheetNum){
		List<List<String>> excelList = new ArrayList<List<String>>();
		XSSFWorkbook wb = null;
        try {
        	wb = new XSSFWorkbook(file.getInputStream());
			XSSFSheet sheet = wb.getSheetAt(sheetNum);
			XSSFRow nRow = null;
			Cell nCell = null;
			int beginRowNo = 0;//定义拿取数据的起始行0开始
			int endRowNo = sheet.getLastRowNum();//拿到文件中数据总条数
			nRow = sheet.getRow(0);
			short endColNo = sheet.getRow(beginRowNo).getLastCellNum();
			if(endRowNo == 0) {
			    wb.close();
			} else {
			    for(int rData = beginRowNo; rData <= endRowNo; ++rData) {
			        nRow = sheet.getRow(rData);
			        List<String> e = new ArrayList<String>();
			        //hasValue 标识是否存在数据
			        boolean hasValue=false;
			        for(int j = 0; j <= endColNo && nRow!=null; ++j) {
			            nCell = nRow.getCell(j);
			            if(nCell != null) {
			                nCell.setCellType(1);
			                if(StringUtils.isNotBlank(nCell.getStringCellValue())) {
			                	hasValue=true;
			                    e.add(getCellFormatValue(nCell));//拿到文件单行数据
			                } else {
			                    e.add("");
			                }
			            } else {
			                e.add("");
			            }
			        }
			        if(hasValue){
			        	excelList.add(e);//拿到文件中所有的值
			        }
			    }
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
		}finally{
			try {
				wb.close();
			} catch (IOException e) {
				CommonUtil.printStackTraceToStr(e);
			}
		}
        return excelList;
	}
	
	/**
     * 获取单元格数据内容为字符串类型的数据
     * 
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private static String getCellFormatValue(Cell cell) {
    	if (cell == null) {
            return "";
        }
    	String strCell = "";
		switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            strCell = cell.getStringCellValue().trim();
            break;
        case Cell.CELL_TYPE_NUMERIC:
        	if (HSSFDateUtil.isCellDateFormatted(cell)) {    //判断是日期类型  
        		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
                Date dt = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());//获取成DATE类型    
                try{
                	strCell = dateTimeFormat.format(dt);
                	int index = strCell.indexOf(" ");
        			String time = strCell.substring(index+1,strCell.length());
        			if("00:00:00".equals(time)){
        				strCell = dateFormat.format(dt); 
        			}
                }catch(Exception e){
                	strCell = dateFormat.format(dt); 
                }
                  
        	}else{
        		strCell = String.valueOf(cell.getNumericCellValue());
        	}
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            strCell = String.valueOf(cell.getBooleanCellValue());
            break;
        case Cell.CELL_TYPE_BLANK:
            strCell = "";
            break;
        default:
            strCell = "";
            break;
        }
        
        if ("".equals(strCell) || strCell == null) {
            return "";
        }
        return strCell;
     }
}
