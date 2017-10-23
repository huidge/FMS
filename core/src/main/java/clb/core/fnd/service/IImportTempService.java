package clb.core.fnd.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.system.service.IBaseService;

import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.utils.ValidationTableException;

/**
 * 
 * @name IHssSetupImportTempService
 * @author junqiang.xiao@hand-china.com 2016年12月20日下午1:22:23
 * @description 导入临时表
 * @version
 */
public interface IImportTempService
		extends IBaseService<ImportTemp>, ProxySelf<IImportTempService> {


	/**
	 * 查询错误消息
	 * 
	 * @param
	 *
	 * @return
	 */
	List<ImportTemp> selectErrorMessage(Long importBatchId, int page, int pagesize,
                                        IRequest request);


	/**
	 * 查询导入数据
	 * 
	 * @param
	 *
	 * @return
	 */
	List<ImportTemp> selectImportData(Long importBatchId, int page, int pagesize,
                                      IRequest request);

	/**
	 * 错误个数
	 * 
	 * @param
	 *
	 * @return
	 */
	Long selectErrorCount(Long importBatchId);

	/**
	 * @Title: updateError 
	 * @Description: 根据错误信息更新临时表数据  
	 * @param importTemp
	 * @return int
	 * @author xiang.ding@hand-china.com
	 */
	void updateError(ImportTemp importTemp, IRequest request);

	/**
	 * @Description: 根据所得的流水号验证是否已经存在 
	 * @param importBatchId
	 * @return boolean
	 */
	boolean selectImportBactIdIsExist(Long importBatchId);

	/**
	 * @Description: 清空临时表
	 */
	void deleteAll();

	/**
	 * @Description: 查询需要删除的数据 
	 * @return int
	 */
	int selectCountNeedDelete();
	
	/**
	 * @Description:  历史数据导入模板下载 
	 * @param config
	 * @param request
	 * @param httpServletResponse
	 * @param requestContext void
	 * @throws Exception 
	 */
	void downloadExcel(ExportConfig<Map<String, String>, ColumnInfo> config, HttpServletRequest request,
                       HttpServletResponse httpServletResponse, IRequest requestContext) throws Exception;

	/**
	 * @Description: 导入错误信息下载 
	 * @param importTemps
	 * @param request
	 * @param httpServletResponse
	 * @param requestContext void
	 * @throws Exception 
	 */
	void downloadErrorExcel(List<ImportTemp> importTemps, HttpServletRequest request,
                            HttpServletResponse httpServletResponse, IRequest requestContext) throws Exception;

	/**
	 * @Description: 从excel文件中导入数据
	 * @param requestContext
	 * @param file
	 * @return List<ImportTemp>
	 * @throws Exception 
	 */
	List<ImportTemp> batchCreateFromExcel(IRequest requestContext, MultipartFile file) throws Exception;

	/**
	 * @Description: 导入数据 
	 * @param requestContext
	 * @param importBatchId
	 * @param
	 * @param
	 * @throws
	 * @throws Exception 
	 */
	void importData(IRequest requestContext, Long importBatchId, String className) throws ValidationTableException, Exception;
}