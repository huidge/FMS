package clb.core.sys.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.sys.dto.SysFunctionHandlePerson;

public interface SysFunctionHandlePersonMapper extends Mapper<SysFunctionHandlePerson>{

	public List<SysFunctionHandlePerson> queryTodayTaskTimeByFunctionCode(SysFunctionHandlePerson dto);
}
