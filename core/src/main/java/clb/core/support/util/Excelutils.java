package clb.core.support.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
/**
 * Created by wanjun.feng on 2017/4/13.
 */
public class Excelutils {
    public static void download(ByteArrayOutputStream byteArrayOutputStream,
            HttpServletResponse response, HttpServletRequest request, 
            String returnName) throws IOException {
        String userAgent = request.getHeader("USER-AGENT");
        String enocodeType = "";
        if (StringUtils.contains(userAgent, "Mozilla"))
          enocodeType = "iso8859-1";
        else {
          enocodeType = "utf8";
        }

        response.setContentType("application/x-download");
        returnName = response.encodeURL(new String(returnName.getBytes(), enocodeType));
        response.addHeader("Content-Disposition", "attachment;filename=" + returnName);
        response.setContentLength(byteArrayOutputStream.size());

        ServletOutputStream outputstream = response.getOutputStream();
        byteArrayOutputStream.writeTo(outputstream);
        byteArrayOutputStream.close();
        outputstream.flush();
      }

      public static DataValidation setDropDownList(XSSFSheet sheet, int firstRow,
              int lastRow, int firstCol, int lastCol, String[] listOfValues,
              String errorStr) {
        XSSFDataValidationHelper helper = new XSSFDataValidationHelper(sheet);
        DataValidationConstraint constraint = helper.createExplicitListConstraint(listOfValues);
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation dropDownList = helper.createValidation(constraint, regions);

        dropDownList.createErrorBox("error", errorStr);
        dropDownList.setShowErrorBox(true);
        dropDownList.setSuppressDropDownArrow(true);
        return dropDownList;
      }

      public static CellStyle title(Workbook wb, CellStyle nStyle, Font nFont, short colorValue) {
        nFont.setFontName("宋体");
        nFont.setFontHeightInPoints((short) 10);
        nFont.setBoldweight((short) 700);
        nStyle.setFillPattern((short) 2);
        nStyle.setFillForegroundColor(colorValue);
        nStyle.setFillBackgroundColor(colorValue);
        nStyle.setAlignment((short) 2);
        nStyle.setVerticalAlignment((short) 1);

        nStyle.setBorderTop((short) 1);
        nStyle.setBorderBottom((short) 1);
        nStyle.setBorderLeft((short) 1);
        nStyle.setBorderRight((short) 1);

        nStyle.setFont(nFont);
        return nStyle;
      }
}