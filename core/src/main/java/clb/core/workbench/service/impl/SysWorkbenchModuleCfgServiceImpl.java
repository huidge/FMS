package clb.core.workbench.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.workbench.dto.SysWorkbenchModuleCfg;
import clb.core.workbench.mapper.SysWorkbenchModuleCfgMapper;
import clb.core.workbench.service.ISysWorkbenchModuleCfgService;

@Service
@Transactional
public class SysWorkbenchModuleCfgServiceImpl extends BaseServiceImpl<SysWorkbenchModuleCfg> implements ISysWorkbenchModuleCfgService{

	@Autowired
	private SysWorkbenchModuleCfgMapper sysWorkbenchModuleCfgMapper;
	@Autowired
    private ISysFileService fileService;
	
	@Override
	public List<SysWorkbenchModuleCfg> selectFunctionInfo(IRequest requestContext, SysWorkbenchModuleCfg dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		List<SysWorkbenchModuleCfg> selectFunctionInfo = sysWorkbenchModuleCfgMapper.selectFunctionInfo(dto);
		for (SysWorkbenchModuleCfg sysWorkbenchModuleCfg : selectFunctionInfo) {
			if(sysWorkbenchModuleCfg.getIconFileId() != null || "".equals(sysWorkbenchModuleCfg.getIconFileId())){
				SysFile sysFile = fileService.selectByPrimaryKey(requestContext, sysWorkbenchModuleCfg.getIconFileId());
                if (sysFile != null) {
                	sysWorkbenchModuleCfg.set_token(sysFile.get_token());
                }else {
                	sysWorkbenchModuleCfg.set_token(null);
                }
			}
		}
		return selectFunctionInfo;
		//return sysWorkbenchModuleCfgMapper.selectFunctionInfo(dto);
		//return null;
	}

	@Override
	public List<SysWorkbenchModuleCfg> wsSelectFunctionInfo(IRequest requestContext, SysWorkbenchModuleCfg dto, int page,
														  int pageSize) {
//		PageHelper.startPage(page, pageSize);
//		JSONObject session = getSessionBean(httpServletRequest);
//		IRequest requestContext = requestListener.newInstance();
//		if (session != null) {
//			requestContext.setUserId(session.getLong("userId"));
//			Locale locale = RequestContextUtils.getLocale(httpServletRequest);
//			if (locale != null) {
//				requestContext.setLocale(locale.toString());
//			}
//		}
		dto.setUserId(requestContext.getUserId());
		List<SysWorkbenchModuleCfg> selectFunctionInfo = sysWorkbenchModuleCfgMapper.wsSelectFunctionInfo(dto);
		for (SysWorkbenchModuleCfg sysWorkbenchModuleCfg : selectFunctionInfo) {
			if(sysWorkbenchModuleCfg.getIconFileId() != null || "".equals(sysWorkbenchModuleCfg.getIconFileId())){
				SysFile sysFile = fileService.selectByPrimaryKey(requestContext, sysWorkbenchModuleCfg.getIconFileId());
				if (sysFile != null) {
					sysWorkbenchModuleCfg.set_token(sysFile.get_token());
				}else {
					sysWorkbenchModuleCfg.set_token(null);
				}
			}
		}
		return selectFunctionInfo;
	}
}