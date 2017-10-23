package clb.core.commission.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.commission.dto.CmnSupplierItemRelation;

public interface CmnSupplierItemRelationMapper extends Mapper<CmnSupplierItemRelation>{

	List<CmnSupplierItemRelation> selectByCondition(CmnSupplierItemRelation dto);

}