package clb.core.channel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.channel.dto.CnlLevel;

public interface CnlLevelMapper extends Mapper<CnlLevel>{

	/**
	 * 查询数据
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
	 * 查询数据条数
	 * @param cnlLevel
	 * @return
	 */
	List<CnlLevel> selectCountByCondition(CnlLevel cnlLevel);
	
	/**
	 * 通过条件查询上一版本的信息
	 * @return
	 */
	CnlLevel selectPreviousVersion(@Param(value="channelClassCode")String channelClassCode, @Param(value="levelName")String levelName,@Param(value="supplierId")Long supplierId,@Param(value="version")Long version);
}