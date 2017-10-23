package clb.core.sys.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.sys.dto.SysTaskTimeCfg;

/*****
 * @author tiansheng.ye
 * @Date 2017-06-28
 */
public interface ISysTaskTimeCfgService extends IBaseService<SysTaskTimeCfg>,ProxySelf<ISysTaskTimeCfgService>{

	public List<SysTaskTimeCfg> queryAllField(IRequest request,int page,int pagesize,SysTaskTimeCfg dto);
	
	public List<SysTaskTimeCfg> batchUpdate(IRequest request,List<SysTaskTimeCfg> dtoList);
}
