package clb.core.sys.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.sys.dto.SysFunctionAllocationRule;
/*****
 * @author tiansheng.ye
 * @Date 2017-05-25
 */
public interface SysFunctionAllocationRuleMapper extends Mapper<SysFunctionAllocationRule>{

	public List<SysFunctionAllocationRule> selectByDto(SysFunctionAllocationRule dto);
}
