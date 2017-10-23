package clb.core.channel.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.dto.CnlLevel;
import clb.core.channel.mapper.CnlLevelMapper;
import clb.core.channel.service.ICnlLevelService;
import clb.core.common.utils.SpringConfigTool;

@Service
@Transactional
public class CnlLevelServiceImpl extends BaseServiceImpl<CnlLevel> implements ICnlLevelService{

	@Autowired
	private CnlLevelMapper cnlLevelMapper;
	
	@Override
	public List<CnlLevel> selectByCondition(CnlLevel cnlLevel) {
		return cnlLevelMapper.selectByCondition(cnlLevel);
	}

	@Override
	public List<CnlLevel> selectByCondition(CnlLevel cnlLevel, IRequest requestContext, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return cnlLevelMapper.selectByCondition(cnlLevel);
	}

	@Override
	public int cnlLevelInsert(CnlLevel cnlLevel) {
		return cnlLevelMapper.cnlLevelInsert(cnlLevel);
	}

	@Override
	public List<CnlLevel> selectCountByCondition(CnlLevel cnlLevel) {
		return cnlLevelMapper.selectCountByCondition(cnlLevel);
	}

	@Override
	public CnlLevel selectPreviousVersion(String channelClassCode, String levelName, Long supplierId, Long version) {
		return cnlLevelMapper.selectPreviousVersion(channelClassCode, levelName, supplierId, version);
	}

}