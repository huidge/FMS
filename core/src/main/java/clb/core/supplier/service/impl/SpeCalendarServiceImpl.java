package clb.core.supplier.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ImportUtil;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.supplier.dto.SpeCalendar;
import clb.core.supplier.dto.SpeCalendarLine;
import clb.core.supplier.exceptions.SpeException;
import clb.core.supplier.mapper.SpeCalendarMapper;
import clb.core.supplier.service.ISpeCalendarLineService;
import clb.core.supplier.service.ISpeCalendarService;
import sun.misc.BASE64Decoder;

@Service
@Transactional
public class SpeCalendarServiceImpl extends BaseServiceImpl<SpeCalendar> implements ISpeCalendarService{
	
	private static Logger log = LoggerFactory.getLogger(SpeCalendarServiceImpl.class);
	@Autowired
	private SpeCalendarMapper calendarMapper;
	@Autowired
	private ISpeCalendarLineService calendarLineService;
	@Autowired
	private MessageSource messageSource;
	
	@Override
	public List<SpeCalendar> selectData(SpeCalendar dto, int page, int pagesize) {
		PageHelper.startPage(page,pagesize);
		return calendarMapper.selectData(dto);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<ImportMessage> calendarUpdate(HttpServletRequest request,IRequest iRequest, SpeCalendar calendarHeader) throws CommonException {
		String fileData = calendarHeader.getFileData();
		List<ImportMessage> messages = new ArrayList<>();
		//先更新头表
		List<SpeCalendar> param = new ArrayList<SpeCalendar>();
		param.add(calendarHeader);
		this.batchUpdate(iRequest,param);
		calendarHeader = param.get(0);
		Long CalendarId = calendarHeader.getCalendarId();
		if(fileData != null){
			//删除行表数据
			calendarLineService.deleteByCalendarId(CalendarId);
			//让文件名不重复
	    	UUID uuid = UUID.randomUUID();
	    	File file = null;
	    	String fileName = uuid.toString();
	        // 去掉头部
			fileData = fileData.substring(fileData.indexOf(",")+1);
	        //获取字节数据
	        BASE64Decoder decoder = new BASE64Decoder();
	        try {
				byte[] decodedBytes = decoder.decodeBuffer(fileData);
				//在根目录创建文件
				String filePath=request.getServletContext().getRealPath("/");
				file = new File(filePath+fileName);
				log.info("文件路径为:"+file.getAbsolutePath());
				OutputStream os = new FileOutputStream(file);
				os.write(decodedBytes);
				os.flush();
				os.close();
			    messages = getLineData(file,calendarHeader.getFileType(),request);
				for(ImportMessage message:messages){
					if("false".equals(message.getStatus()))continue;
					SpeCalendarLine l = (SpeCalendarLine) message.getData();
					//设置参数，用于保存数据
					l.setCalendarId(CalendarId);
					l.set__status(DTOStatus.ADD);
					if("休息日".equals(l.getDayType())){
						l.setDayType("XXR");
					}else{
						l.setDayType("GZR");
					}
					calendarLineService.insertSelective(iRequest,l);
				}
			} catch (Exception e) {
				if(e instanceof CommonException){
					CommonException e1 = (CommonException)e;
					throw e1;
				}else{
					log.error("导入失败",e);
					throw new CommonException("FET","导入失败！",null);
				}
			}finally {
				//操作完成后，删除文件
				if(file != null){
					file.delete();
				}
				
			}
		}
		return messages;
	}
	
	/**
	 * 从Excel表格中读取行信息 
	 * @param File Excel文件
	 * @param String 文件类型
	 * @throws SpeException 
	 */
	public List<ImportMessage> getLineData(File f,String fileType,HttpServletRequest request) throws CommonException{
		Map<String,String> mapping = new HashMap<>();
		mapping = ImportUtil.getMapping(SpeCalendarLine.class, request,messageSource);
		return ImportUtil.getData(SpeCalendarLine.class,f,mapping,fileType);
	}

}