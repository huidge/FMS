package clb.core.whtcustom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.whtcustom.dto.WhtOfficialAccountCfg;
import clb.core.whtcustom.mapper.WhtOfficialAccountCfgMapper;
import clb.core.whtcustom.service.IWhtOfficialAccountCfgService;

@Service
@Transactional
public class WhtOfficialAccountCfgServiceImpl extends BaseServiceImpl<WhtOfficialAccountCfg> implements IWhtOfficialAccountCfgService{
	
	@Autowired
	private WhtOfficialAccountCfgMapper whtOfficialAccountCfgMapper;
	
	@Override
	public List<WhtOfficialAccountCfg> selectAllField(IRequest requestContext,
			WhtOfficialAccountCfg whtOfficialAccountCfg, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<WhtOfficialAccountCfg> list = whtOfficialAccountCfgMapper.selectAllField(whtOfficialAccountCfg);
		return list;
		//return null;
	}

	@Override
	public List<WhtOfficialAccountCfg> selectAccountName(IRequest requestContext,
			WhtOfficialAccountCfg whtOfficialAccountCfg) {
		List<WhtOfficialAccountCfg> list = whtOfficialAccountCfgMapper.selectAccountName(whtOfficialAccountCfg);
		return list;
		//return null;
	}

	@Override
	public List<WhtOfficialAccountCfg> selectAll(IRequest requestContext,
			WhtOfficialAccountCfg whtOfficialAccountCfg) {
		List<WhtOfficialAccountCfg> list = whtOfficialAccountCfgMapper.selectAllField(whtOfficialAccountCfg);
		return list;
	}

}