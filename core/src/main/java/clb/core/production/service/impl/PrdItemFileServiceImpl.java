package clb.core.production.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.production.dto.PrdItemFile;
import clb.core.production.mapper.PrdItemFileMapper;
import clb.core.production.service.IPrdItemFileService;


@Service
@Transactional
public class PrdItemFileServiceImpl extends BaseServiceImpl<PrdItemFile> implements IPrdItemFileService{

	@Autowired
	private PrdItemFileMapper prdItemFileMapper;
	
	@Override
	public List<PrdItemFile> selectByItemId(IRequest requestContext,PrdItemFile prdItemFile , int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return prdItemFileMapper.selectByItemId(prdItemFile);
	}

}
