package clb.core.order.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdAfterFollow;
import clb.core.order.mapper.OrdAfterFollowMapper;
import clb.core.order.service.IOrdAfterFollowService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdAfterFollowServiceImpl extends BaseServiceImpl<OrdAfterFollow> implements IOrdAfterFollowService{
	
	@Autowired
	private OrdAfterFollowMapper mapper;
	
	@Override
	public List<OrdAfterFollow> queryByAfterId(IRequest requestCtx, OrdAfterFollow dto, int page,int pageSize) {
		PageHelper.startPage(page, pageSize);
		return mapper.queryByAfterId(dto);
	}
	/**
	 * 查询最后一条记录
	 */
	@Override
	public OrdAfterFollow queryLastOrdAfterFollowByAfterId(OrdAfterFollow dto) {
		return mapper.queryLastOrdAfterFollowByAfterId(dto);
	}

}