package clb.core.prc.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.prc.dto.PrcItemScore;

public interface PrcItemScoreMapper extends Mapper<PrcItemScore>{

	
	/**
	 * 查出所有产品
	 * @param attSetId
	 * @return
	 */
	List<Map<String, String>> queryColumns(Long attSetId);
	
	
	/**
	 * 获取所有行信息名称
	 * @param attSetId
	 * @return
	 */
	List<PrcItemScore> selectNameInfo(Long attSetId);
	
	
	/**
	 * 查询所有产品分数关系
	 * @param attSetId
	 * @return
	 */
	PrcItemScore selectItemScoreInfo(@Param(value="compDimName")String compDimName, @Param(value="itemId")Long itemId, @Param(value="attSetId")Long attSetId);
	
	/**
	 * 查询所有产品
	 * @return
	 */
	List<PrcItemScore> queryItems(Long attSetId);
	
	
	/**
	 * 查询行维度
	 * @param attSetId
	 * @return
	 */
	List<PrcItemScore> queryRadarLine(Long attSetId); 
	
	/**
	 * 查询行名细维度
	 * @param lineId
	 * @return
	 */
	List<PrcItemScore> queryRadarLineDetail(Long lineId); 
	
	/**
	 * 通过名称查询行维度信息
	 * @param compDimName
	 * @return
	 */
	PrcItemScore queryRadarLineByName(@Param(value="compDimName")String compDimName,@Param(value="attSetIt")Long attSetIt);
	
	/**
	 * 通过名称查询行明细维度信息
	 * @param detailName
	 * @return
	 */
	PrcItemScore queryRadarLineDetailByName(String detailName);
	
	/**
	 * 按条件查询是否存在产品分数信息
	 * @param prcItemScore
	 * @return
	 */
	PrcItemScore selectByCondition(PrcItemScore prcItemScore);
	
	/**
	 * 插入数据
	 * @param prcItemScore
	 * @return
	 */
	int insertItemScore(PrcItemScore prcItemScore);
	
	/**
	 * 数据更新
	 * @param prcItemScore
	 * @return
	 */
	int updateItemScore(PrcItemScore prcItemScore);
	
}