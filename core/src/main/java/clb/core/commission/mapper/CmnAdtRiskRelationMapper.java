package clb.core.commission.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.commission.dto.CmnAdtRiskRelation;

public interface CmnAdtRiskRelationMapper extends Mapper<CmnAdtRiskRelation>{

	List<CmnAdtRiskRelation> queryAll(CmnAdtRiskRelation dto);
	/**
	 *  查询 产品公司+附加险+主险组合  是否存在  by 产品公司+附加险 主险组合为空
	 * @param cmnAdtRiskRelation
	 * @return
	 */
	List<CmnAdtRiskRelation> queryByCondition(CmnAdtRiskRelation cmnAdtRiskRelation);

}