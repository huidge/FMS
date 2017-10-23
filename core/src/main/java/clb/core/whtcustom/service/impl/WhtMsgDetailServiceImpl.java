package clb.core.whtcustom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.whtcustom.dto.WhtMsgDetail;
import clb.core.whtcustom.mapper.WhtMsgDetailMapper;
import clb.core.whtcustom.service.IWhtMsgDetailService;

@Service
@Transactional
public class WhtMsgDetailServiceImpl extends BaseServiceImpl<WhtMsgDetail> implements IWhtMsgDetailService{
	
	@Autowired
	private WhtMsgDetailMapper WhtMsgDetailMapper;

	@Override
	public List<WhtMsgDetail> selectAllField(IRequest requestContext, WhtMsgDetail WhtMsgDetail, int page,
			int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<WhtMsgDetail> list = WhtMsgDetailMapper.selectAllField(WhtMsgDetail);
		return list;
		//return null;
	}

	@Override
	public List<WhtMsgDetail> selectTemplateName(IRequest requestContext, WhtMsgDetail WhtMsgDetail) {
		List<WhtMsgDetail> list = WhtMsgDetailMapper.selectTemplateName(WhtMsgDetail);
		return list;
		//return null;
	}
}