package clb.core.prc.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.prc.dto.PrcAttributeSet;

public interface PrcAttributeSetMapper extends Mapper<PrcAttributeSet>{

	List<PrcAttributeSet> selectSetName();
	
	List<PrcAttributeSet> selectByCondition(PrcAttributeSet prcAttributeSet);
}