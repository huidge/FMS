package clb.core.common.service;

import java.util.HashMap;
import java.util.List;

/***
 * @Desc 通用service
 * @author tiansheng.ye@hand-china.com
 *
 */
@SuppressWarnings("rawtypes")
public interface ICommonService {
	
	/****
	 * @Desc 根据sql查询数据
	 * @param sql
	 * @param obj
	 * @return
	 */
	public List<HashMap> selectResultsBySql(String sql,Object obj);
}
