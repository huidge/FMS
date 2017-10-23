package clb.core.channel.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.channel.dto.CnlProSupRelation;


public interface CnlProSupRelationMapper extends Mapper<CnlProSupRelation>{

	/**
	 * 查询渠道供应商产品关系信息
	 * @param cnlProSupRelation
	 * @return
	 */
	List<CnlProSupRelation> selectByCondition(CnlProSupRelation cnlProSupRelation);
	
	/**
	 * 通过渠道名称查询渠道id
	 * @param channelName
	 * @return
	 */
	Long selectChannelId(String channelName);
	
	/**
	 * 通过供应商名称查询供应商id
	 * @param name
	 * @return
	 */
	Long selectSupplierId(String name);
	
	/**
	 * 通过产品名称查询产品id 
	 * @param itemName
	 * @return
	 */
	Long selectProductId(String itemName);
	
	/**
	 * 插入产品渠道供应商关系
	 * @param cnlProSupRelation
	 * @return
	 */
	int insertFmsCnlProSupRelation(CnlProSupRelation cnlProSupRelation);
	
	/**
	 * 判断去从逻辑
	 * @param cnlProSupRelation
	 * @return
	 */
	int selectByAllInfo(CnlProSupRelation cnlProSupRelation);

	List<CnlProSupRelation> selectByConditionNull(CnlProSupRelation cnlProSupRelation);
}
