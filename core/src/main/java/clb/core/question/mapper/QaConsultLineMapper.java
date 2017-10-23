package clb.core.question.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.question.dto.QaConsultLine;

public interface QaConsultLineMapper extends Mapper<QaConsultLine>{
	List<QaConsultLine> selectAllFields(QaConsultLine qaConsultLine);

    List<QaConsultLine> query(QaConsultLine dto);
}