package clb.core.fnd.service.impl;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import clb.core.fnd.utils.Constants;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.mapper.ImportTempMapper;
import clb.core.fnd.service.IImportTempService;
import clb.core.common.service.ISequenceService;
import clb.core.fnd.utils.ValidationTableException;

@Transactional
@Service
public class ImportTempServiceImpl extends BaseServiceImpl<ImportTemp> implements IImportTempService {

	@Autowired
	private ISequenceService sequenceService;
	@Autowired
	private ImportTempMapper importTempMapper;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ApplicationContext context;
	
	@Override
	public List<ImportTemp> selectErrorMessage(Long importBatchId, int page, int pagesize, IRequest request) {
		PageHelper.startPage(page, pagesize);
		return importTempMapper.selectErrorMessage(importBatchId);
	}

	@Override
	public List<ImportTemp> selectImportData(Long importBatchId, int page, int pagesize, IRequest request) {
		PageHelper.startPage(page, pagesize);
		return importTempMapper.selectImportData(importBatchId);
	}

	@Override
	public Long selectErrorCount(Long importBatchId) {
		return importTempMapper.selectErrorCount(importBatchId);
	}

	/**
	 * @Description: 根据错误信息更新临时表数据 
	 * @param importTemp
	 * @param request 
	 * @see clb.core.fnd.service.IImportTempService#updateError(clb.core.fnd.dto.ImportTemp, IRequest)
	 */
	@Override
	public void updateError(ImportTemp importTemp, IRequest request) {
		ImportTemp importTempUpdate = new ImportTemp();
		importTempUpdate.setImportTempId(importTemp.getImportTempId());
		if(null == importTemp.getImportMessage() || "".equals(importTemp.getImportMessage())){
			importTempUpdate.setImportStatus("N");
			importTempUpdate.setImportMessage("");
		}else{
			importTempUpdate.setImportStatus("E");
			importTempUpdate.setImportMessage(importTemp.getImportMessage());
		}
		self().updateByPrimaryKeySelective(request, importTempUpdate);
	}

	/**
	 * @Description: 根据所得的流水号验证是否已经存在  
	 * @param importBatchId
	 * @return boolean
	 * @see clb.core.fnd.service.IImportTempService#selectImportBactIdIsExist(Long)
	 */
	@Override
	public boolean selectImportBactIdIsExist(Long importBatchId) {
		int count = importTempMapper.selectImportBactIdIsExist(importBatchId);
		return count > 0;
	}

	@Override
	public void deleteAll() {
		importTempMapper.deleteAll();
	}

	@Override
	public int selectCountNeedDelete() {
		return importTempMapper.selectCountNeedDelete();
	}
	
	/**
	 * @Description: 历史数据导入模板下载
	 * @param config
	 * @param request
	 * @param httpServletResponse
	 * @param requestContext
	 * @throws Exception
	 */
	@Override
	public void downloadExcel(ExportConfig<Map<String, String>, ColumnInfo> config, HttpServletRequest request,
			HttpServletResponse httpServletResponse, IRequest requestContext) throws Exception {
		httpServletResponse.addHeader("Content-Disposition",
				"attachment;filename=\"" + URLEncoder.encode(config.getFileName()+ ".xlsx", Constants.ENC) + "\"");
		httpServletResponse.setContentType("application/vnd.ms-excel" + ";charset=" + Constants.ENC);
		httpServletResponse.setHeader("Accept-Ranges", "bytes");
		try (OutputStream outputStream = httpServletResponse.getOutputStream()) {
			exportExcel(config, requestContext, outputStream);
		}
	}

	/**
	 * @Description: 导入数据错误信息下载
	 * @param importTemps
	 * @param request
	 * @param httpServletResponse
	 * @param requestContext
	 * @throws Exception
	 */
	@Override
	public void downloadErrorExcel(List<ImportTemp> importTemps, HttpServletRequest request,
			HttpServletResponse httpServletResponse, IRequest requestContext) throws Exception {
		httpServletResponse.addHeader("Content-Disposition",
				"attachment;filename=\"" + URLEncoder.encode(importTemps.get(0).getOrigFileName(), Constants.ENC) + "\"");
		httpServletResponse.setContentType("application/vnd.ms-excel" + ";charset=" + Constants.ENC);
		httpServletResponse.setHeader("Accept-Ranges", "bytes");
		try (OutputStream outputStream = httpServletResponse.getOutputStream()) {
			exportErrorExcel(importTemps, requestContext, outputStream);
		}
	}

	/**
	 * @Description: 生成导入模板EXCEL
	 * @param config
	 * @param requestContext
	 * @param outputStream
	 *            void
	 */
	private void exportExcel(ExportConfig<Map<String, String>, ColumnInfo> config, IRequest requestContext,
			OutputStream outputStream) throws Exception {

		XSSFWorkbook workbook = new XSSFWorkbook();
		
		// sheet页row索引
		final AtomicInteger rowIndex = new AtomicInteger(1);

		XSSFSheet sheet = workbook.createSheet("1");
		XSSFRow firstRow = sheet.createRow(0);

		int i = 0;// 单元格数(减1)
		rowIndex.set(1);// sheet页row索引归1
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		// 设置标题
		List<ColumnInfo> columns = config.getColumnsInfo();
		Iterator<ColumnInfo> iterator = columns.iterator();
		while (iterator.hasNext()) {
			ColumnInfo column = iterator.next();
			if (null != column.getTitle() && !"".equals(column.getTitle())) {
				XSSFCell cell = firstRow.createCell(i);
				// 设置列宽度
				cell.setCellValue(column.getTitle());
				// 设置列宽度
	            sheet.setColumnWidth(i, column.getWidth() * 80);
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
				// 设置列字体align
				cell.setCellStyle(cellStyle);
				i++;
			}
		}
		workbook.write(outputStream);
		workbook.close();
	}

	/**
	 * @Description: 生成错误信息模板
	 * @param importTemps
	 * @param requestContext
	 * @param outputStream
	 *            void
	 */
	private void exportErrorExcel(List<ImportTemp> importTemps, IRequest requestContext, OutputStream outputStream)
			throws Exception {
		List<String> sheetList = new ArrayList<String>();// sheet页名的List集合，不在其中新建一页

		XSSFWorkbook workbook = new XSSFWorkbook();
		
		String[] locales = requestContext.getLocale().split("_");
		Locale locale = new Locale(locales[0],locales[1]);

		PropertyDescriptor attributeProperty = null;

		Map<Integer, XSSFCellStyle> styles = null;

		// sheet页row索引
		final AtomicInteger rowIndex = new AtomicInteger(1);

		for (ImportTemp importTemp : importTemps) {

			if (null == sheetList || !sheetList.contains(importTemp.getSheet())) {
				styles = new HashMap<Integer, XSSFCellStyle>();

				String sheetName = importTemp.getSheet();// sheet页名
				sheetList.add(sheetName);

				XSSFSheet sheet = workbook.createSheet(sheetName);
				XSSFRow firstRow = sheet.createRow(0);

				int i = 0;// 单元格数(减1)
				rowIndex.set(1);// sheet页row索引归1
				// 通过循环设置标题数据
				for (int j = 1; j <= 30; j++) {
					// 得到属性attribute1-attribute30的值
					attributeProperty = new PropertyDescriptor(Constants.ATTRIBUTE + (j), importTemp.getClass());
					Method method = attributeProperty.getReadMethod();
					Object titleObj = method.invoke(importTemp);

					if (null != titleObj ) {
						XSSFCell firstCell = firstRow.createCell(i);
						firstCell.setCellValue((String) titleObj);
						// 设置列字体align
						XSSFCellStyle cellStyle = workbook.createCellStyle();
						cellStyle.setAlignment(HorizontalAlignment.CENTER);
						firstCell.setCellStyle(cellStyle);
						styles.put(j, cellStyle);
						i++;
					} else {
						XSSFCell firstCell = firstRow.createCell(i);
						if(null != importTemp.getImportMessage() && !"".equals(importTemp.getImportMessage())){
							firstCell.setCellValue(importTemp.getImportMessage());
						}else{
							firstCell.setCellValue(messageSource.getMessage(Constants.ERROR_MSG, null, locale));
						}
						// 设置列字体align
						XSSFCellStyle cellStyle = workbook.createCellStyle();
						cellStyle.setAlignment(HorizontalAlignment.CENTER);
						firstCell.setCellStyle(cellStyle);
						styles.put(j, cellStyle);
						break;
					}
				}
			} else {
				XSSFRow row = workbook.getSheet(importTemp.getSheet()).createRow(rowIndex.getAndIncrement());
				int ii = 0;
				// 通过循环设置各行各列数据
				for (int j = 1; j <= styles.size() - 1; j++) {
					// 得到属性attribute1-attribute30的值
					attributeProperty = new PropertyDescriptor(Constants.ATTRIBUTE + (j), importTemp.getClass());
					Method method = attributeProperty.getReadMethod();
					Object filedObj = method.invoke(importTemp);

					if (null != filedObj) {
						XSSFCell cell = row.createCell(ii++);
						cell.setCellStyle(styles.get(ii - 1));
						cell.setCellType(XSSFCell.CELL_TYPE_STRING);
						cell.setCellValue((String) filedObj);
					}
				}
				if (null != importTemp.getImportMessage() && !"".equals(importTemp.getImportMessage())) {
					XSSFCell cell = row.createCell(styles.size() - 1);
					cell.setCellStyle(styles.get(styles.size() - 1));
					cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(importTemp.getImportMessage());
				}
			}
		}
		workbook.write(outputStream);
		workbook.close();
	}

	/**
	 * @Description: 从excel文件中导入数据，批量插入导入临时表
	 * @param requestContext
	 * @param file
	 * @return List<ImportTemp>
	 */
	@Override
	public List<ImportTemp> batchCreateFromExcel(IRequest requestContext, MultipartFile file) throws Exception {
		List<ImportTemp> list = excelFileToList(requestContext, file);
		if(self().selectCountNeedDelete() > 0){
			self().deleteAll();
		}
		self().batchUpdate(requestContext, list);
		return list;
	}

	/**
	 * @Description: 将文件解析插入导入临时表
	 * @param requestContext
	 * @param file
	 * @param
	 * @param
	 * @param
	 * @return List<ImportTemp>
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private List<ImportTemp> excelFileToList(IRequest requestContext, MultipartFile file) throws Exception {
		// 先固定 seperatorStr
		String seperatorStr = ",";

		List<ImportTemp> list = new ArrayList<>();
		InputStream inputStream = file.getInputStream();
		Workbook xwb = null;
		String originalFilename = file.getOriginalFilename();
		if (originalFilename.endsWith("xlsx") || originalFilename.endsWith("csv")) {
			// 2007
			xwb = new XSSFWorkbook(inputStream);
		} else if (originalFilename.endsWith("xls")) {
			// 2003
			xwb = new HSSFWorkbook(inputStream);
		} else {
			return list;
		}
		SimpleDateFormat strdf = new SimpleDateFormat();
		DecimalFormat numdf = new DecimalFormat("##.######");
		int sheetNumbers = xwb.getNumberOfSheets();
		String cellString = null;
		int cellType;
		FormulaEvaluator evaluator = xwb.getCreationHelper().createFormulaEvaluator();
		// 待流水号功能开发移植之后使用
		// 获取流水号
		Long importBatchId = null;
		try {
			boolean isExist = true;
			while(isExist){
				importBatchId = Long.valueOf(sequenceService.getSequence(Constants.IMPORT_SEQUENCE_CODE));
				isExist = self().selectImportBactIdIsExist(importBatchId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(Constants.SERIAL_NUMBER_ERROR);
		}
		// String lineString = null;
		// 循环sheet页
		for (int i = 0; i < sheetNumbers; i++) {
			// 循环行
			Sheet sheet = xwb.getSheetAt(i);
			if (sheet.getPhysicalNumberOfRows() == 0) {
				// return null;
				continue;
			}
			String sheetName = sheet.getSheetName();

			for (int k = sheet.getFirstRowNum(); k <= sheet.getLastRowNum(); k++) {
				Row row = sheet.getRow(k);
				if (null == row) {
					continue;
				}

				int maxCell = row.getLastCellNum();
				// 最多单元格不超过 30
				if (maxCell > Constants.MAX_ATTR) {
					maxCell = Constants.MAX_ATTR;
				}

				ImportTemp importTempDto = new ImportTemp();

				for (int j = row.getFirstCellNum(); j < maxCell; j++) {
					Cell cell = row.getCell(j);

					if (cell == null) {
						cellString = "";
					} else {
						cellType = cell.getCellType();
						if (cellType == Cell.CELL_TYPE_FORMULA) {
							if (evaluator == null) {
								cellString = cell.getCellFormula();
							}
							cellType = evaluator.evaluateFormulaCell(cell);
						}

						switch (cellType) {
						case Cell.CELL_TYPE_STRING:
							cellString = cell.getRichStringCellValue().getString();
							break;

						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cell)) {
								if (cell.getNumericCellValue() == (int) cell.getNumericCellValue()) {
									strdf.applyPattern("yyyy-MM-dd");
									cellString = strdf.format(cell.getDateCellValue());
								} else {
									strdf.applyPattern("yyyy-MM-dd HH:mm:ss");
									cellString = strdf.format(cell.getDateCellValue());
								}
							} else {
								cellString = numdf.format(cell.getNumericCellValue());
							}
							break;

						case Cell.CELL_TYPE_BOOLEAN:
							cellString = String.valueOf(cell.getBooleanCellValue());
							break;

						case Cell.CELL_TYPE_BLANK:
							cellString = "";
						}// switch end
					}

					// translate the special Character
					cellString = escapeEmbeddedCharacters(cellString, 0, seperatorStr);

					PropertyDescriptor attributeProperty;

					attributeProperty = new PropertyDescriptor(Constants.ATTRIBUTE + (j + 1), importTempDto.getClass());
					Method method = attributeProperty.getWriteMethod();
					method.invoke(importTempDto, cellString);

					importTempDto.set__status(DTOStatus.ADD);
					importTempDto.setImportStatus(ImportTemp.NEW_STAUTS);
					importTempDto.setLineNumber(Long.parseLong(String.valueOf(k + 1)));
					importTempDto.setSheet(sheetName);
					importTempDto.setImportBatchId(importBatchId);
					importTempDto.setImportCode("FHI");//ImportCode在此功能中已经是无用字段
					importTempDto.setOrigFileName(originalFilename);

				} // end loop j
				int countNull = 0;
				for(int j = 0; j < maxCell; j++){//去掉空行
					PropertyDescriptor attributeProperty;
					attributeProperty = new PropertyDescriptor(Constants.ATTRIBUTE + (j + 1), importTempDto.getClass());
					Method method = attributeProperty.getReadMethod();
					String objValue = (String)method.invoke(importTempDto);
					if(null == objValue || "".equals(objValue)){
						countNull++;
					}
				}
				if(countNull != maxCell){
					list.add(importTempDto);
				}

			}

		}
		return list;
	}

	/**
	 * @Description: 字符转义 
	 * @param field
	 * @param formattingConvention
	 * @param separator
	 * @return String
	 */
	private String escapeEmbeddedCharacters(String field, int formattingConvention, String separator) {
		StringBuffer buffer = null;

		if (formattingConvention == 0) {
			if (field.contains("\"")) {
				buffer = new StringBuffer(field.replaceAll("\"", "\\\"\\\""));
				buffer.insert(0, "\"");
				buffer.append("\"");
			} else {
				buffer = new StringBuffer(field);
				if ((buffer.indexOf(separator) > -1) || (buffer.indexOf("\n") > -1)) {
					buffer.insert(0, "\"");
					buffer.append("\"");
				}
			}
			return buffer.toString().trim();
		}

		if (field.contains(separator)) {
			field = field.replaceAll(separator, "\\\\" + separator);
		}
		if (field.contains("\n")) {
			field = field.replaceAll("\n", "\\\\\n");
		}
		return field;
	}

	/**
	 * @Description: 导入数据  
	 * @param requestContext
	 * @param importBatchId
	 * @param
	 * @param
	 * @param
	 * @throws ValidationTableException
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ValidationTableException.class)
	public void importData(IRequest requestContext, Long importBatchId, String className) throws ValidationTableException, Exception {
		AbstractImportExecute importExecute = null;
		importExecute = (AbstractImportExecute) context.getBean(Class.forName(className));
		Map<String, Object> arg = new HashMap<String, Object>();
		if(null != importBatchId && importBatchId.longValue() > 0l){
			arg.put("importBatchId", importBatchId);
			arg.put("request", requestContext);
			importExecute.execute(arg);
		}else{
			throw new ValidationTableException(Constants.DATA_EMPTY, null);
		}
	}
	
}
