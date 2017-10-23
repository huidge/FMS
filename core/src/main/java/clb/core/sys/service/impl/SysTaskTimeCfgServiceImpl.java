package clb.core.sys.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.sys.dto.SysTaskTimeCfg;
import clb.core.sys.mapper.SysTaskTimeCfgMapper;
import clb.core.sys.service.ISysTaskTimeCfgService;

@Service
@Transactional
public class SysTaskTimeCfgServiceImpl extends BaseServiceImpl<SysTaskTimeCfg> implements ISysTaskTimeCfgService{

	@Autowired
	private SysTaskTimeCfgMapper sysTaskTimeCfgMapper;
	
	@Override
	public List<SysTaskTimeCfg> queryAllField(IRequest request, int page, int pagesize, SysTaskTimeCfg dto) {
		PageHelper.startPage(page, pagesize);
		return sysTaskTimeCfgMapper.queryAllField(dto);
	}

	@Override
	public List<SysTaskTimeCfg> batchUpdate(IRequest request,List<SysTaskTimeCfg> dtoList) {
		for(SysTaskTimeCfg dto:dtoList){
			List<SysTaskTimeCfg>list= sysTaskTimeCfgMapper.queryCfg(dto);
        	if(!CollectionUtils.isEmpty(list)){
        		if(dto.getCfgId()==null || !list.get(0).getCfgId().equals(dto.getCfgId())){
                    throw new RuntimeException("设置的数据中含有重复数据，新增/修改失败");
        		}
        	}
			if(dto.getCfgId()==null){
				self().insertSelective(request, dto);
			}else{
				self().updateByPrimaryKey(request, dto);
			}
		}
		return dtoList;
	}


}