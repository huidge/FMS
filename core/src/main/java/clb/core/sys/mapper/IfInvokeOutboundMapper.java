package clb.core.sys.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.sys.dto.IfInvokeOutbound;

public interface IfInvokeOutboundMapper extends Mapper<IfInvokeOutbound>{
	/**
	 * 定期清除    sys_if_invoke_inbound、sys_if_invoke_outbound表中的数据
	 * @param sql
	 */
	void truncateTable(String sql);

}