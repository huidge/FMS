package clb.core.course.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.course.dto.TrnSupport;

public interface TrnSupportMapper extends Mapper<TrnSupport>{
	List<TrnSupport> selectAllField(TrnSupport trnSupport);

	Long supportQueryTotal(TrnSupport trnSupport);

	List<TrnSupport> updateAmountInvalid(TrnSupport trnSupport);
}