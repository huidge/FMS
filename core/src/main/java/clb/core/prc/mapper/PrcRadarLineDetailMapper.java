package clb.core.prc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.prc.dto.PrcRadarLineDetail;

public interface PrcRadarLineDetailMapper extends Mapper<PrcRadarLineDetail>{

	List<PrcRadarLineDetail> selectByLineId(Long lineId);
	
	List<PrcRadarLineDetail> selectAttSetInfo(PrcRadarLineDetail dto);
	
	PrcRadarLineDetail selectLineDetailInfo(@Param(value="attSetId")Long AttSetId, @Param(value="detailName")String detailName,@Param(value="detailId")Long detailId);
	
	List<PrcRadarLineDetail> wsSelectLineDetail(PrcRadarLineDetail dto);
	
}