package clb.core.sys.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import clb.core.sys.dto.SysCommonFile;

public interface ISysCommonFileService extends IBaseService<SysCommonFile>, ProxySelf<ISysCommonFileService>{

	/**
	 * 查询所有文件
	 * @param sysCommonFile
	 * @return
	 */
	List<SysCommonFile> selectSysCommonFileInfo(IRequest requestContext,SysCommonFile sysCommonFile,int page,int pageSize);

	/**
	 * 导入资料库文件
	 * @param requestCtx
	 * @param dto
	 */
	void importSysFile(IRequest requestCtx, SysCommonFile dto) throws Exception;
	
	/**
	 * 资料导入
	 * @param requestContext
	 * @param fileName
	 * @param type
	 * @return
	 */
	ResponseData insertFileInfo(IRequest requestContext, String fileName, String type) throws Exception;
}