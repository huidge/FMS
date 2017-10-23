package clb.core.whtcustom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.whtcustom.dto.WhtMsgTemplate;
import clb.core.whtcustom.mapper.WhtMsgTemplateMapper;
import clb.core.whtcustom.service.IWhtMsgTemplateService;

@Service
@Transactional
public class WhtMsgTemplateServiceImpl extends BaseServiceImpl<WhtMsgTemplate> implements IWhtMsgTemplateService{
	
	@Autowired
    private WhtMsgTemplateMapper WhtMsgTemplateMapper;
	
	@Override
	public List<WhtMsgTemplate> selectAllField(IRequest requestContext, WhtMsgTemplate WhtMsgTemplate, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<WhtMsgTemplate> WhtMsgTemplate2 = WhtMsgTemplateMapper.selectAllField(WhtMsgTemplate);
		return WhtMsgTemplate2;
	}
	
	@Override
	public List<WhtMsgTemplate> selectAll(IRequest requestContext, WhtMsgTemplate WhtMsgTemplate) {
		List<WhtMsgTemplate> WhtMsgTemplate2 = WhtMsgTemplateMapper.selectAllField(WhtMsgTemplate);
		return WhtMsgTemplate2;
	}
}