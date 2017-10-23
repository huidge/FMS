package clb.core.sys.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.sys.dto.IfInvokeInbound;

public interface IfInvokeInboundMapper extends Mapper<IfInvokeInbound>{
	/**
	 * 定期清除    sys_if_invoke_inbound、sys_if_invoke_outbound表中的数据
	 * @param sql
	 */
	void truncateTable(String sql);

}