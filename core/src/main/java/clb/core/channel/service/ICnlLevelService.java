package clb.core.channel.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.channel.dto.CnlLevel;

public interface ICnlLevelService extends IBaseService<CnlLevel>, ProxySelf<ICnlLevelService>{

	/**
	 * 通过分页查询
	 * @param cnlLevel
	 * @param requestContext
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<CnlLevel> selectByCondition(CnlLevel cnlLevel,IRequest requestContext,int page,int pageSize);
	
	/**
	 * 直接查询
	 * @param cnlLevel
	 * @return
	 */
	List<CnlLevel> selectByCondition(CnlLevel cnlLevel);
	
	/**
	 * 插入数据
	 * @param cnlLevel
	 * @return
	 */
	int cnlLevelInsert(CnlLevel cnlLevel);
	
	/**
	 * 查询条数
	 * @param cnlLevel
	 * @return
	 */
	List<CnlLevel> selectCountByCondition(CnlLevel cnlLevel);
	
	/**
	 * 查询上一版本信息
	 * @param channelClassCode
	 * @param levelName
	 * @param supplierId
	 * @param version
	 * @return
	 */
	CnlLevel selectPreviousVersion(String channelClassCode, String levelName,Long supplierId,Long version);
}