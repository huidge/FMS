package clb.core.supplier.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;


import clb.core.supplier.dto.SpeArchives;

public interface SpeArchivesMapper extends Mapper<SpeArchives>{
	
	public List<SpeArchives> getData(SpeArchives dto);

}