package clb.core.production.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.production.dto.PrdItemFile;

public interface PrdItemFileMapper extends Mapper<PrdItemFile> {
	
	List<PrdItemFile> selectByItemId(PrdItemFile prdItemFile);

}
