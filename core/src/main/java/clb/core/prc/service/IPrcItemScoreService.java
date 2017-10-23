package clb.core.prc.service;

import java.util.List;
import java.util.Map;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.prc.dto.PrcItemScore;

public interface IPrcItemScoreService extends IBaseService<PrcItemScore>, ProxySelf<IPrcItemScoreService>{

	/**
	 * 查询列信息
	 * @param attSetId
	 * @return
	 */
	List<Map<String, String>> queryColumns(Long attSetId);
	
	/**
	 * 前台页面查询维度分数信息
	 * @param attSetId
	 * @return
	 */
	List<Map<String,String>> selectItemScoreInfo(Long attSetId,IRequest requestCtx);

	/**
	 * 更新维度信息
	 * @param map
	 */
	 void updateScoreItem(Map<String, String> map,Long attSetId, IRequest request );
	 
}