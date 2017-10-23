package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdFile;

import java.util.List;

public interface OrdFileMapper extends Mapper<OrdFile>{
    /**
     * 订单客户查询
     * @param ordFile
     * @return
     */
    public List<OrdFile> queryOrdFile(OrdFile ordFile);
}