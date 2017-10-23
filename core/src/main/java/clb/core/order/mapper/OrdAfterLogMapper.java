package clb.core.order.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdAfterLog;

public interface OrdAfterLogMapper extends Mapper<OrdAfterLog>{

	List<OrdAfterLog> query(OrdAfterLog dto);

}