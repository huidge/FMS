package clb.core.channel.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.channel.dto.CnlProSupRelation;
import clb.core.channel.service.ICnlProSupRelationService;
import clb.core.common.utils.CommonUtil;
import jodd.util.CollectionUtil;

@Controller
@RequestMapping("/cnlProSupRelation")
public class CnlProSupRelationController extends BaseController {

	@Autowired
	private ICnlProSupRelationService cnlProSupRelationService;

	/**
	 * 查询数据
	 * 
	 * @param vendorRelationship
	 * @param request
	 * @param page
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData query(CnlProSupRelation cnlProSupRelation, HttpServletRequest request,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(
				cnlProSupRelationService.selectByCondition(requestContext, cnlProSupRelation, page, pagesize));
	}

	/**
	 * 更新数据
	 * 
	 * @param request
	 * @param prdClassList
	 * @param result
	 * @return
	 */
	@RequestMapping(value = { "/submit" }, method = { RequestMethod.POST })
	@ResponseBody
	public ResponseData submit(HttpServletRequest request, @RequestBody List<CnlProSupRelation> cnlProSupRelationList,
			BindingResult result) {
		getValidator().validate(cnlProSupRelationList, result);
		ResponseData responseData = new ResponseData();
		if (result.hasErrors()) {
			responseData.setSuccess(false);
			responseData.setMessage(getErrorMessage(result, request));
			return responseData;
		}
		IRequest iRequest = createRequestContext(request);
		try {
			for (CnlProSupRelation dto1 : cnlProSupRelationList) {
				CnlProSupRelation dto = new CnlProSupRelation();
				dto=this.toNullProSupRelation(dto1,dto);
				if (dto.getId() == null) {
					List<CnlProSupRelation> selectByCondition = cnlProSupRelationService.selectByConditionNull(iRequest,
							dto, 1, 2);
					if (CollectionUtils.isNotEmpty(selectByCondition)) {
						responseData.setSuccess(false);
						responseData.setMessage("请勿重复保存!");
						return responseData;
					}else{
						cnlProSupRelationService.insertSelective(iRequest, dto);
					}

				} else {
					List<CnlProSupRelation> selectByCondition = cnlProSupRelationService.selectByConditionNull(iRequest,
							dto, 1, 2);
					if (CollectionUtils.isNotEmpty(selectByCondition)) {
						responseData.setSuccess(false);
						responseData.setMessage("请勿重复保存!");
						return responseData;
					}else{
						cnlProSupRelationService.updateByPrimaryKey(iRequest, dto);
					}
				}
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			ResponseData response = new ResponseData(false);
			response.setMessage(e.getMessage());
			return response;
		}
		CnlProSupRelation relation = new CnlProSupRelation();
		List<CnlProSupRelation> cnlProSupRelationlist = cnlProSupRelationService.selectByCondition(iRequest, relation, 1, 10);
		return new ResponseData(cnlProSupRelationlist);
	}

	private CnlProSupRelation toNullProSupRelation(CnlProSupRelation origin, CnlProSupRelation destination) {
		if (origin == null || destination == null)
            return destination;
        if (!origin.getClass().equals(destination.getClass()))
            return destination;
 
        Field[] fields = origin.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                fields[i].setAccessible(true);
                Object value = fields[i].get(origin);
                if (null != value&&!("".equals(value))) {
                    fields[i].set(destination, value);
                }
                fields[i].setAccessible(false);
            } catch (Exception e) {
            }
        }
        return destination;
        
    }


	@RequestMapping(value = "/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<CnlProSupRelation> dto) {
		cnlProSupRelationService.batchDelete(dto);
		return new ResponseData();
	}

	/**
	 * 导入excel
	 * 
	 * @param request
	 * @return
	 * @throws StoragePathNotExsitException
	 * @throws UniqueFileMutiException
	 * @throws Exception
	 */
	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public ResponseData upload(HttpServletRequest request, MultipartFile files)
			throws IOException, InvalidFormatException {
		IRequest iRequest = createRequestContext(request);
		String fileName = files.getOriginalFilename();
		Workbook wb = WorkbookFactory.create(files.getInputStream());
		Sheet sheet = wb.getSheetAt(0);
		int endRowNo = sheet.getLastRowNum();

		ResponseData responseData = new ResponseData();
		if (endRowNo == 0) {
			responseData.setMessage(fileName + "是一个空文件！");
			responseData.setSuccess(false);
			wb.close();
			return responseData;
		} else {
			try {
				cnlProSupRelationService.insertAllValue(wb, iRequest);
				responseData.setSuccess(true);
			} catch (Exception e) {
				responseData.setMessage(e.getMessage());
				responseData.setSuccess(false);
				return responseData;
			} finally {
				wb.close();
			}

			return responseData;
		}
	}

	/**
	 * excel模板下载
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/templateExcelOut", method = RequestMethod.GET)
	public void excelStandardTemplateOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String str = request.getSession().getServletContext().getRealPath("/");
		str = str + "/template/渠道产品供应商关系导入模板.xlsx";// Excel模板所在的路径。
		File f = new File(str);
		// 设置response参数，可以打开下载页面
		response.setContentType("application/vnd.ms-excel");
		try {
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(("渠道产品供应商关系导入模板" + ".xlsx").getBytes("gbk"), "iso-8859-1")); // 下载文件的名称
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ServletOutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(f));
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
	}
}
