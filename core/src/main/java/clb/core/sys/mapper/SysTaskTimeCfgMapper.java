package clb.core.sys.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.sys.dto.SysTaskTimeCfg;

public interface SysTaskTimeCfgMapper extends Mapper<SysTaskTimeCfg>{

	public List<SysTaskTimeCfg> queryAllField(SysTaskTimeCfg dto);
	
	public List<SysTaskTimeCfg> queryPlanHandle(SysTaskTimeCfg dto);
	
	public List<SysTaskTimeCfg> queryByParams(SysTaskTimeCfg dto);
	
	public List<SysTaskTimeCfg> queryCfg(SysTaskTimeCfg dto);
}