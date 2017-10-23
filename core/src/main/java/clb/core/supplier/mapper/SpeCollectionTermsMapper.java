package clb.core.supplier.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;


import clb.core.supplier.dto.SpeCollectionTerms;

public interface SpeCollectionTermsMapper extends Mapper<SpeCollectionTerms>{
	
	public List<SpeCollectionTerms> selectData(SpeCollectionTerms dto);

}