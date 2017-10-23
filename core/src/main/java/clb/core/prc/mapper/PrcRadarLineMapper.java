package clb.core.prc.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.prc.dto.PrcRadarLine;

public interface PrcRadarLineMapper extends Mapper<PrcRadarLine>{

	List<PrcRadarLine> selectByAttSetID(Long attSetId);
}